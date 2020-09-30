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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.main.MsgDetailActivity;
import com.android.waylinkage.bean.MsgInfo;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.util.List;
import java.util.Timer;


/**
 * @author Goll Lee
 * @since 2016-07-4
 */
public class MsgFragmentAdapter extends BaseAdapter {
    private Timer timer = new Timer();
    private List<MsgInfo> msgList;
    private Context context;

    public MsgFragmentAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setDate(List<MsgInfo> fileInfoList) {
        this.msgList = fileInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (msgList != null) {
            return msgList.size();
        }
        return 0;
    }


    @Override
    public Object getItem(int position) {
        if (msgList != null) {
            return msgList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final MsgInfo msgInfo = msgList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_fragment_msg_lv, parent, false);
            holder.titleTv = (TextView) convertView.findViewById(R.id.msg_item_title_tv);
            holder.contentTv = (TextView) convertView.findViewById(R.id.msg_item_content_tv);
            holder.publisherNameTv = (TextView) convertView.findViewById(R.id.msg_item_publisher_name_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.msg_item_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (msgInfo != null) {
            holder.titleTv.setText(msgInfo.getTitle());
            holder.contentTv.setText(msgInfo.getSummary());
            holder.publisherNameTv.setText(msgInfo.getPublisherName());
            holder.timeTv.setText(TextUtil.substringTime(msgInfo.getPublishTime()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MsgDetailActivity.class);
                intent.putExtra(KeyConstant.id, msgInfo.getId());
                intent.putExtra(KeyConstant.TITLE, msgInfo.getTitle());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private TextView titleTv, contentTv, publisherNameTv, timeTv;

    }

}














