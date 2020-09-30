package com.android.waylinkage.bean;

import java.io.Serializable;

/**
 *Gool Lee
 */
public class EmployeeInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private String realInDate;
    private String address;
    private int age;
    private String cardNo;
    private String certification;
    private String degree;
    private String emergyName;
    private String emergyPhone;
    private int groupId;
    private String name;
    private String phone;
    private String sex;
    private String status;
    private int workYears;

    public String getRealInDate() {
        return realInDate;
    }

    public void setRealInDate(String realInDate) {
        this.realInDate = realInDate;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getEmergyName() {
        return emergyName;
    }

    public void setEmergyName(String emergyName) {
        this.emergyName = emergyName;
    }

    public String getEmergyPhone() {
        return emergyPhone;
    }

    public void setEmergyPhone(String emergyPhone) {
        this.emergyPhone = emergyPhone;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getWorkYears() {
        return workYears;
    }

    public void setWorkYears(int workYears) {
        this.workYears = workYears;
    }
}
