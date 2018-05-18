package com.wefluent.vipteachers.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.wefluent.vipteachers.managers.AuthManager;

/**
 * Created by khalid on 3/23/18.
 */

public class App extends Application {

    private static Context appContext = null;

    private static Activity activity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        AuthManager.getInstance().init();

    }


    public static Context getAppContext() {

        return appContext;

    }

    public static void setActivity(Activity activity) {
        App.activity = activity;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static Context getActiveContext() {

        if(activity == null || activity.isDestroyed() || activity.isFinishing()) {

            return appContext;
        }

        return activity;
    }
}
