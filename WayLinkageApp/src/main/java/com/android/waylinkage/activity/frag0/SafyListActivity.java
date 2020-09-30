package com.android.waylinkage.activity.frag0;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.SafyItemInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 * <p>
 * 安全生产 :   教育  事故   排查
 */
public class SafyListActivity extends BaseFgActivity {
    private SafyListActivity context;
    private List<SafyItemInfo> mDataList = new ArrayList<>();
    private ListView mListView;
    private SafyListAdapter mAdapter;
    private RefreshLayout mRefreshLayout;
    private int mTabPosition;
    private String mTabTitle, utl_type_small, utl_type_big;
    private String id = "";
    private int TYPE_SMALL;
    private long minTime;
    private long maxTime;
    private TextView emptyTv ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();

        setContentView(R.layout.activity_report_progress_quality_list);
        id = getIntent().getStringExtra(KeyConstant.id);
        mTabTitle = getIntent().getStringExtra(KeyConstant.TITLE);
        mTabPosition = getIntent().getIntExtra(KeyConstant.tab_position, 0);


        TYPE_SMALL = getIntent().getIntExtra(KeyConstant.tab_position, 0);
        utl_type_small = TYPE_SMALL == 0 ? "trains" : TYPE_SMALL == 1 ? "accidents" :
                TYPE_SMALL == 2 ? "materials" : TYPE_SMALL == 3 ? "flags" : "checks";

        //项目   标段  工地
        utl_type_big = App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? "buildSiteId=" :
                App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? "contractId=" : "projectId=";
        context = this;
        init();
    }


    private void init() {
        initTitleBackBt(mTabTitle);
        findViewById(R.id.fragment0_report_top_tab).setVisibility(View.GONE);
        View filterBt = findViewById(R.id.on_filter_bt);
        emptyTv = findViewById(R.id.empty_tv);
        filterBt.setVisibility(View.VISIBLE);
        filterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter();
            }
        });

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        mListView = (ListView) findViewById(R.id.hub_circle_lv);

        mAdapter = new SafyListAdapter(context, mDataList, TYPE_SMALL);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SafyItemInfo safyItemInfo = mDataList.get(position);
                Intent intent = new Intent(context, SafyListDetailActivity.class);
                intent.putExtra(KeyConstant.TYPE, mTabPosition);
                TextView titleTv = (TextView) view.findViewById(R.id.report_item_title_tv);
                intent.putExtra(KeyConstant.TITLE, mTabTitle);
                intent.putExtra(KeyConstant.id, safyItemInfo.getId());
                context.startActivity(intent);
            }
        });

        Utils.setLoadHeaderFooter(context, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mDataList.clear();
                getDatas(refreshLayout, 0, 0);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });
    }

    private void getDatas(final RefreshLayout refreshLayout, long minTime, long maxTime) {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_security + "/" +
                utl_type_small + UrlConstant.all + utl_type_big + id;
        if (minTime == 0 && maxTime == 0) {
        } else {
            url = url + "&start=" + DateUtil.getFormatYYYYmmdd().format(new Date(minTime))
                    + "&end=" + DateUtil.getFormatYYYYmmdd().format(new Date(maxTime));
        }
        Response.Listener<List<SafyItemInfo>> successListener = new Response
                .Listener<List<SafyItemInfo>>() {
            @Override
            public void onResponse(List<SafyItemInfo> result) {
                mDataList.clear();
                mAdapter.setData(mDataList);
                if (result == null || result.size() == 0) {
                    refreshLayout.finishRefresh(0);
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    return;
                }
                mDataList = result;
                mAdapter.setData(mDataList);

                refreshLayout.finishRefresh(0);
            }
        };

        Request<List<SafyItemInfo>> versionRequest = new
                GsonRequest<List<SafyItemInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshLayout.finishRefresh(0);
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<List<SafyItemInfo>>() {
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

    //筛选
    private void filter() {
        final Dialog dialog = new Dialog(context, R.style.dialog_top_to_bottom);
        View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_rank_filter, null);
        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.menu_ok_bt:
                        if (minTime == 0 && maxTime == 0) {
                            ToastUtil.show(context, "请选择查询时间");
                            return;
                        }
                        if (maxTime == 0) {
                            maxTime = System.currentTimeMillis();
                        }

                        getDatas(mRefreshLayout, minTime, maxTime);

                        dialog.dismiss();
                        break;
                    case R.id.filter_time_bt_0:
                        showPickeTimeDilog((TextView) v, 1);
                        break;
                    case R.id.filter_time_bt_1:
                        showPickeTimeDilog((TextView) v, 2);
                        break;
                }
            }
        };
        inflate.findViewById(R.id.menu_ok_bt).setOnClickListener(mDialogClickLstener);
        TextView minTv = inflate.findViewById(R.id.filter_time_bt_0);
        TextView maxTv = inflate.findViewById(R.id.filter_time_bt_1);
        if (minTime > 0) {
            minTv.setText(DateUtil.getStrTime_Y_M_D(minTime));
            minTv.setBackgroundResource(R.drawable.shape_tab_seleted);
            minTv.setTextColor(context.getResources().getColor(R.color.mainColor));
        }
        if (maxTime > 0) {
            maxTv.setText(DateUtil.getStrTime_Y_M_D(maxTime));
            maxTv.setBackgroundResource(R.drawable.shape_tab_seleted);
            maxTv.setTextColor(context.getResources().getColor(R.color.mainColor));
        }
        minTv.setOnClickListener(mDialogClickLstener);
        maxTv.setOnClickListener(mDialogClickLstener);
        dialog.setContentView(inflate);


        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    private void showPickeTimeDilog(final TextView timeBt, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 1) {
                            if (millseconds > System.currentTimeMillis()) {
                                ToastUtil.show(context, "开始时间不能大于现在");
                                return;
                            }
                            if (maxTime != 0 && millseconds > maxTime) {
                                ToastUtil.show(context, "开始时间不能大于结束时间");
                                return;
                            }
                            minTime = millseconds;
                        } else {
                            if (millseconds < minTime) {
                                ToastUtil.show(context, "结束时间不能小于开始时间");
                                return;
                            }
                            maxTime = millseconds;
                        }
                        timeBt.setText(DateUtil.getStrTime_Y_M_D(millseconds));
                        timeBt.setBackgroundResource(R.drawable.shape_tab_seleted);
                        timeBt.setTextColor(context.getResources().getColor(R.color.mainColor));
                    }
                })
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setThemeColor(context.getResources().getColor(R.color.mainColor))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(16)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

}