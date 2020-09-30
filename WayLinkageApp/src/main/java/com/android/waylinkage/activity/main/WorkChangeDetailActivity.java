package com.android.waylinkage.activity.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.waylinkage.activity.frag0.WorkChangeAddActivity;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.PictureBean;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.FileInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.HistoryInfo;
import com.android.waylinkage.bean.WorkApplyBean;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.RetrofitUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.AuthsUtils;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.FileTypeUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;
import com.android.waylinkage.widget.mulpicture.MulPictureActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 * @Date 完工申请详情
 */
public class WorkChangeDetailActivity extends CommonBaseActivity {

    private int processorConfigId, applyId;
    private WorkApplyBean reportInfo;
    private String buildSitName;
    private String[] changeLevelArr = new String[]{"一般", "重要", "重大"};
    private boolean status;
    public static WorkChangeDetailActivity context;
    private LinearLayout planItemLayout;
    private TextView descRemarkTv;
    private String remark;
    private long applyWorkDate;
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;
    private List<FileListInfo> fileData = new ArrayList<>();
    private int buildSiteId;
    private EditText dialogChangeRemarkTv;
    private LinearLayout processorItemsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_work_change_detail);
        Intent intent = getIntent();
        applyId = intent.getIntExtra(KeyConstant.id, 0);
        processorConfigId = intent.getIntExtra(KeyConstant.processorConfigId, 0);
        reportInfo = (WorkApplyBean) intent.getSerializableExtra(KeyConstant.LIST_OBJECT);
        applyId = reportInfo.getId();
        initTitleBackBt("变更申请详情");
        context = this;

        buildSitName = intent.getStringExtra(KeyConstant.name);

        status = reportInfo.isStatus();

        initItemsLayoutView();
        initView();
        //附件
        setFileListData();
        getHostoryData();

    }

    //权限控制
    private void initAusthBt() {
        View changeBt = findViewById(R.id.start_change_bt);
        View agressRejectBt = findViewById(R.id.start_audit_layout);
        //要权限时候打开下面
        AuthsUtils.setViewAuth(changeBt, AuthsConstant.BIZ_CHANGE_CHNAGE);
        agressRejectBt.setVisibility(changeBt.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        changeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WorkChangeAddActivity.class);
                i.putExtra(KeyConstant.processorConfigId, processorConfigId);
                i.putExtra(KeyConstant.applyId, applyId);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) reportInfo);//序列化,要注意转化(Serializable)
                i.putExtras(bundle);
                context.startActivity(i);
                //修改
                //showChangeDialog();
            }
        });
    }

    private boolean isDevice, isEmployee, isMaterial, isPlan, isPrepare;

    private void showChangeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme_fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_update_change, null);
        ((TextView) v.findViewById(R.id.work_change_title_tv)).setText(buildSitName);

        dialogChangeRemarkTv = (EditText) v.findViewById(R.id.remark_tv);


        dialogChangeRemarkTv.setText(remark);
        dialogChangeRemarkTv.setSelection(remark == null ? 0 : remark.length());

        final Dialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//让EditText获取焦点
        window.setContentView(v);

        v.findViewById(R.id.dialog_btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        //附件
        listView = (ScrollListView) v.findViewById(R.id.horizontal_gridview);
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
        //如果不关闭当前界面,关闭dialog时,就要fileListAdapter.setAllowDelete(false);
        fileListAdapter.setAllowDelete(true);
        fileListAdapter.setCallBack(new FileListAdapter.DataRemoveCallBack() {
            @Override
            public void finish(List<FileListInfo> data) {
                fileData = data;
            }
        });

        v.findViewById(R.id.card_detail_file_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFileDialog();
            }
        });

        v.findViewById(R.id.work_change_commit_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePost();
            }
        });

    }

    private void changePost() {
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_start_applies;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        remark = dialogChangeRemarkTv.getText().toString();
        JSONObject idObj = new JSONObject();
        try {
            idObj.put(KeyConstant.id, buildSiteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizBuildSite, idObj);

        map.put(KeyConstant.applyWorkDate, DateUtil.millonsToUTC(applyWorkDate));
        //附件
        if (fileData != null) {
            JSONArray picArr = new JSONArray();
            JSONArray attachmentArr = new JSONArray();
            for (FileListInfo fileInfo : fileData) {
                String fileUrl = fileInfo.filePath;
                if (ImageUtil.isImageSuffix(fileUrl)) {
                    picArr.put(fileUrl);
                } else {
                    attachmentArr.put(fileUrl);
                }
            }
            map.put(KeyConstant.attachment, attachmentArr);
            map.put(KeyConstant.pic, picArr);
        }

        map.put(KeyConstant.id, applyId);
        map.put(KeyConstant.isDevice, isDevice);
        map.put(KeyConstant.isEmployee, isEmployee);
        map.put(KeyConstant.isMaterial, isMaterial);
        map.put(KeyConstant.isPlan, isPlan);
        map.put(KeyConstant.isPrepare, isPrepare);
        map.put(KeyConstant.remark, this.remark);

        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT, url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result == null) {
                    DialogHelper.hideWaiting(fm);
                    ToastUtil.show(context, "修改失败");
                    return;
                }
                ToastUtil.show(context, "修改成功");
                DialogHelper.hideWaiting(fm);
                context.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "修改失败" + error);
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

    private void initItemsLayoutView() {
        processorItemsLayout = (LinearLayout) findViewById(R.id.change_processor_items_layout);
        processorItemsLayout.removeAllViews();
        List<WorkApplyBean.DetailBean> detail = reportInfo.getDetail();
        if (detail == null) {
            return;
        }
        for (WorkApplyBean.DetailBean detailBean : detail) {
            View itemView = View.inflate(context, R.layout.item_work_change_detail_item, null);
            LinearLayout layoutPeriod = (LinearLayout) itemView.findViewById(R.id.layout_period);
            LinearLayout layoutInvest = (LinearLayout) itemView.findViewById(R.id.layout_change_invest);
            TextView nameTv = (TextView) itemView.findViewById(R.id.item_name_tv);
            TextView periodTv = (TextView) itemView.findViewById(R.id.item_period_value_tv);//工期
            ImageView periodImgIv = (ImageView) itemView.findViewById(R.id.item_period_img_iv);//工期

            TextView changeInvestTv = (TextView) itemView.findViewById(R.id.item_change_invest_value_tv);//工期
            ImageView changeInvestImgIv = (ImageView) itemView.findViewById(R.id.item_change_invest_img_iv);//工期

            final String planName = detailBean.getName();
            nameTv.setText(planName);

            int changePeriod = detailBean.getChangePeriod();
            final double planInvest = detailBean.getPlanInvest();
            final String planBeginDate = detailBean.getPlanBeginDate();
            final String planEndDate = detailBean.getPlanEndDate();
            if (changePeriod == 0) {
                layoutPeriod.setVisibility(View.GONE);
            } else {
                periodImgIv.setImageResource(changePeriod > 0 ? R.drawable.ic_change_increase_arrow :
                        R.drawable.ic_change_reduce_arrow);
                //取正数
                periodTv.setText(Math.abs(changePeriod) + getString(R.string.period_unit));//工期
            }

            //产值
            double changeInvest = detailBean.getChangeInvest();
            if (changeInvest == 0) {
                layoutInvest.setVisibility(View.GONE);
            } else {
                changeInvestImgIv.setImageResource(changePeriod > 0 ? R.drawable.ic_change_increase_arrow : R.drawable.ic_change_reduce_arrow);
                changeInvestTv.setText(Math.abs(changeInvest) + getString(R.string.money_unit));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = View.inflate(context, R.layout.layout_dialog_plan_detail, null);
                    setViewData(view, R.id.detail_actual_invers_tv, "未知");
                    setViewData(view, R.id.actual_start_end_date_tv, "未知");
                    setViewData(view, R.id.plan_invest_tv, planInvest + "万元");
                    setViewData(view, R.id.plan_start_end_date_tv,
                            planBeginDate + "-" + planEndDate);
                    new MaterialDialog.Builder(context).title(planName + "详情")
                            .customView(view, true)
                            .positiveText(R.string.sure)
                            .positiveColorRes(R.color.color999999)
                            .show();
                }
            });
            processorItemsLayout.addView(itemView);
        }
    }

    private void setViewData(View view, int viewId, String text) {
        ((TextView) view.findViewById(viewId)).setText(text);
    }

    private void initView() {

        ((TextView) findViewById(R.id.buildsit_name_tv)).setText(buildSitName);
        descRemarkTv = (TextView) findViewById(R.id.remark_tv);

        TextView statusTv = (TextView) findViewById(R.id.work_start_status_tv);
        TextView placeTv = (TextView) findViewById(R.id.change_detail_place_tv);
        TextView levelTv = (TextView) findViewById(R.id.change_detail_level_tv);
        TextView drawingTv = (TextView) findViewById(R.id.change_detail_drawing_tv);
        TextView drawingNoTv = (TextView) findViewById(R.id.change_detail_drawing_no_tv);

        remark = reportInfo.getChangeRemark();
        descRemarkTv.setText(TextUtil.isEmpty(remark) ? "无" : remark);
        statusTv.setText(status ? "审核通过" : "等待审核");
        statusTv.setTextColor(ContextCompat.getColor(this, status ?
                R.color.status_ok : R.color.f9c744));

        placeTv.setText(reportInfo.getChangePlace());
        int changeLevel = reportInfo.getChangeLevel();
        levelTv.setText(changeLevelArr[changeLevel - 1]);
        drawingTv.setText(reportInfo.getDesignDraw());
        drawingNoTv.setText(reportInfo.getDesignDrawNo());

        if (status) {//(通过/驳回/修改)按钮
            findViewById(R.id.start_audit_layout).setVisibility(View.GONE);
            findViewById(R.id.start_change_bt).setVisibility(View.GONE);
        } else {
            initAusthBt();
        }
    }

    private void getHostoryData() {
        planItemLayout = (LinearLayout) findViewById(R.id.start_item_layout);
        String url = Constant.WEB_SITE + "/biz/change/audits/all?applyId=" + applyId;
        Response.Listener<List<HistoryInfo>> successListener = new Response.
                Listener<List<HistoryInfo>>() {
            @Override
            public void onResponse(List<HistoryInfo> result) {
                if (result == null || result.size() == 0) {
                    return;
                }
                Collections.reverse(result);
                addHostoryItem(result);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        };

        Request<List<HistoryInfo>> request = new
                GsonRequest<List<HistoryInfo>>(Request.Method.GET,
                        url, successListener, errorListener, new TypeToken<List<HistoryInfo>>() {
                }.getType()) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(request);
    }

    private void addHostoryItem(List<HistoryInfo> planInfos) {
        planItemLayout.removeAllViews();
        planItemLayout.setVisibility(View.VISIBLE);
        for (int i = 0; i < planInfos.size(); i++) {
            final HistoryInfo itemInfo = planInfos.get(i);
            View itemView = View.inflate(context, R.layout.item_work_hostory, null);
            TextView ciycleNameTv = (TextView) itemView.findViewById(R.id.name_ciycle_tv);
            TextView nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            TextView timeTv = (TextView) itemView.findViewById(R.id.time_tv);
            final TextView remarkTv = (TextView) itemView.findViewById(R.id.remark_tv);
            TextView actionTv = (TextView) itemView.findViewById(R.id.action_tv);
            String auditorUsername = itemInfo.getAuditorUsername();
            int length = auditorUsername == null ? 0 : auditorUsername.length();
            ciycleNameTv.setText(length > 2 ? auditorUsername.substring(length - 2) : auditorUsername);//操作人头像
            nameTv.setText(auditorUsername);//操作人
            int status = itemInfo.getStatus();
            String actionStr = "";
            if (status == 1) {
                actionStr = "发起申请";
            } else if (status == 2) {
                actionStr = "<font color='#1890ff' >修改申请</font>";
            } else if (status == 3) {
                actionStr = "<font color='#F98444' >驳回申请</font>";
            } else if (status == 4) {
                actionStr = "<font color='#66cc90' >已同意</font>";
            } else {
                actionStr = "发起申请";
            }
            actionTv.setText(Html.fromHtml(actionStr));
            String auditDate = itemInfo.getAuditDate();
            final HistoryInfo.BizChangeApplyBean startApplyInfo = itemInfo.getBizChangeApply();
            String createTime = startApplyInfo.getCreateTime();
            timeTv.setText(TextUtil.substringTimeMMDD_HHMM(status == 1 ? createTime : auditDate));//时间
            String commit = itemInfo.getCommit() == null ? "无" : itemInfo.getCommit();
            remarkTv.setText("说明：" + commit);
            nameTv.setText(auditorUsername);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View v = inflater.inflate(R.layout.layout_dialog_file_list, null);
                    List<FileListInfo> fileData = new ArrayList<>();
                    TextView dialogRemarkTv = (TextView) v.findViewById(R.id.remark_tv);
                    dialogRemarkTv.setText(remarkTv.getText());
                    ScrollListView listView = (ScrollListView) v.findViewById(R.id.horizontal_gridview);
                    List<FileInfo> pic = startApplyInfo.getPic();
                    List<FileInfo> attachment = startApplyInfo.getAttachment();
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
                        v.findViewById(R.id.file_list_line).setVisibility(View.GONE);
                    }

                    FileListAdapter fileListAdapter = new FileListAdapter(context, fileData);
                    listView.setAdapter(fileListAdapter);

                    builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(v);
                    builder.show();

                }
            });*/
            planItemLayout.addView(itemView);

        }
    }

    private void setFileListData() {
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        TextView fileTitleTv = (TextView) findViewById(R.id.card_detail_file_title);
        fileTitleTv.setText(R.string.file_link_list);
        fileTitleTv.setTextColor(ContextCompat.getColor(this, R.color.color999999));
        ScrollListView listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
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
            findViewById(R.id.start_desc_tv).setPadding(0, 0, 0, 25);
            descRemarkTv.setPadding(0, 0, 0, 25);
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
        }
        FileListAdapter fileListAdapter = new FileListAdapter(this, fileData);
        listView.setAdapter(fileListAdapter);
    }

    //同意
    public void onFinishAgressBtClick(View view) {
        showCommitDialog(4);
    }

    private void showCommitDialog(final int status) {
        new MaterialDialog.Builder(context).title("")
                .positiveText(R.string.commit)
                .input("意见说明(选填)", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        audit(status, TextUtil.isAnyEmpty(input + "") ? "无" : input + "");
                    }
                }).show();
    }

    private void audit(int status, CharSequence commit) {
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_change_audits;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JSONObject idObj = new JSONObject();
        try {
            idObj.put(KeyConstant.id, applyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizChangeApply, idObj);
        map.put(KeyConstant.status, status);
        map.put(KeyConstant.commit, commit);

        JSONObject jsonObject = new JSONObject(map);
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
                Log.d(TAG, "提交失败" + error);
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

    //驳回
    public void onFinishRejectBtClick(View view) {
        showCommitDialog(3);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
    }
}
