package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool
 */
public class StatData implements Serializable {


    /**
     * data : []
     * total : 1
     */

    private double rate;
    private List<StatInfo> data;

    public List<StatInfo> getData() {
        return data;
    }

    public void setData(List<StatInfo> data) {
        this.data = data;
    }
}
