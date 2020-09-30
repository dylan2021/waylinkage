package com.android.waylinkage.bean;

/**
 * Gool Lee
 */
public class UnitInfo {
    private String nameCn;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UnitInfo(int id, String index, String nameCn) {
        this.id = id;
        this.nameCn = nameCn;
    }


    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }
}
