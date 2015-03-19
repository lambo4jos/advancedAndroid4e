package com.advancedandroidbook.simplefinal;

public class SimpleFinalActivity extends MenuActivity {
	@Override
	void prepareMenu() {

		addMenuItem("1. GPS Sample", GPSActivity.class);
		addMenuItem("2. Geocode Sample", GeoAddressActivity.class);
		addMenuItem("3. Fused Location Sample", FusedLocationActivity.class);
		addMenuItem("4. Mapping Sample", MappingActivity.class);

	}
}