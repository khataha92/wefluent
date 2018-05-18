package com.wefluent.wefluent.managers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wefluent.wefluent.R;
import com.wefluent.wefluent.activities.HomeActivity;
import com.wefluent.wefluent.fragments.BaseFragment;
import com.wefluent.wefluent.utils.App;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid on 4/23/18.
 */

public class FragmentManager {


    public static <T extends BaseFragment> void showFragment(Class<T> clazz) {

        showFragment(clazz, new Bundle());
    }

    public static <T extends BaseFragment> void showFragment(Class<T> clazz, Bundle bundle) {

        showFragment(clazz, bundle, new HashMap<>());

    }

    public static <T extends BaseFragment> void showFragment(Class<T> clazz, Bundle bundle, Map<String, Object> args) {

        try {

            BaseFragment baseFragment = createFragmentAndPutData(clazz, bundle, args);

            if (baseFragment != null) {

                replaceFragment(baseFragment);

            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private static <T extends BaseFragment> BaseFragment createFragmentAndPutData(Class<T> clazz, Bundle bundle, Map<String, Object> args) {

        BaseFragment baseFragment = null;

        try {

            baseFragment = clazz.newInstance();

            baseFragment.setArguments(bundle);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return baseFragment;
    }

    private static void replaceFragment(final BaseFragment newFragment) {

        if (App.getActivity() instanceof HomeActivity && !App.getActivity().isFinishing()) {

            InputMethodManager inputManager = (InputMethodManager) App.getActiveContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            HomeActivity activity = (HomeActivity) App.getActivity();

            View v = activity.getCurrentFocus();

            if (v != null && inputManager != null) {

                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }

            BaseFragment currentFragment = (BaseFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            FragmentTransaction tr = activity.getSupportFragmentManager().beginTransaction();

            tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            tr.add(R.id.fragment_container, newFragment, newFragment.getCustomTag());

            tr.setReorderingAllowed(true);

            if (currentFragment != null) {

                currentFragment.onDetach();
            }

            tr.commitAllowingStateLoss();

        }

    }
}
