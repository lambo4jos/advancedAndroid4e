package com.advancedandroidbook.simpledrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

public class DrawCustomFontActivity extends DrawingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new ViewWithChessBoardFont(this));
	}

	private static class ViewWithChessBoardFont extends View {
		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private Typeface mType;
		private int margin = 25;

		public ViewWithChessBoardFont(Context context) {
			super(context);

			mType = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/chess1.ttf");
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			mPaint.setTypeface(mType);
			mPaint.setColor(Color.BLACK);

			int size = Math.min(canvas.getWidth(), canvas.getHeight());
			int textSize = (size - (margin * 2)) / 10;
			mPaint.setTextSize(textSize);
			int line = margin * 2;

			// Draw the chess board
			canvas.drawText("5111111116", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3RMBWKVNT2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3OPOPOPOP2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3 / / / /2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3/ / / / 2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3 / / / /2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3/ / / / 2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3popopopo2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("3tnvqlbmr2", margin, line, mPaint);
			line += textSize;
			canvas.drawText("7444444448", margin, line, mPaint);
		}
	}
}
