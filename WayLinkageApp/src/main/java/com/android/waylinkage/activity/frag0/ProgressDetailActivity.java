package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.AuthsUtils;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee 汇报详情
 */

public class ProgressDetailActivity extends BaseFgActivity {
    private ProgressDetailActivity content;
    private int STATUS;
    private Button rightBt;
    private EditText sureHasFinishInvestEd;
    private TextView sureHasFinishInvestTv;
    private ReportBean.ContentBean reportInfo;
    private TextView reportTv0, reportTv1, reportTv2, reportTv3, reportTv4,
            reportTv5, reportTv7, reportTv8, reportTv9, reportTv10, reportTv11;
    private String contactBulidSiteProcessorName = "";
    private ReportBean.ContentBean.BizProcessorPlanBean bizProcessorPlan;
    private ProgressDetailActivity context;
    private int reportId;
    private TextView remarkTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initStatusBar();
        setContentView(R.layout.activity_process_detail);
        STATUS = getIntent().getIntExtra(KeyConstant.type, 0);
        reportId = getIntent().getIntExtra(KeyConstant.id, 0);
        contactBulidSiteProcessorName = getIntent().getStringExtra(KeyConstant.name);

        reportInfo = (ReportBean.ContentBean) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);

        sureHasFinishInvestEd = (EditText) findViewById(R.id.report_drtail_tv_6);
        sureHasFinishInvestTv = (TextView) findViewById(R.id.report_drtail_tv_66);
        reportTv0 = (TextView) findViewById(R.id.report_drtail_tv_0);//所属工地/工序
        remarkTv = (TextView) findViewById(R.id.remark_tv);//所属工地/工序
        reportTv0.setText(contactBulidSiteProcessorName);

        reportTv1 = (TextView) findViewById(R.id.report_drtail_tv_1);
        reportTv2 = (TextView) findViewById(R.id.report_drtail_tv_2);
        reportTv3 = (TextView) findViewById(R.id.report_drtail_tv_3);
        reportTv4 = (TextView) findViewById(R.id.report_drtail_tv_4);
        reportTv5 = (TextView) findViewById(R.id.report_drtail_tv_5);

        reportTv7 = (TextView) findViewById(R.id.report_drtail_tv_7);
        reportTv8 = (TextView) findViewById(R.id.report_drtail_tv_8);
        reportTv9 = (TextView) findViewById(R.id.report_drtail_tv_9);
        reportTv10 = (TextView) findViewById(R.id.report_drtail_tv_10);
        reportTv11 = (TextView) findViewById(R.id.report_drtail_tv_11);

        if (reportInfo != null) {
            bizProcessorPlan = reportInfo.getBizProcessorPlan();
            reportTv1.setText(bizProcessorPlan.getName());//计划名称
            reportTv2.setText(reportInfo.getReportInvest() + getString(R.string.money_unit));//汇报产值
            reportTv3.setText(reportInfo.getRealBeginDate() + "-" + reportInfo.getRealEndDate());//汇报周期
            String createUsername = reportInfo.getCreatorUsername();
            reportTv4.setText(createUsername == null ? "未知" : createUsername);//汇报人
            reportTv5.setText(TextUtil.substringTime(reportInfo.getCreateTime()));//汇报时间

            String confirmInvest = reportInfo.getConfirmInvest() == null ? "0" : reportInfo.getConfirmInvest() + getString(R.string.money_unit);
            //sureHasFinishInvestEd.setText("");
            sureHasFinishInvestTv.setText(confirmInvest);//确认产值

            String invest = bizProcessorPlan.getInvest();
            reportTv7.setText(invest + getString(R.string.money_unit));//计划产值
            reportTv8.setText(bizProcessorPlan.getPlanPercentage() + "%");//计划进度
            String planBeginDate = bizProcessorPlan.getPlanBeginDate();
            reportTv9.setText(planBeginDate);
            String endDate = bizProcessorPlan.getPlanEndDate();
            reportTv10.setText(endDate);

            int period = 0;
            try {
                Date startTime = DateUtil.getFormat().parse(planBeginDate);
                Date endTime = DateUtil.getFormat().parse(endDate);
                period = TextUtil.differentDaysByMillisecond2(endTime, startTime);
            } catch (ParseException e) {
            }
            reportTv11.setText( period+ "天");//计划工期
            String description = reportInfo.getDescription();
            remarkTv.setText(description==null?"无":description);//计划工期

            //附件
            setFileListData();
        }

        if (STATUS == 0) {
            //待确认
            sureHasFinishInvestTv.setVisibility(View.VISIBLE);
            sureHasFinishInvestTv.setText("");
            rightBt = (Button) findViewById(R.id.commit_bt);
            sureHasFinishInvestEd.setVisibility(View.VISIBLE);
            rightBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmData();
                }
            });

            //进度确认 权限
            AuthsUtils.setViewAuth(rightBt, AuthsConstant.BIZ_PROCESSOR_FIN_AUDIT);
            AuthsUtils.setViewAuth(sureHasFinishInvestEd, AuthsConstant.BIZ_PROCESSOR_FIN_AUDIT);
        } else {
            sureHasFinishInvestTv.setVisibility(View.VISIBLE);
        }
        content = this;
        initTitleBackBt("汇报详情");
    }

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    private void setFileListData() {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        ((TextView) findViewById(R.id.card_detail_file_title)).setText(R.string.file_link_list);
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        List<FileInfo> pic = reportInfo.getPic();
        List<FileInfo> attachment = reportInfo.getAttachment();
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
            linkTv.setVisibility(View.GONE);
        }
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
    }

    //确认产值
    private void confirmData() {
        String sureHasFinishInvest = sureHasFinishInvestEd.getText().toString();
        if (TextUtil.isEmpty(sureHasFinishInvest) ||
                Integer.valueOf(sureHasFinishInvest) == 0
                ) {
            ToastUtil.show(context, "确认已完成产值不能为空哦");
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_processorFins + "/confirm";
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        DialogHelper.showWaiting(getSupportFragmentManager(), "加载中...");

        Map<String, String> map = new HashMap<>();
        map.put(KeyConstant.id, reportId + "");
        map.put(KeyConstant.confirmInvest, sureHasFinishInvest);
        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(getSupportFragmentManager());
                            ToastUtil.show(context, "汇报审核提交失败,稍后重试");
                            return;
                        }
                        ToastUtil.show(context, "汇报审核提交成功");
                        DialogHelper.hideWaiting(getSupportFragmentManager());
                        context.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(getSupportFragmentManager());
                context.finish();
                Log.d(TAG, "返回错误:" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Content_Type, Constant.application_json);
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);

                return params;
            }
        };
        App.requestQueue.add(jsonRequest);
    }
}
