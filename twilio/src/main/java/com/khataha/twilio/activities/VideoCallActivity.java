package com.khataha.twilio.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.khataha.twilio.R;
import com.khataha.twilio.controllers.VideoCallActivityController;
import com.khataha.twilio.databinding.ActivityVideoCallBinding;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableModel;
import com.khataha.twilio.observable.ObservableType;
import com.khataha.twilio.utils.App;
import com.khataha.twilio.utils.Constants;

import java.util.Observable;
import java.util.Observer;

public class VideoCallActivity extends AppCompatActivity implements Observer{

    ActivityVideoCallBinding binding;

    private static final String ROOM_NAME = "ROOM_NAME";

    private VideoCallActivityController controller;

    private static VideoCallActivity instance = null;

    String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        App.getInstance().setActivity(this);

        App.getInstance().onCreate();

        if(getIntent().getExtras() != null && getIntent().hasExtra(ROOM_NAME)) {

            roomName = getIntent().getStringExtra(ROOM_NAME);
        }

        EventObservable.addObservabls(this, ObservableType.UPCOMING_CALL);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call);
        
        controller = new VideoCallActivityController(binding);

        if (!controller.checkPermissionForMicrophone()) {

            requestPermissionForCameraAndMicrophone();

        } else {

            controller.createAudioTrack();
        }

        controller.intializeUI();

    }

    @Override
    protected void onPause() {

        controller.pause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        controller.destroy();

        instance = null;

        EventObservable.deleteObservabls(this, ObservableType.UPCOMING_CALL);

        super.onDestroy();
    }

    @Override
    protected  void onResume() {

        super.onResume();

        controller.initializeComponents();

        if(roomName != null) {

            controller.connectToRoom(roomName);
        }

    }

    private void requestPermissionForCameraAndMicrophone(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constants.CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == Constants.CAMERA_MIC_PERMISSION_REQUEST_CODE) {

            boolean micPermissionGranted = true;

            for (int grantResult : grantResults) {

                micPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (micPermissionGranted) {

                controller.createAudioTrack();

            }
        }
    }

    public static void start(Context context) {

        if(instance == null) {

            context.startActivity(new Intent(context, VideoCallActivity.class));

        }
    }


    public static void start(Context context, String roomToConnect) {

        if(instance == null) {

            Intent intent = new Intent(context, VideoCallActivity.class);

            intent.putExtra(ROOM_NAME, roomToConnect);

            context.startActivity(intent);

        }
    }

    @Override
    public void update(Observable o, Object arg) {

        ObservableModel model = (ObservableModel) arg;

        switch (model.getType()) {

            case UPCOMING_CALL:

                String roomName = model.getData().toString();

                if(roomName != null && !roomName.equals("NULL")) {

                    controller.connectToRoom(roomName);

                }

                break;
        }


    }

    public static void terminate() {

        if(instance != null) {

            instance.finish();
        }
    }

}
