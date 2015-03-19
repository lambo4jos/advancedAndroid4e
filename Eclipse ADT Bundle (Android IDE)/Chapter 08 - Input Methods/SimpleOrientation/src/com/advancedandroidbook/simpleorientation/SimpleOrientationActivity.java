package com.advancedandroidbook.simpleorientation;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.TextView;

public class SimpleOrientationActivity extends Activity {

	private static final String DEBUG_TAG = "SimpleOrientationActivity";

	OrientationEventListener mOrientationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mOrientationListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			@Override
			public void onOrientationChanged(int orientation) {
				Log.v(DEBUG_TAG, "Orientation changed");
				TextView orientationStatus = (TextView) findViewById(R.id.oStatus);
				String resultOrientation = String.format(getResources()
						.getString(R.string.orientation_status), orientation);
				orientationStatus.setText(resultOrientation);

				TextView orientationCommentary = (TextView) findViewById(R.id.oCommentary);
				if (orientation == -1) {
					orientationCommentary.setText(getResources().getString(R.string.unknown));
				} else if (orientation < 10 || orientation > 350) {
					orientationCommentary.setText(getResources().getString(R.string.s0));
				} else if (orientation < 100 && orientation > 80) {
					orientationCommentary.setText(getResources().getString(R.string.s90));
				} else if (orientation < 190 && orientation > 170) {
					orientationCommentary.setText(getResources().getString(R.string.s180));
				} else if (orientation < 280 && orientation > 260) {
					orientationCommentary.setText(getResources().getString(R.string.s270));
				} else
				{
					orientationCommentary.setText("");
				}
			}
		};

		if (mOrientationListener.canDetectOrientation() == true) {
			Log.v(DEBUG_TAG, "Can detect orientation");
			mOrientationListener.enable();
		} else {
			Log.v(DEBUG_TAG, "Cannot detect orientation");
			mOrientationListener.disable();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mOrientationListener.disable();
	}

}