package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 *Gool
 */
public class SafyInfo implements Serializable {


    private String type;
    private String value;
    private String time;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
