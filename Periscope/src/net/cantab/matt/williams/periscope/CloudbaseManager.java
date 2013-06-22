package net.cantab.matt.williams.periscope;

import java.util.ArrayList;

import android.app.Activity;

import com.cloudbase.CBHelper;
import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBQueuedRequest;
import com.google.gson.internal.StringMap;

public class CloudbaseManager implements CBHelperResponder {
    private static final String CLOUDBASE_APP_CODE = "geoviddeo";
    private static final String CLOUDBASE_APP_SECRET = "6962204d83ba56e7a24fb798b8451f40";
    private static final String CLOUDBASE_APP_PASSWORD = "8207da8cebeff54968639df7ab83d4e8";
    private static final String CLOUDBASE_COLLECTION = "geostreams";
    private final CBHelper mCbHelper;
    private final GeoStreamCallback mGeoStreamCallback;

    public interface GeoStreamCallback {
        public void gotGeoStream(float latitude, float longitude, float altitude, String sessionId, String tokenKey);
    }

    public CloudbaseManager(Activity activity, GeoStreamCallback geoStreamCallback) {
        mGeoStreamCallback = geoStreamCallback;
        mCbHelper = new CBHelper(CLOUDBASE_APP_CODE, CLOUDBASE_APP_SECRET, activity);
        mCbHelper.setPassword(CLOUDBASE_APP_PASSWORD);
        mCbHelper.searchDocument(CLOUDBASE_COLLECTION, this);
    }

    public void write() {
        GeoStream geoStream = new GeoStream();

        /*geoStream.setLatitude((float)mLocationTracker.getLatitude());
        geoStream.setLongitude((float)mLocationTracker.getLongitude());
        geoStream.setAltitude((float)mLocationTracker.getAltitude());
        geoStream.setSessionId(sessionId);
        geoStream.setTokenKey(tokenKey);
        */
        mCbHelper.insertDocument(geoStream, CLOUDBASE_COLLECTION);
    }

    @Override
    public void handleResponse(CBQueuedRequest req, CBHelperResponse rsp) {
        if (rsp.isSuccess()) {
            for (StringMap entry : (ArrayList<StringMap>)rsp.getData()) {
                if (!((Boolean)entry.get("deleted")).booleanValue()) {
                    mGeoStreamCallback.gotGeoStream(((Number)entry.get("longitude")).floatValue(),
                                                    ((Number)entry.get("latitude")).floatValue(),
                                                    ((Number)entry.get("altitude")).floatValue(),
                                                    (String)entry.get("sessionId"),
                                                    (String)entry.get("tokenKey"));
                }
            }
        }
    }
}
