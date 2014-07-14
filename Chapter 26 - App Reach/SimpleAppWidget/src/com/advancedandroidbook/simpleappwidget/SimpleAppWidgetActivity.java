package com.advancedandroidbook.simpleappwidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SimpleAppWidgetActivity extends Activity {
	public static final String APP_PREFERENCES = "SimpleWidgetPrefs";
	public static final String PREFS_CUR_THREAT_LEVEL = "CUR_THREAT_LEVEL";
	public static final String PREFS_LAST_CHECK = "LAST_CHECK";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// start a check
		checkThreatLevel();
	}

	@Override
	protected void onPause() {
		SharedPreferences prefs = getSharedPreferences(
				SimpleAppWidgetActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
		prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// listen for prefs changes
		SharedPreferences prefs = getSharedPreferences(
				SimpleAppWidgetActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
		prefs.registerOnSharedPreferenceChangeListener(prefsListener);
		super.onResume();
	}

	private void checkThreatLevel() {
		Intent serviceIntent = new Intent(this, SimpleDataUpdateService.class);
		this.startService(serviceIntent);
	}

	public void onRequestUpdate(View view) {
		checkThreatLevel();
	}

	public static String getThreatMessage(Context context,
			SharedPreferences prefs) {
		String messageFormat = context.getResources().getString(
				R.string.message_format);
		String threatLevel = prefs
				.getString(PREFS_CUR_THREAT_LEVEL, "ELEVATED");
		String lastUpdate = prefs.getString(PREFS_LAST_CHECK, "N/A");
		String newMessage = String.format(messageFormat, threatLevel,
				lastUpdate);
		return newMessage;
	}

	private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			final String newMessage = getThreatMessage(
					SimpleAppWidgetActivity.this, sharedPreferences);
			// the listener is called from a different thread
			SimpleAppWidgetActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					TextView text = (TextView) findViewById(R.id.tl_message);
					text.setText(newMessage);
				}
			});
		}
	};
}