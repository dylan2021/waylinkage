package com.android.waylinkage.activity.other;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Gool Lee
 */
@SuppressLint("WrongConstant")
public class ContractListMapActivity extends BaseFgActivity {
    private String TAG = ContractListMapActivity.class.getSimpleName();
    private ContractListMapActivity context;
    private ContractListMapFragment contractListMapFragment;
    private FragmentManager fragmentManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String projectId = "0";

    private DialogHelper dialogHelper;
    private String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contart_map);
        context = this;
        initStatusBar();
        initTitleBackBt("");

        projectId = getIntent().getStringExtra(KeyConstant.id);
        projectName = getIntent().getStringExtra(KeyConstant.name);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (null == contractListMapFragment) {
            contractListMapFragment = new ContractListMapFragment(projectId);
            transaction.add(R.id.main_fragments, contractListMapFragment);
        }
        transaction.show(contractListMapFragment);
        transaction.commitAllowingStateLoss();
        getContractsListData();
    }

    //获取标段列表
    private void getContractsListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("获取中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_contracts + "/all?projectId=" + projectId;
        Log.d(TAG, "获取标段数据:" + url);
        Response.Listener<List<ContractInfo>> successListener = new Response
                .Listener<List<ContractInfo>>() {
            @Override
            public void onResponse(List<ContractInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                contractListMapFragment.setData(projectId, result);
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
            }
        };

        Request<List<ContractInfo>> versionRequest = new
                GsonRequest<List<ContractInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d(TAG, "获取标段数据,错误返回" + volleyError.getMessage());
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<ContractInfo>>() {
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


    public void onContractBackClick(View view) {
        finish();
    }
}
