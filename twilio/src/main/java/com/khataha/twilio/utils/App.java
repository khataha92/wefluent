package com.khataha.twilio.utils;

import android.app.Activity;
import android.content.Context;

/**
 * Created by khalid on 3/23/18.
 */

public class App {

    private static App instance = null;

    private App() {

    }

    public static App getInstance() {

        if(instance == null) {

            instance = new App();
        }

        return instance;
    }

    private Context appContext = null;

    private Activity activity = null;

    public void onCreate() {
        AppPref.initialize();

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public Context getActiveContext() {

        if(activity == null || activity.isDestroyed() || activity.isFinishing()) {

            return appContext;
        }

        return activity;
    }
}
