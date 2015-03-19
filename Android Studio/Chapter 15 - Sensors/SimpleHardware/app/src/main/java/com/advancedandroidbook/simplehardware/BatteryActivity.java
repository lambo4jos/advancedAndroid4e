package com.advancedandroidbook.simplehardware;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BatteryActivity extends Activity {
	private boolean receiverRegistered = false;
	private static final String DEBUG_TAG = "BatteryActivity";
	private static final SparseArray<String> healthValueMap = new SparseArray<String>() {
		{
			put(BatteryManager.BATTERY_HEALTH_DEAD, "Dead");
			put(BatteryManager.BATTERY_HEALTH_GOOD, "Good");
			put(BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE, "Over voltage");
			put(BatteryManager.BATTERY_HEALTH_OVERHEAT, "Over heating");
			put(BatteryManager.BATTERY_HEALTH_UNKNOWN, "Unknown");
			put(BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE,
					"Failure, but unknown");
			put(-1, "Not Reported");
		}
	};
	
	private static final SparseArray<String> statusValueMap = new SparseArray<String>() {
		{
			put(BatteryManager.BATTERY_STATUS_CHARGING, "Charging");
			put(BatteryManager.BATTERY_STATUS_DISCHARGING, "Discharging");
			put(BatteryManager.BATTERY_STATUS_FULL, "Full");
			put(BatteryManager.BATTERY_STATUS_NOT_CHARGING, "Not Charging");
			put(BatteryManager.BATTERY_STATUS_UNKNOWN, "Unknown");
			put(-1, "Not Reported");
		}
	};
	
	private static final SparseArray<String> pluggedValueMap = new SparseArray<String>() {
		{
			put(BatteryManager.BATTERY_PLUGGED_AC, "On AC");
			put(BatteryManager.BATTERY_PLUGGED_USB, "On USB");
			put(-1, "Not Reported");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.battery);
		final Button start = (Button) findViewById(R.id.start);
		final Button stop = (Button) findViewById(R.id.stop);
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					registerReceiver(batteryRcv, new IntentFilter(
							Intent.ACTION_BATTERY_CHANGED));
					Toast.makeText(BatteryActivity.this,
							"Battery monitoring started", Toast.LENGTH_SHORT)
							.show();
					receiverRegistered = true;
				} catch (Exception e) {
					Log.e(DEBUG_TAG, "Failed to register receiver");
				}
				start.setVisibility(View.GONE);
				stop.setVisibility(View.VISIBLE);
			}
		});
		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (receiverRegistered) {
					try {
						unregisterReceiver(batteryRcv);
					} catch (Exception e) {
						Log.e(DEBUG_TAG,
								"Failed to unregister receiver. Was it really registered?",
								e);
					}
					receiverRegistered = false;
				}
				Toast.makeText(BatteryActivity.this,
						"Battery monitoring stopped", Toast.LENGTH_SHORT)
						.show();
				stop.setVisibility(View.GONE);
				start.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	protected void onPause() {
		if (receiverRegistered) {
			try {
				unregisterReceiver(batteryRcv);
			} catch (Exception e) {
				Log.e(DEBUG_TAG,
						"Failed to unregister receiver. Was it really registered?",
						e);
			}
			receiverRegistered = false;
		}
		super.onPause();
	}

	private final BroadcastReceiver batteryRcv = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int maxValue = intent.getIntExtra(BatteryManager.EXTRA_SCALE,
						-1);
				int batteryStatus = intent.getIntExtra(
						BatteryManager.EXTRA_STATUS, -1);
				int batteryHealth = intent.getIntExtra(
						BatteryManager.EXTRA_HEALTH, -1);
				int batteryPlugged = intent.getIntExtra(
						BatteryManager.EXTRA_PLUGGED, -1);
				String batteryTech = intent
						.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
				int batteryIcon = intent.getIntExtra(
						BatteryManager.EXTRA_ICON_SMALL, -1);
				float batteryVoltage = (float) intent.getIntExtra(
						BatteryManager.EXTRA_VOLTAGE, -1) / 1000;
				boolean battery = intent.getBooleanExtra(
						BatteryManager.EXTRA_PRESENT, false);
				float batteryTemp = (float) intent.getIntExtra(
						BatteryManager.EXTRA_TEMPERATURE, -1) / 10;
				/*
				 * used to determine keys and types Bundle extras =
				 * intent.getExtras(); Set<String> keys = extras.keySet();
				 * Iterator<String> allKeys = keys.iterator(); while
				 * (allKeys.hasNext()) { String key = allKeys.next();
				 * Log.v("Battery", key); }
				 */
				int chargedPct = (level * 100) / maxValue;
				String batteryInfo = "Battery Info:\nHealth="
						+ healthValueMap.get(batteryHealth) + "\n" + "Status="
						+ statusValueMap.get(batteryStatus) + "\n"
						+ "Charged % = " + chargedPct + "%\n" + "Plugged = "
						+ pluggedValueMap.get(batteryPlugged) + "\n"
						+ "Type = " + batteryTech + "\n" + "Voltage = "
						+ batteryVoltage + " volts\n" + "Temperature = "
						+ batteryTemp + "¡C\n" + "Battery present = " + battery
						+ "\n";
				TextView status = (TextView) findViewById(R.id.status);
				ImageView icon = (ImageView) findViewById(R.id.icon);
				status.setText(batteryInfo);
				icon.setImageResource(batteryIcon);
				Toast.makeText(BatteryActivity.this, "Battery state changed",
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e(DEBUG_TAG, "Battery receiver failed: ", e);
			}
		}
	};
}
