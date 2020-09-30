package com.android.waylinkage.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Gool Lee
 */

public class EmployeeRecordInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private String authName;
    private int employeeId;
    private FromGroupBean fromGroup;
    private ToGroupBean toGroup;

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

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public FromGroupBean getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(FromGroupBean fromGroup) {
        this.fromGroup = fromGroup;
    }

    public ToGroupBean getToGroup() {
        return toGroup;
    }

    public void setToGroup(ToGroupBean toGroup) {
        this.toGroup = toGroup;
    }

    public static class FromGroupBean {

        private int id;
        private String createTime;
        private String updateTime;
        private String name;
        private String code;
        private String planInDate;
        private int planInPeoples;
        private String planOutDate;
        private BizBuildSiteBean bizBuildSite;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPlanInDate() {
            return planInDate;
        }

        public void setPlanInDate(String planInDate) {
            this.planInDate = planInDate;
        }

        public int getPlanInPeoples() {
            return planInPeoples;
        }

        public void setPlanInPeoples(int planInPeoples) {
            this.planInPeoples = planInPeoples;
        }

        public String getPlanOutDate() {
            return planOutDate;
        }

        public void setPlanOutDate(String planOutDate) {
            this.planOutDate = planOutDate;
        }

        public BizBuildSiteBean getBizBuildSite() {
            return bizBuildSite;
        }

        public void setBizBuildSite(BizBuildSiteBean bizBuildSite) {
            this.bizBuildSite = bizBuildSite;
        }

        public static class BizBuildSiteBean {
            private String name;

            public String getNameX() {
                return name;
            }

            public void setNameX(String name) {
                this.name = name;
            }
        }
    }

    public static class ToGroupBean {
        /**
         * id : 1
         * createTime : 2018/10/05 20:51:52
         * updateTime : 2018/10/05 20:53:16
         * name : 木工
         * code : 1003
         * planInDate : 2018/10/05
         * planInPeoples : 10
         * planOutDate : 2018/10/05
         * bizBuildSite : {"id":2,"createTime":"2018/09/22 09:33:40","updateTime":"2018/09/22 09:35:06","attachment":"string","code":"GD1001","description":"工地","invest":100,"loadLevel":"1","name":"工地1","period":0,"pic":"string","planBeginDate":"2018/10/22","planEndDate":"2018/09/22","realEndDate":"2018/09/22","realStartDate":"2018/09/22","roadLevel":"1","specification":{"BRIDGE_WIDTH":"10","BRIDGE_LENGTH":"100","BRIDGE_CENTER_CHIANAGE":"GREAT","BRIDGE_WATER_FREQUENCY":"80"},"speed":0,"type":"1","qualified":false,"overdue":false,"bizContract":{"id":1,"createTime":"2018/09/22 15:43:55","updateTime":"2018/09/22 15:43:55","attachment":"string"},"designUnit":{"id":1,"createTime":"2018/08/17 18:21:12","updateTime":"2018/10/05 11:46:44","nameCn":"武汉测试公司01"},"constructionUnit":{"id":1,"createTime":"2018/08/17 18:21:12","updateTime":"2018/10/05 11:46:44","nameCn":"武汉测试公司01"}}
         */
        private FromGroupBean.BizBuildSiteBean bizBuildSite;
        private int id;
        private String createTime;
        private String updateTime;
        private String name;
        private String code;
        private String planInDate;
        private int planInPeoples;
        private String planOutDate;
        public FromGroupBean.BizBuildSiteBean getBizBuildSite() {
            return bizBuildSite;
        }
        public static class BizBuildSiteBean {
            private String name;

            public String getNameX() {
                return name;
            }

            public void setNameX(String name) {
                this.name = name;
            }
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPlanInDate() {
            return planInDate;
        }

        public void setPlanInDate(String planInDate) {
            this.planInDate = planInDate;
        }

        public int getPlanInPeoples() {
            return planInPeoples;
        }

        public void setPlanInPeoples(int planInPeoples) {
            this.planInPeoples = planInPeoples;
        }

        public String getPlanOutDate() {
            return planOutDate;
        }

        public void setPlanOutDate(String planOutDate) {
            this.planOutDate = planOutDate;
        }


    }
}
