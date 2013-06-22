package net.cantab.matt.williams.periscope;

import android.location.Location;
import android.os.Bundle;

public class BroadcastActivity extends LocationActivity {
    private final String mPublishSessionId = "2_MX4zMzAwOTg0Mn4xMjcuMC4wLjF-U2F0IEp1biAyMiAwNjozNDozOCBQRFQgMjAxM34wLjIzMTcxNDQ5fg";
    private final String mPublishTokenKey = "T1==cGFydG5lcl9pZD0zMzAwOTg0MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz05YTA5OTIwMDM3NzM3MTVjNjA5OTI5YmI5NzlmNGE2OGVkYWMzODNkOnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9Ml9NWDR6TXpBd09UZzBNbjR4TWpjdU1DNHdMakYtVTJGMElFcDFiaUF5TWlBd05qb3pORG96T0NCUVJGUWdNakF4TTM0d0xqSXpNVGN4TkRRNWZnJmNyZWF0ZV90aW1lPTEzNzE5MDgwOTImbm9uY2U9MC4zMTY4OTI3MDM2ODQ4MzA4JmV4cGlyZV90aW1lPTEzNzI1MTI4OTImY29ubmVjdGlvbl9kYXRhPQ==";
    private CloudbaseManager mCloudbaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        mCloudbaseManager = new CloudbaseManager(this);

        ((PublisherView)findViewById(R.id.publisherview)).setSession(mPublishSessionId, mPublishTokenKey);
    }

    @Override
    public void onDestroy() {
        mCloudbaseManager.deleteStream(mPublishSessionId, mPublishTokenKey);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        android.util.Log.e("BroadcastActivity.onLocationChanged", "Got here: " + location);
        mCloudbaseManager.updateStream((float)location.getLatitude(), (float)location.getLongitude(), (float)location.getAltitude(), mPublishSessionId, mPublishTokenKey);
    }
}
