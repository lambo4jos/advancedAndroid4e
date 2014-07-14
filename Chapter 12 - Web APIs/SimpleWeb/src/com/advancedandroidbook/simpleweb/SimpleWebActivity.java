package com.advancedandroidbook.simpleweb;

public class SimpleWebActivity extends MenuActivity {

    @Override
    void prepareMenu() {
        addMenuItem("Basic WebView", WebViewActivity.class);
        addMenuItem("HTML WebView", HTMLWebViewActivity.class);
        addMenuItem("Local WebView", LocalWebViewActivity.class);
        addMenuItem("Full Screen WebView", FullScreenWebViewActivity.class);}
}