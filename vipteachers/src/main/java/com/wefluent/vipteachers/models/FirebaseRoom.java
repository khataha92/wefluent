package com.wefluent.vipteachers.models;

import com.google.firebase.database.PropertyName;

/**
 * Created by khalid on 4/22/18.
 */

public class FirebaseRoom {

    private static final String KEY_NAME = "name";
    private static final String KEY_STUDENT_ID = "studentId";

    @PropertyName("studnet_name")
    String studentName;

    @PropertyName("studnet_image")
    String studenImage;

    @PropertyName(KEY_NAME)
    String name;

    @PropertyName(KEY_STUDENT_ID)
    String studentId;

    @PropertyName(KEY_NAME)
    public String getName() {
        return name;
    }

    @PropertyName(KEY_NAME)
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName(KEY_STUDENT_ID)
    public String getStudentId() {
        return studentId;
    }

    @PropertyName(KEY_STUDENT_ID)
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @PropertyName("studnet_image")
    public void setStudenImage(String studenImage) {
        this.studenImage = studenImage;
    }

    @PropertyName("studnet_name")
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @PropertyName("studnet_image")
    public String getStudenImage() {
        return studenImage;
    }

    @PropertyName("studnet_name")
    public String getStudentName() {
        return studentName;
    }

    public void set(String key, String value) {

        switch (key) {

            case KEY_NAME:

                setName(value);

                break;

            case KEY_STUDENT_ID:

                setStudentId(value);

                break;
        }
    }
}
