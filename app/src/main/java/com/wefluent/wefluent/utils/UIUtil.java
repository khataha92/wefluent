package com.wefluent.wefluent.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wefluent.wefluent.R;
import com.wefluent.wefluent.models.Teacher;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Created by khalid on 4/23/18.
 */

public class UIUtil {

    private  static Dialog callDialog = null;

    public static void removeShiftMode(BottomNavigationView view) {

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);

        try {

            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");

            shiftingMode.setAccessible(true);

            shiftingMode.setBoolean(menuView, false);

            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {

                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);

                item.setShiftingMode(false);

                item.setChecked(item.getItemData().isChecked());

            }

        } catch (NoSuchFieldException e) {

            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");

        } catch (IllegalAccessException e) {

            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");

        }
    }

    public static void showCallDialog(String contactName, String imageUrl, Runnable onCancel) {

        if(callDialog != null) {

            return;
        }

        callDialog = new android.app.Dialog(App.getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        callDialog.setContentView(R.layout.call_teacher_dialog);

        callDialog.setCancelable(false);

        TextView callerName = callDialog.findViewById(R.id.caller_name);

        ImageView callerImage = callDialog.findViewById(R.id.avatar);

        callerName.setText(contactName);

        Glide.with(App.getActiveContext()).load(imageUrl).into(callerImage);

        callDialog.findViewById(R.id.reject).setOnClickListener(v -> {

            callDialog.dismiss();

            callDialog = null;

            if(onCancel != null) {

                onCancel.run();
            }
        });

        callDialog.show();

    }

    public static void hideCallDialog() {

        if(callDialog != null) {

            callDialog.dismiss();

            callDialog = null;
        }
    }

    public static void showConfirmCallDialog(String teacherName, Runnable onConfirm) {

        Context context = App.getActiveContext();

        String title = context.getString(R.string.CONFIRM_CALL);

        String message = String.format(Locale.ENGLISH, context.getString(R.string.CONFIRM_CALL_MESSAGE), teacherName);

        String confirm = context.getString(R.string.CONFIRM).toUpperCase();

        String cancel = context.getString(R.string.CANCEL).toUpperCase();

        new AlertDialog.Builder(App.getActivity()).setTitle(title)
                .setMessage(message)
                .setPositiveButton(confirm, (dialog, which) -> {

                    if(onConfirm != null) {

                        onConfirm.run();
                    }

                })
                .setNegativeButton(cancel, null)
                .show();
    }

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

    public static void showCallDialog(Teacher teacher) {


    }
}
