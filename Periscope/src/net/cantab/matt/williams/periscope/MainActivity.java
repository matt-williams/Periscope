package net.cantab.matt.williams.periscope;

import java.util.ArrayList;
import java.util.List;

import net.cantab.matt.williams.periscope.CloudbaseManager.GeoStreamCallback;
import android.app.Activity;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

// Based on git@github.com:opentok/Android-Hello-World.git

/**
 * This application demonstrates the basic workflow for getting started with the OpenTok Android SDK.
 * Currently the user is expected to provide rendering surfaces for the SDK, so we'll create
 * SurfaceHolder instances for each component.
 *
 */
public class MainActivity extends Activity implements LocationListener, GeoStreamCallback {
    private static final Criteria CRITERIA = new Criteria();
    static {
        CRITERIA.setAccuracy(Criteria.ACCURACY_COARSE);
    }
    private static final long MIN_UPDATE_TIME = 10000;
    private static final float MIN_UPDATE_DISTANCE = 10.0f;

    private class Subscriber {
        public LatLng latLng;
        public View view;
    }

    private MapView mMapView;
    private GoogleMap mMap;
    private final Handler mHandler = new Handler();
    private CloudbaseManager mCloudbaseManager;

    List<Subscriber> mSubscribers = new ArrayList<Subscriber>();
//    private View mPublisherView;
//    private TokBoxView mSubscriberView1;
//    private TokBoxView mSubscriberView2;
    private final String mPublishSessionId = "2_MX4zMzAwOTg0Mn4xMjcuMC4wLjF-U2F0IEp1biAyMiAwNjozNDozOCBQRFQgMjAxM34wLjIzMTcxNDQ5fg";
    private final String mPublishTokenKey = "T1==cGFydG5lcl9pZD0zMzAwOTg0MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz05YTA5OTIwMDM3NzM3MTVjNjA5OTI5YmI5NzlmNGE2OGVkYWMzODNkOnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9Ml9NWDR6TXpBd09UZzBNbjR4TWpjdU1DNHdMakYtVTJGMElFcDFiaUF5TWlBd05qb3pORG96T0NCUVJGUWdNakF4TTM0d0xqSXpNVGN4TkRRNWZnJmNyZWF0ZV90aW1lPTEzNzE5MDgwOTImbm9uY2U9MC4zMTY4OTI3MDM2ODQ4MzA4JmV4cGlyZV90aW1lPTEzNzI1MTI4OTImY29ubmVjdGlvbl9kYXRhPQ==";
    private final Runnable mUpdateLayouts = new Runnable() {
        @Override
        public void run() {
            for (Subscriber subscriber : mSubscribers) {
                Point point = mMap.getProjection().toScreenLocation(subscriber.latLng);
                AbsoluteLayout.LayoutParams params = (AbsoluteLayout.LayoutParams)subscriber.view.getLayoutParams();
                params.x = point.x - params.width / 2;
                params.y = point.y - params.height;
                subscriber.view.setLayoutParams(params);
            }
            mHandler.postDelayed(mUpdateLayouts, 10);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView)findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMap = mMapView.getMap();
        mMap.setMyLocationEnabled(true);
        try {
            MapsInitializer.initialize(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            android.util.Log.e("MainActivity", "Caught exception", e);
        }

        mCloudbaseManager = new CloudbaseManager(this, this);

        mUpdateLayouts.run();

/*
        mPublisherView = (TokBoxView)findViewById(R.id.publisherview);
        mPublisherView.setSession(mPublishSessionId, mPublishTokenKey);
        mSubscriberView1 = (TokBoxView)findViewById(R.id.subscriberview1);
        mSubscriberView1.setSession(mPublishSessionId, mPublishTokenKey);
        mSubscriberView2 = (TokBoxView)findViewById(R.id.subscriberview2);
        mSubscriberView2.setSession(mPublishSessionId, mPublishTokenKey);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
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
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition()).target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(18.0f).tilt(75.0f).build()));
    }

    @Override
    public void gotGeoStream(float latitude, float longitude, float altitude, String sessionId, String tokenKey) {
        // TODO Auto-generated method stub
        android.util.Log.e("MainActivity", "gotGeoStream(" + latitude + ", " + longitude + ", " + altitude + ", " + sessionId + ", " + tokenKey + ")");

        Subscriber subscriber = new Subscriber();
        subscriber.latLng = new LatLng(longitude, latitude);

        AbsoluteLayout layout = (AbsoluteLayout)findViewById(R.id.layout);
        View.inflate(this, R.layout.overlay_subscriber, layout);
        subscriber.view = layout.getChildAt(layout.getChildCount() - 1);
        TokBoxView tokBoxView = (TokBoxView)(((AbsoluteLayout)subscriber.view).getChildAt(2));
        tokBoxView.setSession(sessionId, tokenKey);
        tokBoxView.setZOrderOnTop(true);
        mSubscribers.add(subscriber);
    }
}
