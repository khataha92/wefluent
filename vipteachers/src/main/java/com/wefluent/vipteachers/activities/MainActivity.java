package com.wefluent.vipteachers.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.wefluent.vipteachers.R;
import com.wefluent.vipteachers.databinding.ActivityMainBinding;
import com.wefluent.vipteachers.managers.AuthManager;
import com.wefluent.vipteachers.managers.FirebaseManager;
import com.wefluent.vipteachers.utils.App;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 10010;

    private ActivityMainBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.setActivity(this);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();

//        FirebaseManager.getInstance().getTeachers();

//        AuthManager.getInstance().verifyPhone("+970595202192");
    }

    private void init() {

        dataBinding.googleLogin.setOnClickListener(v -> {

            Intent signInIntent = AuthManager.getInstance().getGoogleSignInClient().getSignInIntent();

            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        AuthManager.getInstance().setAfterLoginListener(() -> {

            FirebaseManager.getInstance().addTeacher();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthManager.getInstance().firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);

            }

        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}
