package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.ProcessorProgressInfo;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class ProcessorListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ProcessorProgressInfo> progressList = new ArrayList<>();
    private ProcessorListActivity context;

    public ProcessorListAdapter(ProcessorListActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return progressList.size();
    }

    @Override
    public Object getItem(int i) {
        return progressList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ProcessorProgressInfo processorInfo = progressList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ProcessorListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.item_processor, viewGroup, false);
            holder.processorNameTv = convertView.findViewById(R.id.processor_name_tv);
            holder.processor_status_tv = convertView.findViewById(R.id.processor_status_tv);
            holder.processor_plan_invest_tv = convertView.findViewById(R.id.processor_plan_invest_tv);
            holder.processor_actual_invest_tv = convertView.findViewById(R.id.processor_actual_invest_tv);
            holder.processor_plan_count_tv = convertView.findViewById(R.id.processor_plan_count_tv);
            holder.processor_actual_start_end_date_tv = convertView.findViewById(R.id.processor_actual_start_end_date_tv);
            holder.processor_plan_start_end_date_tv = convertView.findViewById(R.id.processor_plan_start_end_date_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (null != processorInfo) {
            holder.processorNameTv.setText(processorInfo.getName());
            holder.processor_status_tv.setText(processorInfo.getStatus());
            holder.processor_plan_invest_tv.setText(processorInfo.getPlanInvest() + "万元");
            holder.processor_actual_invest_tv.setText(processorInfo.getActualInvest() + "万元");
            holder.processor_plan_count_tv.setText(processorInfo.getPlanCount() + "个");
            String realBeginDate = TextUtil.substringTime(processorInfo.getRealBeginDate());
            String realEndnDate = TextUtil.substringTime(processorInfo.getRealEndDate());
            holder.processor_actual_start_end_date_tv.setText(realBeginDate + " - " + realEndnDate);

            String planBeginDate = TextUtil.substringTime(processorInfo.getPlanBeginDate());
            String planEndnDate = TextUtil.substringTime(processorInfo.getPlanEndDate());
            holder.processor_plan_start_end_date_tv.setText(planBeginDate + " - " + planEndnDate);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlanListActivity.class);
                    intent.putExtra(KeyConstant.TITLE, processorInfo.getName());
                    intent.putExtra(KeyConstant.id, processorInfo.getId());
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public void setData(List<ProcessorProgressInfo> processorList) {
        progressList = processorList;
        notifyDataSetChanged();
    }


    public class ViewHolder {
        private TextView positionTv, processorNameTv, processor_status_tv, processor_actual_invest_tv, processor_actual_start_end_date_tv, processor_plan_invest_tv,
                processor_plan_count_tv, processor_plan_start_end_date_tv;
    }

}
