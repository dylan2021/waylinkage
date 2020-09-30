package com.android.waylinkage.activity.frag0;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.android.waylinkage.view.MyExpandableListView;
import com.android.waylinkage.view.ScrollListView;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;


/**
 * @author Gool Lee 汇报详情
 */

public class QualityDetailActivity extends CommonBaseActivity {
    private int tabPositon;
    private Button rightBt;
    private TextView checkTypeTv;
    private String contactBulidSiteProcessorName = "";
    private ReportBean.ContentBean.BizProcessorPlanBean bizProcessorPlan;
    private QualityDetailActivity context;
    private MyExpandableListAdapter myExpandableListAdapter;
    private ExpandableListView expandableListView;
    private List<GroupInfo> groupInfos = new ArrayList<>();
    private StringBuffer seletedGroupNames;
    private List<Integer> seletedIdList = new ArrayList<>();
    private String checker = "";
    private String reformSuggest = "";
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
    private File file;
    private TextView footStatusTv;
    private boolean isOverdue;
    private long checkTime, createTimeDate,mustReformTime;
    private String createTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        initStatusBar();
        setContentView(R.layout.activity_quailty_report_detail);
        Intent intent = getIntent();
        tabPositon = intent.getIntExtra(KeyConstant.TYPE, 0);//待确认,已确认,待整改
        processorConfigId = intent.getIntExtra(KeyConstant.processorConfigId, 0);
        itemInfoId = intent.getIntExtra(KeyConstant.id, 0);
        isOverdue = getIntent().getBooleanExtra(KeyConstant.isOverdue, false);
        contactBulidSiteProcessorName = intent.getStringExtra(KeyConstant.name);
        nameTimeStr = intent.getStringExtra(KeyConstant.time_name);

        qualityInfo = (QualityReportBean.ContentBean) intent.getSerializableExtra(KeyConstant.LIST_OBJECT);
        createTime = qualityInfo.getCreateTime();
        try {
            createTimeDate = DateUtil.getFormat().parse(createTime).getTime();
        } catch (ParseException e) {


        }
        reportQualityList = qualityInfo.getDetail();
     /*   if (null != qualityInfo && qualityInfo.getDetail() != null && tabPositon == 2) {
            List<QualityReportBean.ContentBean.DetailBean> detail = qualityInfo.getDetail();
            for (QualityReportBean.ContentBean.DetailBean detailBean : detail) {
                String confirmResult = detailBean.getConfirmResult();
                if (confirmResult.equals("3")) {
                    reportQualityList.add(detailBean);
                }
            }
        } else {
        }*/


        initTitleBackBt(tabPositon == 0 ? isOverdue ? "待整改" : "待确认" : tabPositon == 1 ? "已确认" : "待整改");

        expandableListView = (MyExpandableListView) findViewById(R.id.expand_list);
        myExpandableListAdapter = new MyExpandableListAdapter(context);
        expandableListView.setAdapter(myExpandableListAdapter);

        //设置分组项的点击监听事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false; // 返回 false，否则分组不会展开
            }
        });
        //确认人
        checkorTv = (TextView) findViewById(R.id.checkor_tv);//确认人
        setInputDialog(checkorTv, InputType.TYPE_CLASS_TEXT);
        reformSuggestTv = (TextView) findViewById(R.id.reform_suggest_tv);
        setInputDialog(reformSuggestTv, InputType.TYPE_CLASS_TEXT);
        titleContactTv = (TextView) findViewById(R.id.title_report_contact_tv);
        titleContactTv.setText(contactBulidSiteProcessorName);
        titleTimeReportTv = (TextView) findViewById(R.id.title_report_time_name_tv);
        titleTimeReportTv.setText("（" + nameTimeStr + "）");
        checkTypeTv = (TextView) findViewById(R.id.check_type_tv);
        bottomLayout = (LinearLayout) findViewById(R.id.quality_bottom_layout);
        initBottomStatusTv();

        reformLayout = (LinearLayout) findViewById(R.id.layout_reform);
        checkResultTv = (TextView) findViewById(R.id.check_result_tv);
        checkResultTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。
                builder.setItems(checkResultArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();

                        checkResultTv.setText(checkResultArr[i]);
                        reformLayout.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
                        checkResultTv.setTextColor(getResources().getColor(i == 0 ? R.color.mainColor : R.color.warnig_yellow));

                        if (i == 1) {
                            isToConfirmed = false;
                            Intent i1 = new Intent(context, QualityCheckTypeSettingActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) groupInfos);
                            i1.putExtras(bundle);
                            i1.putExtra(KeyConstant.id, processorConfigId);
                            context.startActivityForResult(i1, 1);
                        } else {
                            isToConfirmed = true;
                        }

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        if (tabPositon == 1) {
            bottomLayout.setVisibility(View.GONE);
        } else {
            initRightBt();
            if (tabPositon == 2 || isOverdue) {
                ((TextView) findViewById(R.id.result_title_tv)).setText("整改结果");
                ((TextView) findViewById(R.id.checkor_title_tv)).setText("整改人");
                ((TextView) findViewById(R.id.suggest_title_tv)).setText("整改说明");
                ((TextView) findViewById(R.id.checker_time_tv)).setText("整改完成时间");
                ((TextView) findViewById(R.id.check_result_time_tv)).setText("整改结果");
                findViewById(R.id.must_finish_time_layout).setVisibility(View.GONE);
                findViewById(R.id.check_type_layout).setVisibility(View.GONE);
                findViewById(R.id.check_result_next_iv).setVisibility(View.GONE);
                checkResultTv.setClickable(false);

                reformLayout.setVisibility(View.VISIBLE);

                //质量确认 权限
                AuthsUtils.setViewAuth(rightBt, AuthsConstant.BIZ_QUALITY_REPORT);
            } else {
                AuthsUtils.setViewAuth(rightBt, AuthsConstant.BIZ_QUALITY_AUDIT);
            }
        }

        //选择检查类型
        checkTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(context, QualityCheckTypeSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) needReformGroupList);
                i1.putExtras(bundle);
                context.startActivityForResult(i1, 1);
            }
        });
        getListData();
        //附件
        setFileListData();
    }

    //状态statusTv
    private void initBottomStatusTv() {
        String mustReformTime = qualityInfo.getMustReformTime() == null ? "未知" : qualityInfo.getMustReformTime();
        String tabStatus1 = "<font color='#666666' >确认结果：通过</font>" +
                "<br><font color='#666666'>检查人：</font>" + qualityInfo.getChecker() +
                "<br><font color='#666666'>检查时间：</font>" + TextUtil.substringTime(qualityInfo.getCheckTime());
        String reformor = qualityInfo.getReformor();
        if (tabPositon == 1 && reformor != null) {
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
        footStatusTv.setText(Html.fromHtml(tabPositon == 0 ? tabStatus0 : tabPositon == 1 ? tabStatus1 : tabStatus2));
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

    private void initRightBt() {
        rightBt = (Button) findViewById(R.id.commit_bt);
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = checkorTv.getText().toString();
                reformSuggest = reformSuggestTv.getText().toString();
                if (TextUtil.isEmpty(checker)) {
                    ToastUtil.show(context, tabPositon == 0 ? "请填写检查人" : "请填写整改人");
                    return;
                }
                String timeTitle = tabPositon == 0 ? "检查时间" : "整改完成时间";
                if (checkTime == 0) {
                    ToastUtil.show(context, timeTitle + "不能为空");
                    return;
                }
                if (checkTime < createTimeDate) {
                    ToastUtil.show(context, timeTitle + "不能小于汇报时间");
                    return;

                }
                String url = "";
                Map<String, Object> map = new HashMap<>();
                map.put(KeyConstant.id, itemInfoId);
                //汇报附件
                //map.put(KeyConstant.attachment, "no");
            /*    if (fileData != null) {
                    for (FileListInfo fileInfo : fileData) {
                        String fileUrl = fileInfo.fileUrl;
                        map.put(KeyConstant.pic, fileUrl.toString());
                    }
                }*/
                if (tabPositon == 0) {
                    url = Constant.WEB_SITE + UrlConstant.url_biz_qualities_confirm;
                    if (!isToConfirmed) {
                        if (null == needReformGroupList || needReformGroupList.size() == 0) {
                            ToastUtil.show(context, "待整改项不能为空");
                            return;
                        }
                        if (0 == mustReformTime) {
                            ToastUtil.show(context, "要求完成时间不能为空");
                            return;
                        }
                        if (mustReformTime < createTimeDate) {
                            ToastUtil.show(context, "要求完成时间不能小于汇报时间");
                            return;
                        }
                        map.put(KeyConstant.mustReformTime, DateUtil.millonsToUTC(mustReformTime));
                        map.put(KeyConstant.reformSuggest, TextUtil.isEmpty(reformSuggest) ? "no" : reformSuggest);
                        //map.put(KeyConstant.status, 2);
                    } else {
                        //map.put(KeyConstant.status, 4);
                        seletedIdList.clear();
                    }
                    JSONArray detailArr = new JSONArray();
                    for (GroupInfo groupInfo : groupInfos) {
                        for (GroupInfo.ChildrenBean childrenBean : groupInfo.getChildren()) {
                            JSONObject resultObj = new JSONObject();
                            try {
                                int beanId = childrenBean.getId();
                                resultObj.put(KeyConstant.qualifyConfigId, beanId);
                                boolean containsId = seletedIdList.contains(beanId);
                                resultObj.put(KeyConstant.confirmResult, containsId ? 2 : 1);//2,待整改 1,通过
                                detailArr.put(resultObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    map.put(KeyConstant.detail, detailArr);
                    map.put(KeyConstant.checkTime, DateUtil.millonsToUTC(checkTime));
                    map.put(KeyConstant.checker, checker);
                    // 提交  确认/待整改
                } else {
                    //待整改
                    url = Constant.WEB_SITE + UrlConstant.url_biz_qualities_reform;
                    JSONArray detailArr = new JSONArray();
                    for (GroupInfo groupInfo : groupInfos) {
                        for (GroupInfo.ChildrenBean childrenBean : groupInfo.getChildren()) {
                            JSONObject resultObj = new JSONObject();
                            try {
                                int beanId = childrenBean.getId();
                                resultObj.put(KeyConstant.qualifyConfigId, beanId);
                                resultObj.put(KeyConstant.reportResult, 1);//(包含)3,待整改 2,通过
                                detailArr.put(resultObj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    map.put(KeyConstant.detail, detailArr);
                    map.put(KeyConstant.reformTime, checkTime);
                    map.put(KeyConstant.reformor, checker);
                    map.put(KeyConstant.reformDesc, reformSuggest);//整改说明
                }
                postToConfirm(url, new JSONObject(map));
            }
        });

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
                                    if ("2".equals(confirmResult) && (isOverdue || tabPositon == 2)) {
                                        childrenBean.setName(name + "<font color='#ff9800'>（待整改）</font>");
                                    }
                                    //confirmResult  3待确认 1通过  2 待整改
                                    //  status  1 待确认    4 已确认  2 待整改
                                    if ("2".equals(confirmResult) && tabPositon == 0 && !isOverdue) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            needReformGroupList = (List<GroupInfo>) data.getSerializableExtra(KeyConstant.GROUP_LIST);
            if (null == needReformGroupList || needReformGroupList.size() == 0) {
                return;
            }
            //剔除 未选中的数据.
            ListIterator<GroupInfo> groupsIterator = needReformGroupList.listIterator();
            seletedGroupNames = new StringBuffer();//选中的groupName
            while (groupsIterator.hasNext()) {
                GroupInfo groupInfo = groupsIterator.next();
                List<GroupInfo.ChildrenBean> childrenBeanList = groupInfo.getChildren();
                if (childrenBeanList != null && groupInfo.isAllChecked()) {
                    //------------- 项   -----------------
                    Iterator<GroupInfo.ChildrenBean> childsIterator = childrenBeanList.iterator();
                    while (childsIterator.hasNext()) {
                        GroupInfo.ChildrenBean childrenBean = childsIterator.next();
                        //没有被选择的项
                        if (!childrenBean.isChildChecked()) {
                            childsIterator.remove();
                        }
                    }
                    //拼接 选中group
                    seletedGroupNames.append(groupInfo.getName() + ",");
                } else {
                    groupsIterator.remove();
                }
            }
            if (null != seletedGroupNames && seletedGroupNames.length() > 0) {
                seletedGroupNames.deleteCharAt(seletedGroupNames.length() - 1);
                checkTypeTv.setText(seletedGroupNames);
            }

            //待整改项 id
            for (GroupInfo groupInfo : needReformGroupList) {
                for (GroupInfo.ChildrenBean childrenBean : groupInfo.getChildren()) {
                    int id = childrenBean.getId();
                    seletedIdList.add(id);
                }
            }

        }

    }

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;

    private void setInputDialog(final TextView tvClick, final int inputType) {
        tvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.layout_dialog_update_process, null);
                Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);
                final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id
                        .dialog_add_card_title);
                etContent.setInputType(inputType);
                final Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String checker = etContent.getText().toString();
                        if (TextUtil.isEmpty(checker)) {
                            ToastUtil.show(context, context.getString(R.string.enter_cannot_empty));
                            return;
                        }
                        tvClick.setText(checker);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    //确认  -> 通过
    private void postToConfirm(String url, JSONObject jsonObject) {
        String replace = jsonObject.toString().replace("\\/",
                "/");
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        DialogHelper.hideWaiting(fm);
                        ToastUtil.show(context, "汇报提交成功");
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
                if (null == error) {
                    finish();
                    ToastUtil.show(context, "汇报提交成功");
                } else if (error.getMessage() != null && error.getMessage().contains("End of input at character 0 of")) {
                    finish();
                } else {
                    ToastUtil.show(context, getString(R.string.server_exception));
                }
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

    public void onRealStartTimeClick(View view) {
        showTimeSet(R.id.real_start_time_tv, 0);
    }

    public void onMustReformTimeTimeClick(View view) {
        showTimeSet(R.id.must_reform_time_tv, 1);
    }

    private void showTimeSet(int id, final int type) {
        final TextView curTv = (TextView) findViewById(id);
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            if (millseconds > System.currentTimeMillis()) {
                                ToastUtil.show(context, (tabPositon == 0 ? "检查时间" : "整改完成时间") + getString(R.string.cannot_later_than_today));
                                return;
                            }
                            checkTime = millseconds;
                        } else if (type == 1) {
                            if (millseconds < System.currentTimeMillis()/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset()) {
                                ToastUtil.show(context, "要求完成时间不能早于今日");
                                return;
                            }
                            mustReformTime = millseconds;
                        }

                        curTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                    }
                })
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setCancelStringId(getString(R.string.time_dialog_title_cancel))
                .setSureStringId(getString(R.string.time_dialog_title_sure))
                .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                .setThemeColor(context.getResources().getColor(R.color.mainColorDrak))
                .setWheelItemTextSize(16)
                .setType(Type.YEAR_MONTH_DAY)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

}