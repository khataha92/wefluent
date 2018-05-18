package com.wefluent.vipteachers.managers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khataha.twilio.activities.VideoCallActivity;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableType;
import com.khataha.twilio.utils.TwilioUIUtil;
import com.wefluent.vipteachers.enums.TeacherStatus;
import com.wefluent.vipteachers.firebaseListeners.RoomChangedListener;
import com.wefluent.vipteachers.models.FirebaseRoom;
import com.wefluent.vipteachers.models.Teacher;
import com.wefluent.vipteachers.utils.App;
import com.wefluent.vipteachers.utils.UIUtil;

/**
 * Created by khalid on 4/22/18.
 */

public class FirebaseManager {

    private static FirebaseManager instance;

    private static final String TEACHERS = "teachers";
    private static final String ACTIVE_CALL = "ACTIVE_CALL";
    private static final String STUDENT_ID = "studentId";
    private static final String NAME = "name";

    private String roomName = "";
    private DatabaseReference mFirebaseDatabase;

    private FirebaseManager() {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void getTeachers() {

        mFirebaseDatabase.child(TEACHERS).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try {

                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    teacher.setId(dataSnapshot.getKey());

                    EventObservable.withType(ObservableType.TEACHER_ADDED).notifyData(teacher);

                } catch (Exception e) {

                    e.printStackTrace();

                    // log event to know if the process is not working
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                try {

                    Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    teacher.setId(dataSnapshot.getKey());

                    EventObservable.withType(ObservableType.TEACHER_CHANGED).notifyData(teacher);

                } catch (Exception e) {

                    e.printStackTrace();

                    // log event to know if the process is not working
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
        });
    }

    public static FirebaseManager getInstance() {

        if (instance == null) {

            instance = new FirebaseManager();
        }

        return instance;
    }

    public void addTeacher() {

        Teacher teacher = new Teacher();

        FirebaseUser user = AuthManager.getInstance().getCurrentUser();

        teacher.setId(user.getUid());

        teacher.setName(user.getDisplayName());

        if (user.getPhotoUrl() != null) {

            String image = user.getPhotoUrl().toString();

            String[] tokens = image.split("/");

            String img = "";

            for (int i = 0; i < tokens.length; i++) {

                if (i != tokens.length - 2) {

                    img += tokens[i];
                }

                if (i != tokens.length - 1) {

                    img += "/";
                }
            }

            teacher.setImageUrl(img);

        }

        FirebaseRoom activeCall = new FirebaseRoom();

        activeCall.setName("NULL");

        activeCall.setStudentId("NULL");

        teacher.setActiveRoom(activeCall);

        teacher.setStatus(TeacherStatus.OFFLINE.name());

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).setValue(teacher).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                SessionManager.getInstance().setCurrentTeacher(teacher);

                mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).removeEventListener(RoomChangedListener.getInstance(mFirebaseDatabase));

                mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).addChildEventListener(RoomChangedListener.getInstance(mFirebaseDatabase));

                VideoCallActivity.start(App.getActiveContext());

            }

        });

    }
}
