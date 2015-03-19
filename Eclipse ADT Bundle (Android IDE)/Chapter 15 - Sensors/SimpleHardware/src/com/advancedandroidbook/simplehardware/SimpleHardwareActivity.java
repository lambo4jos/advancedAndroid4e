package com.advancedandroidbook.simplehardware;

public class SimpleHardwareActivity extends MenuActivity {
	@Override
	void prepareMenu() {
		addMenuItem("1. Sensors Sample", SensorsActivity.class);
		addMenuItem("2. Battery Monitor", BatteryActivity.class);
	}
}