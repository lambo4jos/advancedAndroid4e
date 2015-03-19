package com.advancedandroidbook.simpleopengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLDebugHelper;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ShowGLTextureActivity extends Activity {
	public final static String USE_COLOR_CUBE_EXTRA = "COLOR_CUBE";

	private boolean useColoredCube = false;

	// used to send messages back to this thread
	public final Handler mHandler = new Handler();

	// the view to draw the FPS on
	public TextView mFPSText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent starter = getIntent();
		useColoredCube = starter.getBooleanExtra(USE_COLOR_CUBE_EXTRA, false);

		mAndroidSurface = new BasicGLSurfaceView(this);

		setContentView(R.layout.constrained);
		FrameLayout v = (FrameLayout) findViewById(R.id.gl_container);
		v.addView(mAndroidSurface);

		mFPSText = (TextView) findViewById(R.id.fps_text);
	}

	private class BasicGLSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {
		SurfaceHolder mAndroidHolder;

		BasicGLSurfaceView(Context context) {
			super(context);
			mAndroidHolder = getHolder();
			mAndroidHolder.addCallback(this);


			setFocusable(true);
			// if the following is off, key events will stop coming in
			setFocusableInTouchMode(true);
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mGLThread = new BasicGLThread(this);

			mGLThread.start();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mGLThread != null) {
				mGLThread.requestStop();
			}
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_F:
				mGLThread.toggleFPSDisplay();
				return true;
			case KeyEvent.KEYCODE_P:
				mGLThread.setAnim(false);
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			switch (keyCode) {

			case KeyEvent.KEYCODE_P:
				mGLThread.setAnim(true);
				return true;
			}
			return super.onKeyUp(keyCode, event);
		}

	}

	BasicGLThread mGLThread;

	private class BasicGLThread extends Thread {
		private static final String DEBUG_TAG_GL = "GL";
		private static final String DEBUG_TAG = "BasicGLThread";
		SurfaceView sv;

		BasicGLThread(SurfaceView view) {
			sv = view;
		}

		private boolean animState = true;

		public void setAnim(boolean newState) {
			animState = newState;
		}

		private boolean showFPS = true;

		public void toggleFPSDisplay() {
			showFPS = !showFPS;
			if (!showFPS) {
				mHandler.post(new Runnable() {
					public void run() {
						mFPSText.setText("FPS off (press 'f' to turn on)");
					}
				});
			}
		}

		final long mSkipTime = 5000;
		long mFrames;

		long mLastTime;

		public void calculateAndDisplayFPS() {
			if (showFPS) {
				long thisTime = System.currentTimeMillis();
				if (thisTime - mLastTime < mSkipTime) {
					mFrames++;
				} else {
					mFrames++;
					final long fps = mFrames / ((thisTime - mLastTime) / 1000);
					mFrames = 0;
					mLastTime = thisTime;
					mHandler.post(new Runnable() {
						public void run() {
							mFPSText.setText("FPS = " + fps
									+ " (press 'f' to turn off)");
						}
					});
				}
			}
		}

		private boolean mDone = false;

		public void run() {
			try {
				initEGL();
				initGL();

				TexCubeSmallGLUT cube = new TexCubeSmallGLUT(3);

				// create room for two textures
				mGL.glEnable(GL10.GL_TEXTURE_2D);

				// use this texture unit
				mGL.glActiveTexture(GL10.GL_TEXTURE0);

				int[] textures = new int[1];
				mGL.glGenTextures(1, textures, 0);
				cube.setTex(mGL, sv.getContext(), textures[0],
						R.drawable.android);

				mGL.glMatrixMode(GL10.GL_MODELVIEW);
				mGL.glLoadIdentity();
				GLU.gluLookAt(mGL, 0, 0, 8f, 0, 0, 0, 0, 1, 0f);
				while (!mDone) {
					if (animState) {
						mGL.glClear(GL10.GL_COLOR_BUFFER_BIT
								| GL10.GL_DEPTH_BUFFER_BIT);
						mGL.glRotatef(1f, 1f, 1f, 1f);
						if (useColoredCube) {
							mGL.glColor4f(1f, 0f, 0f, 1f);
						}
						cube.draw(mGL);

						mEGL.eglSwapBuffers(mGLDisplay, mGLSurface);

						calculateAndDisplayFPS();
					}
				}
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "GL Failure", e);
			} finally {
				cleanupGL();
			}
		}

		public void requestStop() {
			mDone = true;
			try {
				join();
			} catch (InterruptedException e) {
				Log.e(DEBUG_TAG_GL, "failed to stop gl thread", e);
			}

			cleanupGL();
		}

		private void cleanupGL() {
			if (mEGL != null) {
				mEGL.eglMakeCurrent(mGLDisplay, EGL10.EGL_NO_SURFACE,
						EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
				mEGL.eglDestroySurface(mGLDisplay, mGLSurface);
				mEGL.eglDestroyContext(mGLDisplay, mGLContext);
				mEGL.eglTerminate(mGLDisplay);
				mEGL = null;
			}
			Log.i(DEBUG_TAG_GL, "GL Cleaned up");
		}

		public void initGL() {
			int width = sv.getWidth();
			int height = sv.getHeight();
			mGL.glViewport(0, 0, width, height);
			mGL.glMatrixMode(GL10.GL_PROJECTION);
			mGL.glLoadIdentity();
			float aspect = (float) width / height;
			GLU.gluPerspective(mGL, 45.0f, aspect, 1.0f, 30.0f);
			mGL.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
			mGL.glClearDepthf(1.0f);

			// light
			mGL.glEnable(GL10.GL_LIGHTING);

			// the first light
			mGL.glEnable(GL10.GL_LIGHT0);

			// ambient values
			mGL.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, new float[] { 0.1f,
					0.1f, 0.1f, 1f }, 0);

			// light that reflects in all directions
			mGL.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, new float[] { 1f,
					1f, 1f, 1f }, 0);

			// place it in projection space
			mGL.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, new float[] { 10f,
					0f, 10f, 1 }, 0);

			// allow our object colors to create the diffuse/ambient material
			// setting
			mGL.glEnable(GL10.GL_COLOR_MATERIAL);

			// some rendering options
			mGL.glShadeModel(GL10.GL_SMOOTH);

			mGL.glEnable(GL10.GL_DEPTH_TEST);
			// mGL.glDepthFunc(GL10.GL_LEQUAL);
			mGL.glEnable(GL10.GL_CULL_FACE);

			mGL.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

			// the only way to draw primitives with OpenGL ES
			mGL.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			Log.i(DEBUG_TAG_GL, "GL initialized");
		}

		public void initEGL() throws Exception {
			mEGL = (EGL10) GLDebugHelper.wrap(EGLContext.getEGL(),
					GLDebugHelper.CONFIG_CHECK_GL_ERROR
							| GLDebugHelper.CONFIG_CHECK_THREAD, null);

			if (mEGL == null) {
				throw new Exception("Couldn't get EGL");
			}

			mGLDisplay = mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

			if (mGLDisplay == null) {
				throw new Exception("Couldn't get display for GL");
			}

			int[] curGLVersion = new int[2];
			mEGL.eglInitialize(mGLDisplay, curGLVersion);

			Log.i(DEBUG_TAG_GL, "GL version = " + curGLVersion[0] + "."
					+ curGLVersion[1]);

			EGLConfig[] configs = new EGLConfig[1];
			int[] num_config = new int[1];
			mEGL.eglChooseConfig(mGLDisplay, mConfigSpec, configs, 1,
					num_config);
			mGLConfig = configs[0];

			mGLSurface = mEGL.eglCreateWindowSurface(mGLDisplay, mGLConfig,
					sv.getHolder(), null);

			if (mGLSurface == null) {
				throw new Exception("Couldn't create new surface");
			}

			mGLContext = mEGL.eglCreateContext(mGLDisplay, mGLConfig,
					EGL10.EGL_NO_CONTEXT, null);

			if (mGLContext == null) {
				throw new Exception("Couldn't create new context");
			}

			if (!mEGL.eglMakeCurrent(mGLDisplay, mGLSurface, mGLSurface,
					mGLContext)) {
				throw new Exception("Failed to eglMakeCurrent");
			}

			mGL = (GL10) GLDebugHelper.wrap(mGLContext.getGL(),
					GLDebugHelper.CONFIG_CHECK_GL_ERROR
							| GLDebugHelper.CONFIG_CHECK_THREAD
							| GLDebugHelper.CONFIG_LOG_ARGUMENT_NAMES, null);

			if (mGL == null) {
				throw new Exception("Failed to get GL");
			}

		}

		//  OpenGL variables
		GL10 mGL;
		EGL10 mEGL;
		EGLDisplay mGLDisplay;
		EGLConfig mGLConfig;
		EGLSurface mGLSurface;
		EGLContext mGLContext;
		int[] mConfigSpec = { EGL10.EGL_RED_SIZE, 5, EGL10.EGL_GREEN_SIZE, 6,
				EGL10.EGL_BLUE_SIZE, 5, EGL10.EGL_DEPTH_SIZE, 16,
				EGL10.EGL_NONE };
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	SurfaceView mAndroidSurface;
}