package com.advancedandroidbook.simplendk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class NativeBasicsActivity extends Activity {
	private static final String DEBUG_TAG = "NativeBasicsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.native_basics);
	}

	public void onBasicCall(View view) {
		basicNativeCall();
	}

	public void onBasicWithParams(View view) {
		String sumString = formattedAddition(5, 2, "Here ->%i<- is the sum.");
		Log.d(DEBUG_TAG, sumString);
		sumString = formattedAddition(7965, 413372, "Another result: %i");
		Log.d(DEBUG_TAG, sumString);
	}

	public void onExceptional(View view) {
		try {
			throwsException(42);
			throwsException(25);
			throwsException(55); // won't get here
		} catch (IllegalArgumentException e) {
			Log.e(DEBUG_TAG, "Call failed: ", e);
		}
	}

	public void onCatches(View view) {
		checksException(42);
		checksException(21);
	}

	private void javaThrowsException(int num) throws IllegalArgumentException {
		if (num == 42) {
			throw new IllegalArgumentException("Anything but that number!");
		} else {
			Log.v(DEBUG_TAG, "Good choice in numbers.");
		}
	}

	private native void basicNativeCall();

	private native String formattedAddition(int num1, int num2,
			String formatString);

	private native void throwsException(int num)
			throws IllegalArgumentException;

	private native void checksException(int num);

    static {
        System.loadLibrary("simplendk");
    }
}
