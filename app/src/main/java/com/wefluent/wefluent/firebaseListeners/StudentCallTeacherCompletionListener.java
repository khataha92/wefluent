package com.wefluent.wefluent.firebaseListeners;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.wefluent.wefluent.managers.FirebaseManager;
import com.wefluent.wefluent.models.Teacher;
import com.wefluent.wefluent.utils.UIUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid on 5/16/18.
 */

public class StudentCallTeacherCompletionListener implements OnCompleteListener {

    private static Map<Teacher, StudentCallTeacherCompletionListener> mapOfListeners = new HashMap<>();

    private DatabaseReference mFirebaseDatabase = null;
    private Teacher teacher;
    private static final String TEACHERS = "teachers";
    private static final String ACTIVE_CALL = "ACTIVE_CALL";
    private static final String ROOM_NAME = "name";

    private StudentCallTeacherCompletionListener(DatabaseReference reference, Teacher teacher) {

        mFirebaseDatabase = reference;

        this.teacher = teacher;

    }

    public static StudentCallTeacherCompletionListener getInstance(DatabaseReference reference, Teacher teacher) {

        StudentCallTeacherCompletionListener listener = mapOfListeners.get(teacher);

        if(listener == null) {

            listener = new StudentCallTeacherCompletionListener(reference, teacher);

            mapOfListeners.put(teacher, listener);
        }

        return listener;

    }

    @Override
    public void onComplete(@NonNull Task task) {

        UIUtil.showCallDialog(teacher.getName(), teacher.getImageUrl(), () -> {

            FirebaseManager.getInstance().removeCallListener(teacher);

        });

        mFirebaseDatabase.child(TEACHERS).child(teacher.getId()).child(ACTIVE_CALL).addChildEventListener(StudentTeacherChildEventListener.getInstance(teacher));

    }
}
