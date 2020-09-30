package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.android.waylinkage.activity.main.WorkFinishDetailActivity;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.PictureBean;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.GroupInfo;
import com.android.waylinkage.bean.ProcessorsInfo;
import com.android.waylinkage.bean.UnitInfo;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee   完工申请
 */
public class WorkFinishAddActivity extends CommonBaseActivity {
    private WorkFinishAddActivity context;
    private LinearLayout contentLayout;
    private TextView gongDiTv, heTongDuanTv, totalPeriodTv;
    private int contractId;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();
    private int buildSiteId, constructionUnitId, prospectUnitId, designUnitId, contractUnitId,
            supervisionUnitId, archDrawAuditUnitId, detectUnitId;
    private List<ProcessorsInfo> processorsInfo = new ArrayList<>();
    private FragmentManager fm;
    private List<ContractInfo> contractList = new ArrayList<>();
    private List<GroupInfo> groupInfos = new ArrayList<>();
    private String constructionPermitNo, constructionCertNo;
    private List<GroupInfo.ChildrenBean> childrenInfoList;
    private int reportResult = 1;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private String realStartDate = "";
    private long actualFinishDate;
    private TextView constructionPermitNoTv, detectUnitTvNo, archDrawAuditUnitTvNo;
    private TextView detectUnitTvName, archDrawAuditUnitTvName;
    private TextView buildUnitTv, contractUnitTv, designUnitTv;
    private TextView buildUnitTv0, contractUnitTv0, designUnitTv0;
    private TextView prospectUnitTv, supervisionUnitTv;
    private TextView prospectUnitTv0, supervisionUnitTv0;
    private TextView remarkTv;
    private int applyId;
    private WorkApplyBean reportInfo;
    private TextView tv;
    private TextView actualFinishDateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_work_finish_add);
        fm = getSupportFragmentManager();
        context = this;
        applyId = getIntent().getIntExtra(KeyConstant.applyId, 0);
        buildSiteId = getIntent().getIntExtra(KeyConstant.id, 0);
        reportInfo = (WorkApplyBean) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button rightBt = (Button) findViewById(R.id.commit_bt);
        rightBt.setText(applyId == 0 ? R.string.commit : R.string.update);
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                postReport();
            }
        });
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText(applyId != 0 ? "完工申请修改" : "完工申请");

        gongDiTv = (TextView) findViewById(R.id.report_add_gongqu_tv);
        totalPeriodTv = (TextView) findViewById(R.id.total_period_tv);
        heTongDuanTv = (TextView) findViewById(R.id.report_add_hetongduan_tv);


        initFileView();

        actualFinishDateTv = (TextView) findViewById(R.id.work_end_time_tv);
        actualFinishDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet((TextView) v, 0);
            }
        });


        //施工许可证号
        constructionPermitNoTv = (TextView) findViewById(R.id.construction_license_number_tv);

        buildUnitTv = (TextView) findViewById(R.id.build_unit_tv);
        prospectUnitTv = (TextView) findViewById(R.id.exploration_unit_tv);
        designUnitTv = (TextView) findViewById(R.id.design_unit_tv);
        remarkTv = (TextView) findViewById(R.id.remark_tv);
        contractUnitTv = (TextView) findViewById(R.id.contractor_unit_tv);
        supervisionUnitTv = (TextView) findViewById(R.id.supervision_unit_tv);
        archDrawAuditUnitTvNo = (TextView) findViewById(R.id.construction_drawing_check_unit_tv);
        detectUnitTvNo = (TextView) findViewById(R.id.detect_unit_tv);

        showInputDialog(constructionPermitNoTv, "施工许可证号");
        showInputDialog(buildUnitTv, "建设单位资质证号");
        showInputDialog(prospectUnitTv, "勘查单位资质证号");
        showInputDialog(designUnitTv, "设计单位资质证号");
        showInputDialog(contractUnitTv, "承建单位资质证号");
        showInputDialog(supervisionUnitTv, "监理单位资质证号");
        showInputDialog(detectUnitTvNo, "检测单位资质证号");
        showInputDialog(archDrawAuditUnitTvNo, "施工图审查单位资质证号");

        buildUnitTv0 = (TextView) findViewById(R.id.build_unit_tv0);
        prospectUnitTv0 = (TextView) findViewById(R.id.exploration_unit_tv0);
        designUnitTv0 = (TextView) findViewById(R.id.design_unit_tv0);
        contractUnitTv0 = (TextView) findViewById(R.id.contractor_unit_tv0);
        supervisionUnitTv0 = (TextView) findViewById(R.id.supervision_unit_tv0);
        archDrawAuditUnitTvName = (TextView) findViewById(R.id.construction_drawing_check_unit_tv0);
        detectUnitTvName = (TextView) findViewById(R.id.detect_unit_tv0);
        showUnitSeleteDialog(buildUnitTv0);//建设单位
        showUnitSeleteDialog(prospectUnitTv0);
        showUnitSeleteDialog(designUnitTv0);
        showUnitSeleteDialog(contractUnitTv0);
        showUnitSeleteDialog(supervisionUnitTv0);
        showUnitSeleteDialog(archDrawAuditUnitTvName);
        showUnitSeleteDialog(detectUnitTvName);

        getBuildSiteListData();
        getUnitData();

        //修改才会进这里
        if (applyId != 0) {
            setUpdateView();
        }
    }

    //修改才会进这里
    private void setUpdateView() {
        //施工许可证号

        constructionPermitNoTv.setText(reportInfo.getConstructionPermitNo());

        WorkApplyBean.ConstructionUnitBean constructionUnit = reportInfo.getConstructionUnit();
        if (constructionUnit != null) {
            constructionUnitId = constructionUnit.getIdX();
            buildUnitTv0.setText(constructionUnit.getNameCn());
            buildUnitTv.setText(reportInfo.getConstructionCertNo());
        }
        WorkApplyBean.ProspectUnitBean prospectUnit = reportInfo.getProspectUnit();
        //勘察单位
        if (prospectUnit != null) {
            prospectUnitId = prospectUnit.getIdX();
            prospectUnitTv0.setText(prospectUnit.getNameCn());
            prospectUnitTv.setText(reportInfo.getProspectCertNo());
        }
        WorkApplyBean.DesignUnitBean designUnit = reportInfo.getDesignUnit();
        if (designUnit != null) {
            designUnitId = designUnit.getIdX();
            designUnitTv0.setText(designUnit.getNameCn());
            designUnitTv.setText(reportInfo.getDesignCertNo());
        }
        WorkApplyBean.ContractUnitBean contractUnit = reportInfo.getContractUnit();
        if (contractUnit != null) {
            contractUnitId = contractUnit.getIdX();
            contractUnitTv0.setText(contractUnit.getNameCn());
            contractUnitTv.setText(reportInfo.getContractCertNo());
        }
        WorkApplyBean.SupervisionUnitBean supervisionUnit = reportInfo.getSupervisionUnit();
        if (supervisionUnit != null) {
            supervisionUnitId = supervisionUnit.getIdX();
            supervisionUnitTv0.setText(supervisionUnit.getNameCn());
            supervisionUnitTv.setText(reportInfo.getSupervisionCertNo());
        }
        WorkApplyBean.DetectUnitBean detectUnit = reportInfo.getDetectUnit();
        if (detectUnit != null) {
            detectUnitId = detectUnit.getIdX();
            detectUnitTvName.setText(detectUnit.getNameCn());
            detectUnitTvNo.setText(reportInfo.getDetectCertNo());
        }
        WorkApplyBean.ArchDrawAuditUnitBean archDrawAuditUnit = reportInfo.getArchDrawAuditUnit();
        if (archDrawAuditUnit != null) {
            archDrawAuditUnitId = archDrawAuditUnit.getIdX();
            archDrawAuditUnitTvName.setText(archDrawAuditUnit.getNameCn());
            archDrawAuditUnitTvNo.setText(reportInfo.getDesignDrawNo());
        }
        realStartDate = reportInfo.getBizBuildSite().getRealStartDate();

        remarkTv.setText(reportInfo.getRemark());
        String actualFinishDateStr = reportInfo.getActualFinishDate();

        try {
            actualFinishDateTv.setText(actualFinishDateStr);
            actualFinishDate = format.parse(actualFinishDateStr).getTime();

            totalPeriodTv.setText(TextUtil.differentDaysByMillisecond2(
                    new Date(actualFinishDate), format.parse(realStartDate)) + "天");
        } catch (Exception e) {

        }

        //附件
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

    private void showUnitSeleteDialog(final TextView onClickTv) {
        onClickTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (unitNameList == null || unitNameList.size() == 0) {
                    ToastUtil.show(context, "获取单位列表数据失败,请稍后重试");
                    return;
                }
                MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(context);
                mBuilder.items(unitNameList);
                mBuilder.autoDismiss(true);
                mBuilder.itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position,
                                            CharSequence name) {
                        onClickTv.setText(name);
                        Integer unitId = unitIdList.get(position);
                        if (onClickTv == buildUnitTv0) {
                            constructionUnitId = unitId;
                        } else if (onClickTv == prospectUnitTv0) {
                            prospectUnitId = unitId;
                        } else if (onClickTv == designUnitTv0) {
                            designUnitId = unitId;
                        } else if (onClickTv == contractUnitTv0) {
                            contractUnitId = unitId;
                        } else if (onClickTv == supervisionUnitTv0) {
                            supervisionUnitId = unitId;
                        } else if (onClickTv == archDrawAuditUnitTvName) {
                            archDrawAuditUnitId = unitId;
                        } else if (onClickTv == detectUnitTvName) {
                            detectUnitId = unitId;
                        }
                    }
                });
                mBuilder.show();
            }
        });
    }

    private void getUnitData() {
        String url = Constant.WEB_SITE + "/system/corporations/all";

        Response.Listener<List<UnitInfo>> successListener = new Response
                .Listener<List<UnitInfo>>() {
            @Override
            public void onResponse(List<UnitInfo> result) {
                if (result == null || result.size() == 0) {
                    return;
                }
                unitNameList.clear();
                unitIdList.clear();
                for (UnitInfo unitInfo : result) {
                    unitNameList.add(unitInfo.getNameCn());
                    unitIdList.add(unitInfo.getId());
                }
            }
        };

        Request<List<UnitInfo>> versionRequest = new
                GsonRequest<List<UnitInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }, new TypeToken<List<UnitInfo>>() {
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

    private ArrayList<String> unitNameList = new ArrayList<>();
    private ArrayList<Integer> unitIdList = new ArrayList<>();
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

    private String designCertNo, detectCertNo, prospectCertNo, contractCertNo,
            supervisionCertNo, archDrawAuditCertNo;

    private void postReport() {
        constructionPermitNo = constructionPermitNoTv.getText().toString();//施工许可证号

        designCertNo = designUnitTv.getText().toString();//设计资质证号
        constructionCertNo = buildUnitTv.getText().toString();
        prospectCertNo = prospectUnitTv.getText().toString();
        contractCertNo = contractUnitTv.getText().toString();
        supervisionCertNo = supervisionUnitTv.getText().toString();
        detectCertNo = detectUnitTvNo.getText().toString();
        archDrawAuditCertNo = archDrawAuditUnitTvNo.getText().toString();

        if (0 == actualFinishDate) {
            ToastUtil.show(context, "实际完工日期不能为空");
            return;
        }
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_finish_applies;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject idObj = new JSONObject();
        try {
            idObj.put(KeyConstant.id, buildSiteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject designUnitObj = new JSONObject();
        try {
            designUnitObj.put(KeyConstant.id, designUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject archDrawAuditUnitObj = new JSONObject();
        try {
            archDrawAuditUnitObj.put(KeyConstant.id, archDrawAuditUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject supervisionObj = new JSONObject();//监理
        try {
            supervisionObj.put(KeyConstant.id, supervisionUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject prospectUnitObj = new JSONObject();//勘察单位
        try {
            prospectUnitObj.put(KeyConstant.id, prospectUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject constructionUnitObj = new JSONObject();//建设单位
        try {
            constructionUnitObj.put(KeyConstant.id, constructionUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject contractUnitObj = new JSONObject();//承建单位
        try {
            contractUnitObj.put(KeyConstant.id, contractUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject detectUnitObj = new JSONObject();//承建单位
        try {
            detectUnitObj.put(KeyConstant.id, detectUnitId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizBuildSite, idObj);
        map.put(KeyConstant.actualFinishDate, DateUtil.millonsToUTC(actualFinishDate));//实际完工日期
        map.put(KeyConstant.constructionPermitNo, constructionPermitNo);//施工许可号

        map.put(KeyConstant.contractUnit, contractUnitId == 0 ? null : contractUnitObj);//承建单位
        map.put(KeyConstant.contractCertNo, contractCertNo);

        map.put(KeyConstant.designUnit, designUnitId == 0 ? null : designUnitObj);//设计单位
        map.put(KeyConstant.designCertNo, designCertNo);//证号

        map.put(KeyConstant.supervisionUnit, supervisionUnitId == 0 ? null : supervisionObj);//监理单位
        map.put(KeyConstant.supervisionCertNo, supervisionCertNo);

        map.put(KeyConstant.archDrawAuditUnit, archDrawAuditUnitId == 0 ? null : archDrawAuditUnitObj);//施工图审查单位
        map.put(KeyConstant.archDrawAuditCertNo, archDrawAuditCertNo);

        map.put(KeyConstant.prospectUnit, prospectUnitId == 0 ? null : prospectUnitObj);//勘察单位
        map.put(KeyConstant.prospectCertNo, prospectCertNo);//勘察单位

        map.put(KeyConstant.detectUnit, detectUnitId == 0 ? null : detectUnitObj);//检测单位
        map.put(KeyConstant.detectCertNo, detectCertNo);//检测单位

        map.put(KeyConstant.constructionUnit, constructionUnitId == 0 ? null : constructionUnitObj);//建设单位
        map.put(KeyConstant.constructionCertNo, constructionCertNo);//建设单位号

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

        map.put(KeyConstant.remark, remarkTv.getText() == null ? "无" : remarkTv.getText().toString());

        int postType = Request.Method.POST;
        if (applyId != 0) {
            map.put(KeyConstant.id, applyId);
            postType = Request.Method.PUT;
        }

        JSONObject jsonObject = new JSONObject(map);
        Log.d(TAG, "请求数据:"+jsonObject.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(postType, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        DialogHelper.hideWaiting(fm);
                        if (result == null) {
                            ToastUtil.show(context, "汇报提交失败");
                            return;
                        }
                        if (applyId != 0) {
                            ToastUtil.show(context, "汇报修改成功");
                            WorkFinishDetailActivity.context.finish();
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
                if (error != null && error.networkResponse.statusCode == 400) {
                    DialogUtils.showTipDialog(context, getString(R.string.work_has_finished));
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
/*
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
                    BuildSiteInfo buildSiteInfo = WorkFinishAddActivity.this.buildSiteInfo.get(i);
                    buildSiteId = buildSiteInfo.getId();
                    realStartDate = TextUtil.substringTime(buildSiteInfo.getPlanBeginDate());

                    try {
                        totalPeriodTv.setText(
                                TextUtil.differentDaysByMillisecond2(new Date(actualFinishDate),
                                        format.parse(realStartDate)) + "天");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, 1000);

    }*/

    //选择标段
    public void seleteContracts(View view) {
        int size = contractList.size();
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = contractList.get(i).getName();
        }
        //showSeletedDialog(array, heTongDuanTv, 0);
    }

    //选择工地
    public void seleteGongQu(View view) {
        if (contractId == 0) {
            ToastUtil.show(context, "请选择标段");
            return;
        }
        //getBuildSiteListData();
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
                        realStartDate = TextUtil.substringTime(buildSiteInfo1.getRealStartDate());
                        break;
                    }
                }


            }
        };

        Request<List<BuildSiteInfo>> versionRequest = new
                GsonRequest<List<BuildSiteInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
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

    private void showTimeSet(final TextView timeTv, final int type) {
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (type == 0) {
                            try {
                                actualFinishDate = millseconds;
                                totalPeriodTv.setText(TextUtil.differentDaysByMillisecond2(
                                        new Date(actualFinishDate), format.parse(realStartDate)) + "天");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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

}
