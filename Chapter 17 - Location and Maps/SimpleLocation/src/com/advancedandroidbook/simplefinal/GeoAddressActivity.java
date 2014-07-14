package com.advancedandroidbook.simplefinal;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GeoAddressActivity extends Activity {

	private EditText name;
	private Geocoder coder;
	private TextView results;
	private Button map;
	private double lat = 0f;
	private double lon = 0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geoaddress);

		name = (EditText) findViewById(R.id.placename);
		coder = new Geocoder(getApplicationContext());
		results = (TextView) findViewById(R.id.result);
		map = (Button) findViewById(R.id.map);

		Button geocode = (Button) findViewById(R.id.geocode);
		geocode.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Start the background task
				(new GeoCodeTask()).execute();
			}
		});
	}

	protected class GeoCodeTask extends AsyncTask<Location, Void, String> {
		public GeoCodeTask() {
			super();
		}

		@Override
		protected String doInBackground(Location... params) {
			if (Geocoder.isPresent()) {
				String placeName = name.getText().toString();

				try {
					List<Address> geocodeResults = coder.getFromLocationName(
							placeName, 3);

					StringBuilder locInfo = new StringBuilder("Results:\n");

					for (Address loc : geocodeResults) {
						lat = loc.getLatitude();
						lon = loc.getLongitude();
						locInfo.append("Location: ").append(lat).append(", ")
								.append(lon).append("\n");
					}

					return locInfo.toString();

				} catch (IOException e) {
					Log.e("GeoAddress", "Failed to get location info", e);
				}
			} else {
				Toast.makeText(GeoAddressActivity.this,
						"No geocoding available", Toast.LENGTH_LONG).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String locInfo) {
			results.setText(locInfo);

			final String geoURI = "geo: " + lat + ", " + lon;

			map.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Uri geo = Uri.parse(geoURI);
					Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
					startActivity(geoMap);
				}

			});
			map.setVisibility(View.VISIBLE);
		}
	}
}