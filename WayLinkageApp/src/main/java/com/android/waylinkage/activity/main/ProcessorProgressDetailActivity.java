package com.android.waylinkage.activity.main;

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
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.ProcessorProgressInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 * 工序-进度整体详情
 */

public class ProcessorProgressDetailActivity extends BaseFgActivity {
    private ProcessorProgressDetailActivity context;
    private int TYPE;
    private int id;
    private String TITLE = "";
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_processor_progress_detail);

        context = this;

        id = getIntent().getIntExtra(KeyConstant.id, 0);
        TITLE = getIntent().getStringExtra(KeyConstant.TITLE);
        initTitleBackBt(TITLE + "整体进度");

        getProcessorProcessData();
    }

    private void getProcessorProcessData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url_type = "";
        switch (App.CHOOSE_AUTH_TYPE) {
            case Constant.BUILDSITE:
                url_type = UrlConstant.url_biz_processors;
                break;
            case Constant.CONTRACT:
                url_type = UrlConstant.url_biz_buildSites;
                break;
            case Constant.PROJECT:
                url_type = UrlConstant.url_biz_contracts;
                break;
        }
        String url = Constant.WEB_SITE + url_type + "/" + id +
                UrlConstant.progress;
        Response.Listener<ProcessorProgressInfo> successListener = new Response
                .Listener<ProcessorProgressInfo>() {
            @Override
            public void onResponse(ProcessorProgressInfo result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                setView(result);
                //附件
                setFileListData(result);
            }
        };

        Request<ProcessorProgressInfo> versionRequest = new
                GsonRequest<ProcessorProgressInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ToastUtil.show(context, "获取整体进度信息失败,稍后重试");
                    }
                }, new TypeToken<ProcessorProgressInfo>() {
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

    private void setView(ProcessorProgressInfo result) {
        int actualPercentage = (int) result.getCompletedPercentage();
        int planPercentage = (int) result.getPlanPercentage();
        String planPercentageStr = "";
        setViewData(R.id.detail_actual_progress_tv, actualPercentage + "%");//实际进度
    /*    int value = actualPercentage - planPercentage;
        if (value > 0) {
            planPercentageStr = "(>计划" + value + "%)";
        } else {
            planPercentageStr = "(<计划" + Math.abs(value) + "%)";
        }*/
        setViewData(R.id.progress_actual_plan_comple_value_tv, "(计划" + planPercentage + "%)");//计划进度

        //实际产值  /计划产值
        double actualInvest = result.getActualInvest();
        double planInvest = result.getPlanInvest();
        setViewData(R.id.detail_actual_invers_tv, actualInvest + getString(R.string.money_unit));
        setViewData(R.id.inverse_plan_invers_tv, "(计划" + planInvest + getString(R.string.money_unit) + ")");
        //计划数量
        setViewData(R.id.plan_nums_tv, result.getPlanCount() + "个");

        String realBeginDate = result.getRealBeginDate() == null ? "未知" : result.getRealBeginDate();
        String realEndDate = result.getRealEndDate() == null ? "未知" : result.getRealEndDate();
        setViewData(R.id.actual_start_end_date_tv, realBeginDate + " ~ " + realEndDate);

        String planBeginDate = result.getPlanBeginDate();
        String planEndDate = result.getPlanEndDate();
        setViewData(R.id.plan_start_end_date_tv, planBeginDate + " ~ " + planEndDate);

    }

    private void setViewData(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }


    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    private void setFileListData(ProcessorProgressInfo result) {
        //附件
        ((TextView) findViewById(R.id.card_detail_file_title)).setText(R.string.file_link_list);
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        ProcessorProgressInfo.BizProcessorFinBean bizProcessorFin = result.getBizProcessorFin();
        if (bizProcessorFin == null) {
            return;
        }
        List<FileInfo> pic = bizProcessorFin.getPic();
        List<FileInfo> attachment = bizProcessorFin.getAttachment();

        if (attachment != null) {
            for (FileInfo att : attachment) {
                fileData.add(new FileListInfo(att.name, att.url, Constant.TYPE_SEE));
            }
        }
        if (pic != null) {
            for (FileInfo url : pic) {
                fileData.add(new FileListInfo(url.name, url.url, Constant.TYPE_SEE));
            }
        }

        if (fileData == null || fileData.size() == 0) {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.VISIBLE);
        }
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
    }

}
