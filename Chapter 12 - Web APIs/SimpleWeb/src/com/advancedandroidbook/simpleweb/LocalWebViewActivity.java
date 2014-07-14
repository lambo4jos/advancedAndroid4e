package com.advancedandroidbook.simpleweb;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class LocalWebViewActivity extends Activity {

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.justawebview);

	        final WebView wv = (WebView) findViewById(R.id.web_content);
	        wv.loadUrl("file:///android_asset/webby.html");
	    }
	}