package com.wefluent.wefluent.controllers;

import com.khataha.twilio.activities.VideoCallActivity;
import com.khataha.twilio.observable.EventObservable;
import com.khataha.twilio.observable.ObservableModel;
import com.khataha.twilio.observable.ObservableType;
import com.wefluent.wefluent.enums.TeacherStatus;
import com.wefluent.wefluent.fragments.LiveFragment;
import com.wefluent.wefluent.managers.FirebaseManager;
import com.wefluent.wefluent.models.Teacher;
import com.wefluent.wefluent.utils.App;
import com.wefluent.wefluent.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by khalid on 4/23/18.
 */

public class LiveFragmentController implements Observer{

    LiveFragment liveFragment;

    public LiveFragmentController(LiveFragment liveFragment) {

        this.liveFragment = liveFragment;

        EventObservable.addObservabls(this, ObservableType.TEACHER_ADDED, ObservableType.TEACHER_CHANGED, ObservableType.UPCOMING_CALL, ObservableType.TERMINATE_CALL);
    }

    public void getTeachers() {

        FirebaseManager.getInstance().getTeachers();
    }

    @Override
    public void update(Observable o, Object arg) {

        ObservableModel model = (ObservableModel) arg;

        switch (model.getType()) {

            case TEACHER_ADDED: {

                Teacher teacher = (Teacher) model.getData();

                liveFragment.addTeacher(teacher);

                break;

            }

            case TEACHER_CHANGED: {

                Teacher teacher = (Teacher) model.getData();

                liveFragment.updateTeacher(teacher);

                break;
            }

            case UPCOMING_CALL:

                String roomName = model.getData().toString();

                if(roomName != null && !roomName.equalsIgnoreCase("NULL")) {

                    VideoCallActivity.start(App.getActiveContext(), roomName);

                }

                break;

            case TERMINATE_CALL:

                Teacher teacher = (Teacher) model.getData();

                UIUtil.hideCallDialog();

                FirebaseManager.getInstance().removeCallListener(teacher);

                break;

        }

    }

    private List<Teacher> getTeachersByStatus(TeacherStatus status) {

        List<Teacher> onlineTeachers = new ArrayList<>();

        for(Teacher teacher: liveFragment.getTeachers()) {

            if(teacher.getTeacherStatus() == status) {

                onlineTeachers.add(teacher);
            }
        }

        return onlineTeachers;
    }

    public List<Teacher> getOnlineTeachers() {

        return getTeachersByStatus(TeacherStatus.ONLINE);
    }

    public List<Teacher> getBusyTeachers() {

        return getTeachersByStatus(TeacherStatus.BUSY);
    }

    public List<Teacher> getOfflineTeachers() {

        List<Teacher> teachers = new ArrayList<>();

        teachers.addAll(getTeachersByStatus(TeacherStatus.OFFLINE));

        teachers.addAll(getTeachersByStatus(TeacherStatus.INVISIBLE));

        return teachers;
    }

    public void orderTeachersList(List<Teacher> teachers) {

        List<Teacher> onlineTeachers = getOnlineTeachers();

        List<Teacher> busyTeachers = getBusyTeachers();

        List<Teacher> offlineTeachers = getOfflineTeachers();

        teachers.clear();

        teachers.addAll(onlineTeachers);

        teachers.addAll(busyTeachers);

        teachers.addAll(offlineTeachers);
    }

    public void deleteObservers() {

        EventObservable.deleteObservabls(this, ObservableType.TEACHER_ADDED, ObservableType.TEACHER_CHANGED);
    }
}
