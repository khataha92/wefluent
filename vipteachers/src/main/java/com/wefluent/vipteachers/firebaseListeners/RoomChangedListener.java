package com.wefluent.vipteachers.firebaseListeners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableType;
import com.khataha.twilio.utils.TwilioUIUtil;
import com.wefluent.vipteachers.managers.SessionManager;
import com.wefluent.vipteachers.models.FirebaseRoom;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid on 5/17/18.
 */

public class RoomChangedListener implements ChildEventListener {

    private static RoomChangedListener instance = null;

    DatabaseReference mFirebaseDatabase;

    private RoomChangedListener(DatabaseReference reference) {

        mFirebaseDatabase = reference;

    }

    public static RoomChangedListener getInstance(DatabaseReference reference) {

        if(instance == null) {

            instance = new RoomChangedListener(reference);
        }

        return instance;
    }
    private static final String TEACHERS = "teachers";
    private static final String STUDENT_ID = "studentId";
    private static final String ACTIVE_CALL = "ACTIVE_CALL";
    private static final String NAME = "name";
    private static String roomName = "";


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        String key = dataSnapshot.getKey();

        String value = dataSnapshot.getValue(String.class);

        SessionManager.getInstance().getCurrentTeacher().getActiveRoom().set(key, value);

        switch (key) {

            case STUDENT_ID:

                mFirebaseDatabase.child(TEACHERS).child(SessionManager.getInstance().getCurrentTeacher().getId())
                        .child(ACTIVE_CALL).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {

                            FirebaseRoom room = dataSnapshot.getValue(FirebaseRoom.class);

                            if(room != null && !room.getName().equalsIgnoreCase("NULL") && !room.getName().equalsIgnoreCase(roomName)) {

                                TwilioUIUtil.showCallDialog(room.getStudentName(), room.getStudenImage(), () -> acceptStudentCall(value));

                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void acceptStudentCall(String studentId) {

        String teacherId = SessionManager.getInstance().getCurrentTeacher().getId();

        roomName = studentId + "_" + teacherId;

        mFirebaseDatabase.child(TEACHERS).child(SessionManager.getInstance().getCurrentTeacher().getId()).child(ACTIVE_CALL).child(NAME).setValue(roomName).addOnCompleteListener(task -> {

            if(task.isSuccessful()) {

                EventObservable.withType(ObservableType.UPCOMING_CALL).notifyData(roomName);
            }

        });
    }
}
