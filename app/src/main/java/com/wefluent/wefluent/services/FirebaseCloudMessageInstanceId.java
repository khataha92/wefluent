package com.wefluent.wefluent.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by khalid on 3/24/18.
 */

public class FirebaseCloudMessageInstanceId extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("FCM Token", "Refreshed token: " + refreshedToken);


        //sendRegistrationToServer(refreshedToken);
    }
}
