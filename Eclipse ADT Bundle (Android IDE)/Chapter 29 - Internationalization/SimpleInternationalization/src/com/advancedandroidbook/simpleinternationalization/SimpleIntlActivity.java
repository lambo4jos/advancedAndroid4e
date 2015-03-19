package com.advancedandroidbook.simpleinternationalization;

import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SimpleIntlActivity extends Activity {

	private static final String DEBUG_TAG = "SimpleInternationalizationActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.i(DEBUG_TAG, "The locale is: "
				+ Locale.getDefault().getDisplayName());
	}
}