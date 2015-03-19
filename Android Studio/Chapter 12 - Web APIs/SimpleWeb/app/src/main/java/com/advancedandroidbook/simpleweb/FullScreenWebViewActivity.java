package com.advancedandroidbook.simpleweb;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class FullScreenWebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView wv = new WebView(this);
		setContentView(wv);

		wv.loadUrl("http://www.perlgurl.org/");
		wv.setInitialScale(30);
	}
}