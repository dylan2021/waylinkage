/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.waylinkage.activity.frag0;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.StatInfo;

import java.util.ArrayList;
import java.util.List;

public class QualityOverAdapter extends BaseExpandableListAdapter {
    private int tabType = 3;
    private List<StatInfo> groupList = new ArrayList<>();

    public QualityOverAdapter() {
    }

    @Override
    public int getGroupCount() {
        return null == groupList ? 0 : groupList.size();
    }

    //获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupList == null) {
            return 0;
        }
        StatInfo StatInfo = groupList.get(groupPosition);

        List<StatInfo.ItemsBean> childrenBeanList = StatInfo.getItems();
        if (childrenBeanList == null) {
            return 0;
        }
        return childrenBeanList.size();
    }

    //        获取指定的分组数据
    @Override
    public Object getGroup(int groupPosition) {
        return groupList == null ? null : groupList.get(groupPosition);
    }

    // 获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        StatInfo StatInfo = groupList.get(groupPosition);
        if (groupList == null) {
            return null;
        }
        List<StatInfo.ItemsBean> childrenBeanList = StatInfo.getItems();
        if (childrenBeanList == null) {
            return null;
        }
        return childrenBeanList.get(childPosition);
    }


    //获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded    该组是展开状态还是伸缩状态
     * @param convertView   重用已有的视图对象
     * @param parent        返回的视图对象始终依附于的视图组
     */
// 获取显示指定分组的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.quality_partent_item, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_title);
            groupViewHolder.tvNumber = (TextView) convertView.findViewById(R.id.group_number_tv);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        StatInfo statInfo = groupList.get(groupPosition);
        if (statInfo != null) {
            groupViewHolder.tvTitle.setText(statInfo.getName());
            if (statInfo.getItems() == null || statInfo.getItems().size() == 0) {
                groupViewHolder.tvNumber.setText("0");
            }
                groupViewHolder.tvNumber.setText(statInfo.getTotal() + "项待整改");

        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.quality_child_item, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.childTitleTv = (TextView) convertView.findViewById(R.id.expand_child);
            childViewHolder.childNumberTv = (TextView) convertView.findViewById(R.id.child_number_tv);
            convertView.setTag(childViewHolder);

        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        StatInfo StatInfo = groupList.get(groupPosition);
        if (StatInfo != null) {
            List<StatInfo.ItemsBean> itemBeanList = StatInfo.getItems();
            //子类
            if (itemBeanList != null && itemBeanList.size() > 0) {

                StatInfo.ItemsBean itemsBean = itemBeanList.get(childPosition);
                if (null != itemsBean) {
                    childViewHolder.childTitleTv.setText(itemsBean.getName());
                    childViewHolder.childNumberTv.setText(itemsBean.getTotal() + "项");
                }
            }
        }
        return convertView;
    }

    //指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(List<StatInfo> result) {
        this.groupList = result;
        notifyDataSetChanged();
    }

    static class GroupViewHolder {
        TextView tvTitle, tvNumber;
    }

    static class ChildViewHolder {
        TextView childTitleTv, childNumberTv;

    }
}














