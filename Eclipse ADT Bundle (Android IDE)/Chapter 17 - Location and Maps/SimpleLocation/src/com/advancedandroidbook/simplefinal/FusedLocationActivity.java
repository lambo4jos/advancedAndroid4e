package com.advancedandroidbook.simplefinal;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
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
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

public class FusedLocationActivity extends FragmentActivity implements
		ConnectionCallbacks, LocationListener, OnConnectionFailedListener,
		OnMyLocationButtonClickListener {

	private LocationClient locationClient;
	private Location location;
	private LocationRequest locationRequest = LocationRequest.create()
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
			.setFastestInterval(16)
			.setInterval(5000);
	private GoogleMap map;
	private Geocoder coder;
	private TextView results;
	private CameraPosition.Builder destBuilder;
	private double lat = 0f;
	private double lon = 0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);

		coder = new Geocoder(getApplicationContext());
		results = (TextView) findViewById(R.id.result);
		destBuilder = new CameraPosition.Builder();

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		Button geocode = (Button) findViewById(R.id.getloc);
		geocode.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Start the background task
				map.clear();
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

				if (locationClient != null && locationClient.isConnected()) {
					location = locationClient.getLastLocation();
					try {
						List<Address> geocodeResults = coder.getFromLocation(
								location.getLatitude(),
								location.getLongitude(), 3);

						StringBuilder locInfo = new StringBuilder("Results:\n");

						for (Address loc : geocodeResults) {
							lat = loc.getLatitude();
							lon = loc.getLongitude();
							locInfo.append("Location: ").append(lat)
									.append(", ").append(lon).append("\n");
						}
						return locInfo.toString();

					} catch (IOException e) {
						Log.e("Mapping", "Failed to get location info", e);
					}
				} else {
					Toast.makeText(FusedLocationActivity.this,
							"No location available", Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(FusedLocationActivity.this,
						"No geocoding available", Toast.LENGTH_LONG).show();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String locInfo) {
			results.setText(locInfo);

			CameraPosition dest = destBuilder.target(new LatLng(lat, lon))
					.zoom(15.5f).bearing(300).tilt(50).build();

			map.animateCamera(CameraUpdateFactory.newCameraPosition(dest));
			map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
					.title("Marker"));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationClient != null) {
			locationClient.disconnect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (locationClient == null) {
			locationClient = new LocationClient(getApplicationContext(), this,
					this);
		}
		locationClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		results.setText("Location = " + location);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
				.show();
		return false;
	}
}