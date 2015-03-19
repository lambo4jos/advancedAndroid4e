package com.advancedandroidbook.simplenotifications;

import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

public class SimpleNotificationsActivity extends Activity {
	private static final int NOTIFY_1 = 0x1001;
	private static final int NOTIFY_2 = 0x1002;
	private static final int NOTIFY_3 = 0x1003;
	private static final int NOTIFY_4 = 0x1004;
	private static final int NOTIFY_5 = 0x1005;
	private static final int NOTIFY_6 = 0x1006;
	private NotificationManager notifier = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		final class Counter {
			private int count;

			public Counter() {
				setCount(1);
			}

			public int increment() {
				this.setCount(this.getCount() + 1);
				return this.getCount();
			}

			public int getCount() {
				return count;
			}

			private void setCount(int count) {
				this.count = count;
			}
		}

		final Counter counterNotify1 = new Counter();
		Button notify1 = (Button) findViewById(R.id.notify1);
		notify1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
				notifyBuilder.setTicker("Hello!");
				notifyBuilder.setWhen(System.currentTimeMillis());

				if (counterNotify1.getCount() == 1) {
					notifyBuilder.setNumber(1);
				} else {
					notifyBuilder.setNumber(counterNotify1.getCount());
				}
				counterNotify1.increment();

				notifyBuilder.setAutoCancel(true);
				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));
				notifyBuilder.setContentTitle("Hi there!");
				notifyBuilder.setContentText("This is even more text.");
				Notification notify = notifyBuilder.build();
				notifier.notify(NOTIFY_1, notify);
			}
		});

		Button notify2 = (Button) findViewById(R.id.notify2);
		notify2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);
				notifyBuilder.setTicker("Vibrate!");
				notifyBuilder.setWhen(System.currentTimeMillis());

				notifyBuilder.setVibrate(new long[] { 200, 200, 600, 600, 600,
						200, 200, 600, 600, 200, 200, 200, 200, 600, 200, 200,
						600, 200, 200, 600, 600, 200, 600, 200, 600, 600, 200,
						200, 200, 600, 600, 200, 200, 200, 200, 600, });

				notifyBuilder.setAutoCancel(true);
				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));
				notifyBuilder.setContentTitle("Bzzt!");
				notifyBuilder.setContentText("This vibrated your phone.");
				Notification notify = notifyBuilder.build();
				notifier.notify(NOTIFY_2, notify);
				// more than one way to vibrate
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(500);
			}
		});

		final Counter counterNotify3 = new Counter();
		Button notify3 = (Button) findViewById(R.id.notify3);
		notify3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
				notifyBuilder.setTicker("Lights!");
				notifyBuilder.setWhen(System.currentTimeMillis());
				notifyBuilder.setAutoCancel(true);

				if (counterNotify3.getCount() == 1) {
					notifyBuilder.setNumber(1);
				} else {
					notifyBuilder.setNumber(counterNotify3.getCount());
				}

				int argb;
				int onMs;
				int offMs;

				if (counterNotify3.getCount() < 2) {
					argb = Color.GREEN;
					onMs = 1000;
					offMs = 1000;
				} else if (counterNotify3.getCount() < 3) {
					argb = Color.BLUE;
					onMs = 750;
					offMs = 750;
				} else if (counterNotify3.getCount() < 4) {
					argb = Color.WHITE;
					onMs = 500;
					offMs = 500;
				} else {
					argb = Color.RED;
					onMs = 50;
					offMs = 50;
				}

				counterNotify3.increment();

				notifyBuilder.setLights(argb, onMs, offMs);

				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));
				notifyBuilder.setContentTitle("Bright!");
				notifyBuilder.setContentText("This lit up your phone.");
				Notification notify = notifyBuilder.build();

				notifier.notify(NOTIFY_3, notify);
			}
		});

		Button notify4 = (Button) findViewById(R.id.notify4);
		notify4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
				notifyBuilder.setTicker("Noise!");
				notifyBuilder.setWhen(System.currentTimeMillis());
				notifyBuilder.setAutoCancel(true);

				notifyBuilder.setSound(
						Uri.parse("android.resource://com.advancedandroidbook.simplenotifications/"
								+ R.raw.fallbackring),
						AudioManager.STREAM_NOTIFICATION);

				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));
				notifyBuilder.setContentTitle("Wow!");
				notifyBuilder.setContentText("This made your phone noisy.");
				Notification notify = notifyBuilder.build();

				notifier.notify(NOTIFY_4, notify);
			}
		});

		Button notifyRemote = (Button) findViewById(R.id.notifyRemote);
		notifyRemote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
				notifyBuilder.setTicker("Remote!");
				notifyBuilder.setWhen(System.currentTimeMillis());
				notifyBuilder.setAutoCancel(true);

				RemoteViews remote = new RemoteViews(getPackageName(),
						R.layout.remote);
				remote.setTextViewText(R.id.text1, "Big text here!");
				remote.setTextViewText(R.id.text2, "Red text down here!");
				notifyBuilder.setContent(remote);

				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));

				Notification notify = notifyBuilder.build();

				notifier.notify(NOTIFY_5, notify);
			}
		});

		Button notify6 = (Button) findViewById(R.id.notifyExpand);
		notify6.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
						getApplicationContext());
				notifyBuilder.setSmallIcon(R.drawable.ic_launcher);
				notifyBuilder.setTicker("Expand!");
				notifyBuilder.setWhen(System.currentTimeMillis());

				notifyBuilder.setAutoCancel(true);
				Intent toLaunch = new Intent(SimpleNotificationsActivity.this,
						SimpleNotificationsActivity.class);

				notifyBuilder.setAutoCancel(true);
				notifyBuilder.setContentIntent(PendingIntent.getActivity(
						SimpleNotificationsActivity.this, 0, toLaunch, 0));
				notifyBuilder.setContentTitle("Expanding!");
				notifyBuilder.setContentText("This is even more text.");

				notifyBuilder.setStyle(new NotificationCompat.BigTextStyle()
						.bigText("This is a really long message that is used "
								+ "for expanded notifications in the status bar"));

				PendingIntent action1 = PendingIntent.getActivity(
						getApplicationContext(), 0, toLaunch, 0);
				PendingIntent action2 = PendingIntent.getActivity(
						getApplicationContext(), 0, toLaunch, 0);

				notifyBuilder.addAction(R.drawable.ic_launcher, "Action 1",
						action1);
				notifyBuilder.addAction(R.drawable.ic_launcher, "Action 2",
						action2);

				Notification notify = notifyBuilder.build();
				notifier.notify(NOTIFY_6, notify);
			}
		});
	}
}