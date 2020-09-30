package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.main.WorkChangeDetailActivity;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.PictureBean;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.PlanChangedInfo;
import com.android.waylinkage.bean.ProcessorsInfo;
import com.android.waylinkage.bean.WorkApplyBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.RetrofitUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.FileTypeUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;
import com.android.waylinkage.widget.mulpicture.MulPictureActivity;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 * 新增汇报
 */

public class WorkChangeAddActivity extends CommonBaseActivity {
    private WorkChangeAddActivity context;
    private LinearLayout contentLayout;
    private TextView gongDiTv, heTongDuanTv;
    private TextView gongXuTv;
    private TextView planAddTv;
    private String[] changeLevelArr = new String[]{"一般", "重要", "重大"};
    private int contractId;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();
    private int applyId, buildSiteId, processorId;
    private List<ProcessorsInfo> processorsInfo = new ArrayList<>();
    private ArrayList<String> planTitleList = new ArrayList<>();
    private int processorPlanId = -1;
    private String changePlace;
    private List<PlanChangedInfo> planAllInfoList = new ArrayList<>();
    private List<PlanChangedInfo> planSeletedInfoList = new ArrayList<>();
    private FragmentManager fm;
    private List<ContractInfo> contractList = new ArrayList<>();
    private Date date1, date2;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private TextView changeLevelTv, drawingNoTv, drawingNameTv, changePlaceTv;
    private int changeLevel = 0;
    private LinearLayout planItemLayout;
    private String changeRemark, designDrawNo, designDraw;
    private String[] processorArray;
    private WorkApplyBean reportInfo;
    private TextView remarkTv;
    private RelativeLayout seleteProceesorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_work_changed_add);
        fm = getSupportFragmentManager();
        context = this;
        Intent intent = getIntent();
        buildSiteId = intent.getIntExtra(KeyConstant.id, 0);
        applyId = intent.getIntExtra(KeyConstant.applyId, 0);
        processorId = intent.getIntExtra(KeyConstant.processorConfigId, 0);

        reportInfo = (WorkApplyBean) intent.getSerializableExtra(KeyConstant.LIST_OBJECT);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button rightBt = (Button) findViewById(R.id.commit_bt);
        rightBt.setText(applyId != 0 ? R.string.update : R.string.commit);
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReport();
            }
        });
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText(applyId != 0 ? "变更申请修改" : "变更申请");

        gongDiTv = (TextView) findViewById(R.id.report_add_gongqu_tv);
        seleteProceesorLayout = (RelativeLayout) findViewById(R.id.selete_change_proceesor_layout);
        heTongDuanTv = (TextView) findViewById(R.id.report_add_hetongduan_tv);
        gongXuTv = (TextView) findViewById(R.id.report_add_gongxu_tv);
        planAddTv = (TextView) findViewById(R.id.report_add_stage_time_seleted_tv);
        planItemLayout = (LinearLayout) findViewById(R.id.plan_item_layout);

        contentLayout = (LinearLayout) findViewById(R.id.report_content_layout);
        planAddTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processorId == 0) {
                    ToastUtil.show(context, "请选择要变更的工序");
                } else {
                    showPlansDialog();
                }
            }
        });

        changeLevelTv = (TextView) findViewById(R.id.change_level_tv);
        changeLevelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeletedDialog(changeLevelArr, changeLevelTv, 3);
            }
        });

        changePlaceTv = (TextView) findViewById(R.id.change_place_tv);
        drawingNameTv = (TextView) findViewById(R.id.drawing_name_tv);
        drawingNoTv = (TextView) findViewById(R.id.drawing_number_tv);
        remarkTv = (TextView) findViewById(R.id.change_remark_tv);

        showInputDialog(changePlaceTv, "变更部位");
        showInputDialog(drawingNameTv, "原设计图名称");
        showInputDialog(drawingNoTv, "图号");
        initFileView();


        //修改
        if (applyId != 0) {
            getPlansData();
            seleteProceesorLayout.setVisibility(View.GONE);
            setUpdateView();
        } else {//新增
            getProcessorsData();//工序
        }
    }

    //修改才会进这里
    private void setUpdateView() {

        drawingNameTv = (TextView) findViewById(R.id.drawing_name_tv);
        drawingNoTv = (TextView) findViewById(R.id.drawing_number_tv);

        changeRemark = reportInfo.getChangeRemark();
        remarkTv.setText(TextUtil.isEmpty(changeRemark) ? "无" : changeRemark);

        changePlace = reportInfo.getChangePlace();
        changePlaceTv.setText(changePlace);

        changeLevel = reportInfo.getChangeLevel();
        changeLevelTv.setText(changeLevelArr[changeLevel - 1]);
        designDraw = reportInfo.getDesignDraw();
        drawingNameTv.setText(designDraw);
        designDrawNo = reportInfo.getDesignDrawNo();
        drawingNoTv.setText(designDrawNo);

        //计划列表
        List<WorkApplyBean.DetailBean> detail = reportInfo.getDetail();
        if (detail == null) {
            return;
        }
        planSeletedInfoList.clear();
        PlanChangedInfo info;
        for (WorkApplyBean.DetailBean detailBean : detail) {
            int planId = detailBean.getProcessorPlanId();
            String invest = detailBean.getPlanInvest() + "";
            String planBeginDate = detailBean.getPlanBeginDate();
            String endDate = detailBean.getPlanEndDate();
            info = new PlanChangedInfo(planId, detailBean.getName(), invest, planBeginDate, endDate);
            planSeletedInfoList.add(info);
        }
        notifyPlanItemDataSetChanged(planSeletedInfoList);


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
        fileListAdapter.setDate(fileData);
        ImageUtil.reSetLVHeight(context, listView);

    }

    private void showInputDialog(final TextView onClickTv, final String hint) {
        onClickTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context).title("")
                        .input(hint, "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                onClickTv.setText(input);
                            }
                        }).show();
            }
        });
    }


    private void postReport() {
        changeRemark = remarkTv.getText().toString();
        changePlace = changePlaceTv.getText().toString();
        designDraw = drawingNameTv.getText().toString();
        designDrawNo = drawingNoTv.getText().toString();
        if (processorId == 0) {
            ToastUtil.show(context, "请选择变更的工序");
            return;
        }
        if (planSeletedInfoList == null || planSeletedInfoList.size() == 0) {
            ToastUtil.show(context, "请选择要变更的计划");
            return;
        }

        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_change_applies;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject j = new JSONObject();
        try {
            j.put(KeyConstant.id, processorId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizProcessor, j);
        map.put(KeyConstant.changeLevel, changeLevel == 0 ? null : changeLevel);
        map.put(KeyConstant.changePlace, changePlace);
        map.put(KeyConstant.designDraw, designDraw);//设计图名称
        map.put(KeyConstant.designDrawNo, designDrawNo);//

        JSONArray detailArr = new JSONArray();
        for (PlanChangedInfo planChangedInfo : planSeletedInfoList) {
            JSONObject resultObj = new JSONObject();
            try {
                resultObj.put(KeyConstant.processorPlanId, planChangedInfo.getId());
                long planBeginDate = format.parse(planChangedInfo.getPlanBeginDate()).getTime();
                resultObj.put(KeyConstant.planBeginDate, DateUtil.millonsToUTC(planBeginDate));
                long planEndDate = format.parse(planChangedInfo.getPlanEndDate()).getTime();
                resultObj.put(KeyConstant.planEndDate, DateUtil.millonsToUTC(planEndDate));
                resultObj.put(KeyConstant.planInvest, planChangedInfo.getInvest());//确认,待整改
                detailArr.put(resultObj);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        map.put(KeyConstant.detail, detailArr);
        //附件
        if (fileData != null) {
            try {
                JSONArray picArr = new JSONArray();
                JSONArray attachmentArr = new JSONArray();
                for (FileListInfo fileInfo : fileData) {
                    String fileUrl = fileInfo.fileUrl;
                    String fileName = fileInfo.fileName;
                    JSONObject fileObj = new JSONObject();
                    fileObj.put(KeyConstant.name, fileName);
                    fileObj.put(KeyConstant.url, fileUrl);
                    picArr.put(fileObj);
                }
                map.put(KeyConstant.pic, picArr);
                map.put(KeyConstant.attachment, attachmentArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        map.put(KeyConstant.changeRemark, changeRemark);

        int postType = Request.Method.POST;
        if (applyId != 0) {
            map.put(KeyConstant.id, applyId);
            postType = Request.Method.PUT;
        }

        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(postType, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                DialogHelper.hideWaiting(fm);
                if (result == null) {
                    DialogHelper.hideWaiting(fm);
                    ToastUtil.show(context, "提交失败");
                    return;
                }
                if (applyId != 0) {
                    ToastUtil.show(context, "汇报修改成功");
                    WorkChangeDetailActivity.context.finish();
                    context.finish();
                } else {
                    ToastUtil.show(context, "申请提交成功");
                    context.finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                DialogHelper.hideWaiting(fm);
                ToastUtil.show(context, getString(R.string.server_exception));
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

    //plans列表  选择工序后,请求,默认拿第一条
    private void getPlansData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_processors + "/" + processorId +
                UrlConstant.processorPlans;

        Response.Listener<List<PlanChangedInfo>> successListener = new Response
                .Listener<List<PlanChangedInfo>>() {
            @Override
            public void onResponse(List<PlanChangedInfo> result) {
                if (result == null || result.size() == 0 || null == result.get(0)) {
                    ToastUtil.show(context, "暂无计划信息");
                    return;
                }
                setPlanView(result);
            }
        };

        Request<List<PlanChangedInfo>> versionRequest = new
                GsonRequest<List<PlanChangedInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, "获取计划信息失败,稍后重试");
                    }
                }, new TypeToken<List<PlanChangedInfo>>() {
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

    //默认第一条
    private void setPlanView(List<PlanChangedInfo> result) {
        planAllInfoList = result;
        //设置默认计划数据

        planTitleList.clear();
        for (PlanChangedInfo PlanChangedInfo : result) {
            planTitleList.add(PlanChangedInfo.getName());
        }
    }

    private void notifyPlanItemDataSetChanged(List<PlanChangedInfo> planInfos) {
        planItemLayout.removeAllViews();

        for (int i = 0; i < planInfos.size(); i++) {
            final PlanChangedInfo itemInfo = planInfos.get(i);
            View itemView = View.inflate(context, R.layout.item_work_change_add, null);
            TextView planNameTv = (TextView) itemView.findViewById(R.id.plan_name_tv);
            final TextView realStartDateTv = (TextView) itemView.findViewById(R.id.real_start_time_tv);
            final TextView realEndDateTv = (TextView) itemView.findViewById(R.id.real_end_time_tv);
            final TextView planPeriodTv = (TextView) itemView.findViewById(R.id.plan_period_tv);


            planNameTv.setText(itemInfo.getName());
            realStartDateTv.setText(TextUtil.substringTime(itemInfo.getPlanBeginDate()));
            realEndDateTv.setText(TextUtil.substringTime(itemInfo.getPlanEndDate()));
            final String planInvest = itemInfo.getInvest() == null ? "0" : itemInfo.getInvest();
            planPeriodTv.setText(planInvest + getString(R.string.money_unit));

            final int index = i;
            planPeriodTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(context).title("计划产值")
                            .inputType(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            .input(planInvest, "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    planPeriodTv.setText(input + getString(R.string.money_unit));
                                    planSeletedInfoList.get(index).setInvest(input.toString());
                                }
                            }).show();
                }
            });
            realStartDateTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeSet(realStartDateTv, 0, index);
                }
            });
            realEndDateTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeSet(realEndDateTv, 1, index);
                }
            });
            planItemLayout.addView(itemView);
        }
    }

    private void showPlansDialog() {
        if (null == planTitleList || planTitleList.size() == 0) {
            ToastUtil.show(context, "计划阶段数据为空");
            return;
        }
        new MaterialDialog.Builder(context)
                .title("选择要变更的计划")
                .items(planTitleList)
                .positiveText(R.string.sure)
                .negativeText(R.string.cancel)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog,
                                               Integer[] indexArr, CharSequence[] itemNameStr) {

                        planSeletedInfoList.clear();
                        int size = planAllInfoList.size();
                        for (int i = 0; i < size; i++) {
                            if (Arrays.asList(indexArr).contains(i)) {
                                planSeletedInfoList.add(planAllInfoList.get(i));
                            }
                        }

                        if (null != planSeletedInfoList && planSeletedInfoList.size() != 0) {
                            notifyPlanItemDataSetChanged(planSeletedInfoList);
                        }
                        return true;
                    }
                }).show();

        //设置计划的数据
        //setPlanItemView(planInfo);


    }

    public void showSeletedDialog(final String[] arr, final TextView titleTv, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                titleTv.setText(arr[i]);
                if (type == 0) {
                    contractId = contractList.get(i).getId();
                } else if (type == 1) {
                    buildSiteId = buildSiteInfo.get(i).getId();
                } else if (type == 2) {
                    processorId = processorsInfo.get(i).getId();

                    //请求计划
                    getPlansData();
                } else if (type == 3) {
                    changeLevel = i + 1;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, 1000);

    }

    //选择标段
    public void seleteContracts(View view) {
        int size = contractList.size();
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = contractList.get(i).getName();
        }
        showSeletedDialog(array, heTongDuanTv, 0);
    }

    //选择工地
    public void seleteGongQu(View view) {
        if (contractId == 0) {
            ToastUtil.show(context, "请选择标段");
            return;
        }
        getBuildSiteListData();
    }

    //获取工地列表
    private void getBuildSiteListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_contracts + "/"
                + contractId + UrlConstant.buildSites;

        Response.Listener<List<BuildSiteInfo>> successListener = new Response
                .Listener<List<BuildSiteInfo>>() {
            @Override
            public void onResponse(List<BuildSiteInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                buildSiteInfo.clear();
                buildSiteInfo = result;

                int size = buildSiteInfo.size();
                String[] array = new String[size];
                for (int i = 0; i < size; i++) {
                    array[i] = buildSiteInfo.get(i).getName();
                }

                //工地
                showSeletedDialog(array, gongDiTv, 1);
            }
        };

        Request<List<BuildSiteInfo>> versionRequest = new
                GsonRequest<List<BuildSiteInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<BuildSiteInfo>>() {
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

    String workArr1[] = new String[]{"工程进度", "工程质量", "安全生产"};

    //工序
    public void seleteChangeProcessors(View view) {
    /*    String gongqu = gongDiTv.getText().toString();
        if (TextUtil.isEmpty(gongqu)) {
            ToastUtil.show(context, "请选择要汇报的工地");
        } else {
            getProcessorsData();//工序
        }*/
        if (processorArray != null) {
            showSeletedDialog(processorArray, gongXuTv, 2);
        }
    }

    //工序
    private void getProcessorsData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_buildSites + "/"
                + buildSiteId + UrlConstant.processors;

        Response.Listener<List<ProcessorsInfo>> successListener = new Response
                .Listener<List<ProcessorsInfo>>() {
            @Override
            public void onResponse(List<ProcessorsInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                processorsInfo.clear();
                processorsInfo = result;

                int size = processorsInfo.size();
                processorArray = new String[size];
                for (int i = 0; i < size; i++) {
                    ProcessorsInfo.BizProcessorConfigBean bizProcessorConfig = processorsInfo.get(i).getBizProcessorConfig();
                    String name = bizProcessorConfig.getName();
                    processorArray[i] = name == null ? "工序" + i : name;
                }
            }
        };

        Request<List<ProcessorsInfo>> versionRequest = new
                GsonRequest<List<ProcessorsInfo>>(
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
                }, new TypeToken<List<ProcessorsInfo>>() {
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

    String realStartTime;
    String realEndTime;

    private void showTimeSet(final TextView curTv, final int type, final int index) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        String changedDateYMD = DateUtil.getStrTime_YMD(millseconds);
                        curTv.setText(changedDateYMD);
                        if (type == 0) {
                            date1 = new Date(millseconds);
                            realStartTime = millseconds + "";
                            planSeletedInfoList.get(index).setPlanBeginDate(changedDateYMD);
                        } else {
                            date2 = new Date(millseconds);
                            realEndTime = millseconds + "";
                            planSeletedInfoList.get(index).setPlanEndDate(changedDateYMD);
                        }

                        if (realStartTime != null && realEndTime != null) {
                            String periodStr = TextUtil.differentDaysByMillisecond2(date2, date1) + "天";//实际工期
                        }

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


    private List<FileListInfo> fileData = new ArrayList<>();

    private void initFileView() {
        //附件
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
        fileListAdapter.setCallBack(new FileListAdapter.DataRemoveCallBack() {
            @Override
            public void finish(List<FileListInfo> data) {
                fileData = data;
            }
        });

        findViewById(R.id.card_detail_file_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFileDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //上传附件
        String fileType = "";
        String path = "";
        if (data != null && data.getData() != null) {
            path = FileTypeUtil.getPath(context, data.getData());
            //不是合格的类型
            if (!FileTypeUtil.isFileType(path) && !ImageUtil.isImageSuffix(path)) {
                ToastUtil.show(context, "暂不支持该文件类型");
                return;
            }
            fileType = ImageUtil.isImageSuffix(path) ? Constant.FILE_TYPE_IMG : Constant.FILE_TYPE_DOC;
        }
        //上传图片
        if (requestCode == 101 && data != null) {
            setIntent(data);
            getBundleP();
            if (pictures != null && pictures.size() > 0) {
                fileType = Constant.FILE_TYPE_IMG;
                for (int i = 0; i < pictures.size(); i++) {
                    path = pictures.get(i).getLocalURL();
                    fileType = Constant.FILE_TYPE_IMG;
                }
            }
        }
        File file = new File(path);
        uploadPictureThread(file, fileType);
    }

    private void uploadPictureThread(final File file, final String fileType) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KeyConstant.fileType, fileType);
        final String url = Constant.WEB_SITE_FILE + UrlConstant.url_biz_files;

        new Thread() {
            @Override
            public void run() {
                try {
                    RetrofitUtil.upLoadByCommonPost(url, file, map,
                            new RetrofitUtil
                                    .FileUploadListener() {
                                @Override
                                public void onProgress(long pro, double precent) {
                                    Log.d(TAG, "上传附件...." + pro);
                                }

                                @Override
                                public void onFinish(int code, final String responseUrl,
                                                     Map<String, List<String>> headers) {
                                    if (200 == code && responseUrl != null) {
                                        final String finalResponseUrl = responseUrl;
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileData.add(new FileListInfo(
                                                        file.getName(), finalResponseUrl, file.length(), finalResponseUrl));
                                                fileListAdapter.setDate(fileData);
                                                ImageUtil.reSetLVHeight(context, listView);
                                            }
                                        });
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    public void getBundleP() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictureBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictureBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictureBean>();
            }
        }
    }

    private void choisePicture() {
        int choose = 9 - pictures.size();
        Intent intent = new Intent(context, MulPictureActivity.class);
        bundle = setBundle();
        bundle.putInt("imageNum", choose);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    //选择文件
    private void choiseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void showChooseFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme);
        //    指定下拉列表的显示数据
        final String[] chooseFileArr = {"图片", "手机文件"};
        //    设置一个下拉的列表选择项
        builder.setItems(chooseFileArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    //文件
                    case 0:
                        choisePicture();
                        break;
                    //图片
                    case 1:
                        choiseFile();
                        break;
                }
            }
        });
        builder.show();
    }
}
