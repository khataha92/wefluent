package com.khataha.twilio.utils;

import android.app.*;
import android.app.Dialog;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.khataha.twilio.R;

/**
 * Created by khalid on 5/3/18.
 */

public class TwilioUIUtil {

    private  static Dialog callDialog = null;

    public static void showCallDialog(String contactName, String imageUrl, Runnable onCallAccepted) {

        if(callDialog != null) {

            return;
        }

        callDialog = new android.app.Dialog(App.getInstance().getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        callDialog.setContentView(R.layout.call_dialog);

        callDialog.setCancelable(false);

        callDialog.findViewById(R.id.accept).setOnClickListener(v -> {

            if(onCallAccepted != null) {

                onCallAccepted.run();

                callDialog.dismiss();

                callDialog = null;
            }
        });

        TextView callerName = callDialog.findViewById(R.id.caller_name);

        ImageView callerImage = callDialog.findViewById(R.id.avatar);

        callerName.setText(contactName);

        Glide.with(App.getInstance().getActiveContext()).load(imageUrl).into(callerImage);

        callDialog.findViewById(R.id.reject).setOnClickListener(v -> {

            callDialog.dismiss();

            callDialog = null;
        });

        callDialog.show();

    }
}
