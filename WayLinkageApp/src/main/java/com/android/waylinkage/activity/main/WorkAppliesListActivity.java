package com.android.waylinkage.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.bean.WorkApplyBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
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
public class WorkAppliesListActivity extends BaseFgActivity {
    private WorkAppliesListActivity context;
    private TextView titleTv;
    private List<ReportBean.ContentBean> mDataList = new ArrayList<>();
    private ListView mListView;
    private WorkAppliesListAdapter mAdapter;
    private Button rightBt;
    private TabLayout mTopTab;
    private RefreshLayout mRefreshLayout;
    private String[] topArrReport = {"待审核", "已审核"};
    private int mTabPosition;
    private String buildSiteId = "";
    private int TYPE_WORK = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_work_start_list);
        context = this;
        buildSiteId = getIntent().getStringExtra(KeyConstant.id);
        TYPE_WORK = getIntent().getIntExtra(KeyConstant.TYPE_WORK, 0);
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
        titleTv = (TextView) findViewById(R.id.center_tv);
        emptyTv = findViewById(R.id.empty_tv);
        titleTv.setText(TYPE_WORK == 0 ? "开工申请" : TYPE_WORK == 1 ? "完工申请" : "变更申请");
        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        //mRefreshLayout.setPrimaryColors(Color.WHITE);//刷新头的背景颜色
        mRefreshLayout.autoRefresh();

        String[] tabArr = topArrReport;
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
        mAdapter = new WorkAppliesListAdapter(context, TYPE_WORK);
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
        getReportListData(mRefreshLayout, mTabPosition == 0 ? false : true);
    }

    private void getReportListData(final RefreshLayout refreshLayout, final boolean is1) {
        mAdapter.setData(new ArrayList<WorkApplyBean>(), is1 ? 1 : 0);
        String urlStart = TYPE_WORK == 0 ? "start" : TYPE_WORK == 1 ? "finish" : "change";
        String url_type = App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? "buildSiteId=" :
                App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? "contractId=" : "projectId=";
        String url = Constant.WEB_SITE + "/biz/" + urlStart + "/applies/all?" + url_type +
                buildSiteId + "&status=" + is1;
        Response.Listener<List<WorkApplyBean>> successListener = new Response.Listener<List<WorkApplyBean>>() {
            @Override
            public void onResponse(List<WorkApplyBean> result) {
                refreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    return;
                }
                Collections.reverse(result);
                mAdapter.setData(result, is1 ? 1 : 0);
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

        Request<List<WorkApplyBean>> request = new GsonRequest<List<WorkApplyBean>>(Request.Method.GET,
                url, successListener, errorListener, new TypeToken<List<WorkApplyBean>>() {
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

}