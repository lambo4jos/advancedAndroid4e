package com.advancedandroidbook.simplewebextension;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.annotation.SuppressLint;

@SuppressLint("SetJavaScriptEnabled")
public class SimpleWebExtension extends Activity {
	private static final String DEBUG_TAG = "SimpleWebExtension";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final WebView wv = (WebView) findViewById(R.id.html_viewer);
		WebSettings settings = wv.getSettings();
		settings.setJavaScriptEnabled(true);
		WebChromeClient webChrome = new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				Log.v(DEBUG_TAG, consoleMessage.lineNumber() + ": "
						+ consoleMessage.message());
				return true;
			}
		};

		// have to set _any_ web chrome client for console and alerts to work
		wv.setWebChromeClient(webChrome);
		wv.addJavascriptInterface(new JavaScriptExtensions(), "jse");

		// load our sample HTML
		wv.loadUrl("file:///android_asset/sample.html");
	}

	public void setHTMLText(View view) {
		WebView wv = (WebView) findViewById(R.id.html_viewer);
		// this style call is similar to so-called "bookmarklets" in that the
		// browser
		// executes the javascript on the currently loaded page
		wv.loadUrl("javascript:doSetFormText('Java->JS call');");
	}

	class JavaScriptExtensions {
		public static final int TOAST_LONG = Toast.LENGTH_LONG;
		public static final int TOAST_SHORT = Toast.LENGTH_SHORT;

		@JavascriptInterface
		public void toast(String message, int length) {
			Toast.makeText(SimpleWebExtension.this, message, length).show();
		}
	}

	@Override
	protected void onPause() {
		WebView wv = (WebView) findViewById(R.id.html_viewer);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			wv.onPause();
		} else {
			wv.pauseTimers();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();
		WebView wv = (WebView) findViewById(R.id.html_viewer);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			wv.onResume();
		} else {
			wv.resumeTimers();
		}
	}

}