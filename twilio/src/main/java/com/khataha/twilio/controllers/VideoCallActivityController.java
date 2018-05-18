package com.khataha.twilio.controllers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.khataha.twilio.R;
import com.khataha.twilio.databinding.ActivityVideoCallBinding;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableModel;
import com.khataha.twilio.observable.ObservableType;
import com.khataha.twilio.utils.App;
import com.khataha.twilio.utils.AppPref;
import com.khataha.twilio.utils.Constants;
import com.khataha.twilio.utils.Dialog;
import com.khataha.twilio.utils.VideoConfig;
import com.twilio.video.AudioCodec;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.RoomState;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoTrack;

import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by khalid on 3/24/18.
 */

public class VideoCallActivityController implements Observer{

    private AudioManager audioManager;

    private int previousAudioMode;

    private boolean previousMicrophoneMute;

    private String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzU2NjkwNWZmZWQ1NTFlYTlmMjExOTYxZDNjOTlmMzhiLTE1MjYxNjAzNTgiLCJpc3MiOiJTSzU2NjkwNWZmZWQ1NTFlYTlmMjExOTYxZDNjOTlmMzhiIiwic3ViIjoiQUMzMTYxOGVmNWI0ZmEwNWU3Y2M1ZWI5Y2NmNGFmYTJlMSIsImV4cCI6MTUyNjE2Mzk1OCwiZ3JhbnRzIjp7ImlkZW50aXR5Ijoic3R1ZGVudCIsInZpZGVvIjp7fX19.g9nOFHRL6Zl9ouSMCMnLQQFt9MNM70t-lL0TBvsWISE";

    public LocalAudioTrack localAudioTrack;

    private AudioCodec audioCodec;

    private EncodingParameters encodingParameters;

    private Room room;

    ActivityVideoCallBinding binding;

    private boolean disconnectedFromOnDestroy;

    private String remoteParticipantIdentity;

    private AlertDialog connectDialog;

    public VideoCallActivityController(ActivityVideoCallBinding binding) {

        this.binding = binding;

        audioManager = (AudioManager) App.getInstance().getActiveContext().getSystemService(Context.AUDIO_SERVICE);

        if(audioManager != null) {

            audioManager.setSpeakerphoneOn(true);
        }
    }

    private void requestAudioFocus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes playbackAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build();

            AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes).setAcceptsDelayedFocusGain(true).build();

            audioManager.requestAudioFocus(focusRequest);

        } else {

            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch
            requestAudioFocus();
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            /*
             * Always disable microphone mute during a WebRTC call.
             */
            previousMicrophoneMute = audioManager.isMicrophoneMute();
            audioManager.setMicrophoneMute(false);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
            audioManager.setMicrophoneMute(previousMicrophoneMute);
        }
    }

    public void connectToRoom(String roomName) {

        configureAudio(true);

        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions.Builder(accessToken).roomName(roomName);

        if (localAudioTrack != null) {
            connectOptionsBuilder.audioTracks(Collections.singletonList(localAudioTrack));
        }

        /*
         * Set the preferred audio and video codec for media.
         */
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));

        /*
         * Set the sender side encoding parameters.
         */
        connectOptionsBuilder.encodingParameters(encodingParameters);

        room = Video.connect(App.getInstance().getActiveContext(), connectOptionsBuilder.build(), roomListener());

        setDisconnectAction();
    }

    private void setDisconnectAction() {
        binding.connectActionFab.setImageDrawable(ContextCompat.getDrawable(App.getInstance().getActiveContext(),
                R.drawable.ic_call_end_white_24px));
        binding.connectActionFab.show();
        binding.connectActionFab.setOnClickListener(disconnectClickListener());
    }

    private View.OnClickListener disconnectClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Disconnect from room
                 */
                if (room != null) {
                    room.disconnect();
                }

                intializeUI();
            }
        };
    }

    private void addRemoteParticipantVideo(VideoTrack videoTrack) {

        binding.primaryVideoView.setMirror(false);

        videoTrack.addRenderer(binding.primaryVideoView);

    }


    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {

                binding.videoStatusTextview.setText("onAudioTrackPublished");
            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {

                binding.videoStatusTextview.setText("onAudioTrackUnpublished");
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {

                binding.videoStatusTextview.setText("onDataTrackPublished");
            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {

                binding.videoStatusTextview.setText("onDataTrackUnpublished");
            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {

                binding.videoStatusTextview.setText("onVideoTrackPublished");
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                binding.videoStatusTextview.setText("onVideoTrackUnpublished");
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {

                binding.videoStatusTextview.setText("onAudioTrackSubscribed");
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {

                binding.videoStatusTextview.setText("onAudioTrackUnsubscribed");
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {

                binding.videoStatusTextview.setText("onAudioTrackSubscriptionFailed");
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {

                binding.videoStatusTextview.setText("onDataTrackSubscribed");
            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {

                binding.videoStatusTextview.setText("onDataTrackUnsubscribed");
            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {

                binding.videoStatusTextview.setText("onDataTrackSubscriptionFailed");
            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {

                binding.videoStatusTextview.setText("onVideoTrackSubscribed");
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {

                binding.videoStatusTextview.setText("onVideoTrackUnsubscribed");
                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {

                binding.videoStatusTextview.setText("onVideoTrackSubscriptionFailed");
                Snackbar.make(binding.connectActionFab,
                        String.format("Failed to subscribe to %s video track",
                                remoteParticipant.getIdentity()),
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }


    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                binding.videoStatusTextview.setText("Connected to " + room.getName());
                App.getInstance().getActivity().setTitle(room.getName());

                for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                    addRemoteParticipant(remoteParticipant);
                    break;
                }
            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                binding.videoStatusTextview.setText("Failed to connect");
                configureAudio(false);
                intializeUI();
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                binding.videoStatusTextview.setText("Disconnected from " + room.getName());
                VideoCallActivityController.this.room = null;
                // Only reinitialize the UI if disconnect was not called from onDestroy()
                if (!disconnectedFromOnDestroy) {
                    configureAudio(false);
                    intializeUI();
                }
            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);

            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);
            }

            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a FirebaseRoom is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("TAG", "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a FirebaseRoom is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("TAG", "onRecordingStopped");
            }
        };
    }

    public void pause() {

    }

    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {

        remoteParticipantIdentity = remoteParticipant.getIdentity();

        binding.videoStatusTextview.setText("RemoteParticipant "+ remoteParticipantIdentity + " joined");

        if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);


            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                addRemoteParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());
            }
        }

        remoteParticipant.setListener(remoteParticipantListener());
    }

    public void destroy() {

        if (room != null && room.getState() != RoomState.DISCONNECTED) {

            room.disconnect();

            disconnectedFromOnDestroy = true;

        }

        if (localAudioTrack != null) {

            localAudioTrack.release();

            localAudioTrack = null;

        }
    }

    private AudioCodec getAudioCodecPreference(String key, String defaultValue) {

        final String audioCodecName = AppPref.getString(key, defaultValue);

        switch (audioCodecName) {

            case IsacCodec.NAME:

                return new IsacCodec();

            case OpusCodec.NAME:

                return new OpusCodec();

            case PcmaCodec.NAME:

                return new PcmaCodec();

            case PcmuCodec.NAME:

                return new PcmuCodec();

            case G722Codec.NAME:

                return new G722Codec();

            default:
                return new OpusCodec();
        }
    }

    private EncodingParameters getEncodingParameters() {

        final int maxAudioBitrate = Integer.parseInt(AppPref.getString(VideoConfig.PREF_SENDER_MAX_AUDIO_BITRATE, VideoConfig.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT));

        final int maxVideoBitrate = Integer.parseInt(AppPref.getString(VideoConfig.PREF_SENDER_MAX_VIDEO_BITRATE, VideoConfig.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT));

        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
    }

    public void initializeComponents() {

        audioCodec = getAudioCodecPreference(VideoConfig.PREF_AUDIO_CODEC, VideoConfig.PREF_AUDIO_CODEC_DEFAULT);

        encodingParameters = getEncodingParameters();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {

        videoTrack.removeRenderer(binding.primaryVideoView);

    }

    public boolean checkPermissionForMicrophone(){

        int resultMic = ContextCompat.checkSelfPermission(App.getInstance().getActiveContext(), Manifest.permission.RECORD_AUDIO);

        return resultMic == PackageManager.PERMISSION_GRANTED;

    }

    public void intializeUI() {
        binding.connectActionFab.setImageDrawable(ContextCompat.getDrawable(App.getInstance().getActiveContext(), R.drawable.ic_video_call_white_24dp));

        binding.connectActionFab.show();

        binding.connectActionFab.setOnClickListener(connectActionClickListener());

        binding.muteActionFab.show();

        binding.muteActionFab.setOnClickListener(muteClickListener());

    }

    private View.OnClickListener muteClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (localAudioTrack != null) {

                    boolean enable = !localAudioTrack.isEnabled();

                    localAudioTrack.enable(enable);

                    int icon = enable ? R.drawable.ic_mic_white_24dp : R.drawable.ic_mic_off_black_24dp;

                    binding.muteActionFab.setImageDrawable(ContextCompat.getDrawable(App.getInstance().getActiveContext(), icon));

                }
            }
        };
    }

    private View.OnClickListener connectActionClickListener() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showConnectDialog();
            }
        };
    }

    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                intializeUI();

                connectDialog.dismiss();
            }
        };
    }

    private void showConnectDialog() {
        EditText roomEditText = new EditText(App.getInstance().getActiveContext());

        connectDialog = Dialog.createConnectDialog(roomEditText, connectClickListener(roomEditText), cancelConnectDialogClickListener(), App.getInstance().getActiveContext());

        connectDialog.show();
    }

    private DialogInterface.OnClickListener connectClickListener(final EditText roomEditText) {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                connectToRoom(roomEditText.getText().toString());

            }
        };
    }

    public void createAudioTrack() {

        localAudioTrack = LocalAudioTrack.create(App.getInstance().getActiveContext(), true, Constants.LOCAL_AUDIO_TRACK_NAME);

        binding.primaryVideoView.setMirror(false);

    }

    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {

        binding.videoStatusTextview.setText("RemoteParticipant " + remoteParticipant.getIdentity() + " left.");

        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {

            return;
        }

        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {

            RemoteVideoTrackPublication remoteVideoTrackPublication = remoteParticipant.getRemoteVideoTracks().get(0);

            if (remoteVideoTrackPublication.isTrackSubscribed()) {

                removeParticipantVideo(remoteVideoTrackPublication.getRemoteVideoTrack());

            }
        }

    }

    @Override
    public void update(Observable o, Object arg) {


    }
}
