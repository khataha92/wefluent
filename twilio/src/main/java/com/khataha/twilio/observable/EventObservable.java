package com.khataha.twilio.observable;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by khalid on 4/22/18.
 */

public class EventObservable extends Observable{

    private static Map<ObservableType, EventObservable> map = new HashMap<>();

    private ObservableModel<Object> model;

    public static EventObservable withType(@NotNull ObservableType observableType) {

        EventObservable observable = map.get(observableType);

        if(observable == null) {

            observable = new EventObservable();

            observable.model.setType(observableType);

            map.put(observableType, observable);
        }

        return observable;
    }

    public static void addObservabls(Observer observer, ObservableType ... observableTypes) {

        if(observableTypes == null || observer == null) {

            return;
        }

        for(ObservableType observableType: observableTypes) {

            withType(observableType).addObserver(observer);

        }
    }

    public static void deleteObservabls(Observer observer, ObservableType ... observableTypes) {

        if(observableTypes == null || observer == null) {

            return;
        }

        for(ObservableType observableType: observableTypes) {

            withType(observableType).deleteObserver(observer);
        }
    }

    private EventObservable () {

        model = new ObservableModel<>();
    }

    public <T> void notifyData(T data) {

        model.setData(data);

        notifyEvent(model);

    }

    private void notifyEvent(ObservableModel model) {

        setChanged();

        notifyObservers(model);
    }
}
