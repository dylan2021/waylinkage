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

public class PlanListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<ProcessorProgressInfo> progressList = new ArrayList<>();
    private PlanListActivity context;

    public PlanListAdapter(PlanListActivity context) {
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
        final ProcessorProgressInfo planInfo = progressList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new PlanListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_plan, viewGroup, false);
            holder.processorNameTv = convertView.findViewById(R.id.name_tv);
            holder.positionTv = convertView.findViewById(R.id.positon_ciycle_tv);
            holder.processor_status_tv = convertView.findViewById(R.id.processor_status_tv);
            holder.processor_plan_invest_tv = convertView.findViewById(R.id.processor_plan_invest_tv);
            holder.processor_actual_invest_tv = convertView.findViewById(R.id.processor_actual_invest_tv);
            holder.processor_actual_start_end_date_tv = convertView.findViewById(R.id.processor_actual_start_end_date_tv);
            holder.processor_plan_start_end_date_tv = convertView.findViewById(R.id.processor_plan_start_end_date_tv);
            convertView.setTag(holder);
        } else {
            holder = (PlanListAdapter.ViewHolder) convertView.getTag();
        }
        if (null != planInfo) {
            holder.positionTv.setText(position + 1 + "");
            //holder.positionTv.setText(position + 1 + "");
            holder.processorNameTv.setText(planInfo.getName());
            holder.processor_status_tv.setText(planInfo.getStatus());
            holder.processor_plan_invest_tv.setText(planInfo.getInvest() + "万元");
            holder.processor_actual_invest_tv.setText(planInfo.getActualInvest() + "万元");
            String realBeginDate = TextUtil.substringTime(planInfo.getRealBeginDate());
            String realEndnDate = TextUtil.substringTime(planInfo.getRealEndDate());
            holder.processor_actual_start_end_date_tv.setText(realBeginDate + " - " + realEndnDate);

            String planBeginDate = TextUtil.substringTime(planInfo.getPlanBeginDate());
            String planEndnDate = TextUtil.substringTime(planInfo.getPlanEndDate());
            holder.processor_plan_start_end_date_tv.setText(planBeginDate + " - " + planEndnDate);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlanProgressDetailActivity.class);
                    intent.putExtra(KeyConstant.TITLE, "");
                    intent.putExtra(KeyConstant.id, planInfo.getId());
                    context.startActivity(intent);
                }
            });
            if (position == getCount() - 1) {
                convertView.setPadding(0,0,0,35);
            }
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
