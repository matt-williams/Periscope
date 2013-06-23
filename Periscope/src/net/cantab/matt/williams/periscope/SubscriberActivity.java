package net.cantab.matt.williams.periscope;

import android.app.Activity;
import android.os.Bundle;

public class SubscriberActivity extends Activity {
    public static final String EXTRA_SESSION_ID = "sessionId";
    public static final String EXTRA_TOKEN_KEY = "tokenKey";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);

        String sessionId = getIntent().getStringExtra(EXTRA_SESSION_ID);
        String tokenKey = getIntent().getStringExtra(EXTRA_TOKEN_KEY);

        ((SubscriberView)findViewById(R.id.subscriberview)).setSession(sessionId, tokenKey);
    }

}
