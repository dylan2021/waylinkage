package com.android.waylinkage.activity.main;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee 汇报列表
 */

public class ProcessorQualityListActivity extends CommonBaseActivity {

    private RefreshLayout mRefreshLayout;
    private ListView mListView;
    private ProcessorQualityListAdapter mAdapter;
    private String name;
    private ProcessorQualityListActivity context;
    private int processId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initStatusBar();
        processId = getIntent().getIntExtra(KeyConstant.id, 0);
        name = getIntent().getStringExtra(KeyConstant.name);
        setContentView(R.layout.activity_processor_quailty_list);

        initTitleBackBt(name);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.autoRefresh();

        mListView = (ListView) findViewById(R.id.hub_circle_lv);

        //设置布局管理器

        mAdapter = new ProcessorQualityListAdapter(context);
        mListView.setAdapter(mAdapter);

        //设置下拉刷新和加载
        Utils.setLoadHeaderFooter(context, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mAdapter.setQualityData(null);
                mRefreshLayout.finishRefresh(0);
                //请求数据
                getQualityListData(refreshLayout);
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

    private void getQualityListData(final RefreshLayout refreshLayout) {
        String url = Constant.WEB_SITE + "/biz/qualities/all?processorId=" + processId;
        Response.Listener<List<QualityReportBean.ContentBean>> successListener =
                new Response.Listener<List<QualityReportBean.ContentBean>>() {
                    @Override
                    public void onResponse(List<QualityReportBean.ContentBean> result) {
                        refreshLayout.finishRefresh(0);
                        if (result == null || result.size() == 0) {
                            ToastUtil.show(context, getString(R.string.no_data));
                            return;
                        }
                        //汇报数据
                        List<QualityReportBean.ContentBean> reportData = result;
                        mAdapter.setQualityData(reportData);
                    }
                };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, "获取列表数据:" + volleyError.getMessage());
                ToastUtil.show(context, getString(R.string.request_failed_retry_later));
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

}