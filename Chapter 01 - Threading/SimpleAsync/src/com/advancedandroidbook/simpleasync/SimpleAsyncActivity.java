package com.advancedandroidbook.simpleasync;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

public class SimpleAsyncActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count);

        // Start counting off the main UI thread
        CountingTask tsk = new CountingTask();
        tsk.execute();
    }

    private class CountingTask extends AsyncTask<Void, Integer, Integer> {

        CountingTask() {}

        @Override
        protected Integer doInBackground(Void... unused) {

            int i = 0;

            while (i < 100) {
                SystemClock.sleep(250);
                i++;

                if (i % 5 == 0) {
                    // update UI with progress every 5%
                    publishProgress(i);
                }
            }

            return i;
        }

        protected void onProgressUpdate(Integer... progress) {
            TextView tv = (TextView) findViewById(R.id.counter);
            tv.setText(progress[0] + "% Complete!");
        }

        protected void onPostExecute(Integer result) {
            TextView tv = (TextView) findViewById(R.id.counter);
            tv.setText("Count Complete! Counted to " + result.toString());
        }

    }
}