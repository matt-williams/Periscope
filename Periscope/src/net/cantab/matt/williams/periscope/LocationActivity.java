package net.cantab.matt.williams.periscope;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public abstract class LocationActivity extends Activity implements LocationListener {
    private static final Criteria CRITERIA = new Criteria();
    static {
        CRITERIA.setAccuracy(Criteria.ACCURACY_COARSE);
    }
    private static final long MIN_UPDATE_TIME = 10000;
    private static final float MIN_UPDATE_DISTANCE = 10.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager locationManager = (LocationManager)getSystemService(Activity.LOCATION_SERVICE);
        String providerName = locationManager.getBestProvider(CRITERIA, true);
        if (providerName == null) {
            providerName = LocationManager.NETWORK_PROVIDER;
        }
        Location location = locationManager.getLastKnownLocation(providerName);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(providerName, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this, Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        LocationManager locationManager = (LocationManager)getSystemService(Activity.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
