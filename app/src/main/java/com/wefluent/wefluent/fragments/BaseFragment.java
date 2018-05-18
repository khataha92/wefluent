package com.wefluent.wefluent.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by khalid taha on 4/22/15.
 * This is the base fragment for all the fragments
 */
public abstract class BaseFragment extends Fragment{

    private static final String TAG = BaseFragment.class.getSimpleName();

    private Runnable onActivityResult = null;


    protected void extractData() {


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        extractData();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public String getCustomTag() {

        return getClass().getSimpleName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (onActivityResult != null) {

            onActivityResult.run();

        }
    }
}
