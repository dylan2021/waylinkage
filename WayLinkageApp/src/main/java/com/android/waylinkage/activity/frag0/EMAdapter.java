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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.ItemInfo;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.util.List;

public class EMAdapter extends BaseAdapter {
    private List<ItemInfo> gameInfoList;
    private Context context;
    private int parentType;

    public EMAdapter(Context context, List<ItemInfo> gameInfoList, int parentType) {
        super();
        this.context = context;
        this.gameInfoList = gameInfoList;
        this.parentType = parentType;

    }

    public void setDate(List<ItemInfo> gameInfos) {
        this.gameInfoList = gameInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (this.gameInfoList != null) {
            return this.gameInfoList.size();
        }
        return 0;
    }

    @Override
    public ItemInfo getItem(int position) {
        if (gameInfoList != null) {
            return gameInfoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ItemInfo peopleBean = (gameInfoList == null) ? null : gameInfoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (parentType == 5) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.item_matirals_lv, parent, false);
            } else {

                convertView = LayoutInflater.from(context).
                        inflate(R.layout.item_people_lv, parent, false);

                holder.tv3 = (TextView) convertView.findViewById(R.id.people_lv_3_tv);
            }

            holder.tv0 = (TextView) convertView.findViewById(R.id.people_lv_1_tv);
            holder.tv1 = (TextView) convertView.findViewById(R.id.people_lv_2_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (peopleBean != null) {
            final String str0 = peopleBean.str0;
            holder.tv0.setText(str0);
            String str1 = peopleBean.str1;
            String str2 = peopleBean.str2;
            if (!TextUtil.isEmpty(str1) && parentType != 3) {
                holder.tv1.setText(str1);
            }
            if (!TextUtil.isEmpty(str2)&&parentType != 3) {
                holder.tv3.setText(str2);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (parentType == 3) {
                        intent.putExtra(KeyConstant.name, str0);
                        intent.putExtra(KeyConstant.id, peopleBean.id + "");
                        intent.setClass(context, EmployeeDetailActivity.class);
                        context.startActivity(intent);
                    } else if (parentType == 4) {
                       /* intent.putExtra(KeyConstant.name, str0);
                        intent.setClass(context, DeviceDetailActivity.class);
                        context.startActivity(intent);*/
                    }
                }
            });
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView tv0, tv1, tv3;

    }

}














