package com.volive.whitecab.util;

import java.util.Observable;

public class MyObservab extends Observable {
    private String name = "";

    /**
     * @return the value
     */
    public String getValue() {
        return name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String name) {
        this.name = name;
        setChanged();
        notifyObservers();
    }
}
