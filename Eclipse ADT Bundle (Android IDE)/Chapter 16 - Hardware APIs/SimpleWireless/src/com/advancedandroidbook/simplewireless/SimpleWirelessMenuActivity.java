package com.advancedandroidbook.simplewireless;

public class SimpleWirelessMenuActivity extends MenuActivity {

	@Override
	void prepareMenu() {
		addMenuItem("1. Bluetooth Sample", BluetoothActivity.class);
		addMenuItem("2. WiFi Sample", WiFiActivity.class);
		addMenuItem("3. Android Beam Sample", AndroidBeamActivity.class);
	}
}