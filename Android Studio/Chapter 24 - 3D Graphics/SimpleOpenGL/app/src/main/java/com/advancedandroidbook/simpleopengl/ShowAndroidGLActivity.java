package com.advancedandroidbook.simpleopengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class ShowAndroidGLActivity extends Activity {
	CustomSurfaceView mAndroidSurface = null;

	@Override
	protected void onPause() {
		super.onPause();
		if (mAndroidSurface != null) {
			mAndroidSurface.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAndroidSurface != null) {
			mAndroidSurface.onResume();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAndroidSurface = new CustomSurfaceView(this);
		setContentView(mAndroidSurface);
	}

	private class CustomSurfaceView extends GLSurfaceView {
		final CustomRenderer mRenderer = new CustomRenderer();

		public CustomSurfaceView(Context context) {
			super(context);
			setFocusable(true);
			setFocusableInTouchMode(true);
			setEGLContextClientVersion(1);
			setRenderer(mRenderer);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_P:
				queueEvent(new Runnable() {
					public void run() {
						mRenderer.togglePause();
					}
				});
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
	}

	private class CustomRenderer implements GLSurfaceView.Renderer {

		private static final String DEBUG_TAG = "ShowAndroidGLActivity$CustomRenderer";
		TriangleSmallGLUT mTriangle = new TriangleSmallGLUT(3);
		boolean fAnimPaused = false;

		public void onDrawFrame(GL10 gl) {

			if (!fAnimPaused) {
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
				gl.glRotatef(1f, 0, 0, 1f);

				if (mTriangle != null) {
					mTriangle.drawColorful(gl);
				}
			}
		}

		public void togglePause() {
			if (fAnimPaused == true) {
				fAnimPaused = false;
			} else {
				fAnimPaused = true;
			}
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.v(DEBUG_TAG, "onSurfaceChanged");
			gl.glViewport(0, 0, width, height);

			// configure projection to screen
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
			float aspect = (float) width / height;
			GLU.gluPerspective(gl, 45.0f, aspect, 1.0f, 30.0f);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Log.v(DEBUG_TAG, "onSurfaceCreated");

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			// configure model space
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			GLU.gluLookAt(gl, 0, 0, 10f, 0, 0, 0, 0, 1, 0f);
			gl.glColor4f(1f, 0f, 0f, 1f);
		}
	}
}
