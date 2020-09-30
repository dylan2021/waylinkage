package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.CounterInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gool Lee
 */
public class ReportMenuActivity extends BaseFgActivity {

    public final String TAG = ReportMenuActivity.class.getSimpleName();
    private String chooseId = "";
    private ReportMenuActivity context;
    private TextView redNumQuality, redNumProcess, redNumFinish, redNumChange, redNumStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_report_menu);
        context = this;
        chooseId = getIntent().getStringExtra(KeyConstant.id);
        initTitleBackBt(getString(R.string.report_things));

        initRednNum();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUnreadNumData();
    }

    private void getUnreadNumData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url_type = "";
        switch (App.CHOOSE_AUTH_TYPE) {
            case Constant.BUILDSITE:
                url_type = UrlConstant.url_biz_buildSites;
                break;
            case Constant.CONTRACT:
                url_type = UrlConstant.url_biz_contracts;
                break;
            case Constant.PROJECT:
                url_type = UrlConstant.url_biz_projects;
                break;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + url_type + "/" + chooseId + "/counter";

        Response.Listener<CounterInfo> successListener = new Response
                .Listener<CounterInfo>() {
            @Override
            public void onResponse(CounterInfo result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null) {
                    return;
                }
                //设置数据
                setRedNumInfo(result);
            }
        };

        Request<CounterInfo> versionRequest = new
                GsonRequest<CounterInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<CounterInfo>() {
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

    private void setRedNumInfo(CounterInfo result) {
        setRedNumView(redNumProcess, result.getPendingFin());
        setRedNumView(redNumQuality, result.getPendingQuality());
        setRedNumView(redNumStart, result.getPendingStartApply());
        setRedNumView(redNumFinish, result.getPendingFinishApply());
        setRedNumView(redNumChange, result.getPendingChangeApply());
    }

    private void setRedNumView(TextView tv, int num) {
        tv.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
        tv.setText(num + "");
    }

    //进度汇报
    public void oMenuProcessClick(View view) {
        Intent intent = new Intent(context, ReportReformListActivity.class);
        intent.putExtra(KeyConstant.id, chooseId);
        intent.putExtra(KeyConstant.isProgress, true);//进度
        intent.putExtra(KeyConstant.reportIdicateType, KeyConstant.TYPE_REPORT);
        context.startActivity(intent);
    }

    //质量汇报
    public void onMenuQuailtyClick(View view) {
        Intent intent = new Intent(context, ReportReformListActivity.class);
        intent.putExtra(KeyConstant.id, chooseId);
        intent.putExtra(KeyConstant.isProgress, false);//进度
        intent.putExtra(KeyConstant.reportIdicateType, KeyConstant.TYPE_REPORT);
        context.startActivity(intent);
    }

    //变更
    public void onMenuWorkChangeClick(View view) {
        Intent intent = new Intent(context, WorkAppliesListActivity.class);
        intent.putExtra(KeyConstant.id, chooseId);
        intent.putExtra(KeyConstant.TYPE_WORK, 2);
        context.startActivity(intent);
    }

    //完工
    public void onMenuWorkFinishClick(View view) {
        Intent intent = new Intent(context, WorkAppliesListActivity.class);
        intent.putExtra(KeyConstant.id, chooseId);
        intent.putExtra(KeyConstant.TYPE_WORK, 1);
        context.startActivity(intent);
    }

    //开工
    public void onMenuWorkStartClick(View view) {
        Intent intent = new Intent(context, WorkAppliesListActivity.class);
        intent.putExtra(KeyConstant.id, chooseId);
        intent.putExtra(KeyConstant.TYPE_WORK, 0);
        context.startActivity(intent);
    }

    private void initRednNum() {
        redNumProcess = ((TextView) findViewById(R.id.report_process_red_tv));
        redNumQuality = ((TextView) findViewById(R.id.report_quality_red_tv));
        redNumStart = ((TextView) findViewById(R.id.report_start_red_tv));
        redNumFinish = ((TextView) findViewById(R.id.report_finish_red_tv));
        redNumChange = ((TextView) findViewById(R.id.report_change_red_tv));
    }
}
