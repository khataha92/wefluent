package com.wefluent.vipteachers.managers;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.wefluent.vipteachers.R;
import com.wefluent.vipteachers.utils.App;

import java.util.concurrent.TimeUnit;


/**
 * Created by khalid on 3/24/18.
 */

public class AuthManager {

    private static AuthManager instance = null;

    private static final String TAG = AuthManager.class.getSimpleName();

    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    Runnable afterLoginListener = null;

    GoogleSignInOptions googleSignInOptions;

    private AuthManager() {

        FirebaseApp.initializeApp(App.getActiveContext());

        mAuth = FirebaseAuth.getInstance();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(App.getAppContext(), googleSignInOptions);

    }

    public void init() {

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

                        if(afterLoginListener != null) {

                            afterLoginListener.run();
                        }

                    } else {

                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void logout() {

        FirebaseAuth.getInstance().signOut();

        mGoogleSignInClient.signOut();

    }

    public GoogleSignInClient getGoogleSignInClient() {

        return mGoogleSignInClient;
    }

    private String getString(int resId) {

        try {

            return App.getActiveContext().getString(resId);

        } catch (Exception e){

            return "";
        }
    }

    public void setAfterLoginListener(Runnable afterLoginListener) {
        this.afterLoginListener = afterLoginListener;
    }
}
