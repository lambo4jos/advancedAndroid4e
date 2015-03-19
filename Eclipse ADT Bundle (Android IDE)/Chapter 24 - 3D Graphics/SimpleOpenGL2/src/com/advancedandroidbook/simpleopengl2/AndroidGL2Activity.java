package com.advancedandroidbook.simpleopengl2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

public class AndroidGL2Activity extends Activity {
	private static final String DEBUG_TAG = "AndroidGL2Activity";
	CustomGL2SurfaceView mAndroidSurface = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAndroidSurface = new CustomGL2SurfaceView(this);
		setContentView(mAndroidSurface);
	}

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

	private class CustomGL2SurfaceView extends GLSurfaceView {
		final CustomRenderer renderer;

		public CustomGL2SurfaceView(Context context) {
			super(context);
			// request an OpenGL ES 2.0 context
			setEGLContextClientVersion(2);
			renderer = new CustomRenderer();
			setRenderer(renderer);
		}
	}

	private class CustomRenderer implements GLSurfaceView.Renderer {
		private boolean initialized = false;
		private final float[] vertices = { 0.0f, 0.5f, 0.0f, -0.5f, -0.5f,
				0.0f, 0.5f, -0.5f, 0.0f };
		private FloatBuffer verticesBuffer;

		public CustomRenderer() {
			verticesBuffer = SmallGLUT.getFloatBufferFromFloatArray(vertices);
		}

		@Override
		public void onDrawFrame(GL10 unused) {
			if (!initialized) {
				return;
			}
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			GLES20.glUseProgram(shaderProgram);
			GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 12,
					verticesBuffer);
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		}

		@Override
		public void onSurfaceChanged(GL10 unused, int width, int height) {
			Log.v(DEBUG_TAG, "onSurfaceChanged");
			GLES20.glViewport(0, 0, width, height);
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1);
		}

		@Override
		public void onSurfaceCreated(GL10 unused, EGLConfig unused2) {
			try {
				initShaderProgram(R.raw.simple_vertex, R.raw.simple_fragment);
				initialized = true;
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Failed to init GL");
			}
		}

		private int shaderProgram = 0;

		private void initShaderProgram(int vertexId, int fragmentId)
				throws Exception {
			int vertexShader = loadAndCompileShader(GLES20.GL_VERTEX_SHADER,
					vertexId);
			int fragmentShader = loadAndCompileShader(
					GLES20.GL_FRAGMENT_SHADER, fragmentId);
			shaderProgram = GLES20.glCreateProgram();
			if (shaderProgram == 0) {
				throw new Exception("Failed to create shader program");
			}
			// attach the shaders to the program
			GLES20.glAttachShader(shaderProgram, vertexShader);
			GLES20.glAttachShader(shaderProgram, fragmentShader);
			// bind attribute in our vertex shader
			GLES20.glBindAttribLocation(shaderProgram, 0, "vPosition");
			// link the shaders
			GLES20.glLinkProgram(shaderProgram);
			// check the linker status
			int[] linkerStatus = new int[1];
			GLES20.glGetProgramiv(shaderProgram, GLES20.GL_LINK_STATUS,
					linkerStatus, 0);
			if (GLES20.GL_TRUE != linkerStatus[0]) {
				Log.e(DEBUG_TAG,
						"Linker Failure: "
								+ GLES20.glGetProgramInfoLog(shaderProgram));
				GLES20.glDeleteProgram(shaderProgram);
				throw new Exception("Program linker failed");
			}
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1);
		}

		private int loadAndCompileShader(int shaderType, int shaderId)
				throws Exception {
			InputStream inputStream = AndroidGL2Activity.this.getResources()
					.openRawResource(shaderId);
			String shaderCode = inputStreamToString(inputStream);
			int shader = GLES20.glCreateShader(shaderType);
			if (shader == 0) {
				throw new Exception("Can't create shader");
			}
			// hand the code over to GL
			GLES20.glShaderSource(shader, shaderCode);
			// compile it
			GLES20.glCompileShader(shader);
			// get compile status
			int[] status = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
			if (status[0] == 0) {
				// failed
				Log.e(DEBUG_TAG,
						"Compiler Failure: "
								+ GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				throw new Exception("Shader compilation failed");
			}
			return shader;
		}

		public String inputStreamToString(InputStream is) throws IOException {
			StringBuffer sBuffer = new StringBuffer();
			
			BufferedReader dataIO = new BufferedReader(new InputStreamReader(is));
			
			String strLine = null;
			while ((strLine = dataIO.readLine()) != null) {
				sBuffer.append(strLine + "\n");
			}
			dataIO.close();
			is.close();
			return sBuffer.toString();
		}
	}
}
