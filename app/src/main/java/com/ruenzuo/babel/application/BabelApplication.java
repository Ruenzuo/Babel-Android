package com.ruenzuo.babel.application;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ruenzuo.babel.R;

import java.util.HashMap;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class BabelApplication extends Application {

    public enum TrackerName {
        APP_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, tracker);
        }
        return mTrackers.get(trackerId);
    }

}
