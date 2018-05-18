package com.wefluent.wefluent.managers;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableType;
import com.wefluent.wefluent.firebaseListeners.StudentCallTeacherCompletionListener;
import com.wefluent.wefluent.firebaseListeners.StudentTeacherChildEventListener;
import com.wefluent.wefluent.models.FirebaseRoom;
import com.wefluent.wefluent.models.Teacher;

import static com.wefluent.wefluent.firebaseListeners.StudentTeacherChildEventListener.ROOM_NAME;

/**
 * Created by khalid on 4/22/18.
 */

public class FirebaseManager implements ChildEventListener {

    private static FirebaseManager instance;

    private static final String TEACHERS = "teachers";
    private static final String ACTIVE_CALL = "ACTIVE_CALL";
    private static final String STUDENT_ID = "studentId";
    private static final String STUDENT_NAME = "studnet_name";
    private static final String STUDENT_IMAGE = "studnet_image";


    private DatabaseReference mFirebaseDatabase;

    private FirebaseManager() {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

    }

    public void getTeachers() {

        mFirebaseDatabase.child(TEACHERS).addChildEventListener(this);
    }

    public void terminate() {

        mFirebaseDatabase.child(TEACHERS).removeEventListener(this);
    }

    public static FirebaseManager getInstance() {

        if (instance == null) {

            instance = new FirebaseManager();
        }

        return instance;
    }

    public void callTeacher(Teacher teacher) {

        FirebaseRoom firebaseRoom = new FirebaseRoom();

        FirebaseUser user = AuthManager.getInstance().getCurrentUser();

        firebaseRoom.setStudentName(user.getDisplayName());

        Uri photoUrl = user.getPhotoUrl();

        if(photoUrl != null) {

            firebaseRoom.setStudenImage(photoUrl.toString());
        }

        firebaseRoom.setStudentId(user.getUid());

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).child(STUDENT_IMAGE).setValue(user.getPhotoUrl().toString());

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).child(STUDENT_NAME).setValue(user.getDisplayName());

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).child(ROOM_NAME).setValue("NULL");

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).child(STUDENT_ID)
                .setValue(user.getUid()).addOnCompleteListener(StudentCallTeacherCompletionListener.getInstance(mFirebaseDatabase, teacher));
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        try {

            Teacher teacher = dataSnapshot.getValue(Teacher.class);

            if (teacher != null) {

                teacher.setId(dataSnapshot.getKey());
            }

            EventObservable.withType(ObservableType.TEACHER_ADDED).notifyData(teacher);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        try {

            Teacher teacher = dataSnapshot.getValue(Teacher.class);

            EventObservable.withType(ObservableType.TEACHER_CHANGED).notifyData(teacher);

        } catch (Exception e) {

            e.printStackTrace();

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

    public void removeCallListener(Teacher teacher) {

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).removeEventListener(StudentTeacherChildEventListener.getInstance(teacher));

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).child(ROOM_NAME).setValue("NULL");
    }
}
