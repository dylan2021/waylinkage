package com.android.waylinkage.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 游戏类型实体类
 * Created by zeng on 2016/9/6.
 */
public class StatInfo implements Serializable {


    /**
     * id : 2
     * name : 路基2
     * leaderName : 李元霸
     * items : [{"id":48,"name":"李思","total":5},{"id":90,"name":"李良","total":2}]
     * total : 9
     */

    private int id;
    private String name;

    private int total;
    private List<ItemsBean> items;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        /**
         * id : 48
         * name : 李思班
         * total : 5
         */

        private int id;
        private String name;
        private String unit;
        private String leaderName;
        private int total;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getLeaderName() {
            return leaderName;
        }

        public void setLeaderName(String leaderName) {
            this.leaderName = leaderName;
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

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
