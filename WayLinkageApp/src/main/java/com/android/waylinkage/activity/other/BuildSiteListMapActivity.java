package com.android.waylinkage.activity.other;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.main.PlanDesignActivity;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ExRadioGroup;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 */
public class BuildSiteListMapActivity extends BaseFgActivity {
    private String TAG = BuildSiteListMapActivity.class.getSimpleName();
    private TextView titleTv;
    private Button rightBt;
    private BuildSiteListMapActivity context;
    private String contractName = "", unitName = "", contractId;
    private Dialog dialog;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildsites_map);
        initStatusBar();

        contractId = getIntent().getStringExtra(KeyConstant.id);
        contractName = getIntent().getStringExtra(KeyConstant.name);
        unitName = getIntent().getStringExtra(KeyConstant.unit_name);
        context = this;

        titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText(contractName);//标段名称

        getBuildSiteListData();

    }

    private void getBuildSiteListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE +
                UrlConstant.url_biz_buildSites + "/all?contractId=" + contractId;
        Response.Listener<List<BuildSiteInfo>> successListener = new Response
                .Listener<List<BuildSiteInfo>>() {
            @Override
            public void onResponse(List<BuildSiteInfo> result) {

                if (result == null || result.size() == 0) {
                    if (null != context && !context.isFinishing()) {
                        dialogHelper.hideAlert();
                    }
                    ToastUtil.show(context, getString(R.string.no_data));
                    initSiteBts();
                    return;
                }
                buildSiteInfo.clear();
                buildSiteInfo = result;
                initSiteBts();
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
            }
        };

        Request<List<BuildSiteInfo>> versionRequest = new
                GsonRequest<List<BuildSiteInfo>>(
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

    int[] mRoadBtsId = new int[]{R.id.map_bt_0, R.id.map_bt_1, R.id.map_bt_2, R.id.map_bt_3,
            R.id.map_bt_4, R.id.map_bt_5, R.id.map_bt_6, R.id.map_bt_7,
            R.id.map_bt_8, R.id.map_bt_9, R.id.map_bt_10, R.id.map_bt_11,
    };

    //点击 地图中的路标按钮
    private void initSiteBts() {
        int length = mRoadBtsId.length;
        int size = buildSiteInfo.size();
        for (int i = 0; i < length; i++) {
            ImageButton ib = (ImageButton) findViewById(mRoadBtsId[i]);
            if (i < size) {
                final int finalIndex = i;
                final BuildSiteInfo buildSiteInfo = this.buildSiteInfo.get(i);
                String type = buildSiteInfo.getType();
                int srcId = "2".equals(type) ? R.drawable.ic_map_brige :
                        "1".equals(type) ? R.drawable.ic_map_road :
                                R.drawable.ic_map_tunnel;
                ib.setImageResource(srcId);
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPupWindow(view, buildSiteInfo, finalIndex);
                    }
                });
            } else {
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtil.show(context, "暂无权限查看该工地信息哦");
                    }
                });
            }
        }
    }

    private void showPupWindow(View v, final BuildSiteInfo buildSiteInfo, int index) {
        BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(context).
                inflate(R.layout.layout_road_detail_popup_view, null);
        final PopupWindow popupWindow = BubblePopupHelper.create(context, bubbleLayout);
        TextView tvTitle = (TextView) bubbleLayout.findViewById(R.id.road_detail_title_tv);
        TextView processTv = (TextView) bubbleLayout.findViewById(R.id.process_tv);
        TextView quailyTv = (TextView) bubbleLayout.findViewById(R.id.quaily_tv);
        ImageButton processDetailBt = (ImageButton) bubbleLayout.findViewById(R.id.process_detail_bt);

        final String name = buildSiteInfo.getName();
        tvTitle.setText(name);
        boolean overdue = buildSiteInfo.isOverdue();//逾期
        boolean qualified = buildSiteInfo.isQualified();//合格
        int waringColor = getResources().getColor(R.color.warnig_yellow);
        if (overdue) {
            processTv.setText(R.string.overdue);//工程进度
            processTv.setTextColor(waringColor);//工程进度
        } else if (qualified) {
            processTv.setText(R.string.nomal);//工程进度
        }
        if (qualified) {
            quailyTv.setText(R.string.qualified);//工程质量
        } else {
            quailyTv.setText(R.string.unqualified);//工程质量
            processTv.setTextColor(waringColor);//工程进度
        }
        if (index == 1 || index == 4 || index == 6) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);

            popupWindow.showAtLocation(v,
                    Gravity.NO_GRAVITY, location[0] + 10 + (index == 7 ? v.getMeasuredWidth() / 2 : v.getMeasuredWidth()), location[1] - 150);
        } else {
            int[] location = new int[2];
            v.getLocationInWindow(location);
            popupWindow.showAsDropDown(v);
        }
        processDetailBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlanDesignActivity.class);
                intent.putExtra(KeyConstant.project_plan_title, name);
                intent.putExtra(KeyConstant.id, buildSiteInfo.getId()+"");
                intent.putExtra(KeyConstant.project_plan_info_type, Integer.valueOf(buildSiteInfo.getType()));
                context.startActivity(intent);
            }
        });

    }

    private void showTopDialog() {
        dialog = new Dialog(this, R.style.dialog_top_to_bottom);
        //填充对话框的布局

        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_road_map_top, null);
        ExRadioGroup topLayout = (ExRadioGroup) inflate.findViewById(R.id.road_map_layout);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 0, 0);
        for (BuildSiteInfo buildSiteInfo : buildSiteInfo) {
            final String name = buildSiteInfo.getName();
            final String type = buildSiteInfo.getType();
            final String siteId = buildSiteInfo.getId() + "";

            TextView itemTv = new TextView(context);
            itemTv.setGravity(Gravity.CENTER_VERTICAL);
            itemTv.setPadding(35, 8, 35, 10);
            itemTv.setTextSize(15.5f);
            itemTv.setText(name);
            itemTv.setSingleLine();
            itemTv.setTextColor(context.getResources().getColor(R.color.color666666));
            itemTv.setBackgroundResource(R.drawable.selector_fragment_2_dialog_tab);
            itemTv.setLayoutParams(lp);
            topLayout.addView(itemTv);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PlanDesignActivity.class);
                    intent.putExtra(KeyConstant.project_plan_title, name);
                    intent.putExtra(KeyConstant.id, siteId);
                    intent.putExtra(KeyConstant.project_plan_info_type, Integer.valueOf(type));
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            });
        }

        dialog.setContentView(inflate);//将布局设置给Dialog

        DialogUtils.setDialogWindow(context, dialog, Gravity.TOP);
    }

    public void onRoadMapBackClick(View view) {
        finish();
    }

    public void onSearchClick(View view) {
        showTopDialog();
    }
}