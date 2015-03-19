package com.advancedandroidbook.simpleweb;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HTMLWebViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.justawebview);

        final WebView wv = (WebView) findViewById(R.id.web_content);
        
        String strPageTitle = "The Last Words of Oscar Wilde";
        String strPageContent = "<h1>" + strPageTitle + ": </h1>\"Either that wallpaper goes, or I do.\"";
        String myHTML = "<html><title>" + strPageTitle +"</title><body>"+ strPageContent +"</body></html>"; 
        wv.loadData(myHTML, "text/html", "utf-8");     
        
    }
}
