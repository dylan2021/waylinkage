package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.PictureBean;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.ProcessorPlanInfo;
import com.android.waylinkage.bean.ProcessorsInfo;
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
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.FileTypeUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.EasyPickerView;
import com.android.waylinkage.view.PullScrollView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 * 新增汇报
 */

public class ProgressAddActivity extends CommonBaseActivity {
    private ProgressAddActivity context;
    private LinearLayout contentLayout;
    private TextView gongDiTv, heTongDuanTv;
    private TextView gongXuTv, realPeriodTv, eventTv;
    private TextView planTitleTv;
    private int seletedIndex;
    private int contractId;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();
    private int buildSiteId, processorId = -1;
    private List<ProcessorsInfo> processorsInfo = new ArrayList<>();
    private ArrayList<String> stageDataList = new ArrayList<>();
    private int processorPlanId = -1;
    private int planStatus = 0;
    private EditText realMoneyEt;
    private List<ProcessorPlanInfo> planInfoList = new ArrayList<>();
    private FragmentManager fm;
    private List<ContractInfo> contractList = new ArrayList<>();
    private PullScrollView reportPullScroolView;
    private Date date1, date2;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private String[] processorNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_process_add);
        fm = getSupportFragmentManager();
        context = this;
        buildSiteId = getIntent().getIntExtra(KeyConstant.id, 0);
        contractList = (List<ContractInfo>) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText("进度汇报");

        gongDiTv = (TextView) findViewById(R.id.report_add_gongqu_tv);
        heTongDuanTv = (TextView) findViewById(R.id.report_add_hetongduan_tv);
        eventTv = (TextView) findViewById(R.id.report_event_tv);
        realPeriodTv = (TextView) findViewById(R.id.real_persiod_tv);
        gongXuTv = (TextView) findViewById(R.id.report_add_gongxu_tv);
        planTitleTv = (TextView) findViewById(R.id.report_add_stage_time_seleted_tv);
        contentLayout = (LinearLayout) findViewById(R.id.report_content_layout);

        planTitleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (processorId == -1) {
                    ToastUtil.show(context, "请选择一个工序");
                } else {
                    showPlansDialog();
                }
            }
        });

        realMoneyEt = (EditText) findViewById(R.id.report_real_money_tv);
        reportPullScroolView = (PullScrollView) findViewById(R.id.report_psv);

       /* realMoneyEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    reportPullScroolView.scrollTo(0, 500);
                    realMoneyEt.setCursorVisible(true);
                }
            }
        });
*/
        initFileView();
        getProcessorsData();//工序
    }

    private void postReport() {
        if (processorPlanId == -1) {
            ToastUtil.show(context, "请选择一个计划");
            return;
        }
        if (planStatus == 0) {
            DialogUtils.showTipDialog(context, getString(R.string.unreport_content_progress));
            return;
        }
        if (realStartTime == 0) {
            ToastUtil.show(context, "实际开始时间不能为空");
            return;
        }
        if (realEndTime == 0) {
            ToastUtil.show(context, "实际结束时间不能为空");
            return;
        }
        final String realInvestStr = realMoneyEt.getText().toString();
        final String remarkStr = ((EditText) findViewById(R.id.remark_tv)).getText().toString();
        if (TextUtil.isEmpty(realInvestStr)) {
            ToastUtil.show(context, "实际产值不能为空哦");
            return;
        }
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_biz_processorFins;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject j = new JSONObject();
        try {
            j.put(KeyConstant.id, processorPlanId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizProcessorPlan, j);
        map.put(KeyConstant.reportInvest, realInvestStr);
        map.put(KeyConstant.realBeginDate, DateUtil.millonsToUTC(realStartTime));
        map.put(KeyConstant.realEndDate, DateUtil.millonsToUTC(realEndTime));
        map.put(KeyConstant.description, remarkStr);

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

        JSONObject jsonObject = new JSONObject(map);
        Log.d(TAG, "进度汇报:" + jsonObject.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result == null) {
                    DialogHelper.hideWaiting(fm);
                    ToastUtil.show(context, "提交失败");
                    return;
                }
                ToastUtil.show(context, "提交成功");
                DialogHelper.hideWaiting(fm);
                context.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
                Log.e("进度汇报失败 ---->", (error==null||error.networkResponse==null)?"null":new String(error.networkResponse.data));
                if (error != null && error.networkResponse.statusCode == 400) {
                    DialogUtils.showTipDialog(context, getString(R.string.unreport_content_progress));
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

    //plans列表  选择工序后,请求,默认拿第一条
    private void getPlansData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_processors + "/" + processorId +
                UrlConstant.processorPlans;

        Response.Listener<List<ProcessorPlanInfo>> successListener = new Response
                .Listener<List<ProcessorPlanInfo>>() {
            @Override
            public void onResponse(List<ProcessorPlanInfo> result) {
                if (result == null || result.size() == 0 || null == result.get(0)) {
                    ToastUtil.show(context, "暂无计划信息");
                    return;
                }
                setPlanView(result);
            }
        };

        Request<List<ProcessorPlanInfo>> versionRequest = new
                GsonRequest<List<ProcessorPlanInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, "获取计划信息失败,稍后重试");
                    }
                }, new TypeToken<List<ProcessorPlanInfo>>() {
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
    private void setPlanView(List<ProcessorPlanInfo> result) {
        planInfoList = result;
        ProcessorPlanInfo defInfo = result.get(0);//计划
        processorPlanId = defInfo.getId();
        planStatus = defInfo.getConfirmed();
        //设置默认计划数据

        setPlanItemView(defInfo);

        stageDataList.clear();
        for (ProcessorPlanInfo processorPlanInfo : result) {
            stageDataList.add(processorPlanInfo.getName());
        }
    }

    private void setPlanItemView(ProcessorPlanInfo planInfo) {
        planTitleTv.setText(planInfo.getName());
    }

    private void showPlansDialog() {
        if (stageDataList.size() == 0) {
            ToastUtil.show(context, "计划阶段数据为空");
            return;
        }
        EasyPickerView easyPickerView = new EasyPickerView(context);
        easyPickerView.setDataList(stageDataList);
        easyPickerView.setRecycleMode(false);
        easyPickerView.moveTo(seletedIndex);
        easyPickerView.setOnScrollChangedListener(new EasyPickerView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int curIndex) {
                seletedIndex = curIndex;
            }

            @Override
            public void onScrollFinished(int curIndex) {
                seletedIndex = curIndex;
            }
        });
        final AlertDialog dialog = new AlertDialog.Builder(context).
                setView(easyPickerView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        ProcessorPlanInfo planInfo = planInfoList.get(seletedIndex);
                        planStatus = planInfo.getConfirmed();
                        if (0 == planStatus) {
                            DialogUtils.showTipDialog(context, getString(R.string.unreport_content_progress));
                            return;

                        }
                        processorPlanId = planInfo.getId();

                        //设置计划的数据
                        setPlanItemView(planInfo);

                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

        dialog.show();
        dialog.getWindow().setLayout((ImageUtil.getScreenWidth(context) / 5 * 4),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void showSeletedDialog(final String[] arr, final TextView titleTv, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                titleTv.setText(arr[i].trim());
                if (type == 0) {
                    contractId = contractList.get(i).getId();
                } else if (type == 1) {
                    buildSiteId = buildSiteInfo.get(i).getId();
                } else if (type == 2) {
                    processorId = processorsInfo.get(i).getId();
                    //请求计划
                    getPlansData();
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
    public void seleteProcessors(View view) {
    /*    String gongqu = gongDiTv.getText().toString();
        if (TextUtil.isEmpty(gongqu)) {
            ToastUtil.show(context, "请选择要汇报的工地");
        } else {
            getProcessorsData();
        }*/

        if (null != processorNameArray) {
            showSeletedDialog(processorNameArray, gongXuTv, 2);
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
                processorNameArray = new String[size];
                for (int i = 0; i < size; i++) {
                    ProcessorsInfo.BizProcessorConfigBean bizProcessorConfig = processorsInfo.get(i).getBizProcessorConfig();
                    String name = bizProcessorConfig.getName();
                    processorNameArray[i] = name == null ? "工序" + i : name;
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

    //汇报事项
    public void seleteReportEvent(View view) {
        String gongxu = gongXuTv.getText().toString();
        if (TextUtil.isEmpty(gongxu)) {
            ToastUtil.show(context, "请选择要汇报的工序");
        } else {
            showSeletedDialog(workArr1, eventTv, 3);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    long realStartTime;
    long realEndTime;

    public void onRealStartTimeClick(View view) {
        showTimeSet(R.id.real_start_time_tv, 0);
    }

    private void showTimeSet(int id, final int type) {
        final TextView curTv = (TextView) findViewById(id);
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (millseconds > System.currentTimeMillis()) {
                            ToastUtil.show(context, getString(R.string.begin_end_date_must_be_before_now));
                            return;
                        }
                        if (type == 0) {
                            date1 = new Date(millseconds);
                            realStartTime = millseconds;
                        } else {
                            date2 = new Date(millseconds);
                            realEndTime = millseconds;
                        }
                        if (realEndTime != 0 && realEndTime <= realStartTime) {
                            ToastUtil.show(context, getString(R.string.endtime_must_later_than_starttime));
                            return;
                        }

                        curTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                        realPeriodTv.setText(TextUtil.differentDaysByMillisecond2(date2, date1) + "天");

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
    public void onRealEndTimeClick(View view) {
        showTimeSet(R.id.real_end_time_tv, 1);
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

    //保存提交
    public void onReportCommitClick(View view) {
        postReport();
    }
}
