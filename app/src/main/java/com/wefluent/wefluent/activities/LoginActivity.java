package com.wefluent.wefluent.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.wefluent.wefluent.R;
import com.wefluent.wefluent.constants.Constants;
import com.wefluent.wefluent.databinding.ActivityLoginBinding;
import com.wefluent.wefluent.enums.LoginMethod;
import com.wefluent.wefluent.managers.AuthManager;
import com.wefluent.wefluent.utils.App;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static LoginActivity instance;

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    LoginMethod currentLoginMethod = null;

    ActivityLoginBinding dataBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        closeIfHasPreviousInstance();

        instance = this;

        FirebaseUser user = AuthManager.getInstance().getCurrentUser();

        if(user != null) {

            HomeActivity.start(this);

            closeIfHasPreviousInstance();

            return;
        }

        App.setActivity(this);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        init();

    }

    public static void closeIfHasPreviousInstance() {

        if(instance == null) {

            return;
        }

        instance.finish();

        instance = null;
    }

    private void init() {

        dataBinding.facebookFakeButton.setReadPermissions(Constants.FACEBOOK_PERMISSION_EMAIL, Constants.FACEBOOK_PERMISSION_PUBLIC_PROFILE);

        dataBinding.facebookFakeButton.registerCallback(AuthManager.getInstance().getFacebookCallbackManager(), new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                AuthManager.getInstance().handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {


            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        dataBinding.twitterFakeButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> result) {

                Log.d(TAG, "twitterLogin:success" + result);

                AuthManager.getInstance().handleTwitterSession(result.data);

            }

            @Override
            public void failure(TwitterException exception) {

                Log.w(TAG, "twitterLogin:failure", exception);

                //updateUI(null);

            }
        });

        dataBinding.facebookLogin.setOnClickListener(v -> {

            currentLoginMethod = LoginMethod.FACEBOOK;

            dataBinding.facebookFakeButton.callOnClick();

        });

        dataBinding.twitterLogin.setOnClickListener(v -> {

            currentLoginMethod = LoginMethod.TWITTER;

            dataBinding.twitterFakeButton.callOnClick();

        });

        dataBinding.googleLogin.setOnClickListener(v->{

            Intent signInIntent = AuthManager.getInstance().getGoogleSignInClient().getSignInIntent();

            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthManager.getInstance().firebaseAuthWithGoogle(account);

            } catch (ApiException e) {

                Log.w(TAG, "Google sign in failed", e);

            }
        } else {

            if (currentLoginMethod != null) {

                switch (currentLoginMethod) {

                    case FACEBOOK:

                        AuthManager.getInstance().getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);

                        break;

                    case TWITTER:

                        dataBinding.twitterFakeButton.onActivityResult(requestCode, resultCode, data);

                        break;
                }

            }

        }
    }
}

