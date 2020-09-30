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
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.util.KeyConstant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class ProcessorQualityListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private boolean mIsReportType, mIsInicateType, mIsAfficheType;
    private List<ReportBean.ContentBean> progressList = new ArrayList<>();
    private ProcessorQualityListActivity context;
    private boolean isProgress = true;
    private int tabPosition;
    private List<QualityReportBean.ContentBean> qualityList = new ArrayList<>();
    private int count;
    private ReportBean.ContentBean.BizProcessorPlanBean.BizProcessorBean.BizBuildSiteBean bizBuildSite;
    private ReportBean.ContentBean.BizProcessorPlanBean.BizProcessorBean bizProcessor;

    public ProcessorQualityListAdapter(ProcessorQualityListActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
            holder = new ProcessorQualityListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_fragment0_report_lv_item, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
            holder.postInvestTv = convertView.findViewById(R.id.report_item_post_invest_tv);
            holder.beginEndDateTv = convertView.findViewById(R.id.report_item_begin_end_date_tv);
            holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);
            holder.tag0Tv = convertView.findViewById(R.id.todo_tag_0_tv);
            holder.tag1Tv = convertView.findViewById(R.id.todo_tag_1_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final QualityReportBean.ContentBean qualityBean = (qualityList == null) ? null : qualityList.get(position);

        //质量
        if (qualityBean != null && qualityBean.getBizProcessor() != null) {
            QualityReportBean.ContentBean.BizProcessorBean bizProcessor = qualityBean.getBizProcessor();
            String contractName = bizProcessor.getBizBuildSite().getBizContract().getName();
            String buildSiteName = bizProcessor.getBizBuildSite().getName();//工地
            final QualityReportBean.ContentBean.BizProcessorBean.BizProcessorConfigBean bizProcessorConfig = bizProcessor.getBizProcessorConfig();
            String procesoNname = bizProcessorConfig.getName();
            final String blowContractBuildSite = "" + contractName + " - " + buildSiteName + " - " + procesoNname;


            List<QualityReportBean.ContentBean.DetailBean> detailBeanList = qualityBean.getDetail();
            final int status = qualityBean.getStatus();
            final String statusStr = status == 1 ? "待确认" : status == 2 ? "待整改"
                    : status == 3 ? "已整改" : "已确认";

            String checkNum = "检查项：" + (detailBeanList == null ? 0 : detailBeanList.size()) + "项";
            holder.titleTv.setText(statusStr);//工地


            holder.beginEndDateTv.setVisibility(View.GONE);

            String confirmor = qualityBean.getConfirmorUsername();//确认人
            String creator = qualityBean.getCreatorUsername();//汇报人
            String checker = qualityBean.getChecker();//检查人
            String reformor = qualityBean.getReformor();//整改人

            final String peopleStr = status == 1 ? "汇报人：" + creator : status == 2 ?
                    "检查人：" + checker : status == 3 ? "整改人：" + reformor : "确认人：" + confirmor;
            holder.tag0Tv.setText(peopleStr);
            holder.tag1Tv.setText(checkNum);//汇报人

            final String timeStr = status == 1 ? "汇报时间：" + qualityBean.getCreateTime() :
                            status == 2 ? "检查时间：" + qualityBean.getCheckTime() :
                            status == 3 ? "整改时间：" + qualityBean.getReformTime() :
                            "确认时间：" + qualityBean.getConfirmTime() ;
            holder.postInvestTv.setText(timeStr);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProcessorQualityAddActivity.class);
                    intent.putExtra(KeyConstant.id, qualityBean.getId());
                    intent.putExtra(KeyConstant.type, statusStr);
                    intent.putExtra(KeyConstant.status, status);
                    intent.putExtra(KeyConstant.processorConfigId, bizProcessorConfig.getId());
                    intent.putExtra(KeyConstant.name, blowContractBuildSite);
                    intent.putExtra(KeyConstant.time_name, peopleStr + "\b\b\b" + timeStr);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) qualityBean);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }

            });
        }
        return convertView;
    }

    public void setIsProgress(boolean isProgress, int tabPosition) {
        this.isProgress = isProgress;
        this.tabPosition = tabPosition;
    }

    public class ViewHolder {
        private TextView titleTv, timeTv, nameTv, beginEndDateTv, tag0Tv,tag1Tv, postInvestTv, statusTv, circleTv;
        private LinearLayout bottomTagLayout;

    }

}
