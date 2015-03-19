package com.advancedandroidbook.simpleasync;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

public class SimpleMultiCoreAsyncActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multicount);

        // Start counting off the main UI thread
        CountingTask tsk = new CountingTask();
        //tsk.execute(R.id.counter1);
        tsk.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, R.id.counter1);

        startParallelTask(R.id.counter2);
        startParallelTask(R.id.counter3);
        startParallelTask(R.id.counter4);
        startParallelTask(R.id.counter5);
        startParallelTask(R.id.counter6);
        startParallelTask(R.id.counter7);
        startParallelTask(R.id.counter8);

    }

    private void startParallelTask(int id) {
        CountingTask tsk = new CountingTask();
        tsk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
        //tsk.execute(id);
    }

    private class CountingTask extends AsyncTask<Integer, Integer, Integer> {

        private int counterId;

        CountingTask() {
        }

        @Override
        protected Integer doInBackground(Integer... id) {

            counterId = id[0];
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
            TextView tv = (TextView) findViewById(counterId);
            tv.setText(progress[0] + "% Complete!");
        }

        protected void onPostExecute(Integer result) {
            TextView tv = (TextView) findViewById(counterId);
            tv.setText("Count Complete! Counted to " + result.toString());
        }

    }
}