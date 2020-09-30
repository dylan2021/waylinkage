package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.WorkApplyBean;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class WorkAppliesListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<WorkApplyBean> progressList = new ArrayList<>();
    WorkAppliesListActivity context;
    private boolean isProgress = true;
    private int tabPosition, TYPE_WORK;
    private List<QualityReportBean.ContentBean> qualityList = new ArrayList<>();
    private int count;
    private int processorConfigId;

    public WorkAppliesListAdapter(WorkAppliesListActivity context, int TYPE_WORK) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.TYPE_WORK = TYPE_WORK;
    }

    public void setData(List<WorkApplyBean> data, int tabPosition) {
        this.progressList = data;
        this.tabPosition=tabPosition;
        count = progressList == null ? 0 : progressList.size();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return isProgress ? progressList.get(i) : qualityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new WorkAppliesListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_report_work_start_lv, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
            holder.postInvestTv = convertView.findViewById(R.id.report_item_post_invest_tv);
            holder.beginEndDateTv = convertView.findViewById(R.id.report_item_begin_end_date_tv);
            holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);
            //holder.tag0Tv = convertView.findViewById(R.id.todo_tag_0_tv);
            holder.tag1Tv = convertView.findViewById(R.id.todo_tag_1_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WorkApplyBean postInfo = progressList.get(position);
        final String nameTitle;
        WorkApplyBean.BizBuildSiteBean bizBuildSite = new WorkApplyBean.BizBuildSiteBean();
        if (TYPE_WORK == 2) {//变更
            WorkApplyBean.BizProcessorBean bizProcessor = postInfo.getBizProcessor();
            bizBuildSite = bizProcessor.getBizBuildSite();
            WorkApplyBean.BizProcessorConfigBean bizProcessorConfig = bizProcessor.getBizProcessorConfig();
            String processorConfigName = bizProcessorConfig.getName();
            processorConfigId = bizProcessor.getId();
            String buidSiteName = bizBuildSite.getName();
            String contractName = bizBuildSite.getBizContract().getName();
            nameTitle = contractName + " - " + buidSiteName + " - " + processorConfigName;
        } else {
            bizBuildSite = postInfo.getBizBuildSite();
            String buidSiteName = bizBuildSite.getName();
            String contractName = bizBuildSite.getBizContract().getName();
            nameTitle = contractName + " - " + buidSiteName;
        }

        String reportPeopleTitleTv = tabPosition == 0 ? "申请人" : "确认人";
        String timeTitle = tabPosition == 0 ? "申请时间：" : "确认时间：";

        String content0Title = TYPE_WORK == 0 ? "申请开工日期：" : TYPE_WORK == 1 ? "计划完工日期：" : "变更计划：";
        String content1Title = TYPE_WORK == 0 ? "计划开工日期：" : TYPE_WORK == 1 ? "实际完工日期：" : "变更部位：";
        List<WorkApplyBean.DetailBean> detail = postInfo.getDetail();
        String content0Value = TYPE_WORK == 2 ? (detail == null ? "0" : detail.size()) + "个" : TextUtil.substringTime(TYPE_WORK == 0 ?
                postInfo.getApplyWorkDate() : bizBuildSite.getPlanEndDate());
        String changePlace = postInfo.getChangePlace() == null ? "未知" : postInfo.getChangePlace();
        String content1Value = TYPE_WORK == 2 ? changePlace : TextUtil.substringTime(
                TYPE_WORK == 0 ? bizBuildSite.getPlanBeginDate() : postInfo.getActualFinishDate());

        String createTime = postInfo.getCreateTime();
        String updateTime = postInfo.getUpdateTime();
        holder.timeTv.setText(timeTitle + TextUtil.substringTime(tabPosition==0?createTime:updateTime));


        holder.titleTv.setText(nameTitle);
        holder.postInvestTv.setText(content0Title + content0Value);
        holder.beginEndDateTv.setText(content1Title + content1Value);//汇报周期

        String createUsername = postInfo.getCreatorUsername();//申请人
        holder.tag1Tv.setText(reportPeopleTitleTv + "：" + (createUsername == null ? "未知" : createUsername));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //待确认
                //开工
                if (TYPE_WORK == 0) {
                    Intent intent = new Intent(context, WorkStartDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) postInfo);//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);
                    intent.putExtra(KeyConstant.name, holder.titleTv.getText().toString());
                    context.startActivity(intent);
                    //完工
                } else if (TYPE_WORK == 1) {
                    Intent intent = new Intent(context, WorkFinishDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) postInfo);//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);
                    intent.putExtra(KeyConstant.name, holder.titleTv.getText().toString());
                    context.startActivity(intent);
                    //变更
                } else {
                    Intent intent = new Intent(context, WorkChangeDetailActivity.class);
                    intent.putExtra(KeyConstant.name, holder.titleTv.getText().toString());
                    intent.putExtra(KeyConstant.processorConfigId, processorConfigId);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) postInfo);//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
                //已确认

            }
        });

        return convertView;
    }


    public class ViewHolder {
        private TextView titleTv, timeTv, beginEndDateTv, tag1Tv, postInvestTv;

    }

}
