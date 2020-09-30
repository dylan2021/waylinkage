package com.android.waylinkage.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Gool Lee
 */
public class ChooseDataInfo {

    private String type;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * createTime : 2018/09/22 15:19:04
         * updateTime : 2018/11/10 18:28:45
         * attachment : null
         * code : BH1001
         * description : string
         * endLocation : string
         * invest : 10.22
         * length : 100
         * name : 贵州高速公路项目1
         * period : 0
         * pic : null
         * planBeginDate : 2018/09/22
         * planEndDate : 2018/09/22
         * startLocation : string
         */

        private int id;
        private Object attachment;
        private String code;
        private String description;
        private String endLocation;
        private double invest;
        private double length;
        private String name;
        private int period;
        private Object pic;
        private String planBeginDate;
        private String planEndDate;
        private String startLocation;
        /**
         * bizContract : {"id":1,"name":"合同8","bizProject":{"id":1,"name":"2018-09-22 15:19:04"}}
         */

        private BizContractBean bizContract;
        /**
         * bizProject : {"id":1,"name":"贵州高速公路项目1"}
         */

        private DataBean bizProject;
        /**
         * specification : {"BRIDGE_LENGHT":"100","SUBGRADE_LENGTH":"100.5"}
         */

        private SpecificationBean specification;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getAttachment() {
            return attachment;
        }

        public void setAttachment(Object attachment) {
            this.attachment = attachment;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(String endLocation) {
            this.endLocation = endLocation;
        }

        public double getInvest() {
            return invest;
        }

        public void setInvest(double invest) {
            this.invest = invest;
        }

        public double getLength() {
            return length;
        }

        public void setLength(double length) {
            this.length = length;
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

        public Object getPic() {
            return pic;
        }

        public void setPic(Object pic) {
            this.pic = pic;
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

        public String getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(String startLocation) {
            this.startLocation = startLocation;
        }

        public BizContractBean getBizContract() {
            return bizContract;
        }

        public void setBizContract(BizContractBean bizContract) {
            this.bizContract = bizContract;
        }

        public DataBean getBizProject() {
            return bizProject;
        }

        public void setBizProject(DataBean bizProject) {
            this.bizProject = bizProject;
        }

        public SpecificationBean getSpecification() {
            return specification;
        }

        public void setSpecification(SpecificationBean specification) {
            this.specification = specification;
        }

        public static class BizContractBean {
            /**
             * id : 1
             * name : 合同8
             * bizProject : {"id":1,"name":"2018-09-22 15:19:04"}
             */

            @SerializedName("id")
            private int idX;
            @SerializedName("name")
            private String nameX;
            private DataBean bizProject;

            public int getIdX() {
                return idX;
            }

            public void setIdX(int idX) {
                this.idX = idX;
            }

            public String getNameX() {
                return nameX;
            }

            public void setNameX(String nameX) {
                this.nameX = nameX;
            }

            public DataBean getBizProject() {
                return bizProject;
            }

            public void setBizProject(DataBean bizProject) {
                this.bizProject = bizProject;
            }
        }

        public static class SpecificationBean {
            /**
             * BRIDGE_LENGHT : 100
             * SUBGRADE_LENGTH : 100.5
             */

            private String BRIDGE_LENGHT;
            private String SUBGRADE_LENGTH;
            private String TUNNEL_LEFT_LENGTH;

            public String getTUNNEL_LEFT_LENGTH() {
                return TUNNEL_LEFT_LENGTH;
            }

            public void setTUNNEL_LEFT_LENGTH(String TUNNEL_LEFT_LENGTH) {
                this.TUNNEL_LEFT_LENGTH = TUNNEL_LEFT_LENGTH;
            }

            public String getTUNNEL_RIGHT_LENGTH() {
                return TUNNEL_RIGHT_LENGTH;
            }

            public void setTUNNEL_RIGHT_LENGTH(String TUNNEL_RIGHT_LENGTH) {
                this.TUNNEL_RIGHT_LENGTH = TUNNEL_RIGHT_LENGTH;
            }

            private String TUNNEL_RIGHT_LENGTH;

            public String getBRIDGE_LENGHT() {
                return BRIDGE_LENGHT;
            }

            public void setBRIDGE_LENGHT(String BRIDGE_LENGHT) {
                this.BRIDGE_LENGHT = BRIDGE_LENGHT;
            }

            public String getSUBGRADE_LENGTH() {
                return SUBGRADE_LENGTH;
            }

            public void setSUBGRADE_LENGTH(String SUBGRADE_LENGTH) {
                this.SUBGRADE_LENGTH = SUBGRADE_LENGTH;
            }
        }
    }
}
