package com.wefluent.vipteachers.utils;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.wefluent.vipteachers.R;
import com.wefluent.vipteachers.managers.FirebaseManager;
import com.wefluent.vipteachers.managers.SessionManager;

/**
 * Created by khalid on 4/23/18.
 */

public class UIUtil {

    public static void showLoadingView(View root) {

        if(!(root instanceof ViewGroup)) {

            return;
        }

        View loadingView = root.findViewById(R.id.loading_view);

        if(loadingView == null) {

            loadingView = LayoutInflater.from(App.getAppContext()).inflate(R.layout.loading_view, null);
        }

        loadingView.setVisibility(View.VISIBLE);

        ((ViewGroup)root).addView(loadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public static void hideLoadingView(View root) {

        View loadingView = root.findViewById(R.id.loading_view);

        if(loadingView != null) {

            loadingView.setVisibility(View.GONE);
        }
    }

}
