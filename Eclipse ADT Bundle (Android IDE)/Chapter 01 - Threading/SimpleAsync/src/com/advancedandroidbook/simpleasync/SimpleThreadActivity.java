package com.advancedandroidbook.simpleasync;


import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

public class SimpleThreadActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count);

        final TextView tv = (TextView) findViewById(R.id.counter);
        new Thread(new Runnable() {
            public void run() {

                int i = 0;

                while (i < 100) {
                    SystemClock.sleep(250);
                    i++;

                    final int curCount = i;
                    if (curCount % 5 == 0) {
                        // update UI with progress every 5%
                        tv.post(new Runnable() {
                            public void run() {
                                tv.setText(curCount + "% Complete!");
                            }
                        });
                    }
                }
                
                tv.post(new Runnable() {
                    public void run() {
                        tv.setText("Count Complete!");
                    }
                });
            }

        }).start();

    }

}