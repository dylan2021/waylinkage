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

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.ChooseDataInfo;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.DateUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Gool Lee
 */

public class ChooseDataAdapter extends RecyclerView.Adapter {

    private boolean isFirstLuncher;
    private Activity context;
    private List<ChooseDataInfo.DataBean> contacts;

    public ChooseDataAdapter(List<ChooseDataInfo.DataBean> contacts, Activity c, boolean isFirstLuncher) {
        this.contacts = contacts;
        this.context = c;
        this.isFirstLuncher = isFirstLuncher;
    }

    public void setData(List<ChooseDataInfo.DataBean> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_data_choose, null);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ChooseDataInfo.DataBean data = contacts.get(position);
        MyHolder holder = (MyHolder) viewHolder;
        //显示index
        if (data != null) {
            String belowName = null;
            holder.tv_name.setText(data.getName());
            ChooseDataInfo.DataBean.BizContractBean bizContract = data.getBizContract();
            if (bizContract != null) {
                String contractName = bizContract.getNameX();
                ChooseDataInfo.DataBean bizProject = bizContract.getBizProject();
                String projName = bizProject.getName();
                belowName = projName +"/" +contractName;
            } else {
                ChooseDataInfo.DataBean bizProject = data.getBizProject();
                if (bizProject != null) {
                    belowName = bizProject.getName();
                }
            }
            if (belowName != null) {
                holder.belowContractTv.setPadding(0, 15, 10, 25);
                holder.belowContractTv.setText(belowName);
            }

            holder.inverseTv.setText("总投资：" + data.getInvest() + "万元");

            String planBeginDate = data.getPlanBeginDate();
            String endDate = data.getPlanEndDate();
            String lengthStr = "";

            ChooseDataInfo.DataBean.SpecificationBean specification = data.getSpecification();
            if (specification != null) {
                String bridge_lenght = specification.getBRIDGE_LENGHT();
                lengthStr = "总长：" + bridge_lenght;
                if (null == bridge_lenght) {
                    String subgrade_length = specification.getSUBGRADE_LENGTH();
                    lengthStr = "总长：" + subgrade_length;
                    if (subgrade_length == null) {
                        String left_length = specification.getTUNNEL_LEFT_LENGTH();
                        String right_length = specification.getTUNNEL_RIGHT_LENGTH();
                        lengthStr = "左/右洞长度：\n" + left_length + "/" + right_length;
                        if (left_length == null) {
                            lengthStr = "总长：0";
                        }
                    }
                }
            } else {
                lengthStr="总长："+data.getLength();
            }
            holder.lenthTv.setText(lengthStr + "KM");
            holder.startEndTv.setText("计划起始日期：" + planBeginDate + "~" + endDate);
            int period = 0;
            try {
                Date startTime = DateUtil.getFormat().parse(planBeginDate);
                Date endTime = DateUtil.getFormat().parse(endDate);
                period = TextUtil.differentDaysByMillisecond2(endTime, startTime);
            } catch (ParseException e) {
            }
            holder.periodTv.setText("(" + period + "天)");

            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(view, data.getId(),data.getName());
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {

        TextView inverseTv, belowContractTv, startEndTv;
        TextView tv_name, periodTv, lenthTv;
        View itemView;

        public MyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            inverseTv = (TextView) itemView.findViewById(R.id.all_inverse_tv);
            startEndTv = (TextView) itemView.findViewById(R.id.all_perios_tv);//总工期
            belowContractTv = (TextView) itemView.findViewById(R.id.below_contract_tv);//总长度
            lenthTv = (TextView) itemView.findViewById(R.id.start_date_tv);//总长度
            tv_name = (TextView) itemView.findViewById(R.id.name_tv);
            periodTv = (TextView) itemView.findViewById(R.id.period_tv);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}










