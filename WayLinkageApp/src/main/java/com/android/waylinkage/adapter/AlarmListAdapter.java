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

package com.android.waylinkage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.AlarmInfo;
import com.android.waylinkage.util.TextUtil;

import java.util.List;

/**
 * @author Gool Lee
 * @since
 */
public class AlarmListAdapter extends BaseAdapter {

    private String TAG = AlarmListAdapter.class.getSimpleName();

    private List<AlarmInfo> alarmList;

    private Context context;

    public AlarmListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setData(List<AlarmInfo> alarmList) {
        this.alarmList = alarmList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (alarmList != null) {
            return alarmList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (alarmList != null) {
            return alarmList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alarm_lv, parent, false);
            holder.categoryPic = (ImageView) convertView.findViewById(R.id.ic_category_iv);
            holder.categoryTv = (TextView) convertView.findViewById(R.id.category_tv);

            holder.contentTv = (TextView) convertView.findViewById(R.id.content_tv);
            holder.belowContractTv = (TextView) convertView.findViewById(R.id.below_contract_tv);
            holder.tagIv = (ImageView) convertView.findViewById(R.id.alarm_tag_iv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.postil_item_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AlarmInfo alarmInfo = alarmList.get(position);

        String categoryName = alarmInfo.getCategory();
        int categoryImageId = R.drawable.ic_empty_circle_main_color;
        switch (categoryName) {
            case "进度预警":
                categoryImageId = R.drawable.ic_report_progress;
                break;
            case "质量预警":
                categoryImageId = R.drawable.ic_report_quality;
                break;
            case "安全预警":
                categoryImageId = R.drawable.ic_report_safy;
                break;
        }
        holder.categoryTv.setText(categoryName);
        holder.categoryPic.setImageResource(categoryImageId);

        String belowContract = alarmInfo.getProjectName() + " - " + alarmInfo.getContractName();
        String buildSiteName = alarmInfo.getBuildSiteName();
        if (!TextUtil.isEmpty(buildSiteName)) {
            belowContract = belowContract + "-" + buildSiteName;
        }
        String subObjectName = alarmInfo.getSubObjectName();
        if (!TextUtil.isEmpty(subObjectName)) {
            belowContract = belowContract + "-" + subObjectName;
        }

        holder.belowContractTv.setText(belowContract);
        holder.contentTv.setText(alarmInfo.getContent());

        String severity = alarmInfo.getSeverity();
        if ("critical".equals(severity)) {
            holder.tagIv.setImageResource(R.drawable.ic_warning_serious);
        } else if ("warning".equals(severity)) {
            holder.tagIv.setImageResource(R.drawable.ic_warning_warin);
        }
        String createTime = alarmInfo.getCreateTime();
        holder.timeTv.setText(null == createTime ? alarmInfo.getUpdateTime() : createTime);

        return convertView;
    }

    public class ViewHolder {
        public TextView categoryTv, belowContractTv, contentTv, timeTv;
        public ImageView tagIv, categoryPic;
    }

}














