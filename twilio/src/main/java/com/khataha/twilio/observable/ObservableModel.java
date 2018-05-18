package com.khataha.twilio.observable;

/**
 * Created by khalid on 4/22/18.
 */

public class ObservableModel  <T>{

    ObservableType type;

    T data;

    public ObservableType getType() {
        return type;
    }

    public void setType(ObservableType type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
