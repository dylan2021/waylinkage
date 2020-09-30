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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.ItemInfo;

import java.util.List;

public class EmployeeRecordAdapter extends BaseAdapter {
    private List<ItemInfo> gameInfoList;
    private Context context;

    public EmployeeRecordAdapter(Context context, List<ItemInfo> gameInfoList) {
        super();
        this.context = context;
        this.gameInfoList = gameInfoList;
    }

    public void setDate(List<ItemInfo> gameInfos) {
        this.gameInfoList = gameInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (gameInfoList != null) {
            return gameInfoList.size();
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
        final ItemInfo peopleBean = (gameInfoList == null) ? null :
                gameInfoList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.item_employee_record, parent, false);
            holder.tv1 = (TextView) convertView.findViewById(R.id.people_lv_1_tv);
            holder.tv2 = (TextView) convertView.findViewById(R.id.people_lv_2_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv1.setText(peopleBean.str0);
        holder.tv2.setText(peopleBean.str1 + "\n" + peopleBean.str2);

        return convertView;
    }

    public class ViewHolder {
        private TextView tv1, tv2, tv3;

    }

}














