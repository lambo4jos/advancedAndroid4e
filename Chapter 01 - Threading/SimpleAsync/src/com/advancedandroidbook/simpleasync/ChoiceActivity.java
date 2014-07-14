package com.advancedandroidbook.simpleasync;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChoiceActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onClickThreadButton(View v) {
        Intent newIntent = new Intent(this, SimpleThreadActivity.class);
        startActivity(newIntent);
    }

    public void onClickAsyncButton(View v) {
        Intent newIntent = new Intent(this, SimpleAsyncActivity.class);
        startActivity(newIntent);
    }

    public void onClickMultiAsyncButton(View v) {
        Intent newIntent = new Intent(this, SimpleMultiCoreAsyncActivity.class);
        startActivity(newIntent);
    }
}