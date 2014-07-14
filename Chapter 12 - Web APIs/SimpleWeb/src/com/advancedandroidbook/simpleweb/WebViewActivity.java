package com.advancedandroidbook.simpleweb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class WebViewActivity extends Activity {
    private static final String DEBUG_TAG = "WebViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        
        final TextSwitcher pageTitle = (TextSwitcher) findViewById(R.id.pagetitle);
        pageTitle.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView tv = new TextView(WebViewActivity.this);
                tv.setLayoutParams(new TextSwitcher.LayoutParams(
                		ImageSwitcher.LayoutParams.WRAP_CONTENT, ImageSwitcher.LayoutParams.WRAP_CONTENT));
                return tv;
            }
        });
        
        final ImageSwitcher favImage = (ImageSwitcher) findViewById(R.id.favicon);
        favImage.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView iv = new ImageView(WebViewActivity.this);
                iv.setBackgroundColor(0xFF000000);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                iv.setLayoutParams(new ImageSwitcher.LayoutParams(
                		ImageSwitcher.LayoutParams.MATCH_PARENT, ImageSwitcher.LayoutParams.MATCH_PARENT));
                return iv;
            }
        });
        
        Animation in = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);
        favImage.setInAnimation(in);
        favImage.setOutAnimation(out);
        pageTitle.setInAnimation(in);
        pageTitle.setOutAnimation(out);
        final EditText et = (EditText) findViewById(R.id.url);
        final WebView wv = (WebView) findViewById(R.id.web_holder);

        wv.loadUrl("http://www.perlgurl.org/");
        Button go = (Button) findViewById(R.id.go_button);
        go.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wv.loadUrl(et.getText().toString());
            }
        });
        WebViewClient webClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v(DEBUG_TAG, "Page finished loading");
                super.onPageFinished(view, url);
                String title = wv.getTitle();
                pageTitle.setText(title);

                Bitmap favIcon = wv.getFavicon();
                favImage.setImageDrawable(new BitmapDrawable(getResources(), favIcon));
            }
        };

        // ... and listen for changes
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                Log.v(DEBUG_TAG, "Got new icon");
                super.onReceivedIcon(view, icon);
                favImage.setImageDrawable(new BitmapDrawable(getResources(), icon));
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.v(DEBUG_TAG, "Got new title");
                super.onReceivedTitle(view, title);
                pageTitle.setText(title);
            }

        };
        wv.setWebViewClient(webClient);

        wv.setWebChromeClient(webChrome);
        wv.setInitialScale(60);
        WebSettings settings = wv.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
    }

    @Override
    protected void onPause() {
        WebView wv = (WebView) findViewById(R.id.web_holder);
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
        WebView wv = (WebView) findViewById(R.id.web_holder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wv.onResume();
        } else {
            wv.resumeTimers();
        }
    }

}
