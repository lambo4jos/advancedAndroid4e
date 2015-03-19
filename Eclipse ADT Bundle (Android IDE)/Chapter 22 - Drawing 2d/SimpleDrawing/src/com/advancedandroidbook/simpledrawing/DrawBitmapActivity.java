package com.advancedandroidbook.simpledrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawBitmapActivity extends DrawingActivity {

	private static float density = 1.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ViewWithBitmap(this));

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		density = metrics.density;
	}

	private static class ViewWithBitmap extends View {
		public ViewWithBitmap(Context context) {
			super(context);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.BLUE);

			Bitmap jayPic = BitmapFactory.decodeResource(getResources(),
					R.drawable.bluejay);
			Bitmap jayPicMedium = Bitmap.createScaledBitmap(jayPic, scale(200),
					scale(300), false);

			// Draw the big middle jay
			canvas.drawBitmap(jayPicMedium, scale(60), scale(75), null);

			Matrix maxTopLeft = new Matrix();
			maxTopLeft.preRotate(30);
			Matrix maxBottomLeft = new Matrix();
			maxBottomLeft.preRotate(-30);

			Matrix maxTopRight = new Matrix();
			maxTopRight.preRotate(-30); // tilt 30 degrees left
			maxTopRight.preScale(-1, 1); // mirror image

			Matrix maxBottomRight = new Matrix();
			maxBottomRight.preRotate(30); // tilt 30 degrees right
			maxBottomRight.preScale(-1, 1); // mirror image

			// Create the thumbnail jay
			Bitmap jayPicSmall = Bitmap.createScaledBitmap(jayPic, scale(50),
					scale(75), false);
			Bitmap jayPicTopLeft = Bitmap.createBitmap(jayPicSmall, 0, 0,
					jayPicSmall.getWidth(), jayPicSmall.getHeight(),
					maxTopLeft, false);
			Bitmap jayPicBottomLeft = Bitmap.createBitmap(jayPicSmall, 0, 0,
					jayPicSmall.getWidth(), jayPicSmall.getHeight(),
					maxBottomLeft, false);
			Bitmap jayPicTopRight = Bitmap.createBitmap(jayPicSmall, 0, 0,
					jayPicSmall.getWidth(), jayPicSmall.getHeight(),
					maxTopRight, false);
			Bitmap jayPicBottomRight = Bitmap.createBitmap(jayPicSmall, 0, 0,
					jayPicSmall.getWidth(), jayPicSmall.getHeight(),
					maxBottomRight, false);

			// Free up some memory by dumping bitmaps we don't need anymore
			jayPicSmall.recycle();
			jayPicMedium.recycle();
			jayPic.recycle();

			canvas.drawBitmap(jayPicTopLeft, scale(30), scale(30), null);
			canvas.drawBitmap(jayPicBottomLeft, scale(30), scale(325), null);
			canvas.drawBitmap(jayPicTopRight, scale(225), scale(30), null);
			canvas.drawBitmap(jayPicBottomRight, scale(225), scale(325), null);

			jayPicTopLeft.recycle();
			jayPicBottomLeft.recycle();
			jayPicTopRight.recycle();
			jayPicBottomRight.recycle();
		}

		// adjust for display density
		int scale(int oldSize) {
			int newSize = (int) (oldSize * density);
			return newSize;
		}
	}
}