package com.advancedandroidbook.simpleappwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class SimpleAppWidgetProvider extends AppWidgetProvider {
	private static final String DEBUG_TAG = "SimpleAppWidgetProvider";

	@Override
	public void onDisabled(Context context) {
		Intent serviceIntent = new Intent(context, PrefListenerService.class);
		context.stopService(serviceIntent);
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		Intent serviceIntent = new Intent(context, PrefListenerService.class);
		context.startService(serviceIntent);
		// update the remove view right away
		updateWidgetView(context, context.getSharedPreferences(
				SimpleAppWidgetActivity.APP_PREFERENCES, Context.MODE_PRIVATE));
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// all instantiated widgets will read from the same data, so we don't
		// need to track by widget id
		// Just need to request the data be updated, the internal Service will
		// handle all the rest
		// NOTE: No matter what updatePeriodMillis is set, this will not come
		// any
		// more frequently
		// than once every 30 minutes.
		Intent serviceIntent = new Intent(context,
				SimpleDataUpdateService.class);
		context.startService(serviceIntent);
		Log.v(DEBUG_TAG, "Widget update");
	}

	private static void updateWidgetView(Context context,
			SharedPreferences prefs) {
		String newMessage = SimpleAppWidgetActivity.getThreatMessage(context,
				prefs);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		remoteView.setTextViewText(R.id.widget_text_threat, newMessage);
		// add click handling
		Intent launchAppIntent = new Intent(context,
				SimpleAppWidgetActivity.class);
		PendingIntent launchAppPendingIntent = PendingIntent.getActivity(
				context, 0, launchAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteView.setOnClickPendingIntent(R.id.widget_view,
				launchAppPendingIntent);
		// get the Android component name for the QuizWidgetProvider
		ComponentName simpleWidget = new ComponentName(context,
				SimpleAppWidgetProvider.class);
		// get the instance of the AppWidgetManager
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		// update the widget
		appWidgetManager.updateAppWidget(simpleWidget, remoteView);
	}

	public static class PrefListenerService extends Service {
		@Override
		public void onDestroy() {
			SharedPreferences prefs = getSharedPreferences(
					SimpleAppWidgetActivity.APP_PREFERENCES,
					Context.MODE_PRIVATE);
			prefs.unregisterOnSharedPreferenceChangeListener(prefsListener);
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
			SharedPreferences prefs = getSharedPreferences(
					SimpleAppWidgetActivity.APP_PREFERENCES,
					Context.MODE_PRIVATE);
			prefs.registerOnSharedPreferenceChangeListener(prefsListener);
		}

		private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				updateWidgetView(getBaseContext(), sharedPreferences);
			}
		};

		@Override
		public IBinder onBind(Intent intent) {
			// no binder
			return null;
		}
	}
}
