package com.android.waylinkage.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Gool Lee
 */
public class AlarmInfo {

    /**
     * id : 8
     * content : 质量状态: 待整改
     * cleared : true
     * category : 2
     * severity : "危险的"
     * createTime : 2019-01-04 11:08:05
     * cleared : false
     * subObjectName : 半填半挖
     * objectId : 19
     * projectName : 博深高速公路
     * contractName : 博深t1项目标段
     * buildSiteName : 路基1
     * new : false
     */

    private int id;
    private String content;
    private String category;
    private String severity;
    private String createTime;
    private String updateTime;
    private String subObjectName;
    private int objectId;
    private String projectName;
    private String contractName;
    private String buildSiteName;
    @SerializedName("new")
    private boolean newX;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
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

    public String getSubObjectName() {
        return subObjectName;
    }

    public void setSubObjectName(String subObjectName) {
        this.subObjectName = subObjectName;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getBuildSiteName() {
        return buildSiteName;
    }

    public void setBuildSiteName(String buildSiteName) {
        this.buildSiteName = buildSiteName;
    }

    public boolean isNewX() {
        return newX;
    }

    public void setNewX(boolean newX) {
        this.newX = newX;
    }
}
