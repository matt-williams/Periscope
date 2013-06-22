package net.cantab.matt.williams.periscope;

import android.content.Context;
import android.util.AttributeSet;

import com.opentok.Stream;
import com.opentok.Subscriber;

public class SubscriberView extends TokBoxView {
    private Subscriber mSubscriber;

    public SubscriberView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SubscriberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SubscriberView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onSessionDidReceiveStream(final Stream stream) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mSubscriber = mSession.createSubscriber(SubscriberView.this, stream);
                mSubscriber.connect();
            }});
    }
}
