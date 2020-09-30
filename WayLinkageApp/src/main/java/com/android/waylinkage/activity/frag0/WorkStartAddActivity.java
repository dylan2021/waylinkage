package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.android.waylinkage.bean.GroupInfo;
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
import java.text.ParseException;
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
public class WorkStartAddActivity extends CommonBaseActivity {
    private WorkStartAddActivity context;
    private LinearLayout contentLayout;
    private TextView gongDiTv, heTongDuanTv, totalPeriodTv;
    private TextView planBeginTimeTv, planEndTimeTv, remarkTv, preWorkTv;
    private int contractId;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();
    private int buildSiteId, processorConfigId = -1;
    private List<ProcessorsInfo> processorsInfo = new ArrayList<>();
    private FragmentManager fm;
    private List<ContractInfo> contractList = new ArrayList<>();
    private List<GroupInfo> groupInfos = new ArrayList<>();
    private int processorId;
    private List<GroupInfo.ChildrenBean> childrenInfoList;
    private int reportResult = 1;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private String applyWorkStartData;
    private String planEndDate = "";
    private long applyWorkDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_work_start_add);
        fm = getSupportFragmentManager();
        context = this;
        buildSiteId = getIntent().getIntExtra(KeyConstant.id,0);
        contractList = (List<ContractInfo>) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText("开工申请");

        gongDiTv = (TextView) findViewById(R.id.report_add_gongqu_tv);
        planBeginTimeTv = (TextView) findViewById(R.id.plan_start_time_tv);
        planEndTimeTv = (TextView) findViewById(R.id.plan_end_time_tv);
        totalPeriodTv = (TextView) findViewById(R.id.total_period_tv);
        preWorkTv = (TextView) findViewById(R.id.work_start_tv);
        remarkTv = (TextView) findViewById(R.id.remark_tv);
        heTongDuanTv = (TextView) findViewById(R.id.report_add_hetongduan_tv);

        //选择检查类型
        preWorkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title(R.string.work_start_pre_str)
                        .items(R.array.work_start_items)
                        .positiveText(R.string.sure)
                        .negativeText(R.string.cancel)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog dialog,
                                                       Integer[] indexArr, CharSequence[] itemNameStr) {
                                List<Integer> indexList = Arrays.asList(indexArr);
                                isPrepare = indexList.contains(0) ? true : false;
                                isPlan = indexList.contains(1) ? true : false;
                                isEmployee = indexList.contains(2) ? true : false;
                                isDevice = indexList.contains(3) ? true : false;
                                isMaterial = indexList.contains(4) ? true : false;
                                String preWorkStr = TextUtil.parseArrayToString(itemNameStr);
                                preWorkTv.setText(preWorkStr);
                                return true;
                            }
                        }).show();
            }
        });

        initFileView();

        TextView tv = (TextView) findViewById(R.id.work_start_time_tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet((TextView) v, 0);
            }
        });

        getBuildSiteListData();
    }

    private List<FileListInfo> fileData = new ArrayList<>();
    private boolean isDevice, isEmployee, isMaterial, isPlan, isPrepare;

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

    private void postReport() {
        if (buildSiteId == -1) {
            ToastUtil.show(context, "请选择工地");
            return;
        }
        if (0 == applyWorkDate) {
            ToastUtil.show(context, "申请开工日期不能为空");
            return;
        }
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_start_applies;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject j = new JSONObject();
        try {
            j.put(KeyConstant.id, buildSiteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizBuildSite, j);

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

        map.put(KeyConstant.applyWorkDate, DateUtil.millonsToUTC(applyWorkDate));
        map.put(KeyConstant.isDevice, isDevice);
        map.put(KeyConstant.isEmployee, isEmployee);
        map.put(KeyConstant.isMaterial, isMaterial);
        map.put(KeyConstant.isPlan, isPlan);
        map.put(KeyConstant.isPrepare, isPrepare);
        map.put(KeyConstant.remark, remarkTv.getText() == null ? "" : remarkTv.getText().toString());

        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "汇报提交失败");
                            return;
                        }
                        ToastUtil.show(context, "汇报提交成功");
                        DialogHelper.hideWaiting(fm);
                        context.finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
                if (error != null && error.networkResponse.statusCode == 400) {
                    DialogUtils.showTipDialog(context, getString(R.string.work_has_started));
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
                            new RetrofitUtil.FileUploadListener() {
                                @Override
                                public void onProgress(long pro, double precent) {
                                }

                                @Override
                                public void onFinish(int code, final String responseUrl, Map<String, List<String>> headers) {
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
                    BuildSiteInfo buildSiteInfo = WorkStartAddActivity.this.buildSiteInfo.get(i);
                    buildSiteId = buildSiteInfo.getId();

                    planBeginTimeTv.setText(TextUtil.substringTime(buildSiteInfo.getPlanBeginDate()));
                    planEndDate = TextUtil.substringTime(buildSiteInfo.getPlanEndDate());
                    planEndTimeTv.setText(planEndDate);

                    try {
                        totalPeriodTv.setText(TextUtil.differentDaysByMillisecond2(format.parse(planEndDate), new Date(
                                applyWorkDate)) + "天");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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

    }

    //获取工地列表
    private void getBuildSiteListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_buildSites + "/all";

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

                //工地
                for (int i = 0; i < size; i++) {
                    BuildSiteInfo buildSiteInfo1 = buildSiteInfo.get(i);
                    if (buildSiteId == buildSiteInfo1.getId()) {
                        setBuildInfo(buildSiteInfo1);
                        break;
                    }
                }

                //showSeletedDialog(array, gongDiTv, 1);
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

    private void setBuildInfo(BuildSiteInfo buildSiteInfo) {
        planBeginTimeTv.setText(TextUtil.substringTime(buildSiteInfo.getPlanBeginDate()));
        planEndDate = TextUtil.substringTime(buildSiteInfo.getPlanEndDate());
        planEndTimeTv.setText(planEndDate);
    }

    private void showTimeSet(final TextView timeTv, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            Date date = null;
                            try {
                                date = format.parse(planEndDate);
                                applyWorkDate = millseconds;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            totalPeriodTv.setText(TextUtil.differentDaysByMillisecond2(
                                    date, new Date(applyWorkDate)) + "天");
                        } else {
                        }

                        timeTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                    }
                })
                .setTitleStringId("")//标题
                .setCyclic(false)
                .setCancelStringId(getString(R.string.time_dialog_title_cancel))
                .setSureStringId(getString(R.string.time_dialog_title_sure))
                .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                .setThemeColor(context.getResources().getColor(R.color.mainColor))
                .setWheelItemTextSize(16)
                .setType(Type.YEAR_MONTH_DAY)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
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
        startActivityForResult(intent, 100);
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
    public void onReportCommitClick(View view) {
        //保存
        postReport();
    }
}
