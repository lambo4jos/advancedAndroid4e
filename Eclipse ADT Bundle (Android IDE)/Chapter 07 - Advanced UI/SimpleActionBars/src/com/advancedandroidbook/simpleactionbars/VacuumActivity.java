package com.advancedandroidbook.simpleactionbars;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class VacuumActivity extends ActionBarActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vac);
        
        ActionBar bar = getSupportActionBar();
        bar.hide();
    }
}