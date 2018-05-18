package com.wefluent.wefluent.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.wefluent.wefluent.BuildConfig;
import com.wefluent.wefluent.managers.AuthManager;
import com.wefluent.wefluent.managers.RemoteManager;

import io.fabric.sdk.android.Fabric;

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

        AppPref.initialize();

        RemoteManager.getInstance().init();

        AuthManager.getInstance().init();

        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();

        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());

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
