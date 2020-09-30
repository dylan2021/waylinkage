package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool
 */
public class GroupInfo implements Serializable {

    private int id;
    private String name;
    private boolean isAllChecked;
    private List<ChildrenBean> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllChecked() {
        return isAllChecked;
    }

    public void setAllChecked(boolean allChecked) {
        isAllChecked = allChecked;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    public GroupInfo(int id, String name, boolean isAllChecked, List<ChildrenBean> children) {
        this.id = id;
        this.name = name;
        this.isAllChecked = isAllChecked;
        this.children = children;
    }

    public static class ChildrenBean implements Serializable {
        private int id;
        private String name;
        private boolean isChildChecked;

        public ChildrenBean(int id, String name, boolean isChildChecked) {
            this.id = id;
            this.name = name;
            this.isChildChecked = isChildChecked;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChildChecked() {
            return isChildChecked;
        }

        public void setChildChecked(boolean childChecked) {
            isChildChecked = childChecked;
        }
    }
}
