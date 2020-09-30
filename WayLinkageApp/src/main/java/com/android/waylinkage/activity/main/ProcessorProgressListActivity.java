package com.android.waylinkage.activity.main;

import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.ProgressOverdueObj;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 */
public class ProcessorProgressListActivity extends BaseFgActivity {
    private String TAG = ProcessorProgressListActivity.class.getSimpleName();
    private ProcessorProgressListActivity context;
    private TextView titleTv;
    private List<ReportBean.ContentBean> mDataList = new ArrayList<>();
    private ListView mListView;
    private ProcessorProgressListAdapter mAdapter;
    private TabLayout mTopTab;
    private RefreshLayout mRefreshLayout;
    private String[] topArrQuality = {"正常", "已逾期", "暂未开工"};
    private int mTabPosition;
    private String buildSiteId = "";
    private String buildSiteName;
    private int colorId = R.color.pb_real_color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_report_progress_quality_list);
        context = this;
        buildSiteId = getIntent().getStringExtra(KeyConstant.id);
        buildSiteName = getIntent().getStringExtra(KeyConstant.TITLE);
        init();
    }


    private void initTabs() {
        //默认选中
        TabLayout.Tab tab = mTopTab.getTabAt(0);
        if (tab != null) {
            tab.select();
        }
    }

    private void init() {
        findViewById(R.id.tab_buttom_tag_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTopTab = (TabLayout) findViewById(R.id.fragment0_report_top_tab);


        titleTv = (TextView) findViewById(R.id.center_tv);
        emptyTv = (TextView) findViewById(R.id.empty_tv);

        titleTv.setText(buildSiteName + "进度总览");

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        String[] tabArr = topArrQuality;
        for (String tabTitle : tabArr) {
            TabLayout.Tab tab = mTopTab.newTab();
            tab.setTag(tabTitle);
            tab.setText(tabTitle);
            mTopTab.addTab(tab);
        }
        initTabs();
        mTopTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabPosition = tab.getPosition();
                colorId = mTabPosition == 0 ? R.color.pb_real_color : mTabPosition == 1 ?
                        R.color.pb_overdue_color : R.color.pb_real_color;
                findViewById(R.id.progress_real_view).setBackgroundResource(colorId);
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
        mListView.setDividerHeight(1);
        mAdapter = new ProcessorProgressListAdapter(context);
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
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });

    }

    //请求数据
    private void getListData() {
        mAdapter.setTabPosition(mTabPosition);
        mAdapter.setData(null, colorId);
        String urlEnd = mTabPosition == 0 ? "normal" : mTabPosition == 1 ? "overdue" : "not_started";

        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url_type = App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? "/biz/bi/buildSite" :
                App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? "/biz/bi/contract" : "/biz/bi/project";
        String url = Constant.WEB_SITE + url_type
                + "/progress?id=" + buildSiteId + "&progressType=" + urlEnd;

        Response.Listener<ProgressOverdueObj> successListener = new Response
                .Listener<ProgressOverdueObj>() {
            @Override
            public void onResponse(ProgressOverdueObj result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.getData() == null || result.getData().size() == 0) {
                    mAdapter.setData(null, colorId);
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    return;
                }
                mAdapter.setData(result.getData(), colorId);
            }
        };

        Request<ProgressOverdueObj> versionRequest = new
                GsonRequest<ProgressOverdueObj>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<ProgressOverdueObj>() {
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

    private void showPopWindow(View v) {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.layout_report_menu_pupwindow, null);

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

        popWindow.showAsDropDown(v);
        View.OnClickListener itemMenuPopupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                switch (view.getId()) {
                    case R.id.report_menu_bt_0:

                        break;
                    case R.id.report_menu_bt_1:
                        filter();
                        break;
                }
            }
        };
        inflate.findViewById(R.id.report_menu_bt_0).setOnClickListener(itemMenuPopupClickListener);
        inflate.findViewById(R.id.report_menu_bt_1).setOnClickListener(itemMenuPopupClickListener);
    }
}