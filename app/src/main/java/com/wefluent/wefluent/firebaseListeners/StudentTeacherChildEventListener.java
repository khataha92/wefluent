package com.wefluent.wefluent.firebaseListeners;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableType;
import com.wefluent.wefluent.models.Teacher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid on 5/16/18.
 */

public class StudentTeacherChildEventListener implements ChildEventListener {

    private static Map<Teacher, StudentTeacherChildEventListener> mapOfListeners = new HashMap<>();

    public static final String ROOM_NAME = "name";

    Teacher teacher;

    private StudentTeacherChildEventListener(Teacher teacher) {

        this.teacher = teacher;
    }

    public static StudentTeacherChildEventListener getInstance(Teacher teacher) {

        StudentTeacherChildEventListener listener = mapOfListeners.get(teacher);

        if(listener == null) {

            listener = new StudentTeacherChildEventListener(teacher);

            mapOfListeners.put(teacher, listener);
        }


        return listener;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        String key = dataSnapshot.getKey();

        switch (key) {

            case ROOM_NAME:

                String value = dataSnapshot.getValue(String.class);

                if(value != null && !value.equalsIgnoreCase("NULL")) {

                    EventObservable.withType(ObservableType.UPCOMING_CALL).notifyData(value);

                } else if(value == null || value.equalsIgnoreCase("Null") || value.equalsIgnoreCase("CANCEL")) {

                    EventObservable.withType(ObservableType.TERMINATE_CALL).notifyData(teacher.getId());
                }

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

}
