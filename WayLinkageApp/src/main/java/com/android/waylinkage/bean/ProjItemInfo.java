package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 *Gool Lee
 */

public class ProjItemInfo implements Serializable {

    public String projectId;
    public String projectImg;
    public long updateTime;
    public Object listItemVOList;
    public String name;
    public String desc;
    public int type;

    public ProjItemInfo(String projectId, String name,int type) {
        this.projectId = projectId;
        this.name = name;
        this.type = type;
    }
}
