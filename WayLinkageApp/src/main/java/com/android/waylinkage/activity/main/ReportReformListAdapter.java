package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.frag0.ApplyDetailActivity;
import com.android.waylinkage.activity.frag0.NoticeListActivity;
import com.android.waylinkage.activity.frag0.QualityDetailActivity;
import com.android.waylinkage.activity.frag0.ProgressDetailActivity;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class ReportReformListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    boolean mIsReportType, mIsInicateType, mIsAfficheType;
    List<ReportBean.ContentBean> progressList = new ArrayList<>();
    ReportReformListActivity context;
    private boolean isProgress = true;
    private int tabPosition;
    private List<QualityReportBean.ContentBean> qualityList = new ArrayList<>();
    private int count;
    private ReportBean.ContentBean.BizProcessorPlanBean.BizProcessorBean.BizBuildSiteBean bizBuildSite;
    private ReportBean.ContentBean.BizProcessorPlanBean.BizProcessorBean bizProcessor;
    private boolean isOverdue = false;

    public ReportReformListAdapter(ReportReformListActivity context,
                                   boolean mIsReportType, boolean mIsInicateType,
                                   boolean mIsAfficheType) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.mIsReportType = mIsReportType;
        this.mIsInicateType = mIsInicateType;
        this.mIsAfficheType = mIsAfficheType;
    }

    public void setData(List<ReportBean.ContentBean> data) {
        this.progressList = data;
        count = progressList == null ? 0 : progressList.size();
        notifyDataSetChanged();
    }

    public void setQualityData(List<QualityReportBean.ContentBean> qualityData) {
        this.qualityList = qualityData;
        count = qualityList == null ? 0 : qualityList.size();
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

        ViewHolder holder;
        if (convertView == null) {
            holder = new ReportReformListAdapter.ViewHolder();
            if (mIsInicateType) {//最新评论
                convertView = mInflater.inflate(R.layout.item_fragment0_comment_lv_item, viewGroup, false);
                holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
                holder.nameTv = convertView.findViewById(R.id.report_item_name_tv);
                holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);//业主
                holder.statusTv = convertView.findViewById(R.id.report_item_status_tv);
            } else if (mIsAfficheType) {//公告
                convertView = mInflater.inflate(R.layout.item_fragment0_notif_lv_item, viewGroup, false);
                holder.nameTv = convertView.findViewById(R.id.report_item_name_tv);
                holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
                holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);
            } else if (mIsReportType) {//进度汇报
                convertView = mInflater.inflate(R.layout.item_fragment0_report_lv_item, viewGroup, false);
                holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
                holder.postInvestTv = convertView.findViewById(R.id.report_item_post_invest_tv);
                holder.beginEndDateTv = convertView.findViewById(R.id.report_item_begin_end_date_tv);
                holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);
                holder.tag0Tv = convertView.findViewById(R.id.todo_tag_0_tv);
                holder.tag1Tv = convertView.findViewById(R.id.todo_tag_1_tv);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        if (mIsAfficheType) {//公告通知
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(KeyConstant.afficheTitle, "标题");
                    intent.setClass(context, NoticeListActivity.class);
                    context.startActivity(intent);
                }
            });

        } else {
            if (mIsInicateType) {//最新评论
                holder.titleTv.setText("评论" + position);
            } else {
                if (mIsReportType) {//汇报
                    String reportPeopleTitleTv = tabPosition == 0 ? "汇报人" : tabPosition == 1 ? "确认人" : "检查人";
                    String timeTitle = tabPosition == 0 ? "汇报时间：" : tabPosition == 1 ?
                            "确认时间：" : "检查时间：";
                    //进度
                    if (isProgress) {

                        final ReportBean.ContentBean postInfo = progressList.get(position);
                        ReportBean.ContentBean.BizProcessorPlanBean bizProcessorPlan = postInfo.getBizProcessorPlan();
                        bizProcessorPlan = bizProcessorPlan == null ? new ReportBean.ContentBean.BizProcessorPlanBean() : bizProcessorPlan;
                        final String postInvest = postInfo.getReportInvest() == null ? "0" : postInfo.getReportInvest();//汇报产值
                        final String postTitle = bizProcessorPlan.getName();//计划
                        String createTime = tabPosition == 0 ? postInfo.getCreateTime() : postInfo.getUpdateTime();
                        holder.titleTv.setText(postTitle);
                        holder.timeTv.setText(timeTitle + TextUtil.substringTime((createTime)));

                        bizProcessor = bizProcessorPlan.getBizProcessor();
                        bizBuildSite = bizProcessor.getBizBuildSite();
                        String buidSiteName = bizBuildSite.getName();
                        String contractName = bizBuildSite.getBizContract().getName();
                        String processorName = bizProcessor.getBizProcessorConfig().getName();

                        final String belowProcesssorName = contractName + " - " +
                                buidSiteName + " - " + processorName;
                        holder.tag0Tv.setText("汇报工序：" + belowProcesssorName);//标段
                        holder.postInvestTv.setText("汇报产值：" + postInvest + context.getString(R.string.money_unit));//汇报产值
                        holder.beginEndDateTv.setVisibility(View.VISIBLE);
                        holder.beginEndDateTv.setText("汇报周期：" +
                                postInfo.getRealBeginDate() + " - " + postInfo.getRealEndDate());//汇报周期

                        String createUsername = postInfo.getCreatorUsername();//汇报人
                        holder.tag1Tv.setText(reportPeopleTitleTv + "：" +
                                (createUsername == null ? "未知" : createUsername));

                        convertView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ProgressDetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(KeyConstant.LIST_OBJECT,
                                        (Serializable) postInfo);//序列化,要注意转化(Serializable)
                                intent.putExtras(bundle);
                                intent.putExtra(KeyConstant.type, tabPosition);
                                intent.putExtra(KeyConstant.name, belowProcesssorName);
                                intent.putExtra(KeyConstant.id, postInfo.getId());
                                //传  postInfo
                                context.startActivity(intent);
                            }
                        });
                    } else {

                        final QualityReportBean.ContentBean qualityBean = (qualityList == null) ? null : qualityList.get(position);
                        holder.timeTv.setText(timeTitle + TextUtil.substringTime(
                                tabPosition == 0 ? qualityBean.getCreateTime() : tabPosition == 1 ?
                                        qualityBean.getConfirmTime() + "" : qualityBean.getCheckTime() + ""));

                        //质量
                        if (qualityBean != null && qualityBean.getBizProcessor() != null) {
                            QualityReportBean.ContentBean.BizProcessorBean bizProcessor = qualityBean.getBizProcessor();
                            String contractName = bizProcessor.getBizBuildSite().getBizContract().getName();
                            String buildSiteName = bizProcessor.getBizBuildSite().getName();//工地
                            final QualityReportBean.ContentBean.BizProcessorBean.BizProcessorConfigBean bizProcessorConfig = bizProcessor.getBizProcessorConfig();
                            final String procesoNname = bizProcessorConfig.getName();

                            final String blowContractBuildSite = "" + contractName + " - " + buildSiteName + " - " + procesoNname;
                            holder.titleTv.setText(isOverdue ? procesoNname : blowContractBuildSite);//xx工地


                            List<QualityReportBean.ContentBean.DetailBean> detailBeanList = qualityBean.getDetail();
                            holder.postInvestTv.setText("检查项：" + (detailBeanList == null ? 0 : detailBeanList.size()) + "项");
                            int status = qualityBean.getStatus();
                            String statusStr = status == 1 ? "待确认" : status == 2 ? "待整改" : status == 3 ? "已整改" : "已确认";
                            holder.tag0Tv.setText("检查结果：" + statusStr);

                            holder.beginEndDateTv.setVisibility(View.GONE);

                            String confirmor = qualityBean.getConfirmorUsername();//确认人
                            String creator = qualityBean.getCreatorUsername();//汇报人
                            String checker = qualityBean.getChecker();//检查人
                            holder.tag1Tv.setText(reportPeopleTitleTv + "：" + (tabPosition == 0 ?
                                    creator : tabPosition == 1 ? confirmor : checker));//汇报人
                            final String nameStr = holder.tag1Tv.getText().toString();
                            final String timeStr = holder.timeTv.getText().toString();
                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //质量详情
                                    Intent intent = new Intent(context, QualityDetailActivity.class);
                                    intent.putExtra(KeyConstant.id, qualityBean.getId());
                                    intent.putExtra(KeyConstant.type, tabPosition);
                                    intent.putExtra(KeyConstant.isOverdue, isOverdue);
                                    intent.putExtra(KeyConstant.processorConfigId, bizProcessorConfig.getId());
                                    intent.putExtra(KeyConstant.name, isOverdue ? procesoNname : blowContractBuildSite);
                                    intent.putExtra(KeyConstant.time_name, nameStr + "\b\b\b" + timeStr);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) qualityBean);
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                } else {//申请管理
                    String qiaoliangStr = "桥梁" + (position + 1);
                    holder.titleTv.setText(qiaoliangStr + "开工申请");
                    holder.tag1Tv.setText(qiaoliangStr);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ApplyDetailActivity.class);
                            context.startActivity(intent);

                        }
                    });
                }
            }
        }


        return convertView;
    }

    public void setIsProgress(boolean isProgress, int tabPosition, boolean isOverdue) {
        this.isProgress = isProgress;
        this.tabPosition = tabPosition;
        this.isOverdue = isOverdue;
    }

    public class ViewHolder {
        private TextView titleTv, timeTv, nameTv, beginEndDateTv, tag0Tv, tag1Tv, postInvestTv, statusTv, circleTv;
        private LinearLayout bottomTagLayout;

    }

}
