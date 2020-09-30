package com.android.waylinkage.bean;

import java.util.List;

/**
 * Gool Lee
 */

public class FlagMaterialInfo {

    private List<ContentBean> content;

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * id : 1
         * createTime : 2018/10/18 15:33:24
         * updateTime : 2018/10/18 15:53:32
         * confirmTime : 2018/10/18 15:53:32
         * result : [{"type":"1","completed":"1","standard":"1","typeDesc":"生活区"}]
         * confirmor : 1
         */

        private int id;
        private String createTime;
        private String updateTime;
        private String confirmTime;
        private String confirmor;
        private List<ResultBean> result;
        private List<FileInfo> attachment;
        private List<FileInfo> pic;

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

        public String getConfirmTime() {
            return confirmTime;
        }

        public void setConfirmTime(String confirmTime) {
            this.confirmTime = confirmTime;
        }

        public String getConfirmor() {
            return confirmor;
        }

        public void setConfirmor(String confirmor) {
            this.confirmor = confirmor;
        }

        public List<ResultBean> getResult() {
            return result;
        }

        public void setResult(List<ResultBean> result) {
            this.result = result;
        }

        public static class ResultBean {
            /**
             * type : 1
             * completed : 1
             * standard : 1
             * typeDesc : 生活区
             */

            private String type;
            private String completed;
            private String standard;
            private String typeDesc;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCompleted() {
                return completed;
            }

            public void setCompleted(String completed) {
                this.completed = completed;
            }

            public String getStandard() {
                return standard;
            }

            public void setStandard(String standard) {
                this.standard = standard;
            }

            public String getTypeDesc() {
                return typeDesc;
            }

            public void setTypeDesc(String typeDesc) {
                this.typeDesc = typeDesc;
            }
        }
    }
}
