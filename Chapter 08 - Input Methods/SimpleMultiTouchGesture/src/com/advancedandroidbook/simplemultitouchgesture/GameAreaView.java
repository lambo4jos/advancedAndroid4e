package com.advancedandroidbook.simplemultitouchgesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class GameAreaView extends View {

	private static final String DEBUG_TAG = "SimpleMultiTouchGesture->GameAreaView";

	private ScaleGestureDetector multiGestures;
	private Matrix scale;
	private Bitmap droid;

	public GameAreaView(Context context, int iGraphicResourceId) {
		super(context);

		scale = new Matrix();
		GestureListener listener = new GestureListener(this);
		multiGestures = new ScaleGestureDetector(context, listener);
		droid = BitmapFactory
				.decodeResource(getResources(), iGraphicResourceId);

	}

	public void onScale(float factor) {
		scale.preScale(factor, factor);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.v(DEBUG_TAG, "onDraw");
		Matrix transform = new Matrix(scale);
		float width = droid.getWidth() / 2;
		float height = droid.getHeight() / 2;
		transform.postTranslate(-width, -height);
		transform.postConcat(scale);
		transform.postTranslate(width, height);
		canvas.drawBitmap(droid, transform, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retVal = false;
		retVal = multiGestures.onTouchEvent(event);
		return retVal;
	}

	private class GestureListener implements
			ScaleGestureDetector.OnScaleGestureListener {

		GameAreaView view;

		public GestureListener(GameAreaView view) {
			this.view = view;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			Log.v(DEBUG_TAG, "onScale");
			float scale = detector.getScaleFactor();
			view.onScale(scale);
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			Log.v(DEBUG_TAG, "onScaleBegin");
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			Log.v(DEBUG_TAG, "onScaleEnd");
		}

	}

}
