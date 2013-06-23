package net.cantab.matt.williams.periscope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.cloudbase.CBHelper;
import com.cloudbase.CBHelperResponder;
import com.cloudbase.CBHelperResponse;
import com.cloudbase.CBQueuedRequest;
import com.google.gson.internal.StringMap;

public class CloudbaseManager {
    private static final String CLOUDBASE_APP_CODE = "geoviddeo";
    private static final String CLOUDBASE_APP_SECRET = "6962204d83ba56e7a24fb798b8451f40";
    private static final String CLOUDBASE_APP_PASSWORD = "8207da8cebeff54968639df7ab83d4e8";
    private static final String CLOUDBASE_COLLECTION = "geostreams";
    private final CBHelper mCbHelper;
    private GeoStreamCallback mGeoStreamCallback;

    public interface GeoStreamCallback {
        public void gotGeoStream(float latitude, float longitude, float altitude, String sessionId, String tokenKey);
    }

    public CloudbaseManager(Activity activity) {
        mCbHelper = new CBHelper(CLOUDBASE_APP_CODE, CLOUDBASE_APP_SECRET, activity);
        mCbHelper.setPassword(CLOUDBASE_APP_PASSWORD);
    }

    public CloudbaseManager(Activity activity, GeoStreamCallback geoStreamCallback) {
        this(activity);
        mGeoStreamCallback = geoStreamCallback;
        mCbHelper.searchDocument(CLOUDBASE_COLLECTION, new CBHelperResponder() {
            @Override
            public void handleResponse(CBQueuedRequest req, CBHelperResponse rsp) {
                if (rsp.isSuccess()) {
                    for (StringMap<Object> entry : (ArrayList<StringMap<Object>>)rsp.getData()) {
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
        });
    }

    public void updateStream(float latitude, float longitude, float altitude, String sessionId, String tokenKey) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("deleted", "false");
        map.put("latitude", Float.toString(latitude));
        map.put("longitude", Float.toString(longitude));
        map.put("altitude", Float.toString(altitude));
        map.put("sessionId", sessionId);
        map.put("tokenKey", tokenKey);

        mCbHelper.runCloudFunction("upsert_geostream", map, new CBHelperResponder() {
            @Override
            public void handleResponse(CBQueuedRequest req, CBHelperResponse rsp) {
                android.util.Log.e("CloudbaseManager.upsert", rsp.getResponseDataString());
            }
        });
    }

    public void deleteStream(String sessionId, String tokenKey) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("deleted", "true");
        map.put("latitude", "0");
        map.put("longitude", "0");
        map.put("altitude", "0");
        map.put("sessionId", sessionId);
        map.put("tokenKey", tokenKey);

        mCbHelper.runCloudFunction("upsert_geostream", map, new CBHelperResponder() {
            @Override
            public void handleResponse(CBQueuedRequest req, CBHelperResponse rsp) {
                android.util.Log.e("CloudbaseManager.upsert", rsp.getResponseDataString());
            }
        });
    }
}
