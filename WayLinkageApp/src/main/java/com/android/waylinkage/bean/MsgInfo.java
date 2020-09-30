package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Goll Lee
 */
public class MsgInfo implements Serializable{


    /**
     * id : 123
     * createTime : 2018/12/10 20:32:41
     * updateTime : 2018/12/10 20:34:30
     * creator : 1
     * updator : 1
     * type : 2
     * title : test pic
     * content : 内容
     * keywords : []
     * status : 2
     * publishTime : 2018/12/10 20:34:30
     * publisher : 1
     * publisherName : admin
     */

    private int id;
    private String createTime;
    private String updateTime;
    private int creator;
    private int updator;
    private int type;
    private String title;
    private String summary;
    private String content;
    private int status;
    private String publishTime;
    private int publisher;
    private String publisherName;
    private List<?> keywords;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getUpdator() {
        return updator;
    }

    public void setUpdator(int updator) {
        this.updator = updator;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getPublisher() {
        return publisher;
    }

    public void setPublisher(int publisher) {
        this.publisher = publisher;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public List<?> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<?> keywords) {
        this.keywords = keywords;
    }
}
