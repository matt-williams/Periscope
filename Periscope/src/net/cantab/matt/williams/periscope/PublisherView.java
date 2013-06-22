package net.cantab.matt.williams.periscope;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.opentok.Publisher;

public class PublisherView extends TokBoxView implements Publisher.Listener {
    private Camera mCamera;
    private Publisher mPublisher;

    public PublisherView(Context context) {
        super(context);
    }

    public PublisherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PublisherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Invoked when Our Publisher's rendering surface comes available.
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            // This usually maps to the front camera.
            mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
            mCamera.setPreviewDisplay(getHolder());
            // Note: preview will continue even if we fail to connect.
            mCamera.startPreview();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        super.surfaceChanged(arg0, arg1, arg2, arg3);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onSessionConnected() {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                // Session is ready to publish. Create Publisher instance from our rendering surface and camera, then connect.
                mPublisher = mSession.createPublisher(mCamera, getHolder());
                mPublisher.connect();
            }});
    }

    @Override
    public void onPublisherStreamingStarted() {
        // TODO Auto-generated method stub

    }
    @Override
    public void onPublisherDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPublisherFailed() {
        // TODO Auto-generated method stub

    }
}
