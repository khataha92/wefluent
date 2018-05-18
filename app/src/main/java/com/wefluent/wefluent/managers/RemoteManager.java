package com.wefluent.wefluent.managers;

import android.support.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.wefluent.wefluent.R;

/**
 * Created by khalid on 3/23/18.
 */

public class RemoteManager {

    private FirebaseRemoteConfig mFirebaseRemoteConfig = null;

    private static RemoteManager instance = null;

    public void init() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch(60 * 60)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mFirebaseRemoteConfig.activateFetched();

                        } else {

                        }
                    }
                });

    }

    public static RemoteManager getInstance() {

        if(instance == null) {

            instance = new RemoteManager();

        }

        return instance;
    }

    private boolean getBooleanValue(String key) {

        return mFirebaseRemoteConfig.getBoolean(key);
    }

    public static boolean getBoolean(String key) {

        return getInstance().getBooleanValue(key);
    }

    private String getStringValue(String key) {

        return mFirebaseRemoteConfig.getString(key);
    }

    public static String getString(String key) {

        return getInstance().getStringValue(key);
    }

}
