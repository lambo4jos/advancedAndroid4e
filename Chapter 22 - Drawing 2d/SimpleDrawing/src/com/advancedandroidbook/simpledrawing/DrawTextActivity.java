package com.advancedandroidbook.simpledrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawTextActivity extends DrawingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// this is the text scaling factor
		float textScale = metrics.scaledDensity;

		setContentView(new ViewWithText(this, textScale));

	}

	private static class ViewWithText extends View {
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private Typeface mType;
		private float mTextScale;
		private final int sizeA;
		private final int sizeB;
		private final int sizeC;
		private final int sizeD;

		public ViewWithText(Context context, float textScale) {
			super(context);
			mTextScale = textScale;
			sizeA = scale(16);
			sizeB = scale(18);
			sizeC = scale(20);
			sizeD = scale(22);
			mPaint.setTextSize(sizeA);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.GREEN);
			int currentLinePosition = 40;

			mPaint.setTypeface(null);
			canvas.drawText("Default Typeface (Normal)", 30,
					currentLinePosition, mPaint);

			mType = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Sans Serif Typeface (Normal)", 30,
					currentLinePosition, mPaint);

			mType = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Sans Serif Typeface (Bold)", 30,
					currentLinePosition, mPaint);

			mType = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Monospace Typeface (Normal)", 30,
					currentLinePosition, mPaint);

			mType = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Serif Typeface (Normal)", 30, currentLinePosition,
					mPaint);

			mType = Typeface.create(Typeface.SERIF, Typeface.BOLD);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Serif Typeface (Bold)", 30, currentLinePosition,
					mPaint);

			mType = Typeface.create(Typeface.SERIF, Typeface.ITALIC);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Serif Typeface (Italic)", 30, currentLinePosition,
					mPaint);

			mType = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC);
			mPaint.setTypeface(mType);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Serif Typeface (Bold Italic)", 30,
					currentLinePosition, mPaint);

			mPaint.setTypeface(null);
			mPaint.setTextSize(sizeB);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Text Size " + sizeB, 30, currentLinePosition,
					mPaint);

			mPaint.setTextSize(sizeD);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Text Size " + sizeD, 30, currentLinePosition,
					mPaint);

			mPaint.setTextSize(sizeC);
			mPaint.setAntiAlias(false);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Text Not Anti-Aliased", 30, currentLinePosition,
					mPaint);

			mPaint.setAntiAlias(true);
			mPaint.setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Strike through", 30, currentLinePosition, mPaint);

			mPaint.setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			currentLinePosition += mPaint.getFontSpacing();
			canvas.drawText("Underlined", 30, currentLinePosition, mPaint);

		}

		// adjust for display density
		int scale(int oldSize) {
			int newSize = (int) (oldSize * mTextScale);
			return newSize;
		}
	}
}
