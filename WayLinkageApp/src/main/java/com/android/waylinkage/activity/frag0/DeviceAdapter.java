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

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.bean.DeviceInfo;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceAdapter extends BaseAdapter {
    private List<DeviceInfo> deviceList;
    private Activity context;
    private int parentType;

    public DeviceAdapter(Activity context, List<DeviceInfo> deviceList) {
        super();
        this.context = context;
        this.deviceList = deviceList;
        this.parentType = parentType;

    }

    public void setDate(List<DeviceInfo> gameInfos) {
        this.deviceList = gameInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (this.deviceList != null) {
            return this.deviceList.size();
        }
        return 0;
    }

    @Override
    public DeviceInfo getItem(int position) {
        if (deviceList != null) {
            return deviceList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final DeviceInfo deviceBean = (deviceList == null) ? null :
                deviceList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device_detail_lv, parent, false);
            holder.seeDetailBt = (TextView) convertView.findViewById(R.id.device_detail_see_bt);

            holder.tv0 = (TextView) convertView.findViewById(R.id.device_item_tv_0);
            holder.tv1 = (TextView) convertView.findViewById(R.id.device_item_tv_1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv0.setText(deviceBean.code);
        holder.tv1.setText(deviceBean.name);


        holder.seeDetailBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dilaogView = View.inflate(context, R.layout.dialog_device_detail, null);
                TextView tv0 = (TextView) dilaogView.findViewById(R.id.device_item_tv_0);
                TextView tv1 = (TextView) dilaogView.findViewById(R.id.device_item_tv_1);
                TextView tv2 = (TextView) dilaogView.findViewById(R.id.device_item_tv_2);
                TextView tv4 = (TextView) dilaogView.findViewById(R.id.device_item_tv_4);
                TextView realInNumTv = (TextView) dilaogView.findViewById(R.id.device_item_tv_22);
                TextView tv6 = (TextView) dilaogView.findViewById(R.id.device_item_tv_6);
                TextView tv7 = (TextView) dilaogView.findViewById(R.id.device_item_tv_7);
                TextView tv8 = (TextView) dilaogView.findViewById(R.id.device_item_tv_8);
                TextView deviceTypeTv = (TextView) dilaogView.findViewById(R.id.device_item_tv_9);
                if (deviceBean != null) {

                    tv0.setText(deviceBean.code);
                    tv1.setText(deviceBean.name);
                    deviceTypeTv.setText(deviceBean.spec);
                    tv2.setText(deviceBean.realInDate);
                    realInNumTv.setText(deviceBean.numbers+"");
                    String runStatus = "2".equals(deviceBean.status) ? "待维保" : "3".equals(deviceBean.status)
                            ? "已损坏" : "正常";
                    tv4.setText(runStatus);
                    tv6.setText(deviceBean.owner);
                    tv7.setText(deviceBean.ownerPhone);
                    String sourceStr = deviceBean.source == null ? "不详" : "1".equals(deviceBean.source) ? "自购" : "租赁";
                    tv8.setText(sourceStr);
                }
                MaterialDialog show = new MaterialDialog.Builder(context)
                        .title("设备详情").titleGravity(GravityEnum.CENTER)
                        .customView(dilaogView, true)
                        .positiveText(R.string.close)
                        .positiveColorRes(R.color.color999999)
                        .negativeText("删除该设备")
                        .negativeColorRes(R.color.red)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                showDeleteDialog(deviceBean.id,
                                        deviceBean.name + "(" + deviceBean.code + ")", position);
                            }
                        })
                        .show();

                WindowManager m = show.getWindow().getWindowManager();
                Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams p = show.getWindow().getAttributes(); //获取对话框当前的参数值
                p.height = (int)(d.getHeight()*0.8) ; //高度设置为屏幕的0.3
                p.width = d.getWidth() ; //宽度设置为屏幕的0.5
                show.getWindow().setAttributes(p); //设置生效


            }
        });

        return convertView;
    }

    public void showDeleteDialog(final int deviceId, String nameCode, final int position) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = View.inflate(context, R.layout.layout_dialog_logout, null);

        TextView title_tv = (TextView) inflate.findViewById(R.id.dialog_top_title_tv);
        title_tv.setText("确定删除该设备吗?");
        TextView sureBt = (TextView) inflate.findViewById(R.id.logout_yes_bt);
        sureBt.setText("删除   " + nameCode);
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, context.getString(R.string.no_network));
                    return;
                }
                String url = Constant.WEB_SITE + "/biz/device/details/" + deviceId;

                StringRequest jsonObjRequest = new StringRequest(Request.Method.DELETE, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                if (result == null) {
                                    ToastUtil.show(context, "设备删除失败,稍后重试");
                                    return;
                                } else {
                                    ToastUtil.show(context, "设备删除成功");
                                    deviceList.remove(position);
                                    notifyDataSetChanged();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.show(context, "设备删除失败,稍后重试");
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };

                App.requestQueue.add(jsonObjRequest);
                dialog.cancel();
            }
        });
        inflate.findViewById(R.id.logout_cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });
        dialog.setContentView(inflate);//将布局设置给Dialog
        DialogUtils.setDialogWindow(context, dialog, Gravity.BOTTOM);
    }

    public class ViewHolder {
        private TextView tv0, tv1, deviceTypeTv, seeDetailBt, tv4, tv2, tv6, tv7, tv8;

    }

}














