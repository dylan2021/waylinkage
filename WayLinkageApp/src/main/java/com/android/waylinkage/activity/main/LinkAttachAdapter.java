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

package com.android.waylinkage.activity.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.LinkInfo;
import com.android.waylinkage.util.TextUtil;

import java.util.List;

/**
 * Gool Lee
 */
public class LinkAttachAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<LinkInfo> mList;
    private Context context;

    public LinkAttachAdapter(MoreActivity context, List<LinkInfo> linkInfos) {
        this.mList = linkInfos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_activity_link_lv, parent, false);

            holder.nameTv = (TextView) convertView.findViewById(R.id.link_item_name);
            holder.timeTv = (TextView) convertView.findViewById(R.id.link_item_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LinkInfo itemInfo = mList.get(position);
        String name = itemInfo.linkName;
        String time = itemInfo.linkTime;
        if (!TextUtil.isEmpty(name)) {
            holder.nameTv.setText(name);
            holder.timeTv.setText(time);
        }
        return convertView;
    }

    public class ViewHolder {
        private TextView nameTv, timeTv;


    }

}














