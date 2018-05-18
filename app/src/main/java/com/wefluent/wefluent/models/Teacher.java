package com.wefluent.wefluent.models;

import android.view.View;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;
import com.wefluent.wefluent.enums.TeacherStatus;

/**
 * Created by khalid on 4/22/18.
 */

public class Teacher {

    String id;

    @PropertyName("activeRoom")
    FirebaseRoom activeRoom;

    TeacherStatus status;

    @PropertyName("name")
    String name;

    @SerializedName("image_url")
    @PropertyName("image_url")
    String imageUrl;

    boolean isLastItem = false;

    View.OnClickListener onCallClick;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FirebaseRoom getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(FirebaseRoom activeRoom) {
        this.activeRoom = activeRoom;
    }

    @Exclude
    public TeacherStatus getTeacherStatus() {

        return status;

    }

    public String getVisibleStatus() {

        switch (status) {

            case BUSY:
            case ONLINE:
            case OFFLINE:
                return status.name().toLowerCase();

            case INVISIBLE:
                return TeacherStatus.OFFLINE.name().toLowerCase();

            default:
                return TeacherStatus.OFFLINE.name().toLowerCase();
        }
    }

    @PropertyName("status")
    public String getStatus() {

        if (status == null) {

            return TeacherStatus.OFFLINE.name();
        }

        return status.name().toLowerCase();
    }

    @PropertyName("status")
    public void setStatus(String status) {

        if (status == null) {

            this.status = TeacherStatus.OFFLINE;

        } else {

            this.status = TeacherStatus.getValueOf(status);

        }
    }

    public boolean isLastItem() {
        return isLastItem;
    }

    public void setLastItem(boolean lastItem) {
        isLastItem = lastItem;
    }

    public void setOnCallClick(View.OnClickListener onCallClick) {
        this.onCallClick = onCallClick;
    }

    public View.OnClickListener getOnCallClick() {
        return onCallClick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    @PropertyName("image_url")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
