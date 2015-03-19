package com.advancedandroidbook.simpledrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawGradientActivity extends DrawingActivity {

	private static float density = 1.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new ViewWithGradient(this));

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		density = metrics.density;
	}

	private static class ViewWithGradient extends View {

		Paint circlePaint;
		LinearGradient linGrad;
		RadialGradient radGrad;
		SweepGradient sweepGrad;
		
		public ViewWithGradient(Context context) {
			super(context);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);

			circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

			linGrad = new LinearGradient(0, 0, scale(25),
					scale(25), Color.RED, Color.BLACK, Shader.TileMode.MIRROR);
			circlePaint.setShader(linGrad);
			canvas.drawCircle(scale(100), scale(100), scale(100), circlePaint);

			radGrad = new RadialGradient(scale(250), scale(175),
					scale(50), Color.GREEN, Color.BLACK, Shader.TileMode.MIRROR);
			circlePaint.setShader(radGrad);
			canvas.drawCircle(scale(250), scale(175), scale(50), circlePaint);

			sweepGrad = new SweepGradient(canvas.getWidth()
					- scale(125), canvas.getHeight() - scale(125), new int[] {
				Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE,
				Color.MAGENTA, Color.RED }, null);
			circlePaint.setShader(sweepGrad);
			canvas.drawCircle(canvas.getWidth() - scale(125),
					canvas.getHeight() - scale(125), scale(125), circlePaint);

		}

		// adjust for display density
		int scale(int oldSize) {
			int newSize = (int) (oldSize * density);
			return newSize;
		}
	}
}