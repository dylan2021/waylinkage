package com.android.waylinkage.activity.frag0;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.bean.SafyItemInfo;
import com.android.waylinkage.util.TextUtil;

import java.util.List;

/**
 * Gool Lee
 * 安全生产
 */

public class SafyListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SafyItemInfo> list;
    private SafyListActivity context;
    private int TYPE;

    public SafyListAdapter(SafyListActivity context,
                           List<SafyItemInfo> datats, int TYPE) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        list = datats;
        this.TYPE = TYPE;
    }

    public void setData(List<SafyItemInfo> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (list == null) {
            return null;
        } else {
            return list.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final SafyItemInfo postInfo = list.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new SafyListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.item_safy_list, viewGroup, false);
            holder.titleTv = convertView.findViewById(R.id.report_item_title_tv);
            holder.Tv1 = convertView.findViewById(R.id.safy_item_tv1);
            holder.Tv2 = convertView.findViewById(R.id.safy_item_tv2);
            holder.peopleTv = convertView.findViewById(R.id.safy_item_people_tv);
            holder.timeTv = convertView.findViewById(R.id.report_item_time_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (postInfo != null) {
            String typeDesc = postInfo.getTypeDesc();
            int realPeoples = postInfo.getRealPeoples();
            String trainer = postInfo.getTrainer();
            String confirmor = postInfo.getConfirmor();

            String reason = postInfo.getReason();
            String location = postInfo.getLocation();
            String lossLevel = postInfo.getLossLevel();
            String creatorUsername = postInfo.getCreatorUsername();

            String areaDesc = postInfo.getAreaDesc();
            String result = "";
            if (TYPE == 4) {
                String status = postInfo.getStatus();
                result= (String) postInfo.getResult();
                if (!"1".equals(status)) {
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.color_warning));
                }
            } else if (TYPE == 2 || TYPE == 3) {

            }
            String checker = postInfo.getChecker();
            String completed = postInfo.getCompleted();
            String completedTitle = "0".equals(completed) ? (TYPE == 3 ? "缺失" : "不合格") : "合格";

            String trainTime = TextUtil.substringTime(postInfo.getTime());
            String handTime = TextUtil.substringTime(postInfo.getHandleTime());
            String checkTime = TextUtil.substringTime((TYPE == 2 || TYPE == 3) ? postInfo.getCreateTime() : postInfo.getCheckTime());

            String title = TYPE == 0 ? typeDesc : TYPE == 1 ? location :
                    TYPE == 4 ? areaDesc : completedTitle;

            String item1 = TYPE == 0 ? "实到人数：" + realPeoples + "人" : TYPE == 1 ?
                    "伤亡程度：" + lossLevel : "确认人：" + (confirmor == null ? "未知" : confirmor);

            String item2 = TYPE == 0 ? "授课人：" + trainer : TYPE == 1 ? "事发原因：" + reason : TYPE == 4 ?
                    "检查结果:" + result : "汇报人：" + creatorUsername;
            String people = TYPE == 4 ? "检查人：" + checker : "确认人 ：" + confirmor;
            String time = TYPE == 0 ? "培训时间：" + trainTime : TYPE == 1 ? "事发时间：" + handTime : "检查时间：" + checkTime;

            //大标题
            holder.titleTv.setText(title);
            //设置颜色 2 防护用品   3 警示标志

            holder.Tv1.setText(item1);
            holder.Tv2.setText(item2);
            holder.peopleTv.setText(people == null ? "未知" : people);
            holder.timeTv.setText(time);

            if (TYPE == 2 || TYPE == 3) {
                holder.timeTv.setVisibility(View.GONE);
                holder.peopleTv.setText(time);
                if ("0".equals(completed)) {
                    holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.color_warning));
                }
            }

        }

        return convertView;
    }

    public class ViewHolder {
        private TextView titleTv, Tv1, Tv2, peopleTv, timeTv;
    }

}
