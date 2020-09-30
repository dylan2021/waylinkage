package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool Lee
 */
public class ProcessorProgressInfo implements Serializable {

    private double actualPercentage;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private double completedPercentage;
    private double planPercentage;
    private double actualInvest;
    private double planInvest;
    private double invest;//计划产值
    private int planCount;
    private String realBeginDate;
    private String realEndDate;
    private String name;
    private String status;

    public double getInvest() {
        return invest;
    }

    public void setInvest(double invest) {
        this.invest = invest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String planBeginDate;
    private String planEndDate;
    private BizProcessorFinBean bizProcessorFin;

    public double getCompletedPercentage() {
        return completedPercentage;
    }

    public void setCompletedPercentage(double completedPercentage) {
        this.completedPercentage = completedPercentage;
    }

    public double getActualPercentage() {
        return actualPercentage;
    }

    public void setActualPercentage(double actualPercentage) {
        this.actualPercentage = actualPercentage;
    }

    public double getPlanPercentage() {
        return planPercentage;
    }

    public void setPlanPercentage(double planPercentage) {
        this.planPercentage = planPercentage;
    }

    public double getActualInvest() {
        return actualInvest;
    }

    public void setActualInvest(double actualInvest) {
        this.actualInvest = actualInvest;
    }

    public double getPlanInvest() {
        return planInvest;
    }

    public void setPlanInvest(double planInvest) {
        this.planInvest = planInvest;
    }

    public int getPlanCount() {
        return planCount;
    }

    public void setPlanCount(int planCount) {
        this.planCount = planCount;
    }

    public String getRealBeginDate() {
        return realBeginDate;
    }

    public void setRealBeginDate(String realBeginDate) {
        this.realBeginDate = realBeginDate;
    }

    public String getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(String realEndDate) {
        this.realEndDate = realEndDate;
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

    public BizProcessorFinBean getBizProcessorFin() {
        return bizProcessorFin;
    }

    public void setBizProcessorFin(BizProcessorFinBean bizProcessorFin) {
        this.bizProcessorFin = bizProcessorFin;
    }

    public static class BizProcessorFinBean {
        private List<FileInfo> pic;
        private List<FileInfo> attachment;

        public List<FileInfo> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<FileInfo> attachment) {
            this.attachment = attachment;
        }

        public List<FileInfo> getPic() {
            return pic;
        }

        public void setPic(List<FileInfo> pic) {
            this.pic = pic;
        }
    }
}
