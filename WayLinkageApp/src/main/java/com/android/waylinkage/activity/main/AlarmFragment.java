package com.android.waylinkage.activity.main;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.adapter.AlarmListAdapter;
import com.android.waylinkage.base.fragment.BaseSearchFragment;
import com.android.waylinkage.bean.AlarmInfo;
import com.android.waylinkage.bean.DictTypeInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 */
@SuppressLint({"WrongConstant", "ValidFragment"})
public class AlarmFragment extends BaseSearchFragment {
    private int id;
    private MainActivity context;
    private RefreshLayout mRefreshLayout;
    private TextView titleTv;
    private AlarmListAdapter alarmAdapter;
    private TextView emptyTv;
    private ListView lv;
    private List<DictTypeInfo> typeList = new ArrayList<>();
    private String categoryId = "";

    public AlarmFragment() {
    }

    public AlarmFragment(int chooseId) {
        id = chooseId;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_warning;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        context = (MainActivity) getActivity();

        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(context, mRefreshLayout);
        mRefreshLayout.autoRefresh();

        lv = (ListView) view.findViewById(R.id.listView);
        emptyTv = (TextView) view.findViewById(R.id.empty_tv);
        titleTv = (TextView) view.findViewById(R.id.alarm_center_tv);
        lv.setEmptyView(emptyTv);

        getTypeData();
        alarmAdapter = new AlarmListAdapter(context);
        lv.setAdapter(alarmAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getListData(categoryId);
            }
        });

        titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    private void showFilterDialog() {
        if (typeList.size() == 0) {
            ToastUtil.show(context, "获取设备类型失败");
            return;
        }
        final List<String> typeArr = new ArrayList<>();
        typeArr.add(getString(R.string.alarm_msg));
        for (DictTypeInfo dictTypeInfo : typeList) {
            typeArr.add(dictTypeInfo.getName());
        }
        new MaterialDialog.Builder(context)
                .items(typeArr).itemsColorRes(R.color.color212121)
                .itemsGravity(GravityEnum.CENTER)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        titleTv.setText("预警信息:" + typeArr.get(position));
                        if (position == 0) {
                            categoryId = "";
                        } else {
                            categoryId = typeList.get(position - 1).getValue();
                        }
                        getListData(categoryId);
                    }
                })
                .autoDismiss(true)
                .show();
    }

    private void getTypeData() {
        //先请求
        String url = Constant.WEB_SITE + "/dict/dicts/cached/ALARM_CATEGORY";
        Response.Listener<List<DictTypeInfo>> successListener = new Response
                .Listener<List<DictTypeInfo>>() {
            @Override
            public void onResponse(List<DictTypeInfo> result) {
                //设置布局
                if (result == null) {
                    return;
                }
                typeList = result;
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

    private void getListData(String categoryId) {
        TextUtil.initEmptyTv(context,emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz" + "/alarms/all";
        if ("" != categoryId) {
            url = url + "?category=" + categoryId;
        }
        Response.Listener<List<AlarmInfo>> successListener = new Response
                .Listener<List<AlarmInfo>>() {
            @Override
            public void onResponse(List<AlarmInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    alarmAdapter.setData(result);
                    return;
                }
                alarmAdapter.setData(result);
            }
        };

        Request<List<AlarmInfo>> versionRequest = new
                GsonRequest<List<AlarmInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<List<AlarmInfo>>() {
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

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {
        getListData(categoryId);
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }


}
