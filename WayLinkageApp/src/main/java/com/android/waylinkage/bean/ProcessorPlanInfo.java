package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 * Gool Lee
 */
public class ProcessorPlanInfo implements Serializable {
    private int id;
    private String createTime;
    private String updateTime;
    private String invest;
    private String name;
    private String actualProgress;
    private int period;
    private int planPeriod;
    private String planInvest;
    private int planPercentage;
    private int confirmed;

    public int getPlanPeriod() {
        return planPeriod;
    }

    public void setPlanPeriod(int planPeriod) {
        this.planPeriod = planPeriod;
    }

    public String getPlanInvest() {
        return planInvest;
    }

    public void setPlanInvest(String planInvest) {
        this.planInvest = planInvest;
    }

    private String actualPeriod;
    private String actualInvest;
    private int actualPercentage;
    private int reportPercentage;
    private String reportInvest;
    private String planBeginDate;
    private String lastReportDate;

    public int getReportPercentage() {
        return reportPercentage;
    }

    public void setReportPercentage(int reportPercentage) {
        this.reportPercentage = reportPercentage;
    }

    public String getReportInvest() {
        return reportInvest;
    }

    public void setReportInvest(String reportInvest) {
        this.reportInvest = reportInvest;
    }

    public String getActualInvest() {
        return actualInvest;
    }

    public void setActualInvest(String actualInvest) {
        this.actualInvest = actualInvest;
    }

    public int getPlanPercentage() {
        return planPercentage;
    }

    public void setPlanPercentage(int planPercentage) {
        this.planPercentage = planPercentage;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public String getLastReportDate() {
        return lastReportDate;
    }

    public void setLastReportDate(String lastReportDate) {
        this.lastReportDate = lastReportDate;
    }

    public String getActualPeriod() {
        return actualPeriod;
    }

    public void setActualPeriod(String actualPeriod) {
        this.actualPeriod = actualPeriod;
    }

    public String getActualProgress() {
        return actualProgress;
    }

    public void setActualProgress(String actualProgress) {
        this.actualProgress = actualProgress;
    }

    public int getActualPercentage() {
        return actualPercentage;
    }

    public void setActualPercentage(int actualPercentage) {
        this.actualPercentage = actualPercentage;
    }

    private String planEndDate;
    private BizProcessorBean bizProcessor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getInvest() {
        return invest;
    }

    public void setInvest(String invest) {
        this.invest = invest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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

    public BizProcessorBean getBizProcessor() {
        return bizProcessor;
    }

    public void setBizProcessor(BizProcessorBean bizProcessor) {
        this.bizProcessor = bizProcessor;
    }

    public static class BizProcessorBean {


        private int id;
        private int buildSiteId;
        private String type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getBuildSiteId() {
            return buildSiteId;
        }

        public void setBuildSiteId(int buildSiteId) {
            this.buildSiteId = buildSiteId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
