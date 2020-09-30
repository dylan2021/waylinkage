package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool Lee
 */
public class PDMInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private int planInPeoples;
    private int realInPeoples;
    private int realOutNumbers;
    private int currentInNumbers;
    private int actualInPeople;
    private String code;
    private String type;

    public int getCurrentInNumbers() {
        return currentInNumbers;
    }

    public void setCurrentInNumbers(int currentInNumbers) {
        this.currentInNumbers = currentInNumbers;
    }

    private String name;
    private List<DeviceInfo> details;

    public int getActualInPeople() {
        return actualInPeople;
    }

    public void setActualInPeople(int actualInPeople) {
        this.actualInPeople = actualInPeople;
    }

    public int getRealOutNumbers() {
        return realOutNumbers;
    }

    public void setRealOutNumbers(int realOutNumbers) {
        this.realOutNumbers = realOutNumbers;
    }

    public int getPlanInPeoples() {
        return planInPeoples;
    }

    public void setPlanInPeoples(int planInPeoples) {
        this.planInPeoples = planInPeoples;
    }

    public int getRealInPeoples() {
        return realInPeoples;
    }

    public void setRealInPeoples(int realInPeoples) {
        this.realInPeoples = realInPeoples;
    }

    private String factory;
    private String keeperName;
    private String keeperPhone;
    private String quality;
    private String spec;
    private String unit;
    private int planInNumbers;
    private String planInDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getKeeperName() {
        return keeperName;
    }

    public void setKeeperName(String keeperName) {
        this.keeperName = keeperName;
    }

    public String getKeeperPhone() {
        return keeperPhone;
    }

    public void setKeeperPhone(String keeperPhone) {
        this.keeperPhone = keeperPhone;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    private String planOutDate;
    private int realInNumbers;
    private String realInDate;
    private String realOutDate;
    private int buildSiteId;

    public String getRealOutDate() {
        return realOutDate;
    }

    public void setRealOutDate(String realOutDate) {
        this.realOutDate = realOutDate;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPlanInNumbers() {
        return planInNumbers;
    }

    public void setPlanInNumbers(int planInNumbers) {
        this.planInNumbers = planInNumbers;
    }

    public String getPlanInDate() {
        return planInDate;
    }

    public void setPlanInDate(String planInDate) {
        this.planInDate = planInDate;
    }

    public String getPlanOutDate() {
        return planOutDate;
    }

    public void setPlanOutDate(String planOutDate) {
        this.planOutDate = planOutDate;
    }

    public int getRealInNumbers() {
        return realInNumbers;
    }

    public void setRealInNumbers(int realInNumbers) {
        this.realInNumbers = realInNumbers;
    }

    public String getRealInDate() {
        return realInDate;
    }

    public void setRealInDate(String realInDate) {
        this.realInDate = realInDate;
    }

    public int getBuildSiteId() {
        return buildSiteId;
    }

    public void setBuildSiteId(int buildSiteId) {
        this.buildSiteId = buildSiteId;
    }

    public List<DeviceInfo> getDetails() {
        return details;
    }

    public void setDetails(List<DeviceInfo> details) {
        this.details = details;
    }
}
