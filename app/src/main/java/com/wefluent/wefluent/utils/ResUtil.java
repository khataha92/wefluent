package com.wefluent.wefluent.utils;

/**
 * Created by khalid on 4/20/18.
 */

public class ResUtil {

    public static String getString(int resId) {

        try {

            return App.getActiveContext().getString(resId);

        } catch (Exception e) {

            e.printStackTrace();

            return "";
        }
    }
}
