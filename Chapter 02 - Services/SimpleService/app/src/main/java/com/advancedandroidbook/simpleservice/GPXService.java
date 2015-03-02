package com.advancedandroidbook.simpleservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class GPXService extends Service {
    private static final int GPS_NOTIFY = 0x2001;
    private static final String DEBUG_TAG = "GPXService";
    public static final String EXTRA_UPDATE_RATE = "update-rate";
    public static final String GPX_SERVICE = "com.advancedandroidbook.GPXService.SERVICE";
    private LocationManager location = null;
    private NotificationManager notifier = null;
    private int updateRate = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Android 2.0, L5, version
        Log.v(DEBUG_TAG, "onStartCommand() called, must be on L5 or later");
        if (flags != 0) {
            Log.w(DEBUG_TAG, "Redelivered or retrying service start: " + flags);
        }
        doServiceStart(intent, startId);
        return Service.START_REDELIVER_INTENT;
    }

    private void doServiceStart(Intent intent, int startId) {
        updateRate = intent.getIntExtra(EXTRA_UPDATE_RATE, -1);
        if (updateRate == -1) {
            updateRate = 60000;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String best = location.getBestProvider(criteria, true);
        location.requestLocationUpdates(best, updateRate, 0, trackListener);
        // notify that we've started up
        Intent toLaunch = new Intent(getApplicationContext(),
                ServiceControlActivity.class);
        PendingIntent intentBack = PendingIntent.getActivity(
                getApplicationContext(), 0, toLaunch, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setTicker("Builder GPS Tracking");
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("Builder GPS Tracking");
        builder.setContentText("Tracking start at " + updateRate + "ms intervals with ["
                + best + "] as the provider.");
        builder.setContentIntent(intentBack);
        builder.setAutoCancel(true);
        Notification notify = builder.build();

        notifier.notify(GPS_NOTIFY, notify);
    }

    @Override
    public void onDestroy() {
        Log.v(DEBUG_TAG, "onDestroy() called");
        if (location != null) {
            location.removeUpdates(trackListener);
            location = null;
        }
        // notify that we've stopped
        Intent toLaunch = new Intent(getApplicationContext(),
                ServiceControlActivity.class);
        PendingIntent intentBack = PendingIntent.getActivity(
                getApplicationContext(), 0, toLaunch, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setTicker("Builder GPS Tracking");
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("Builder GPS Tracking");
        builder.setContentText("Tracking stopped");
        builder.setContentIntent(intentBack);
        builder.setAutoCancel(true);
        Notification notify = builder.build();

        notifier.notify(GPS_NOTIFY, notify);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // we only have one, so no need to check the intent
        return mRemoteInterfaceBinder;
    }

    // our remote interface
    private final IRemoteInterface.Stub mRemoteInterfaceBinder = new IRemoteInterface.Stub() {
        public Location getLastLocation() {
            Log.v("interface", "getLastLocation() called");
            return lastLocation;
        }

        public GPXPoint getGPXPoint() {
            if (lastLocation == null) {
                return null;
            } else {
                Log.v("interface", "getGPXPoint() called");
                GPXPoint point = new GPXPoint();
                point.elevation = lastLocation.getAltitude();
                point.latitude = (int) (lastLocation.getLatitude() * 1E6);
                point.longitude = (int) (lastLocation.getLongitude() * 1E6);
                point.timestamp = new Date(lastLocation.getTime());
                return point;
            }
        }
    };

    private Location firstLocation = null;
    private Location lastLocation = null;
    private long lastTime = -1;
    private long firstTime = -1;

    private LocationListener trackListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            long thisTime = System.currentTimeMillis();
            long diffTime = thisTime - lastTime;
            Log.v(DEBUG_TAG, "diffTime == " + diffTime + ", updateRate = "
                    + updateRate);
            if (diffTime < updateRate) {
                // it hasn't been long enough yet
                return;
            }
            lastTime = thisTime;
            String locInfo = String.format(
                    Locale.getDefault(),
                    "Current loc = (%f, %f) @ (%.1f meters up)",
                    location.getLatitude(), location.getLongitude(),
                    location.getAltitude());
            if (lastLocation != null) {
                float distance = location.distanceTo(lastLocation);
                locInfo += String.format("\n Distance from last = %.1f meters",
                        distance);
                float lastSpeed = distance / diffTime;
                locInfo += String.format("\n\tSpeed: %.1fm/s", lastSpeed);
                if (location.hasSpeed()) {
                    float gpsSpeed = location.getSpeed();
                    locInfo += String.format(" (or %.1fm/s)", lastSpeed,
                            gpsSpeed);
                } else {
                }
            }
            if (firstLocation != null && firstTime != -1) {
                float overallDistance = location.distanceTo(firstLocation);
                float overallSpeed = overallDistance / (thisTime - firstTime);
                locInfo += String.format(
                        "\n\tOverall speed: %.1fm/s over %.1f meters",
                        overallSpeed, overallDistance);
            }
            lastLocation = location;
            if (firstLocation == null) {
                firstLocation = location;
                firstTime = thisTime;
            }
            Toast.makeText(getApplicationContext(), locInfo, Toast.LENGTH_LONG)
                    .show();
            Log.v(DEBUG_TAG, "Test time");
        }

        public void onProviderDisabled(String provider) {
            Log.v(DEBUG_TAG, "Provider disabled " + provider);
        }

        public void onProviderEnabled(String provider) {
            Log.v(DEBUG_TAG, "Provider enabled " + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}
