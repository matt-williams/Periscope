package net.cantab.matt.williams.periscope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.cloudbase.CBHelper;
import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBQueuedRequest;
import com.opentok.Publisher;
import com.opentok.Session;
import com.opentok.Stream;
import com.opentok.Subscriber;

// Based on git@github.com:opentok/Android-Hello-World.git

/**
 * This application demonstrates the basic workflow for getting started with the OpenTok Android SDK.
 * Currently the user is expected to provide rendering surfaces for the SDK, so we'll create
 * SurfaceHolder instances for each component.
 *
 */
public class MainActivity extends Activity implements Publisher.Listener, Session.Listener, Callback, CBHelperResponder {
    private static final String GEOSTREAMS = "geostreams";
    ExecutorService executor;
    SurfaceView publisherView;
    SurfaceView subscriberView1;
    SurfaceView subscriberView2;
    Camera camera;
    Publisher publisher;
    Subscriber subscriber1;
    Subscriber subscriber2;
    private Session session;
    private WakeLock wakeLock;
    private LocationTracker mLocationTracker;
    private CBHelper mCbHelper;
    private int mNumStreams;
    private final String mPublishTokenKey = "T1==cGFydG5lcl9pZD0zMzAwOTg0MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz05YTA5OTIwMDM3NzM3MTVjNjA5OTI5YmI5NzlmNGE2OGVkYWMzODNkOnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9Ml9NWDR6TXpBd09UZzBNbjR4TWpjdU1DNHdMakYtVTJGMElFcDFiaUF5TWlBd05qb3pORG96T0NCUVJGUWdNakF4TTM0d0xqSXpNVGN4TkRRNWZnJmNyZWF0ZV90aW1lPTEzNzE5MDgwOTImbm9uY2U9MC4zMTY4OTI3MDM2ODQ4MzA4JmV4cGlyZV90aW1lPTEzNzI1MTI4OTImY29ubmVjdGlvbl9kYXRhPQ==";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLocationTracker = new LocationTracker((LocationManager)getSystemService(Activity.LOCATION_SERVICE));


     // the helper class constructor also receives the current Activity object
     // this is used to get the application details and its cache paths.
     mCbHelper = new CBHelper("geoviddeo", "6962204d83ba56e7a24fb798b8451f40", this);
     mCbHelper.setPassword("8207da8cebeff54968639df7ab83d4e8");
     mCbHelper.searchDocument(GEOSTREAMS, this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        publisherView = (SurfaceView)findViewById(R.id.publisherview);
        subscriberView1 = (SurfaceView)findViewById(R.id.subscriberview1);
        subscriberView2 = (SurfaceView)findViewById(R.id.subscriberview2);

        // Although this call is deprecated, Camera preview still seems to require it :-\
        publisherView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // SurfaceHolders are not initially available, so we'll wait to create the publisher
        publisherView.getHolder().addCallback(this);

        // A simple executor will allow us to perform tasks asynchronously.
        executor = Executors.newCachedThreadPool();

        // Disable screen dimming
        PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
                        "Full Wake Lock");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        // Release the camera when the application is being destroyed, lest we can't acquire it again later.
        if (null != camera) camera.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    /**
     * Invoked when Our Publisher's rendering surface comes available.
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        if (publisher == null) {
            executor.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        // This usually maps to the front camera.
                        camera = Camera.open(Camera.getNumberOfCameras() - 1);
                        camera.setPreviewDisplay(publisherView.getHolder());
                        // Note: preview will continue even if we fail to connect.
                        camera.startPreview();

                        // Since our Publisher is ready, go ahead and prepare session instance and connect.
                        session = Session.newInstance(getApplicationContext(),
                                "2_MX4zMzAwOTg0Mn4xMjcuMC4wLjF-U2F0IEp1biAyMiAwNjozNDozOCBQRFQgMjAxM34wLjIzMTcxNDQ5fg",
                                mPublishTokenKey,
                                "33009842",
                                MainActivity.this);
                        session.connect();

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                }});
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionConnected() {
        executor.submit(new Runnable() {

            @Override
            public void run() {
                // Session is ready to publish. Create Publisher instance from our rendering surface and camera, then connect.
                publisher = session.createPublisher(camera, publisherView.getHolder());
                publisher.connect();

                GeoStream geoStream = new GeoStream();
                geoStream.setLatitude((float)mLocationTracker.getLatitude());
                geoStream.setLongitude((float)mLocationTracker.getLongitude());
                geoStream.setAltitude((float)mLocationTracker.getAltitude());
                geoStream.setSessionId(session.getSessionId());
                geoStream.setTokenKey(mPublishTokenKey);
                mCbHelper.insertDocument(geoStream, GEOSTREAMS);
            }});
    }

    @Override
    public void onSessionDidReceiveStream(final Stream stream) {
        executor.submit(new Runnable() {

            @Override
            public void run() {
                if (mNumStreams == 0)
                {
                    subscriber1 = session.createSubscriber(subscriberView1, stream);
                    subscriber1.connect();
                } else {
                    subscriber2 = session.createSubscriber(subscriberView2, stream);
                    subscriber2.connect();
                }
                mNumStreams++;
            }});
    }

    @Override
    public void onPublisherStreamingStarted() {
        Log.i("hello-world", "publisher is streaming!");
    }

    @Override
    public void onPublisherFailed() {
        Log.e("hello-world", "publisher failed!");
    }

    @Override
    public void onSessionDidDropStream(Stream stream) {
        Log.i("hello-world", String.format("stream %d dropped", stream.toString()));
        mNumStreams--;
    }

    @Override
    public void onSessionError(Exception cause) {
        Log.e("hello-world", "session failed! "+cause.toString());
    }

    @Override
    public void onSessionDisconnected() {
        Log.i("hello-world", "session disconnected");
    }

    @Override
    public void onPublisherDisconnected() {
        Log.i("hello-world", "publisher disconnected");

    }

    @Override
    public void handleResponse(CBQueuedRequest req, CBHelperResponse res) {
        Log.v("logTag", (res.isSuccess()?"OK":"FAILED"));
        Log.v("logTag", res.getResponseDataString());

    }

}
