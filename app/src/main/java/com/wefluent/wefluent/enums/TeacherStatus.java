package com.wefluent.wefluent.enums;

/**
 * Created by khalid on 4/22/18.
 */

public enum TeacherStatus {
    ONLINE, OFFLINE, INVISIBLE, BUSY;

    public static TeacherStatus getValueOf(String name) {

        for(TeacherStatus status: TeacherStatus.values()) {

            if(status.name().toLowerCase().equalsIgnoreCase(name)) {

                return status;
            }
        }

        return OFFLINE;
    }
}
