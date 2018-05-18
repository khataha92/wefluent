package com.wefluent.wefluent.managers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.wefluent.wefluent.R;
import com.wefluent.wefluent.activities.HomeActivity;
import com.wefluent.wefluent.utils.App;

import java.util.concurrent.TimeUnit;

import static com.wefluent.wefluent.utils.ResUtil.getString;

/**
 * Created by khalid on 3/24/18.
 */

public class AuthManager {

    private static AuthManager instance = null;

    private static final String TAG = AuthManager.class.getSimpleName();

    private FirebaseAuth mAuth;

    private CallbackManager facebookCallbackManager;

    private GoogleSignInClient mGoogleSignInClient;

    private TwitterAuthConfig authConfig;

    private TwitterConfig twitterConfig;

    GoogleSignInOptions googleSignInOptions;

    private AuthManager() {

        facebookCallbackManager = CallbackManager.Factory.create();
    }

    public void init() {

        mAuth = FirebaseAuth.getInstance();

        authConfig =  new TwitterAuthConfig(getString(R.string.TWITTER_CONSUMER_KEY), getString(R.string.TWITTER_CONSUMER_SECRET));

        twitterConfig = new TwitterConfig.Builder(App.getAppContext()).twitterAuthConfig(authConfig).build();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(App.getAppContext(), googleSignInOptions);

        Twitter.initialize(twitterConfig);

    }

    public FirebaseUser getCurrentUser() {

        if(mAuth == null) {

            return null;
        }

        return mAuth.getCurrentUser();
    }

    public static AuthManager getInstance() {

        if(instance == null) {

            instance = new AuthManager();

        }

        return instance;
    }

    public void verifyPhone(String phoneNumber) {

        PhoneAuthProvider.getInstance()
        .verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, App.getActivity(), new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                AuthManager.getInstance().signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                e.printStackTrace();
                // todo: Handle the error
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
            }
        });
    }

    public void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(App.getActivity(), task -> {
            if (task.isSuccessful()) {

                FirebaseUser user = mAuth.getCurrentUser();

                HomeActivity.start(App.getAppContext());



            } else {

            }

        });
    }

    public void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token, session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(App.getActivity(), task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();


                    } else {

                    }
                });
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(App.getActivity(), task -> {

            if (task.isSuccessful()) {

                FirebaseUser user = task.getResult().getUser();

                user.getIdToken(false).addOnCompleteListener(task1 -> {

                    if(task1.isSuccessful()) {

                        String token = task1.getResult().getToken();

                    } else {

                        // todo: handle the error
                    }
                });

            } else {

                // todo: handle the error

                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                }
            }
        });
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(App.getActivity(), task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();

                    } else {

                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void logout() {

        TwitterCore.getInstance().getSessionManager().clearActiveSession();

        FirebaseAuth.getInstance().signOut();

        mGoogleSignInClient.signOut();

    }

    public GoogleSignInClient getGoogleSignInClient() {

        return mGoogleSignInClient;
    }

    public CallbackManager getFacebookCallbackManager() {
        return facebookCallbackManager;
    }
}
