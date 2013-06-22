package net.cantab.matt.williams.periscope;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class LocationTracker implements LocationListener {
    private static final Criteria CRITERIA = new Criteria();
    static {
        CRITERIA.setAccuracy(Criteria.ACCURACY_COARSE);
    }
    private static final long MIN_UPDATE_TIME = 10000;
    private static final float MIN_UPDATE_DISTANCE = 10.0f;
    private final LocationManager mLocationManager;
    private double mLatitude;
    private double mLongitude;
    private double mAltitude;

    public LocationTracker(LocationManager locationManager) {
        mLocationManager = locationManager;
    }

    public void onResume() {
        String providerName = mLocationManager.getBestProvider(CRITERIA, true);
        if (providerName == null) {
            providerName = LocationManager.NETWORK_PROVIDER;
        }
        Location location = mLocationManager.getLastKnownLocation(providerName);
        if (location != null) {
            onLocationChanged(location);
        }
        mLocationManager.requestLocationUpdates(providerName, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this, Looper.getMainLooper());
    }

    public void onPause() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAltitude = location.getAltitude();
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getAltitude() {
        return mAltitude;
    }
}