package net.cantab.matt.williams.periscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.cantab.matt.williams.periscope.CloudbaseManager.GeoStreamCallback;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * This application demonstrates the basic workflow for getting started with the OpenTok Android SDK.
 * Currently the user is expected to provide rendering surfaces for the SDK, so we'll create
 * SurfaceHolder instances for each component.
 *
 */
public class MainActivity extends LocationActivity implements GeoStreamCallback {
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_broadcast) {
            Intent intent = new Intent(this, BroadcastActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private class Subscriber {
        public LatLng latLng;
        public View view;
    }

    private MapView mMapView;
    private GoogleMap mMap;
    private final Handler mHandler = new Handler();
    private CloudbaseManager mCloudbaseManager;

    List<Subscriber> mSubscribers = new ArrayList<Subscriber>();
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
            Collections.sort(mSubscribers, new Comparator<Subscriber>() {

                @Override
                public int compare(Subscriber lhs, Subscriber rhs) {
                    if (((AbsoluteLayout.LayoutParams)lhs.view.getLayoutParams()).y < ((AbsoluteLayout.LayoutParams)rhs.view.getLayoutParams()).y) {
                        return -1;
                    } else if (((AbsoluteLayout.LayoutParams)lhs.view.getLayoutParams()).y > ((AbsoluteLayout.LayoutParams)rhs.view.getLayoutParams()).y) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (Subscriber subscriber : mSubscribers) {
                subscriber.view.bringToFront();
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
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder(mMap.getCameraPosition()).target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(18.0f).tilt(75.0f).build()));
    }

    @Override
    public void gotGeoStream(float latitude, float longitude, float altitude, String sessionId, String tokenKey) {
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
