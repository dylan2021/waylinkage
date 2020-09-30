package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.StatData;
import com.android.waylinkage.bean.StatInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.MyExpandableListView;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 * 项目规划
 */

public class QualityOverActivity extends BaseFgActivity {

    private String id;
    private String url_auth_type;
    private MyExpandableListView expendList;
    private QualityOverAdapter qualityOverAdapter;
    private QualityOverActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initStatusBar();
        setContentView(R.layout.activity_quality_over);

        id = getIntent().getStringExtra(KeyConstant.id);
        String stringExtra = getIntent().getStringExtra(KeyConstant.name);


        switch (App.CHOOSE_AUTH_TYPE) {
            case Constant.CONTRACT:
                url_auth_type = UrlConstant.url_biz_buildSites;
                initTitleBackBt(TextUtil.remove_N(stringExtra) + "质量待整改工序");
                break;
            case Constant.PROJECT:
                initTitleBackBt(TextUtil.remove_N(stringExtra) + "质量待整改工地");
                url_auth_type = UrlConstant.url_biz_contracts;
                break;
        }
        expendList = (MyExpandableListView) findViewById(R.id.expand_list);
        qualityOverAdapter = new QualityOverAdapter();
        expendList.setAdapter(qualityOverAdapter);
        //设置分组的监听
        expendList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        //设置子项布局监听
        expendList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // childString[groupPosition][childPosition]
                return true;

            }
        });
        getData();
    }

    //获取工地列表
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(context.getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + url_auth_type + "/" + id + "/quality/stat";//2待整改
        Response.Listener<StatData> successListener = new Response
                .Listener<StatData>() {
            @Override
            public void onResponse(StatData data) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                List<StatInfo> result = data.getData();
                Iterator<StatInfo> iterator = result.iterator();
                while (iterator.hasNext()) {
                    StatInfo next = iterator.next();
                    if (next.getTotal() == 0) {
                        iterator.remove();
                    }
                }
                qualityOverAdapter.setData(result);
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
            }
        };

        Request<StatData> versionRequest = new
                GsonRequest<StatData>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "返回:" + volleyError);
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<StatData>() {
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
