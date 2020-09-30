package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.adapter.MyExpandableListAdapter;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.GroupInfo;
import com.android.waylinkage.bean.QualityReportBean;
import com.android.waylinkage.bean.ReportBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.MyExpandableListView;
import com.android.waylinkage.view.ScrollListView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee 汇报详情
 */

public class ProcessorQualityAddActivity extends CommonBaseActivity {
    private String statusStr;
    private Button rightBt;
    private TextView checkTypeTv;
    private String contactBulidSiteProcessorName = "";
    private ReportBean.ContentBean.BizProcessorPlanBean bizProcessorPlan;
    private ProcessorQualityAddActivity context;
    private MyExpandableListAdapter myExpandableListAdapter;
    private ExpandableListView expandableListView;
    private List<GroupInfo> groupInfos = new ArrayList<>();
    private StringBuffer seletedGroupNames;
    private List<Integer> seletedIdList = new ArrayList<>();
    private String checker = "";
    private String checkTime, reformSuggest = "", mustReformTime;
    private TextView checkorTv;
    private int processorConfigId;
    private String checkResultArr[] = new String[]{"通过", "待整改"};
    private QualityReportBean.ContentBean qualityInfo;
    private String nameTimeStr;
    private TextView titleTimeReportTv, titleContactTv;
    private List<QualityReportBean.ContentBean.DetailBean> reportQualityList = new ArrayList<>();
    private TextView checkResultTv;
    private LinearLayout reformLayout;
    private List<GroupInfo> needReformGroupList;
    private LinearLayout bottomLayout;
    private boolean isToConfirmed = true;
    private int itemInfoId;
    private TextView reformSuggestTv;
    private RelativeLayout fileUploadRl;
    private FileListAdapter fileListAdapter;
    private TextView footStatusTv;
    private int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initStatusBar();
        setContentView(R.layout.activity_processor_quailty);
        Intent intent = getIntent();
        itemInfoId = intent.getIntExtra(KeyConstant.id, 0);
        contactBulidSiteProcessorName = intent.getStringExtra(KeyConstant.name);
        statusStr = intent.getStringExtra(KeyConstant.TYPE);
        nameTimeStr = intent.getStringExtra(KeyConstant.time_name);
        processorConfigId = intent.getIntExtra(KeyConstant.processorConfigId, 0);
        status = intent.getIntExtra(KeyConstant.status, 0);

        qualityInfo = (QualityReportBean.ContentBean) intent.getSerializableExtra(KeyConstant.LIST_OBJECT);
        reportQualityList = qualityInfo.getDetail();

        expandableListView = (MyExpandableListView) findViewById(R.id.expand_list);
        myExpandableListAdapter = new MyExpandableListAdapter(context);
        expandableListView.setAdapter(myExpandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false; // 返回 false，否则分组不会展开
            }
        });

        initTitleBackBt(contactBulidSiteProcessorName);
        initBottomStatusTv();

        getListData();
        //附件
        if (null != qualityInfo) {
            setFileListData();
        }
    }

    //状态statusTv:  status  1 "待确认 2 "待整改"3 "已整改" 4 "已确认";
    private void initBottomStatusTv() {
        String mustReformTime = qualityInfo.getMustReformTime() == null ? "未知" : qualityInfo.getMustReformTime();
        String tabStatus1 = "<font color='#666666' >确认结果：通过</font>" +
                "<br><font color='#666666'>检查人：</font>" + qualityInfo.getChecker() +
                "<br><font color='#666666'>检查时间：</font>" + TextUtil.substringTime(qualityInfo.getCheckTime());
        String reformor = qualityInfo.getReformor();
        if (reformor != null) {
            String refimeMsg = "<br><font color='#666666' >整改人：</font>" + reformor +
                    "<br><font color='#666666'>整改时间：</font>" +
                    TextUtil.substringTime(qualityInfo.getReformTime()) +
                    "<br><font color='#666666'>整改说明：</font>" + qualityInfo.getReformDesc();
            tabStatus1 = tabStatus1 + refimeMsg;
        }
        String tabStatus2 = "<font color='#666666' >要求完成日期</font>：" + mustReformTime +
                "<br><font color='#666666'>整改要求：</font>" + qualityInfo.getReformSuggest();
        String tabStatus0 = "<font color='#666666' >自检结果：</font>" + "通过";
        footStatusTv = ((TextView) findViewById(R.id.status_content_tv));
        footStatusTv.setText(Html.fromHtml(status == 1 ?
                tabStatus0 : status == 2 ? tabStatus2 : status == 3 ? tabStatus1 : tabStatus1));
    }

    private void setFileListData() {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        ((TextView) findViewById(R.id.card_detail_file_title)).setText(R.string.file_link_list);
        listView = findViewById(R.id.horizontal_gridview);
        List<FileInfo> pic = qualityInfo.getPic();
        List<FileInfo> attachment = qualityInfo.getAttachment();

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

        if (fileData != null && fileData.size() > 0) {
            linkTv.setVisibility(View.GONE);
        } else {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        }
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
    }

    private void getListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + "/biz/qualityConfigs/tree?processorConfigId=" + processorConfigId;

        Response.Listener<List<GroupInfo>> successListener = new Response
                .Listener<List<GroupInfo>>() {
            @Override
            public void onResponse(List<GroupInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                groupInfos = result;

                Iterator<GroupInfo> groupsIterator = groupInfos.iterator();
                while (groupsIterator.hasNext()) {
                    GroupInfo groupInfo = groupsIterator.next();
                    List<GroupInfo.ChildrenBean> childrenBeanList = groupInfo.getChildren();
                    if (childrenBeanList != null) {
                        //------------- 项   -----------------
                        Iterator<GroupInfo.ChildrenBean> childsIterator = childrenBeanList.iterator();

                        while (childsIterator.hasNext()) {
                            GroupInfo.ChildrenBean childrenBean = childsIterator.next();
                            int childrenBeanId = childrenBean.getId();
                            String name = childrenBean.getName();
                            boolean isConcat = false;
                            for (QualityReportBean.ContentBean.DetailBean detailBean : reportQualityList) {
                                int qualifyConfigId = detailBean.getQualifyConfigId();

                                if (childrenBeanId == qualifyConfigId) {
                                    isConcat = true;
                                    String confirmResult = detailBean.getConfirmResult();
                                    // status  1 "待确认 2 "待整改"3 "已整改" 4 "已确认";
                                    if ("2".equals(confirmResult) && status == 2) {
                                        childrenBean.setName(name + "<font color='#ff9800'>（待整改）</font>");
                                    }
                                    //confirmResult  3待确认 1通过  2 待整改
                                    //  status  1 待确认    4 已确认  2 待整改
                                    if ("2".equals(confirmResult) && status == 3) {
                                        childrenBean.setName(name + "<font color='#ff9800'>（已整改）</font>");

                                        String reformDesc = TextUtil.isEmpty(qualityInfo.getReformDesc()) ? "未知" : qualityInfo.getReformDesc();
                                        footStatusTv.setText(Html.fromHtml(
                                                "<font color='#666666' >整改说明：</font>" + reformDesc +
                                                        "<br>" + "<font color='#666666'>整改人：</font>" + qualityInfo.getReformor() +
                                                        "<br>" + "<font color='#666666'>整改时间：</font>" + qualityInfo.getReformTime()
                                        ));
                                    }
                                    break;
                                }
                            }
                            if (!isConcat) {
                                childsIterator.remove();
                            }
                        }

                        //如果子项删完了,就移除大项
                        if (childrenBeanList.size() == 0) {
                            groupsIterator.remove();
                        }
                        //拼接 选中group
                    } else {
                        groupsIterator.remove();
                    }
                }
                myExpandableListAdapter.setData(groupInfos, false);
                //展开列表
                for (int i = 0; i < myExpandableListAdapter.getGroupCount(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        };

        Request<List<GroupInfo>> versionRequest = new
                GsonRequest<List<GroupInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<GroupInfo>>() {
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

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;

}