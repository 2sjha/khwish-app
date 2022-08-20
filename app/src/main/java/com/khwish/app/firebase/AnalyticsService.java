package com.khwish.app.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsService {

    public static void log(Context context, String eventName, String activityName) {

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        Bundle bundle = new Bundle();

        if (eventName.toLowerCase().equals("failure")) {
            bundle.putString("TASK_NAME", eventName);
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, activityName);
            mFirebaseAnalytics.logEvent("FAILURE", bundle);
        } else {
            //            bundle.putString("EVENT_NAME", eventName);
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, activityName);
            mFirebaseAnalytics.logEvent(eventName, bundle);
        }

        // optional
//      //Set the minimum engagement time required before starting a session.
//      mFirebaseAnalytics.setMinimumSessionDuration(20000);

//      //Set the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
//      mFirebaseAnalytics.setSessionTimeoutDuration(3000);


    }
}
