package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.ProgressOverdueInfo;
import com.android.waylinkage.util.KeyConstant;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Gool Lee
 */

public class ProcessorProgressListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<ProgressOverdueInfo> progressList = new ArrayList<>();
    ProcessorProgressListActivity context;
    private int pb0ColorId;
    private int mTabPosition;

    public ProcessorProgressListAdapter(ProcessorProgressListActivity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<ProgressOverdueInfo> data, int colorId) {
        this.progressList = data;
        pb0ColorId = colorId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return progressList == null ? 0 : progressList.size();
    }

    @Override
    public Object getItem(int i) {
        return progressList == null ? progressList : progressList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (progressList == null) {
            return null;
        }
        ProgressOverdueInfo processorInfo = progressList.get(position);

        convertView = mInflater.inflate(R.layout.item_processor_progress_item, viewGroup, false);
        TextView titleTv = convertView.findViewById(R.id.progress_item_title_tv);
        NumberProgressBar progressPb0 = convertView.findViewById(R.id.progress_item_pb0);
        NumberProgressBar progressPb1 = convertView.findViewById(R.id.progress_item_pb1);
        TextView startTimeTv = convertView.findViewById(R.id.plan_start_tiem_tv);
        TextView notStarTitleTv = convertView.findViewById(R.id.not_start_tag_iv);

        if (mTabPosition == 2) {
            progressPb0.setVisibility(View.GONE);
            progressPb1.setVisibility(View.GONE);
            titleTv.setVisibility(View.GONE);
            titleTv.setVisibility(View.GONE);
            notStarTitleTv.setVisibility(View.VISIBLE);
            startTimeTv.setCompoundDrawables(null, null, null, null);
        } else {
            startTimeTv.setBackgroundResource(R.color.white);
            progressPb1.setProgressTextColor(ContextCompat.getColor(context, pb0ColorId));
            progressPb1.setReachedBarColor(ContextCompat.getColor(context, pb0ColorId));
        }
        if (null != processorInfo) {
            final int processId = processorInfo.getId();
            //工序
            final String name = processorInfo.getName();
            titleTv.setText(name);

            //未开工
            if (mTabPosition == 2) {
                notStarTitleTv.setText(name);
                String planStartDate = processorInfo.getPlanStartDate();
                startTimeTv.setText(planStartDate == null ? "未知" : planStartDate + "开工");
            }

            int actualProgress = (int) (processorInfo.getActual());
            int planProgress = (int) (processorInfo.getPlan());

            progressPb0.setProgress(planProgress);//实际
            progressPb1.setProgress(actualProgress);//计划
            if (actualProgress == 100) {
                progressPb0.setPadding(0, 0, 35, 0);
            }
            if (planProgress == 100) {
                progressPb1.setPadding(0, 0, 35, 0);
            }

            if (mTabPosition != 2) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProcessorProgressDetailActivity.class);
                        intent.putExtra(KeyConstant.TITLE, name);
                        intent.putExtra(KeyConstant.id, processId);
                        context.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }

    public void setTabPosition(int mTabPosition) {
        this.mTabPosition = mTabPosition;
    }


}
