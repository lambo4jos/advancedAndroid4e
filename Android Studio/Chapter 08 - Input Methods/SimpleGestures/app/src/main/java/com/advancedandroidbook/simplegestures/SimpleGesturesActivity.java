package com.advancedandroidbook.simplegestures;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class SimpleGesturesActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FrameLayout frame = (FrameLayout) findViewById(R.id.game_area);
        GameAreaView image = new GameAreaView(this,R.drawable.gamegraphic);
        frame.addView(image);
    }
}