package com.advancedandroidbook.simplestyles;

import android.app.Activity;
import android.os.Bundle;

public class StyleSamplesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.right);
		setTheme(R.style.green_glow);
		setContentView(R.layout.style_samples);
	}
}
