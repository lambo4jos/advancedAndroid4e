package com.advancedandroidbook.simpledrawing;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class DrawShapeActivity extends DrawingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ViewWithRedDot(this));
	}

	private static class ViewWithRedDot extends View {

		public ViewWithRedDot(Context context) {
			super(context);

		}

		Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.BLACK);

			circlePaint.setColor(Color.RED);

			int radius;
			Configuration config = getResources().getConfiguration();
			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				radius = canvas.getHeight() / 3;
			} else {
				radius = canvas.getWidth() / 3;
			}

			canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,
					radius, circlePaint);

		}
	}

}
