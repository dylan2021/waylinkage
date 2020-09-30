package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.util.Log;
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
import com.android.waylinkage.bean.EmployeeRecordInfo;
import com.android.waylinkage.bean.ItemInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Gool Lee
 * 成员调拨记录
 */
public class EmployeeRecordActivity extends BaseFgActivity {
    private EmployeeRecordActivity context;
    private String id;
    List<ItemInfo> mList = new ArrayList<>();
    private String titleStr = "";
    private ListView mListView;
    private RefreshLayout mRefreshLayout;
    private TextView mEmptyTv;
    private EmployeeRecordInfo.FromGroupBean fromGroup;
    private EmployeeRecordInfo.ToGroupBean toGroup;
    private EmployeeRecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_records);

        initStatusBar();

        context = this;

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(this, mRefreshLayout);//设置头部,底部样式

        mListView = (ListView) findViewById(R.id.people_lv);
        mEmptyTv = (TextView) findViewById(R.id.empty_tv);

        titleStr = getIntent().getStringExtra(KeyConstant.TITLE);
        initTitleBackBt(titleStr + "的调拨记录");
        id = getIntent().getStringExtra(KeyConstant.id);

        recordAdapter = new EmployeeRecordAdapter(context, mList);
        mListView.setAdapter(recordAdapter);

        getEmployeeRecords();
    }

    private void getEmployeeRecords() {
        //获取工地列表
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + "/biz/employee/records/all?employeeId=" + id;
        Response.Listener<List<EmployeeRecordInfo>> successListener = new Response
                .Listener<List<EmployeeRecordInfo>>() {
            @Override
            public void onResponse(List<EmployeeRecordInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    mEmptyTv.setVisibility(View.VISIBLE);
                    if (null != context && !context.isFinishing()) {
                        dialogHelper.hideAlert();
                    }
                    return;
                }
                mEmptyTv.setVisibility(View.GONE);
                setListData(result);
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
            }
        };

        Request<List<EmployeeRecordInfo>> versionRequest = new
                GsonRequest<List<EmployeeRecordInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d("记录返回", "记录返回" + volleyError.toString());
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<EmployeeRecordInfo>>() {
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

    private void setListData(List<EmployeeRecordInfo> result) {
        for (EmployeeRecordInfo employeeRecordInfo : result) {
            String authName = employeeRecordInfo.getAuthName();//授权人
            String createTime = employeeRecordInfo.getCreateTime();
            fromGroup = employeeRecordInfo.getFromGroup();
            toGroup = employeeRecordInfo.getToGroup();
            String fromGroupName = fromGroup.getName();
            String fromBizBuildSiteName = fromGroup.getBizBuildSite().getNameX();
            String toGroupName = toGroup.getName();
            String toBizBuildSiteName = toGroup.getBizBuildSite().getNameX();
            mList.add(new ItemInfo(0, createTime,
                    "调入班组：" + toBizBuildSiteName + "/" + toGroupName,
                    "调出班组：" + fromBizBuildSiteName + "/" + fromGroupName
                            + "\n授权人：" + authName));
        }
        recordAdapter.setDate(mList);

    }

}