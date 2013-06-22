package net.cantab.matt.williams.periscope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.opentok.Session;
import com.opentok.Stream;

public class TokBoxView extends SurfaceView implements SurfaceHolder.Callback, Session.Listener {
    static final String TOKBOX_API_KEY = "33009842";
    ExecutorService mExecutor;
    String mSessionId;
    String mTokenKey;
    Session mSession;

    public TokBoxView(Context context) {
        super(context);
        // Although this call is deprecated, Camera preview still seems to require it :-\
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // SurfaceHolders are not initially available, so we'll wait to create the publisher
        getHolder().addCallback(this);
        mExecutor = Executors.newCachedThreadPool();
    }

    public TokBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Although this call is deprecated, Camera preview still seems to require it :-\
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // SurfaceHolders are not initially available, so we'll wait to create the publisher
        getHolder().addCallback(this);
        mExecutor = Executors.newCachedThreadPool();
    }

    public TokBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Although this call is deprecated, Camera preview still seems to require it :-\
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // SurfaceHolders are not initially available, so we'll wait to create the publisher
        getHolder().addCallback(this);
        mExecutor = Executors.newCachedThreadPool();
    }

    public void setSession(String sessionId, String tokenKey) {
        mSessionId = sessionId;
        mTokenKey = tokenKey;
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Invoked when our rendering surface comes available.
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        mExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    // Go ahead and prepare session instance and connect.
                    mSession = Session.newInstance(getContext().getApplicationContext(),
                            mSessionId,
                            mTokenKey,
                            TOKBOX_API_KEY,
                            TokBoxView.this);
                    mSession.connect();

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }});
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionConnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionDidDropStream(Stream arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionDidReceiveStream(Stream arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSessionError(Exception arg0) {
        // TODO Auto-generated method stub

    }
}
