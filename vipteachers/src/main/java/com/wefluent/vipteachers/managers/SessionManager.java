package com.wefluent.vipteachers.managers;

import com.wefluent.vipteachers.models.Teacher;

/**
 * Created by khalid on 4/30/18.
 */

public class SessionManager {

    private static SessionManager instance = null;

    private Teacher currentTeacher = null;

    private SessionManager() {
    }

    public static SessionManager getInstance() {

        if(instance == null) {

            instance = new SessionManager();
        }

        return instance;
    }

    public void setCurrentTeacher(Teacher currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    public Teacher getCurrentTeacher() {

        if(currentTeacher == null) {

            currentTeacher = new Teacher();
        }

        return currentTeacher;
    }
}
