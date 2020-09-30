package com.android.waylinkage.activity.main;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.ProcessorProgressInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 */
public class ProcessorListActivity extends BaseFgActivity {
    private ProcessorListActivity context;
    private String buildSiteId, TITLE;
    private ProcessorListAdapter mAdapter;
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private List<ProcessorProgressInfo> processorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processor_list);
        buildSiteId = getIntent().getStringExtra(KeyConstant.id);
        TITLE = getIntent().getStringExtra(KeyConstant.TITLE);
        context = this;

        initStatusBar();
        initTitleBackBt(TextUtil.remove_N(TITLE) + "工序计划");
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.circle_lv);
        mAdapter = new ProcessorListAdapter(context);
        mListView.setAdapter(mAdapter);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(context, mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                //请求数据
                getListData();
            }
        });
        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore();
                ToastUtil.show(context, getString(R.string.no_more_data));
            }
        });
    }

    private void getListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_processors + "/stat?buildSiteId=" + buildSiteId;
        Response.Listener<List<ProcessorProgressInfo>> successListener = new Response
                .Listener<List<ProcessorProgressInfo>>() {
            @Override
            public void onResponse(List<ProcessorProgressInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                mAdapter.setData(result);
            }
        };

        Request<List<ProcessorProgressInfo>> versionRequest = new
                GsonRequest<List<ProcessorProgressInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        ToastUtil.show(context, "请求失败,稍后重试");
                    }
                }, new TypeToken<List<ProcessorProgressInfo>>() {
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