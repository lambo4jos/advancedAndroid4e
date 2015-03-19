package com.advancedandroidbook.simplebroadcasts;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SimpleBroadcastsActivity extends Activity {
    public static String ACTION_DANCE = "com.advancedandroidbook.simplebroadcasts.ACTION_DANCE";

    MyDancingBroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mReceiver = new MyDancingBroadcastReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter danceFilter = new IntentFilter(ACTION_DANCE);
        registerReceiver(mReceiver, danceFilter);
    }

    public void onClickBroadcastButton(View v) {
        // Blue button clicked.
        Toast.makeText(SimpleBroadcastsActivity.this, "Broadcasting intent",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ACTION_DANCE);
        sendBroadcast(i);
    }

    public static class MyDancingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Get Down and Boogie!", Toast.LENGTH_LONG)
                    .show();
        }
    }

}