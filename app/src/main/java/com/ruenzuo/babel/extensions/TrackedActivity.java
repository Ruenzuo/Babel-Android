package com.ruenzuo.babel.extensions;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.ruenzuo.babel.application.BabelApplication;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class TrackedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BabelApplication) getApplication()).getTracker(BabelApplication.TrackerName.APP_TRACKER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

}
