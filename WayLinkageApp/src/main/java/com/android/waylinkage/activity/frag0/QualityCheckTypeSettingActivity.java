package com.android.waylinkage.activity.frag0;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
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
import com.android.waylinkage.adapter.MyExpandableListAdapter;
import com.android.waylinkage.bean.GroupInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.MyExpandableListView;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 * 质量检查类型
 */
public class QualityCheckTypeSettingActivity extends BaseFgActivity {
    private Button title_bar;
    private TextView tv_content;
    private int processorConfigId;
    private MyExpandableListView expandableListView;
    private QualityCheckTypeSettingActivity context;
    private MyExpandableListAdapter myExpandableListAdapter;
    private List<GroupInfo> groupInfos = new ArrayList<>();
    private Button rightBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_check_type_setting);
        initTitleBackBt(getString(R.string.check_type));
        context = this;
        processorConfigId = getIntent().getIntExtra(KeyConstant.id, 0);
        groupInfos = (List<GroupInfo>) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);
        if (processorConfigId != 0) {
            initRightBt();
        }

        initExpandLv();

        if (null != groupInfos) {
            initTitleBackBt(0 == processorConfigId ? "需整改项" : "选择待整改项");
            myExpandableListAdapter.setData(groupInfos, 0 == processorConfigId ? false : true);

            for (int i = 0; i < myExpandableListAdapter.getGroupCount(); i++) {
                expandableListView.expandGroup(i);
            }
        } else {
            getQualityConfigs();
        }
    }

    //获取工地列表
    private void getQualityConfigs() {
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
                myExpandableListAdapter.setData(result, true);

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

    private void showPopWindow(View v) {
        View inflate = LayoutInflater.from(context).inflate(
                R.layout.layout_report_menu_pupwindow, null);

        final PopupWindow popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);
        v.getLocationOnScreen(location);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        popWindow.showAsDropDown(v);
        View.OnClickListener itemMenuPopupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                switch (view.getId()) {
                    case R.id.report_menu_bt_0:

                        break;
                    case R.id.report_menu_bt_1:
                        break;
                }
            }
        };
        Button bt0 = (Button) inflate.findViewById(R.id.report_menu_bt_0);
        bt0.setText("检查类型");
        bt0.setOnClickListener(itemMenuPopupClickListener);

        Button bt1 = (Button) inflate.findViewById(R.id.report_menu_bt_1);
        bt1.setText("检查项");
        bt1.setOnClickListener(itemMenuPopupClickListener);
    }


    private void initExpandLv() {
        expandableListView = (MyExpandableListView) findViewById(R.id.expand_list);
        myExpandableListAdapter = new MyExpandableListAdapter(context);
        expandableListView.setAdapter(myExpandableListAdapter);

        //设置分组项的点击监听事件
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView,
                                        View view, int i, long l) {
                AppCompatCheckBox groupCheckBox = (AppCompatCheckBox) view.findViewById(R.id.expand_check_box);

                return false; // 返回 false，否则分组不会展开
            }
        });

        myExpandableListAdapter.setData(groupInfos, true);
    }

    private void initRightBt() {
        rightBt = (Button) findViewById(R.id.title_right_bt);
        rightBt.setVisibility(View.VISIBLE);
        rightBt.setText("确定");
        rightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == groupInfos || groupInfos.size() == 0) {
                    ToastUtil.show(context, "请选择一个要汇报的质量检查项");
                    return;
                }

                //返回选择的数据
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.GROUP_LIST, (Serializable) groupInfos);//序列化,要注意转化(Serializable)
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(2, intent);

                finish();
              /*  Drawable rightBtDrawable = getResources().getDrawable(R.drawable.ic_add);
                rightBt.setCompoundDrawablesWithIntrinsicBounds(null,
                        null, rightBtDrawable, null);
                rightBt.setPadding(0, 0, 55, 0);

                rightBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopWindow(v);
                    }
                });*/
            }
        });

    }

}
