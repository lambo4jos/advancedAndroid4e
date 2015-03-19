package com.advancedandroidbook.simplelivewallpaper;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class SimpleDroidWallpaper extends WallpaperService {
	// private static final String DEBUG_TAG = "SimpleDroidWallpaper";
	private final Handler handler = new Handler();

	@Override
	public Engine onCreateEngine() {
		return new SimpleWallpaperEngine();
	}

	class SimpleWallpaperEngine extends Engine {
		Bitmap droid = null;
		Matrix droidTransform = null;
		float droidDirection = 0.0f;

		boolean canDraw = true;
		Matrix displayTransform;
		final Random rand = new Random();

		private int virtualHeight;
		private int virtualWidth;

		private final Runnable drawRequest = new Runnable() {
			@Override
			public void run() {
				drawDroid();
			}
		};

		public SimpleWallpaperEngine() {
			droid = BitmapFactory.decodeResource(getResources(),
					R.drawable.live_wallpaper_android);
			droidTransform = new Matrix();
			displayTransform = new Matrix();
			droidDirection = rand.nextFloat() * 359.0f;
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			// When touch is enable, all MotionEvents are passed, even those
			// handled by other widgets
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			handler.removeCallbacks(drawRequest);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);

			// setup Matrix to adjust canvas based on the user moving between
			// virtual screens
			displayTransform.setTranslate(xPixelOffset, yPixelOffset);
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			virtualHeight = getDesiredMinimumHeight();
			virtualWidth = getDesiredMinimumWidth();

			drawDroid();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			canDraw = false;
			handler.removeCallbacks(drawRequest);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			canDraw = visible;
			if (visible) {
				drawDroid();
			} else {
				handler.removeCallbacks(drawRequest);
			}
		}

		private void drawDroid() {
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			try {
				canvas = holder.lockCanvas();
				if (canvas != null) {
					// clear the canvas
					canvas.drawColor(0xff000000);
					@SuppressWarnings("deprecation")
					Matrix old = canvas.getMatrix();
					old.reset();
					old.postConcat(displayTransform);
					canvas.setMatrix(old);
					droidTransform.postRotate(droidDirection);
					droidTransform.postTranslate(5.0f, 0.0f);
					droidTransform.postRotate(-droidDirection);
					droidDirection = checkBoundsAndBounce(droidTransform,
							droidDirection, virtualHeight, virtualWidth,
							droid.getHeight(), droid.getWidth());
					canvas.drawBitmap(droid, droidTransform, null);
				}
			} finally {
				if (canvas != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
			handler.removeCallbacks(drawRequest);
			if (canDraw) {
				handler.postDelayed(drawRequest, 33);
			}
		}

		private float checkBoundsAndBounce(Matrix droidTransform,
				float direction, int areaHeight, int areaWidth, int height,
				int width) {
			float[] values = new float[9];
			droidTransform.getValues(values);
			float x = values[Matrix.MTRANS_X];
			float y = values[Matrix.MTRANS_Y];
			float result = direction;
			if (x < 0 && (direction > 90 && direction < 270)) {
				if (direction > 180) {
					result = 270.0f + (270f - direction);
				} else {
					result = 90.0f - (direction - 90.0f);
				}
			}

			if (x + width > areaWidth && (direction > 270 || direction < 90)) {
				if (direction > 270) {
					result = 270f - (direction - 270f);
				} else {
					result = 90.0f + (90f - direction);
				}
			}

			if (y < 0 && (direction < 180)) {
				if (direction > 90) {
					result = 180f + (180f - direction);
				} else {
					result = 360f - direction;
				}
			}

			if (y + height > areaHeight && (direction > 180)) {
				if (direction > 270) {
					result = 360f - direction;
				} else {
					result = 180f - (direction - 180f);
				}
			}
			return result;
		}

		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				droidDirection = rand.nextFloat() * 359.9f;
			}
			super.onTouchEvent(event);
		}
	}
}
