package com.advancedandroidbook.simpleappwidget;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

public class SimpleDataUpdateService extends Service {
	private static final String DEBUG_TAG = "SimpleDataUpdateService";
	private DownloadThread background;
	private SharedPreferences prefs;

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = getSharedPreferences(SimpleAppWidgetActivity.APP_PREFERENCES,
				Context.MODE_PRIVATE);
	}

	@Override
	public void onDestroy() {
		background.interrupt();
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		doServiceStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doServiceStart(intent, startId);
		return Service.START_REDELIVER_INTENT;
	}

	private void doServiceStart(Intent intent, int startId) {
		background = new DownloadThread();
		background.start();
	}

	private class DownloadThread extends Thread {
		public void run() {
			Date now = new Date();
			Editor edit = prefs.edit();
			try {
				String threatLevel = getThreatLevel();
				edit.putString(SimpleAppWidgetActivity.PREFS_CUR_THREAT_LEVEL,
						threatLevel);
			} catch (Exception e) {
				// ignore
				Log.e(DEBUG_TAG, "Error during network check", e);
			} finally {
				// notify complete, failure or not
				edit.putString(SimpleAppWidgetActivity.PREFS_LAST_CHECK,
						now.toString());
				edit.commit();
				// stop the service
				SimpleDataUpdateService.this.stopSelf();
			}
		}

		private String getThreatLevel() throws XmlPullParserException,
				IOException {
			String threatLevel = "ELEVATED THREAT";
			URL threatServicePath = new URL(
					"http://www.dhs.gov/ntas/1.0/alerts.xml");
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			parser.setInput(threatServicePath.openStream(), null);
			int eventType = -1;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String strName = parser.getName();
					if (strName.equals("alert")) {
						threatLevel = parser.getAttributeName(2);
						// this tells use we actually did read it from the XML
						// at dhs.gov
						// it never changes, so that's harder to determine that
						// it should be. :)
						Log.v(DEBUG_TAG, "Found new threat level: "
								+ threatLevel);
					}
				}
				eventType = parser.next();
			}
			return threatLevel;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// No binding
		return null;
	}
}
