package com.android.waylinkage.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
Gool Lee
 */

public class HistoryInfo {

    private int id;
    private BizStartApplyBean bizStartApply;
    private BizFinishApplyBean bizFinishApply;
    private String commit;
    private int status;
    private int auditor;
    private String auditDate;
    private String auditorUsername;

    public BizFinishApplyBean getBizFinishApply() {
        return bizFinishApply;
    }

    public void setBizFinishApply(BizFinishApplyBean bizFinishApply) {
        this.bizFinishApply = bizFinishApply;
    }

    /**
     * bizChangeApply : {"id":11,"createTime":"2018/11/23 16:25:08","changeRemark":"没有","status":null,"pic":null,"attachment":null}
     */

    private BizChangeApplyBean bizChangeApply;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BizStartApplyBean getBizStartApply() {
        return bizStartApply;
    }

    public void setBizStartApply(BizStartApplyBean bizStartApply) {
        this.bizStartApply = bizStartApply;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuditor() {
        return auditor;
    }

    public void setAuditor(int auditor) {
        this.auditor = auditor;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }

    public String getAuditorUsername() {
        return auditorUsername;
    }

    public void setAuditorUsername(String auditorUsername) {
        this.auditorUsername = auditorUsername;
    }

    public BizChangeApplyBean getBizChangeApply() {
        return bizChangeApply;
    }

    public void setBizChangeApply(BizChangeApplyBean bizChangeApply) {
        this.bizChangeApply = bizChangeApply;
    }

    public static class BizFinishApplyBean {

        private int id;
        private String createTime;
        private String updateTime;
        private String actualFinishDate;
        private String remark;
        private boolean status;
        private String creatorUsername;
        private String updatorUsername;
        private List<FileInfo> pic;
        private List<FileInfo> attachment;

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

        public String getActualFinishDate() {
            return actualFinishDate;
        }

        public void setActualFinishDate(String actualFinishDate) {
            this.actualFinishDate = actualFinishDate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getCreatorUsername() {
            return creatorUsername;
        }

        public void setCreatorUsername(String creatorUsername) {
            this.creatorUsername = creatorUsername;
        }

        public String getUpdatorUsername() {
            return updatorUsername;
        }

        public void setUpdatorUsername(String updatorUsername) {
            this.updatorUsername = updatorUsername;
        }

        public List<FileInfo> getPic() {
            return pic;
        }

        public void setPic(List<FileInfo> pic) {
            this.pic = pic;
        }

        public List<FileInfo> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<FileInfo> attachment) {
            this.attachment = attachment;
        }
    }
    public static class BizStartApplyBean {

        private int id;
        private String createTime;
        private String updateTime;
        private String applyWorkDate;
        private String remark;
        private boolean status;
        private String creatorUsername;
        private String updatorUsername;
        private List<FileInfo> pic;
        private List<FileInfo> attachment;

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

        public String getApplyWorkDate() {
            return applyWorkDate;
        }

        public void setApplyWorkDate(String applyWorkDate) {
            this.applyWorkDate = applyWorkDate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getCreatorUsername() {
            return creatorUsername;
        }

        public void setCreatorUsername(String creatorUsername) {
            this.creatorUsername = creatorUsername;
        }

        public String getUpdatorUsername() {
            return updatorUsername;
        }

        public void setUpdatorUsername(String updatorUsername) {
            this.updatorUsername = updatorUsername;
        }

        public List<FileInfo> getPic() {
            return pic;
        }

        public void setPic(List<FileInfo> pic) {
            this.pic = pic;
        }

        public List<FileInfo> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<FileInfo> attachment) {
            this.attachment = attachment;
        }
    }

    public static class BizChangeApplyBean {
        /**
         * id : 11
         * createTime : 2018/11/23 16:25:08
         * changeRemark : 没有
         * status : null
         * pic : null
         * attachment : null
         */

        @SerializedName("id")
        private int idX;
        private String createTime;
        private String changeRemark;
        @SerializedName("status")
        private Object statusX;
        private List<FileInfo> pic;
        private List<FileInfo> attachment;

        public int getIdX() {
            return idX;
        }

        public void setIdX(int idX) {
            this.idX = idX;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getChangeRemark() {
            return changeRemark;
        }

        public void setChangeRemark(String changeRemark) {
            this.changeRemark = changeRemark;
        }

        public Object getStatusX() {
            return statusX;
        }

        public void setStatusX(Object statusX) {
            this.statusX = statusX;
        }

        public List<FileInfo> getPic() {
            return pic;
        }

        public void setPic(List<FileInfo> pic) {
            this.pic = pic;
        }

        public List<FileInfo> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<FileInfo> attachment) {
            this.attachment = attachment;
        }
    }
}
