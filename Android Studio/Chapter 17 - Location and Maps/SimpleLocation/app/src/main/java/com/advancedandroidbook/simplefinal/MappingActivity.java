package com.advancedandroidbook.simplefinal;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class MappingActivity extends FragmentActivity {

	private GoogleMap map;
	private EditText name;
	private Geocoder coder;
	private TextView results;
	private CameraPosition.Builder destBuilder;
	private BitmapDescriptor icon;
	private double lat = 0f;
	private double lon = 0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapping);

		name = (EditText) findViewById(R.id.placename);
		coder = new Geocoder(getApplicationContext());
		results = (TextView) findViewById(R.id.result);
		destBuilder = new CameraPosition.Builder();

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(
				"Marker"));
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		Button geocode = (Button) findViewById(R.id.geocode);
		geocode.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				map.clear();
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
					Log.e("Mapping", "Failed to get location info", e);
				}

			} else {
				Toast.makeText(MappingActivity.this, "No geocoding available",
						Toast.LENGTH_LONG).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String locInfo) {
			results.setText(locInfo);

			CameraPosition dest = destBuilder.target(new LatLng(lat, lon))
					.zoom(15.5f).bearing(300).tilt(50).build();

			map.animateCamera(CameraUpdateFactory.newCameraPosition(dest));
			icon = BitmapDescriptorFactory.fromResource(R.drawable.paw);
			map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
					.title("Marker").icon(icon));
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		}
	}
}