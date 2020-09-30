package com.android.waylinkage.activity.frag0;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.DeviceInfo;
import com.android.waylinkage.bean.DictTypeInfo;
import com.android.waylinkage.bean.PDMInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 */
@SuppressLint("ValidFragment")
public class EdmListBuildSiteFragment extends Fragment {

    public String TAG = EdmListBuildSiteFragment.class.getSimpleName();
    private String id;
    private int infoType;
    private int tabType;
    private BaseFgActivity context;
    private LinearLayout listLayout;
    private TextView mTitleTv, top0Tv, top1Tv, top2Tv, top3Tv;
    private String URL_TYPE = "", URL_START = "";
    private View view;
    private String seeMoreStr;
    private String titleTvStr;
    private long planInTime;
    private long planOutTime;
    private TextView numberTv;
    private List<DictTypeInfo> deviceTypeList = new ArrayList<>();
    private TextView emptyTv;

    public EdmListBuildSiteFragment(int type, int infoType, String id) {
        this.tabType = type;
        this.infoType = infoType;
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        context = (BaseFgActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_project_people, null);
        emptyTv = view.findViewById(R.id.empty_tv);
        if (infoType == -1) {//项目
            URL_TYPE = UrlConstant.projectId;
        } else if (infoType == -2) {//标段
            URL_TYPE = UrlConstant.contractId;
        } else if (infoType >= 0) {//桥梁,路基,隧道
            URL_TYPE = UrlConstant.buildSiteId;
        }

        URL_START = tabType == 3 ? UrlConstant.employee_summary : tabType == 4
                ? UrlConstant.biz_devices : UrlConstant.biz_materials;

        listLayout = (LinearLayout) view.findViewById(R.id.layout_project_people_layout);

        titleTvStr = tabType == 3 ? "人数" : tabType == 4 ? "数量" : "数量";

        seeMoreStr = tabType == 3 ? "人员管理" : tabType == 4 ? "设备管理"
                : "出入库记录";

        //getData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + URL_START + URL_TYPE + id;

        //人员
        if (tabType == 3) {
            if (infoType == -1) {//项目
                URL_TYPE = UrlConstant.url_biz_projects;
            } else if (infoType == -2) {//标段
                URL_TYPE = UrlConstant.url_biz_contracts;
            } else if (infoType >= 0) {//桥梁,路基,隧道
                URL_TYPE = UrlConstant.url_biz_buildSites;
            }
            url = Constant.WEB_SITE + URL_TYPE + "/" + id + URL_START;
        } else if (tabType == 4) {
            url = Constant.WEB_SITE + "/biz/devices/stat?buildSiteId=" + id;
        }

        Response.Listener<List<PDMInfo>> successListener = new Response
                .Listener<List<PDMInfo>>() {
            @Override
            public void onResponse(List<PDMInfo> result) {
                //设置布局
                setItemView(result);
            }
        };

        Request<List<PDMInfo>> versionRequest = new
                GsonRequest<List<PDMInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<List<PDMInfo>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    private void setItemView(List<PDMInfo> result) {
        listLayout.removeAllViews();
        if (result == null || result.size() == 0) {
            emptyTv.setText(context.getString(R.string.no_data));
            emptyTv.setVisibility(View.VISIBLE);
            return;
        }

        final Intent intent = new Intent(context, EdmDetailListActivity.class);
        for (final PDMInfo itemInfo : result) {

            final View itemView = View.inflate(context, R.layout.item_edm_summary, null);

            mTitleTv = (TextView) itemView.findViewById(R.id.people_item_tilte_tv);
            numberTv = (TextView) itemView.findViewById(R.id.edm_number_tv);

            final String unit = itemInfo.getUnit() == null ? "人" : itemInfo.getUnit();

            String titleStr = itemInfo.getName();
            int realInNumbers = itemInfo.getRealInNumbers();
            if (tabType == 4) {//设备
                List<DeviceInfo> details = itemInfo.getDetails();
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) details);//序列化,要注意转化(Serializable)
                intent.putExtras(bundle);
                realInNumbers = 0;
                if (details != null && details.size() > 0) {
                    for (DeviceInfo detail : details) {
                        realInNumbers = realInNumbers + detail.numbers;

                    }
                }
            }
            mTitleTv.setText(titleStr);
            if (tabType == 3) {
                numberTv.setText(itemInfo.getCurrentInNumbers() + unit);
            } else {
                numberTv.setText(realInNumbers + unit);
            }

            top0Tv = (TextView) itemView.findViewById(R.id.top_tv_0_tv);
            top2Tv = (TextView) itemView.findViewById(R.id.top_tv_2_tv);

            int planInNumbers = tabType == 3 ? itemInfo.getPlanInPeoples() :
                    itemInfo.getPlanInNumbers();
            top0Tv.setText(planInNumbers + unit);//计划进场数量

            top2Tv.setText(realInNumbers + unit);//实际进场数量

            top1Tv = (TextView) itemView.findViewById(R.id.top_tv_1_tv);
            top3Tv = (TextView) itemView.findViewById(R.id.top_tv_3_tv);

            top1Tv.setText(itemInfo.getPlanInDate());//计划日期
            top3Tv.setText(TextUtil.substringTime(itemInfo.getRealInDate()));//实际日期

            final String finalTitleStr = titleStr;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemInfoId = itemInfo.getId();
                    String employeesUrl = "";
                    if (infoType == -2) {//标段
                        employeesUrl = Constant.WEB_SITE + UrlConstant.url_biz_buildSites + "/" + itemInfoId + "/employees";
                    } else if (infoType >= 0) {//桥梁,路基,隧道
                        employeesUrl = Constant.WEB_SITE + UrlConstant.url_groups + "/" + itemInfoId + "/employees";
                    }
                    intent.putExtra(KeyConstant.TITLE, finalTitleStr);
                    intent.putExtra(KeyConstant.id, itemInfoId + "");
                    intent.putExtra(KeyConstant.employees_url, employeesUrl);
                    intent.putExtra(KeyConstant.parentType, tabType);//人员,设备,材料

                    //设备detail
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) itemInfo.getDetails());//序列化,要注意转化(Serializable)
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
            //详情,删除
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showPopWindow(itemView, itemInfo, finalTitleStr, unit);
                    return false;
                }
            });
            listLayout.addView(itemView);

        }
    }

    private void showPopWindow(final View v, final PDMInfo info, final String name,
                               final String unit) {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.popup_two_bt, null);

        final PopupWindow popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        v.getLocationOnScreen(location);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        //显示在上方
        popWindow.showAsDropDown(v, ImageUtil.getScreenWidth(context) / 2 - 170, -(v.getHeight() * 2 - 30));
        View.OnClickListener itemMenuPopupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                switch (view.getId()) {
                    case R.id.two_bt_0:
                        if (tabType == 4) {
                            //先请求
                            String url = Constant.WEB_SITE + "/biz/devices/" + info.getId();
                            Response.Listener<PDMInfo> successListener = new Response
                                    .Listener<PDMInfo>() {
                                @Override
                                public void onResponse(PDMInfo result) {
                                    //设置布局
                                    if (result == null) {
                                        return;
                                    }
                                    showDeatailDialog(result, name, unit);
                                }
                            };
                            Request<PDMInfo> versionRequest = new
                                    GsonRequest<PDMInfo>(
                                            Request.Method.GET, url,
                                            successListener, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            ToastUtil.show(context, "获取设备类型详情数据失败");
                                        }
                                    }, new TypeToken<PDMInfo>() {
                                    }.getType()) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                                            return params;
                                        }
                                    };
                            App.requestQueue.add(versionRequest);
                        } else {
                            showDeatailDialog(info, name, unit);
                        }
                        break;
                    case R.id.two_bt_1:
                        showDeleteDialog(v, info.getId(), name);
                        break;
                }
            }
        };
        inflate.findViewById(R.id.two_bt_0).setOnClickListener(itemMenuPopupClickListener);
        inflate.findViewById(R.id.two_bt_1).setOnClickListener(itemMenuPopupClickListener);
    }

    private void showDeatailDialog(PDMInfo info, String name, String unit) {
        View dilaogView = View.inflate(context, R.layout.dialog_group_detail, null);
        TextView titleTv0 = (TextView) dilaogView.findViewById(R.id.group_title_0);
        TextView titleTv1 = (TextView) dilaogView.findViewById(R.id.group_title_1);
        TextView titleTv2 = (TextView) dilaogView.findViewById(R.id.group_title_2);
        TextView titleTv5 = (TextView) dilaogView.findViewById(R.id.group_title_5);
        TextView titleTv6 = (TextView) dilaogView.findViewById(R.id.group_title_6);
        TextView tv0 = (TextView) dilaogView.findViewById(R.id.device_item_tv_0);
        TextView tv1 = (TextView) dilaogView.findViewById(R.id.device_item_tv_1);
        TextView tv2 = (TextView) dilaogView.findViewById(R.id.device_item_tv_2);
        TextView tv3 = (TextView) dilaogView.findViewById(R.id.device_item_tv_3);
        TextView tv4 = (TextView) dilaogView.findViewById(R.id.device_item_tv_4);
        TextView tv5 = (TextView) dilaogView.findViewById(R.id.device_item_tv_5);
        TextView tv6 = (TextView) dilaogView.findViewById(R.id.device_item_tv_6);
        tv0.setText("未知");
        int planInNumbers;
        if (tabType == 3) {
            planInNumbers = info.getPlanInPeoples();
        } else {
            titleTv0.setText("单位");
            tv0.setText(unit);
            titleTv1.setText("计划进场数量");
            titleTv2.setText("实际进场数量");
            unit = "";
            planInNumbers = info.getPlanInNumbers();
        }

        tv1.setText(planInNumbers + unit);
        tv2.setText(info.getRealInNumbers() + unit);
        tv3.setText(TextUtil.substringTime(info.getPlanInDate()));
        tv4.setText(TextUtil.substringTime(info.getRealInDate()));
        tv5.setText(TextUtil.substringTime(info.getPlanOutDate()));
        tv6.setText(TextUtil.substringTime(info.getRealOutDate()));

        if (tabType == 5) {
            titleTv5.setText("保管人员");
            tv5.setText(info.getKeeperName());
            titleTv6.setText("保管人手机");
            tv6.setText(info.getKeeperPhone());
        }
        new MaterialDialog.Builder(context)
                .title(name).titleGravity(GravityEnum.CENTER)
                .customView(dilaogView, true)
                .positiveText(R.string.close)
                .positiveColorRes(R.color.color999999)
                .show();
    }

    public void showDeleteDialog(final View itemView, final int id, final String text) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = View.inflate(context, R.layout.layout_dialog_logout, null);

        TextView title_tv = (TextView) inflate.findViewById(R.id.dialog_top_title_tv);
        title_tv.setText("确定删除" + text + "吗?");
        TextView sureBt = (TextView) inflate.findViewById(R.id.logout_yes_bt);
        sureBt.setText("删除   " + text);
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, context.getString(R.string.no_network));
                    return;
                }

                String url = Constant.WEB_SITE + "/biz/" + (tabType == 3 ? "groups/" : tabType == 4 ? "devices/" : "materials/") + id;

                StringRequest jsonObjRequest = new StringRequest(Request.Method.DELETE, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String result) {
                                Log.d(TAG, "删除成功返回" + result);
                                if (result == null) {
                                    ToastUtil.show(context, "删除失败,稍后重试");
                                    return;
                                } else {
                                    ToastUtil.show(context, "删除成功");
                                    listLayout.removeView(itemView);
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (null != error && error.networkResponse != null
                                && error.networkResponse.statusCode == 400) {
                            DialogUtils.showTipDialog(context, context.getString(R.string.groups_cant_delete));
                        } else {
                            ToastUtil.show(context, "删除失败,稍后重试");
                        }
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_add_people_group, null);
        Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);

        final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id.dialog_add_card_title);
        final MaterialEditText planInPeopleNumEt = (MaterialEditText) v.findViewById(R.id.add_group_plan_in_people_numb_et);

        v.findViewById(R.id.real_start_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContent.clearFocus();
                showTimeSet((TextView) v, 0);
            }
        });

        v.findViewById(R.id.real_end_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet((TextView) v, 1);
                etContent.clearFocus();
            }
        });


        etContent.setHint("班组名称");
        etContent.setFloatingLabelText("班组名称");
        planInPeopleNumEt.setHint("计划进场人数");
        planInPeopleNumEt.setFloatingLabelText("计划进场人数");

        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String groupTitle = etContent.getText().toString();
                final String planInPeopleNum = planInPeopleNumEt.getText().toString();
                if (TextUtil.isEmpty(groupTitle)) {
                    etContent.setError(context.getString(R.string.enter_cannot_empty));
                    ToastUtil.show(context, "请填写班组名称");
                    return;
                }
                if (TextUtil.isEmpty(planInPeopleNum)) {
                    ToastUtil.show(context, "请填写计划进场人数");
                    return;
                }
                if (planInTime == 0) {
                    ToastUtil.show(context, "请选择计划进场日期");
                    return;
                }
                if (planOutTime == 0) {
                    ToastUtil.show(context, "请选择计划退场日期");
                    return;
                }
                String url = Constant.WEB_SITE + UrlConstant.url_groups;
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, context.getString(R.string.no_network));
                    return;
                }

                JSONObject j = new JSONObject();
                try {
                    j.put(KeyConstant.id, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Map<String, Object> map = new HashMap<>();
                map.put(KeyConstant.bizBuildSite, j);
                map.put(KeyConstant.name, groupTitle);
                map.put(KeyConstant.planInPeoples, planInPeopleNum);
                map.put(KeyConstant.planInDate, DateUtil.millonsToUTC(planInTime));
                map.put(KeyConstant.planOutDate, DateUtil.millonsToUTC(planOutTime));

                JSONObject jsonObject = new JSONObject(map);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject result) {
                                if (result == null) {
                                    ToastUtil.show(context, "添加班组失败");
                                    return;
                                }
                                getData();
                                ToastUtil.show(context, "添加班组成功");
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.show(context, context.getString(R.string.server_exception));
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);

                        return params;
                    }
                };
                App.requestQueue.add(jsonRequest);
            }
        });
    }

    private void showTimeSet(final TextView tv, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            planInTime = millseconds;
                        } else {
                            planOutTime = millseconds;

                            if (planOutTime <= planInTime) {
                                ToastUtil.show(context, "计划退场日期不得早于计划进场日期");
                                return;
                            }
                        }
                        tv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                        tv.setBackgroundResource(R.drawable.shape_tab_seleted);
                        tv.setTextColor(context.getResources().getColor(R.color.mainColor));

                    }
                })

                .setTitleStringId("")//标题
                .setCyclic(false)
                .setCancelStringId(context.getString(R.string.time_dialog_title_cancel))
                .setSureStringId(context.getString(R.string.time_dialog_title_sure))
                .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                .setThemeColor(context.getResources().getColor(R.color.mainColorDrak))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(16)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

    //新增
    public void onAddBtClick() {
        //人员
        if (tabType == 3) {
            showAddGroupDialog();
            //设备
        } else if (tabType == 4) {
            showAddDeviceTypeDialog();
        } else {
            //材料
            Intent intent = new Intent(context, MaterailAddActivity.class);
            intent.putExtra(KeyConstant.id, id);
            context.startActivity(intent);
        }
    }

    //添加设备类型
    int deviceType = 0;

    private void showAddDeviceTypeDialog() {
        deviceType = 0;
        planInTime = 0;
        planOutTime = 0;
        getDeviceType();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_add_device_type, null);
        Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);

        final TextView deviceNameTv = (TextView) v.findViewById(R.id.dialog_add_card_title);
        final EditText planDeviceInNumEt = (EditText) v.findViewById(R.id.add_group_plan_in_people_numb_et);

        deviceNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceTypeList.size() == 0) {
                    ToastUtil.show(context, "获取设备类型失败");
                    return;
                }
                List<String> typeArr = new ArrayList<>();
                for (DictTypeInfo dictTypeInfo : deviceTypeList) {
                    typeArr.add(dictTypeInfo.getName());
                }
                new MaterialDialog.Builder(context)
                        .items(typeArr)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                deviceNameTv.setText(text);
                                deviceType = position + 1;
                            }
                        })
                        .autoDismiss(true)//自动消失
                        .show();
            }
        });

        v.findViewById(R.id.real_start_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceNameTv.clearFocus();
                showTimeSet((TextView) v, 0);
            }
        });

        v.findViewById(R.id.real_end_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet((TextView) v, 1);
                deviceNameTv.clearFocus();
            }
        });


        final Dialog dialog = builder.create();
        dialog.show();
        planDeviceInNumEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        });
        dialog.getWindow().setContentView(v);
        dialog.getWindow().setGravity(Gravity.CENTER);
        //保存
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final String planInNum = planDeviceInNumEt.getText().toString();
                if (deviceType == 0) {
                    ToastUtil.show(context, "请选择设备类型");
                    return;
                }
                if (TextUtil.isEmpty(planInNum)) {
                    ToastUtil.show(context, "请填写计划进场设备数量");
                    return;
                }
                if (planInTime == 0) {
                    ToastUtil.show(context, "请选择计划进场日期");
                    return;
                }
                if (planOutTime == 0) {
                    ToastUtil.show(context, "请选择计划退场日期");
                    return;
                }

                if (planOutTime < planInTime) {
                    ToastUtil.show(context, "退场日期不能小于进场日期");
                }
                String url = Constant.WEB_SITE + UrlConstant.url_biz_devices;
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, context.getString(R.string.no_network));
                    return;
                }

                JSONObject j = new JSONObject();
                try {
                    j.put(KeyConstant.id, id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Map<String, Object> map = new HashMap<>();
                map.put(KeyConstant.bizBuildSite, j);
                map.put(KeyConstant.type, deviceType);
                map.put(KeyConstant.planInNumbers, planInNum);
                map.put(KeyConstant.planInDate, DateUtil.millonsToUTC(planInTime));
                map.put(KeyConstant.planOutDate, DateUtil.millonsToUTC(planOutTime));

                JSONObject jsonObject = new JSONObject(map);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject result) {
                                if (result == null) {
                                    ToastUtil.show(context, "添加设备类型失败");
                                    return;
                                }
                                getData();
                                ToastUtil.show(context, "添加设备类型成功");
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            ToastUtil.show(context, "工地下已存在该设备类型");
                        } else {
                            ToastUtil.show(context, context.getString(R.string.server_exception));
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);

                        return params;
                    }
                };
                App.requestQueue.add(jsonRequest);
            }
        });
    }

    //数据类型
    private void getDeviceType() {
        //先请求
        String url = Constant.WEB_SITE + "/dict/dicts/cached/DEVICE_TYPE";
        Response.Listener<List<DictTypeInfo>> successListener = new Response
                .Listener<List<DictTypeInfo>>() {
            @Override
            public void onResponse(List<DictTypeInfo> result) {
                //设置布局
                if (result == null) {
                    return;
                }
                deviceTypeList = result;
            }
        };
        Request<List<DictTypeInfo>> versionRequest = new
                GsonRequest<List<DictTypeInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<DictTypeInfo>>() {
                }.getType()) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }
}
