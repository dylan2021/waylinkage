package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 * Gool Lee
 */
public class PlanChangedInfo implements Serializable {
    private int id;
    private String name;
    private String invest;
    private String planBeginDate;
    private String planEndDate;

    public PlanChangedInfo(int id, String name, String invest, String planBeginDate, String planEndDate) {
        this.id = id;
        this.name = name;
        this.invest = invest;
        this.planBeginDate = planBeginDate;
        this.planEndDate = planEndDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInvest() {
        return invest;
    }

    public void setInvest(String invest) {
        this.invest = invest;
    }

    public String getPlanBeginDate() {
        return planBeginDate;
    }

    public void setPlanBeginDate(String planBeginDate) {
        this.planBeginDate = planBeginDate;
    }

    public String getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }
}
