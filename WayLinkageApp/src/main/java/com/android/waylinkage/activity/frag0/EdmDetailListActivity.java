package com.android.waylinkage.activity.frag0;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.DeviceInfo;
import com.android.waylinkage.bean.EmployeeInfo;
import com.android.waylinkage.bean.ItemInfo;
import com.android.waylinkage.bean.PDMDetailInfo;
import com.android.waylinkage.bean.PDMInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Gool Lee
 * 人员  设备 材料
 */
public class EdmDetailListActivity extends BaseFgActivity {

    private String TAG = EdmDetailListActivity.class.getSimpleName();
    private EdmDetailListActivity context;
    private int tabType;
    private List<ItemInfo> mMeterailOutList = new ArrayList<>();
    private List<ItemInfo> mMeterailInList = new ArrayList<>();
    private List<ItemInfo> mList = new ArrayList<>();
    private List<ItemInfo> noGroupList = new ArrayList<>();
    private String titleStr = "";
    private ListView mListView;
    private int parentType;
    private TextView mLvtitle;
    private RefreshLayout mRefreshLayout;
    private EMAdapter employeeDeviceAdapter;
    private DeviceAdapter deviceAdapter;
    private List<DeviceInfo> deviceList = new ArrayList<>();
    private int materialFilterId = 0;
    private String URL_TYPE;
    private String groupId;
    private String userStr = "";
    private String peopleUrl = "";
    private RelativeLayout employeeAddExitlayout;
    private String actionDate;
    private TextView titleTv;
    private LinearLayout metarailTopLayout;
    private String materailUnit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_device_material);

        initStatusBar();
        context = this;

        deviceList = (List<DeviceInfo>) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(this, mRefreshLayout);//设置头部,底部样式
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (5 != tabType) {
                    getListData();
                }
            }
        });

        titleTv = (TextView) findViewById(R.id.tv_title);
        employeeAddExitlayout = (RelativeLayout) findViewById(R.id.employee_add_exit_layout);
        metarailTopLayout = (LinearLayout) findViewById(R.id.layout_metarail_top_layout);
        mListView = (ListView) findViewById(R.id.people_lv);
        titleStr = getIntent().getStringExtra(KeyConstant.TITLE);
        groupId = getIntent().getStringExtra(KeyConstant.id);
        peopleUrl = getIntent().getStringExtra(KeyConstant.employees_url);
        tabType = getIntent().getIntExtra(KeyConstant.category_Id, 0);
        parentType = getIntent().getIntExtra(KeyConstant.parentType, 0);
        titleTv.setText(titleStr==null?"":Html.fromHtml(titleStr));

        if (parentType != 4) {
            if (parentType == 3) {//人员
                findViewById(R.id.pdm_right_add_bt).setVisibility(View.INVISIBLE);
                mListView.setDividerHeight(1);
                employeeAddExitlayout.setVisibility(View.VISIBLE);
                TextView emplyeeAddBt = (TextView) findViewById(R.id.emplyee_add_bt);
                TextView emplyeeExitBt = (TextView) findViewById(R.id.emplyee_exit_bt);
                emplyeeAddBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialDialog.Builder(context)
                                .items(new String[]{"批量选择", "新增"})
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                        if (0 == position) {
                                            //批量选择
                                            showEmplyeeExitDialog(noGroupList, 0);//入场
                                        } else {
                                            Intent intent = new Intent(context, EmployeeAddActivity.class);
                                            intent.putExtra(KeyConstant.groupId, groupId);
                                            startActivity(intent);
                                        }
                                    }
                                })
                                //.listSelector(R.color.green)//列表的背景颜色
                                .autoDismiss(true)//自动消失
                                .show();

                    }
                });
                emplyeeExitBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEmplyeeExitDialog(mList, 1);//退场
                    }
                });

          /*    AuthsUtils.setViewAuth(findViewById(R.id.pdm_right_add_bt),
                        AuthsConstant.BIZ_EMPLOYEES_ADD);*/
                //initPeopLeList();
            } else if (parentType == 5) {//材料
                URL_TYPE = "/biz/material/records/all?materialId=";
                TextView materialMenuTv = (TextView) findViewById(R.id.material_menu_tv);
                TextView materailInfoTopTv = (TextView) findViewById(R.id.materail_info_top_tv);
                final LinearLayout materailInfoTopLayout = (LinearLayout) findViewById(R.id.materail_info_top_layout);
                materailInfoTopTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int visibility = materailInfoTopLayout.getVisibility();
                        materailInfoTopLayout.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
                    }
                });
                metarailTopLayout.setVisibility(View.VISIBLE);
                mListView.setPadding(0, 50, 0, 0);

                materialMenuTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopWindow((TextView) v);
                    }
                });

                getInfoData();
            }
            employeeDeviceAdapter = new EMAdapter(context, mList, parentType);
            mListView.setAdapter(employeeDeviceAdapter);

        } else {//设备
            TextView tv = (TextView) findViewById(R.id.people_device_top);
            ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
            layoutParams.height = 35;
            tv.setLayoutParams(layoutParams);

            URL_TYPE = "/biz/device/details/all?deviceId=";
            deviceAdapter = new DeviceAdapter(context, deviceList);
            mListView.setAdapter(deviceAdapter);
  /*          AuthsUtils.setViewAuth(findViewById(R.id.pdm_right_add_bt),
                    AuthsConstant.BIZ_DEVICE_DETAIL_BTN_ADD);*/
        }
    }

    private void getInfoData() {
        final TextView storeManTv = (TextView) findViewById(R.id.materail_store_man_tv);
        final TextView specTv = (TextView) findViewById(R.id.materail_spec_tv);
        final TextView topRemainTv = (TextView) findViewById(R.id.materail_remain_tv);
        final TextView qualityTv = (TextView) findViewById(R.id.materail_quality_tv);
        final TextView factoryTv = (TextView) findViewById(R.id.factory_tv);
        final TextView bottomRemainNumberTv = (TextView) findViewById(R.id.remain_number_tv);
        final TextView bottomInNumberTv = (TextView) findViewById(R.id.in_number_tv);
        final TextView bottomOutNumberTv = (TextView) findViewById(R.id.out_number_tv);

        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.biz_materials + "/" + groupId;
        Response.Listener<PDMInfo> successListener = new Response
                .Listener<PDMInfo>() {
            @Override
            public void onResponse(PDMInfo result) {
                //设置布局
                if (result != null) {
                    int realInNumbers = result.getRealInNumbers();
                    int realOutNumbers = result.getRealOutNumbers();
                    materailUnit = result.getUnit();
                    int remainNumberStr = realInNumbers - realOutNumbers;

                    String keeperPhone = result.getKeeperPhone() == null ? "" : "(" + result.getKeeperPhone() + ")";
                    storeManTv.setText(result.getKeeperName() + keeperPhone);
                    specTv.setText(result.getSpec());
                    qualityTv.setText(result.getQuality());
                    factoryTv.setText(result.getFactory());

                    topRemainTv.setText(remainNumberStr + materailUnit);

                    bottomRemainNumberTv.setText(Html.fromHtml("<font color='#1890ff' ><big>" +
                            remainNumberStr + "</big></font><br/>剩余("
                            + materailUnit + ")"));
                    bottomOutNumberTv.setText(Html.fromHtml("<font color='#1890ff' ><big>" +
                            realOutNumbers + "</big></font><br/>出库(" + materailUnit + ")"));
                    bottomInNumberTv.setText(Html.fromHtml("<font color='#1890ff' ><big>"
                            + realInNumbers + "</big></font><br/>入库(" + materailUnit + ")"));

                }
            }
        };

        Request<PDMInfo> versionRequest = new
                GsonRequest<PDMInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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
    }

    private void getListData() {
        if (parentType == 3) { //人员
            if (peopleUrl != null && peopleUrl.contains(UrlConstant.url_groups)) {
                getGroupPeopleData();
            } else {
                getPeopleData();
            }
            getNoGroupPeople();
        } else if (parentType == 4) {
            getDeviceListData();
        } else {
            getMaterailData();
        }
    }

    private void getDeviceListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + URL_TYPE + groupId;
        Response.Listener<List<DeviceInfo>> successListener = new Response
                .Listener<List<DeviceInfo>>() {
            @Override
            public void onResponse(List<DeviceInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                deviceAdapter.setDate(result);
                //设置布局
            }
        };

        Request<List<DeviceInfo>> versionRequest = new
                GsonRequest<List<DeviceInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                    }
                }, new TypeToken<List<DeviceInfo>>() {
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

    private void getNoGroupPeople() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + "/biz/employees/noGroup";
        Response.Listener<JsonObject> successListener = new Response
                .Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject result) {
                noGroupList.clear();
                if (result == null || result.get(KeyConstant.content).getAsJsonArray() == null
                        || result.get(KeyConstant.content).getAsJsonArray().size() == 0
                        ) {
                    return;
                }
                JsonArray jsonArr = result.get(KeyConstant.content).getAsJsonArray();
                for (int i = 0; i < jsonArr.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArr.get(i);
                    if (jsonObject != null) {
                        int id = jsonObject.get(KeyConstant.id).getAsInt();
                        JsonElement nameObj = jsonObject.get(KeyConstant.name);
                        String name = nameObj == null ? "" : nameObj.getAsString();
                        noGroupList.add(new ItemInfo(id, name));
                    }
                }
            }
        };

        Request<JsonObject> versionRequest = new
                GsonRequest<JsonObject>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                    }
                }, new TypeToken<JsonObject>() {
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

    private void showEmplyeeExitDialog(final List<ItemInfo> list, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_emplyee_enter_exit, null);
        TextView groupPeopleNumTv = (TextView) v.findViewById(R.id.group_people_num_tv);
        TextView titleTv = (TextView) v.findViewById(R.id.exit_enter_title_tv);
        final TextView dateTv = (TextView) v.findViewById(R.id.exit_date_tv);
        final String timeTitle = type == 0 ? "入场时间" : "退场时间";
        titleTv.setText(timeTitle);
        LinearLayout itemsLayout = (LinearLayout) v.findViewById(R.id.emplyee_exit_items_layout);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                final ItemInfo itemInfo = list.get(i);
                View itemView = View.inflate(context, R.layout.layout_dialog_emplyee_item, null);
                final TextView nameTv = (TextView) itemView.findViewById(R.id.emplyee_exit_name_tv);
                nameTv.setText(itemInfo.str0);
                nameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean selected = nameTv.isSelected();
                        if (!selected) {
                            //id 加进去
                            itemInfo.str1 = "1";
                        } else {
                            itemInfo.str1 = null;
                        }
                        nameTv.setSelected(!selected);

                    }
                });
                itemsLayout.addView(itemView);
            }

        } else {
            ToastUtil.show(context, "无人员");
        }
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        actionDate = null;
        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet(dateTv);
            }
        });
        v.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.emplyee_exit_save_bt).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (TextUtil.isEmpty(actionDate)) {
                    ToastUtil.show(context, "请选择" + timeTitle);
                    return;
                }
                Map<String, Object> map = new HashMap<>();

                JSONArray employeeIds = new JSONArray();
                for (ItemInfo itemInfo : list) {
                    if (null != itemInfo.str1) {
                        employeeIds.put(itemInfo.id);
                    }
                }
                if (employeeIds.length() == 0) {
                    ToastUtil.show(context, "请至少选择一个人员");
                    return;
                }
                map.put(KeyConstant.employeeIds, employeeIds);
                map.put(KeyConstant.groupId, groupId);
                map.put(KeyConstant.actionDate, actionDate);

                final FragmentManager fm = getSupportFragmentManager();
                DialogHelper.showWaiting(fm, "加载中...");
                String url = Constant.WEB_SITE + (type == 0 ? UrlConstant.url_emplyee_enter : UrlConstant.url_emplyee_exit);
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, getString(R.string.no_network));
                    return;
                }
                JSONObject jsonObject = new JSONObject(map);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject result) {
                                DialogHelper.hideWaiting(fm);
                                getListData();
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideWaiting(fm);
                        if (error != null && error.toString().contains("JSONException: End of input at character 0 of")) {
                            DialogHelper.hideWaiting(fm);
                            getListData();
                            dialog.dismiss();
                        } else {
                            ToastUtil.show(context, getString(R.string.server_exception));

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

    private void showTimeSet(final TextView tv) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        actionDate = DateUtil.millonsToUTC(millseconds);
                        tv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
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

    @Override
    protected void onStart() {
        super.onStart();
        getListData();
    }

    private void getGroupPeopleData() {
        Response.Listener<JsonObject> successListener = new Response
                .Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject result) {
                mRefreshLayout.finishRefresh(0);
                mList.clear();
                if (result == null || result.get(KeyConstant.content).getAsJsonArray() == null
                        || result.get(KeyConstant.content).getAsJsonArray().size() == 0
                        ) {
                    ToastUtil.show(context, "该班组暂无人员");
                    if (employeeDeviceAdapter != null) {
                        employeeDeviceAdapter.setDate(mList);
                    }
                    titleTv.setText(titleStr + "(0人)");
                    return;
                }
                JsonArray jsonArr = result.get(KeyConstant.content).getAsJsonArray();
                for (int i = 0; i < jsonArr.size(); i++) {
                    JsonObject jsonObject = (JsonObject) jsonArr.get(i);
                    int id = jsonObject.get(KeyConstant.id).getAsInt();
                    String name = jsonObject.get(KeyConstant.name).getAsString();
                    mList.add(new ItemInfo(id, name));
                }

                if (employeeDeviceAdapter != null) {
                    employeeDeviceAdapter.setDate(mList);
                }
                titleTv.setText(titleStr + "(" + mList.size() + "人)");
            }
        };

        Request<JsonObject> versionRequest = new
                GsonRequest<JsonObject>(
                        Request.Method.GET, peopleUrl,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        Log.d(TAG, "返回失败" + volleyError);
                    }
                }, new TypeToken<JsonObject>() {
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

    private void getPeopleData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        Response.Listener<List<EmployeeInfo>> successListener = new Response
                .Listener<List<EmployeeInfo>>() {
            @Override
            public void onResponse(List<EmployeeInfo> result) {
                mRefreshLayout.finishRefresh(0);
                mList.clear();
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                setPeopleData(result);
                if (employeeDeviceAdapter != null) {
                    employeeDeviceAdapter.setDate(mList);
                }
            }
        };

        Request<List<EmployeeInfo>> versionRequest = new
                GsonRequest<List<EmployeeInfo>>(
                        Request.Method.GET, peopleUrl,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        Log.d(TAG, "返回失败" + volleyError);
                    }
                }, new TypeToken<List<EmployeeInfo>>() {
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

    private void getMaterailData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + URL_TYPE + groupId;
        Response.Listener<List<PDMDetailInfo>> successListener = new Response
                .Listener<List<PDMDetailInfo>>() {
            @Override
            public void onResponse(List<PDMDetailInfo> result) {
                mRefreshLayout.finishRefresh(0);
                mList.clear();
                if (result == null || result.size() == 0) {
                    if (employeeDeviceAdapter != null) {
                        employeeDeviceAdapter.setDate(mList);
                    }
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                //设置布局
                setMaterialData(result);
                if (employeeDeviceAdapter != null) {
                    employeeDeviceAdapter.setDate(mList);
                }
            }
        };

        Request<List<PDMDetailInfo>> versionRequest = new
                GsonRequest<List<PDMDetailInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                    }
                }, new TypeToken<List<PDMDetailInfo>>() {
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

    //人员
    private void setPeopleData(List<EmployeeInfo> result) {
        for (EmployeeInfo employeeInfo : result) {
            mList.add(new ItemInfo(employeeInfo.getId(), employeeInfo.getName(), employeeInfo.getPhone()));
        }
    }

    //材料
    private void setMaterialData(List<PDMDetailInfo> result) {
        mMeterailOutList.clear();
        mMeterailInList.clear();
        mList.clear();
        Collections.reverse(result);
        for (PDMDetailInfo pdmDetailInfo : result) {
            String type = pdmDetailInfo.getType();
            String userName = pdmDetailInfo.getUser();
            String numbers = pdmDetailInfo.getNumbers() + "";
            String titleStr = "";

            if ("1".equals(type)) {
                titleStr = "入库：" + numbers + materailUnit;
            } else {
                titleStr = "出库：" + numbers + materailUnit + "，领用人：" + (userName == null ? "未知" : userName);
            }
            ItemInfo info = new ItemInfo(pdmDetailInfo.getId(),
                    pdmDetailInfo.getCreateTime(), titleStr);
            if ("1".equals(type)) {//入库
                mMeterailInList.add(info);
            } else {//出库
                mMeterailOutList.add(info);
            }
            mList.add(info);
        }
    }


    public void onBackClick(View view) {
        finish();
    }

    //添加
    public void peopleAddBt(View view) {
        if (parentType == 3) {

        } else if (parentType == 4) {
            Intent intent = new Intent(context, DeviceAddActivity.class);
            intent.putExtra(KeyConstant.groupId, groupId);
            startActivity(intent);
        } else {
            materialRecordAddDialog();
        }
    }

    int TYPE = 2;

    //添加材料进出场记录
    private void materialRecordAddDialog() {
        TYPE = 1;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);

        final View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_material_record_add_dialog, null);
        final MaterialEditText numbEt = (MaterialEditText) inflate.findViewById(R.id.material_dialog_numb_et);
        final MaterialEditText usedPeopleEt = (MaterialEditText) inflate.findViewById(R.id.material_dialog_used_people_et);
        RadioButton outRb = (RadioButton) inflate.findViewById(R.id.material_dialog_rb_0);
        RadioButton inRb = (RadioButton) inflate.findViewById(R.id.material_dialog_rb_1);
        TextView materailUnitTv =  inflate.findViewById(R.id.materail_unit_tv);
        materailUnitTv.setText(materailUnit);
        final Dialog dialog = builder.create();
        dialog.show();
        usedPeopleEt.setVisibility(View.GONE);
        outRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TYPE = 2;//出库
                usedPeopleEt.setVisibility(View.VISIBLE);
            }
        });
        inRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TYPE = 1;//入库
                usedPeopleEt.setVisibility(View.GONE);
            }
        });
        inflate.findViewById(R.id.menu_ok_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numbStr = numbEt.getText().toString();
                if (TextUtil.isEmpty(numbStr)) {
                    ToastUtil.show(context, "请填写数量");
                    return;
                }
                if (TYPE == 2) {//出库 2   入库 1
                    userStr = usedPeopleEt.getText().toString();
                    if (TextUtil.isEmpty(userStr)) {
                        ToastUtil.show(context, "请填写领用人");
                        return;
                    }
                }
                JSONObject idObj = new JSONObject();
                try {
                    idObj.put(KeyConstant.id, groupId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Map<String, Object> map = new HashMap<>();
                map.put(KeyConstant.bizMaterial, idObj);

                map.put(KeyConstant.numbers, numbStr);
                map.put(KeyConstant.user, userStr);
                map.put(KeyConstant.type, TYPE + "");

                //添加

                final FragmentManager fm = getSupportFragmentManager();
                DialogHelper.showWaiting(fm, "加载中...");
                String url = Constant.WEB_SITE + UrlConstant.url_biz_material_records;
                if (!NetUtil.isNetworkConnected(context)) {
                    ToastUtil.show(context, getString(R.string.no_network));
                    return;
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(map),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject result) {
                                if (result == null) {
                                    DialogHelper.hideWaiting(fm);
                                    ToastUtil.show(context, "添加失败");
                                    return;
                                }
                                //刷新列表
                                getMaterailData();
                                getInfoData();
                                DialogHelper.hideWaiting(fm);
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.hideWaiting(fm);
                        ToastUtil.show(context, getString(R.string.server_exception));
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

        Window window = dialog.getWindow();

        window.setContentView(inflate);
        window.setBackgroundDrawableResource(R.drawable.shape_white_20px);
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setGravity(Gravity.CENTER);//可以设置显示的位置


    }

    private void showPopWindow(final TextView filterTv) {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.layout_material_out_in_menu_pupwindow, null);

        final PopupWindow popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        filterTv.getLocationOnScreen(location);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        popWindow.showAsDropDown(filterTv);

        final TextView allTv = (TextView) inflate.findViewById(R.id.report_menu_bt_0);

        final TextView outTv = (TextView) inflate.findViewById(R.id.report_menu_bt_1);
        final TextView inTv = (TextView) inflate.findViewById(R.id.report_menu_bt_2);

        // AuthsUtils.setViewAuth(outTv, AuthsConstant.BIZ_MATERIAL_OUT);//出库权限
        //AuthsUtils.setViewAuth(inTv, AuthsConstant.BIZ_MATERIAL_IN);//出库权限
        initTvColor(materialFilterId == 0 ? allTv : materialFilterId == 1 ? outTv : inTv);
        View.OnClickListener itemMenuPopupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                switch (view.getId()) {
                    //全部
                    case R.id.report_menu_bt_0:
                        materialFilterId = 0;
                        if (employeeDeviceAdapter != null) {
                            employeeDeviceAdapter.setDate(mList);
                        }
                        filterTv.setText("全部");
                        break;
                    //出库
                    case R.id.report_menu_bt_1:
                        materialFilterId = 1;
                        filterTv.setText("按出库");
                        if (employeeDeviceAdapter != null) {
                            employeeDeviceAdapter.setDate(mMeterailOutList);
                        }
                        break;
                    //入库
                    case R.id.report_menu_bt_2:
                        materialFilterId = 2;
                        if (employeeDeviceAdapter != null) {
                            employeeDeviceAdapter.setDate(mMeterailInList);
                        }
                        filterTv.setText("按入库");
                        break;
                }
            }
        };

        allTv.setOnClickListener(itemMenuPopupClickListener);
        outTv.setOnClickListener(itemMenuPopupClickListener);
        inTv.setOnClickListener(itemMenuPopupClickListener);
    }

    private void initTvColor(TextView tv) {
        tv.setTextColor(getResources().getColor(R.color.mainColor));
    }
}