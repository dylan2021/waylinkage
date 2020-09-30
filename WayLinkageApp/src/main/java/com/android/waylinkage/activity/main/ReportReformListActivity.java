package com.android.waylinkage.activity.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 */
public class ReportReformListActivity extends BaseFgActivity {
    private String TAG = ReportReformListActivity.class.getSimpleName();
    private ReportReformListActivity context;
    private TextView titleTv;
    private List<ReportBean.ContentBean> mDataList = new ArrayList<>();
    private ListView mListView;
    private ReportReformListAdapter mAdapter;
    private String activityType;
    private TabLayout mTopTab;
    private boolean mIsReportType, mIsQualityType, mIsAfficheType, mIsInicateType;
    private RefreshLayout mRefreshLayout;
    private String[] topArrReport = {"待确认", "已确认"};
    private String[] arrQualityOverdue = {"待整改", "已确认"};
    private String[] topArrQuality = {"待确认", "已确认", "待整改"};
    private int mTabPosition;
    private String buildSiteId = "";
    private boolean isProgress = true;
    private boolean isOverdue = false;
    private String url_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_report_progress_quality_list);
        context = this;
        activityType = getIntent().getStringExtra(KeyConstant.reportIdicateType);
        buildSiteId = getIntent().getStringExtra(KeyConstant.id);
        isProgress = getIntent().getBooleanExtra(KeyConstant.isProgress, true);
        isOverdue = getIntent().getBooleanExtra(KeyConstant.isOverdue, false);
        mIsReportType = KeyConstant.TYPE_REPORT.equals(activityType);
        mIsAfficheType = KeyConstant.TYPE_AFFICHE.equals(activityType);
        mIsInicateType = KeyConstant.TYPE_IDICATE.equals(activityType);

        url_type = App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE?"buildSiteId=":
                App.CHOOSE_AUTH_TYPE ==Constant.CONTRACT?"contractId=":"projectId=";
        init();
    }

    private void init() {
        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopTab = (TabLayout) findViewById(R.id.fragment0_report_top_tab);
        emptyTv = findViewById(R.id.empty_tv);
        titleTv = (TextView) findViewById(R.id.center_tv);

        if (mIsReportType) {//汇报
            titleTv.setText(isProgress ? "进度汇报" : isOverdue ? "质检结果" : "质量汇报");
        }


        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        //mRefreshLayout.setPrimaryColors(Color.WHITE);//刷新头的背景颜色
        mRefreshLayout.autoRefresh();

        String[] tabArr = isProgress ? topArrReport : isOverdue ? arrQualityOverdue : topArrQuality;
        for (String tabTitle : tabArr) {
            TabLayout.Tab tab = mTopTab.newTab();
            tab.setTag(tabTitle);
            tab.setText(tabTitle);
            mTopTab.addTab(tab);
        }
        mTopTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabPosition = tab.getPosition();
                getListData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mListView = (ListView) findViewById(R.id.hub_circle_lv);
        mAdapter = new ReportReformListAdapter(context,
                mIsReportType, mIsInicateType, mIsAfficheType);
        mListView.setAdapter(mAdapter);

        Utils.setLoadHeaderFooter(context, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                //请求数据
                getListData();
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.longShow(context, getString(R.string.no_more_data));
            }
        });

    }

    //请求数据
    private void getListData() {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        mAdapter.setData(null);
        if (mIsReportType) {
            // 获取进度
            if (isProgress) {
                getProgressData(mRefreshLayout, mTabPosition);
                //获取质量
            } else {
                getQualityListData(mRefreshLayout, mTabPosition);
            }

        } else {
            mRefreshLayout.finishRefresh(0);
            ToastUtil.show(context, getString(R.string.no_data));
        }
    }

    private void getQualityListData(final RefreshLayout refreshLayout, final int tabPosition) {
        int status = tabPosition == 0 ? (isOverdue ? 2 : 1) : tabPosition == 1 ? 4 : 2;//1 待确定4已确认

        String url = Constant.WEB_SITE + "/biz/qualities/all?" +url_type+ buildSiteId + "&status=" + status;
        Response.Listener<List<QualityReportBean.ContentBean>> successListener =
                new Response.Listener<List<QualityReportBean.ContentBean>>() {
                    @Override
                    public void onResponse(List<QualityReportBean.ContentBean> result) {
                        refreshLayout.finishRefresh(0);
                        if (result == null || result.size() == 0) {
                            emptyTv.setText(context.getString(R.string.no_data));
                            emptyTv.setVisibility(View.VISIBLE);
                            return;
                        }
                        //汇报数据
                        mAdapter.setIsProgress(isProgress, tabPosition, isOverdue);
                        Collections.reverse(result);
                        mAdapter.setQualityData(result);
                    }
                };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                emptyTv.setText(context.getString(R.string.server_exception));
                emptyTv.setVisibility(View.VISIBLE);
                refreshLayout.finishRefresh(0);
            }
        };

        Request<List<QualityReportBean.ContentBean>> request =
                new GsonRequest<List<QualityReportBean.ContentBean>>(Request.Method.GET,
                        url,
                        successListener, errorListener, new TypeToken<List<QualityReportBean.ContentBean>>() {
                }.getType()) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(request);
    }


    private void getProgressData(final RefreshLayout refreshLayout, final int tabPosition) {
        String url = Constant.WEB_SITE + "/biz/processorFins?"+url_type + buildSiteId
                + "&status=" + tabPosition;
        Response.Listener<ReportBean> successListener = new Response.Listener<ReportBean>() {
            @Override
            public void onResponse(ReportBean result) {
                refreshLayout.finishRefresh(0);
                if (result == null || result.getContent() == null || result.getContent().size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    return;
                }
                //汇报数据
                List<ReportBean.ContentBean> reportData = result.getContent();
                Collections.reverse(reportData);
                mAdapter.setIsProgress(isProgress, tabPosition, isOverdue);
                mAdapter.setData(reportData);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                emptyTv.setText(context.getString(R.string.server_exception));
                emptyTv.setVisibility(View.VISIBLE);
                refreshLayout.finishRefresh(0);
            }
        };

        Request<ReportBean> request = new GsonRequest<ReportBean>(Request.Method.GET,
                url,
                successListener, errorListener, new TypeToken<ReportBean>() {
        }.getType()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };
        App.requestQueue.add(request);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != mRefreshLayout) {
            mRefreshLayout.autoRefresh(0);
        }
    }

    //筛选
    private void filter() {
        final Dialog dialog = new Dialog(context, R.style.dialog_top_to_bottom);
        dialog.setCanceledOnTouchOutside(true);

        View inflate = LayoutInflater.from(context).inflate(R.layout.
                layout_fragment_2_top_filter, null);

        View.OnClickListener mDialogClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //完成
                    case R.id.menu_ok_bt:
                        dialog.dismiss();
                        break;
                    case R.id.filter_time_bt_0:
                    case R.id.filter_time_bt_1:
                        showPickeTimeDilog((TextView) v, "");
                        break;
                }
            }
        };
        inflate.findViewById(R.id.menu_ok_bt).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.filter_time_bt_0).setOnClickListener(mDialogClickLstener);
        inflate.findViewById(R.id.filter_time_bt_1).setOnClickListener(mDialogClickLstener);
        dialog.setContentView(inflate);

        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    private void showPickeTimeDilog(final TextView timeBt, String title) {

        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        timeBt.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                        timeBt.setBackgroundResource(R.drawable.shape_tab_seleted);
                        timeBt.setTextColor(context.getResources().getColor(R.color.mainColor));
                    }
                })
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setCancelStringId(getString(R.string.time_dialog_title_cancel))
                .setSureStringId(getString(R.string.time_dialog_title_sure))
                .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                .setThemeColor(context.getResources().getColor(R.color.mainColorDrak))
                .setType(Type.YEAR_MONTH_DAY)
                .setWheelItemTextSize(16)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }
}