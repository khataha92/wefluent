package com.wefluent.vipteachers.utils;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by khalid on 4/12/18.
 */

public class AppPref {

    private static SharedPreferences preferences;

    public static void initialize() {

        preferences = PreferenceManager.getDefaultSharedPreferences(App.getActiveContext());
    }

    public static String getString(String key, String defaultValue) {

        return preferences.getString(key, defaultValue);
    }
}
