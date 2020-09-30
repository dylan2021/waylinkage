package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 *Gool Lee
 */
public class LinkInfo implements Serializable {

    public String linkName;
    public String linkUrl;
    public String linkTime;

    public LinkInfo(String linkName,String time) {
        this.linkName = linkName;
        this.linkTime = time;
    }
}
