package com.advancedandroidbook.simplefinal;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GPSActivity extends Activity implements LocationListener {
	LocationManager location = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.gps);

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		final Button start = (Button) findViewById(R.id.start);
		final Button stop = (Button) findViewById(R.id.stop);
		final TextView status = (TextView) findViewById(R.id.status);

		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(GPSActivity.this, "Starting....",
						Toast.LENGTH_SHORT).show();
				Iterator<String> providers = location.getAllProviders()
						.iterator();

				while (providers.hasNext()) {
					Log.v("Location", providers.next());
				}

				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.NO_REQUIREMENT);
				criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

				String best = location.getBestProvider(criteria, true);

				status.setText("Best provider: " + best);

				location.requestLocationUpdates(best, 1000, 0, GPSActivity.this);

				start.setVisibility(View.GONE);
				stop.setVisibility(View.VISIBLE);
			}
		});

		stop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				stop.setVisibility(View.GONE);
				start.setVisibility(View.VISIBLE);
			}
		});

	}

	Location lastLocation = null;

	public void onLocationChanged(Location location) {
		StringBuilder locInfo = new StringBuilder("Current loc = (")
				.append(location.getLatitude()).append(", ")
				.append(location.getLongitude()).append(") @ (")
				.append(location.getAltitude()).append(" meters up)\n");
		if (lastLocation != null) {
			float distance = location.distanceTo(lastLocation);
			locInfo.append("Distance from last = ").append(distance)
					.append(" meters\n");

		}
		lastLocation = location;

		if (Geocoder.isPresent()) {
			Geocoder coder = new Geocoder(this);
			try {
				List<Address> addresses = coder.getFromLocation(
						location.getLatitude(), location.getLongitude(), 3);
				if (addresses != null) {
					for (Address namedLoc : addresses) {
						String placeName = namedLoc.getLocality();
						String featureName = namedLoc.getFeatureName();
						String country = namedLoc.getCountryName();
						String road = namedLoc.getThoroughfare();
						locInfo.append(String.format("[%s][%s][%s][%s]\n",
								placeName, featureName, road, country));
						int addIdx = namedLoc.getMaxAddressLineIndex();
						for (int idx = 0; idx <= addIdx; idx++) {
							String addLine = namedLoc.getAddressLine(idx);
							locInfo.append(String.format("Line %d: %s\n", idx,
									addLine));
						}
					}
				}
			} catch (IOException e) {
				Log.e("GPS", "Failed to get address", e);
			}
		} else {
			Toast.makeText(GPSActivity.this, "No geocoding available",
					Toast.LENGTH_LONG).show();

		}

		TextView status = (TextView) findViewById(R.id.status);
		status.setText(locInfo);

		final String geoURI = String.format(Locale.getDefault(), "geo: %f,%f",
				location.getLatitude(), location.getLongitude());

		Button show = (Button) findViewById(R.id.show_map);
		show.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse(geoURI));
				startActivity(map);
			}
		});
		show.setVisibility(View.VISIBLE);

	}

	public void onProviderDisabled(String provider) {
		Log.v("GPS", "Provider disabled " + provider);

	}

	public void onProviderEnabled(String provider) {
		Log.v("GPS", "Provider enabled " + provider);

	}

	private static final SparseArray<String> providerStatusMap = new SparseArray<String>() {
		{
			put(LocationProvider.AVAILABLE, "Available");
			put(LocationProvider.OUT_OF_SERVICE, "Out of Service");
			put(LocationProvider.TEMPORARILY_UNAVAILABLE,
					"Temporarily Unavailable");
			put(-1, "Not Reported");
		}
	};

	public void onStatusChanged(String provider, int status, Bundle extras) {
		int satellites = extras.getInt("satellites", -1);

		String statusInfo = String.format(Locale.getDefault(),
				"Provider: %s, status: %s, satellites: %d", provider,
				providerStatusMap.get(status), satellites);
		Log.v("GPS", statusInfo);
		TextView statusText = (TextView) findViewById(R.id.status);
		statusText.setText(statusInfo);

	}

}
