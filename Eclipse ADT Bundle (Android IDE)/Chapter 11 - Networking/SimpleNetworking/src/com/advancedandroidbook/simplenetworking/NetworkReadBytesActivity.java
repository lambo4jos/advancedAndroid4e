package com.advancedandroidbook.simplenetworking;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NetworkReadBytesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// disable penaltyDeath()
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyFlashScreen().build();

		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.main);

		Button go = (Button) findViewById(R.id.do_action);
		go.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				try {
					URL text = new URL("http://www.kf6nvr.net/st/index.html");

					InputStream isText = text.openStream();
					byte[] bText = new byte[250];
					int readSize = isText.read(bText);
					Log.i("Net", "readSize = " + readSize);
					Log.i("Net", "bText = " + new String(bText));

					isText.close();
				} catch (Exception e) {
					Log.e("Net", "Error in network call", e);
				}

			}

		});
	}

}
