package com.android.waylinkage.activity.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.frag0.ProgressAddActivity;
import com.android.waylinkage.activity.frag0.QualityAddActivity;
import com.android.waylinkage.activity.frag0.SafyAddActivity;
import com.android.waylinkage.activity.frag0.WorkChangeAddActivity;
import com.android.waylinkage.activity.frag0.WorkFinishAddActivity;
import com.android.waylinkage.activity.frag0.WorkStartAddActivity;
import com.android.waylinkage.activity.user.ChangePwdActivity;
import com.android.waylinkage.activity.user.SendBindCodeActivity;
import com.android.waylinkage.activity.user.SystemSettingsActivity;
import com.android.waylinkage.activity.user.UserCenterActivity;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.AuthsUtils;
import com.android.waylinkage.util.ToastUtil;
import com.daasuu.bl.BubbleLayout;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 */
public class MainActivity extends BaseFgActivity {
    public static MainActivity context;
    private BuildSiteFragment buildSiteFragment;
    private ImageView tabBt2;
    private FragmentManager fragmentManager;
    private LinearLayout video, manager, home, reportBt;
    private RelativeLayout menu_game_hub;
    private Button tabBt0, tabBt1, tabBt4;
    private TextView tabTv1, tabTv4, tabTv3;
    private int colorDark;
    private int colorNormal;
    private SimpleDraweeView mIconIv;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Button tabBt3;
    private TextView mMenuNameTv;
    private TextView tabTv0;
    private AlarmFragment alarmFragment;
    private int chooseId = 0;
    private String employee = "";
    private RelativeLayout meLayout;
    private MsgFragment msgFragment;
    private List<ContractInfo> contractsInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_main);
        context = this;
        AuthsUtils.resolveToken();
        //侧滑菜单
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        chooseId = preferences.getInt(KeyConstant.SP_CHOOSE_ID, 0);
        try {
            App.CHOOSE_AUTH_TYPE = preferences.getInt(AuthsConstant.CHOOSE_AUTH_TYPE, 0);
        } catch (Exception e) {
        }
        home = (LinearLayout) findViewById(R.id.main_tab_0);
        reportBt = (LinearLayout) findViewById(R.id.main_tab_2);
        reportBt.setVisibility(App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? View.VISIBLE : View.GONE);
        menu_game_hub = (RelativeLayout) findViewById(R.id.main_tab_3);
        video = (LinearLayout) findViewById(R.id.main_tab_1);
        manager = (LinearLayout) findViewById(R.id.main_tab_4);
        meLayout = (RelativeLayout) findViewById(R.id.main_me_layout);

        tabBt0 = (Button) findViewById(R.id.menu_tab_0_bt);
        tabBt1 = (Button) findViewById(R.id.menu_video_bt);
        tabBt2 = (ImageView) findViewById(R.id.menu_game_bt);
        tabBt3 = (Button) findViewById(R.id.menu_game_hub_bt);
        tabBt4 = (Button) findViewById(R.id.menu_manager_bt);

        findViewById(R.id.main_me_item_change_pwd).setOnClickListener(mItemLayoutClickListener);
        findViewById(R.id.main_me_item_affiche).setOnClickListener(mItemLayoutClickListener);
        findViewById(R.id.menu_rank_bt).setOnClickListener(mItemLayoutClickListener);
        findViewById(R.id.menu_comment_bt).setOnClickListener(mItemLayoutClickListener);
        findViewById(R.id.main_me_item_setting).setOnClickListener(mItemLayoutClickListener);

        tabTv0 = (TextView) findViewById(R.id.menu_home_tv);
        tabTv1 = (TextView) findViewById(R.id.menu_video_tv);
        tabTv3 = (TextView) findViewById(R.id.menu_gamehub_tv);
        tabTv4 = (TextView) findViewById(R.id.menu_manager_tv);

        mIconIv = (SimpleDraweeView) findViewById(R.id.iv_icon_title);
        mMenuNameTv = (TextView) findViewById(R.id.me_user_name_tv);

        mIconIv.setOnClickListener(mItemLayoutClickListener);
        mMenuNameTv.setOnClickListener(mItemLayoutClickListener);

        colorDark = getResources().getColor(R.color.mainColor);
        colorNormal = getResources().getColor(R.color.color999999);

        fragmentManager = getSupportFragmentManager();
        setCurrentMenu(0);    //当前选中标签

        home.setOnClickListener(mTabClickListener);
        reportBt.setOnClickListener(mTabClickListener);
        menu_game_hub.setOnClickListener(mTabClickListener);
        video.setOnClickListener(mTabClickListener);
        manager.setOnClickListener(mTabClickListener);

    }


    View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_tab_0:
                    setCurrentMenu(0);
                    break;
                case R.id.main_tab_1:
                    setCurrentMenu(1);
                    break;
                case R.id.main_tab_2:
                    setCurrentMenu(2);
                    break;
                case R.id.main_tab_3:
                    setCurrentMenu(3);
                    break;
                case R.id.main_tab_4:
                    setCurrentMenu(4);
                    break;
            }
        }
    };
    View.OnClickListener mItemLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_me_item_change_pwd:
                    startActivity(new Intent(context, ChangePwdActivity.class));
                    break;
                case R.id.menu_comment_bt://审核
                    Intent intent = new Intent(context, ReportReformListActivity.class);
                    intent.putExtra(KeyConstant.reportIdicateType, KeyConstant.TYPE_IDICATE);
                    context.startActivity(intent);
                    break;
                case R.id.main_me_item_setting:
                    startActivity(new Intent(context, SystemSettingsActivity.class));
                    break;
                case R.id.main_me_item_affiche://公告
                    Intent intent1 = new Intent(context, ReportReformListActivity.class);
                    intent1.putExtra(KeyConstant.reportIdicateType, KeyConstant.TYPE_AFFICHE);
                    context.startActivity(intent1);
                    break;
                case R.id.iv_icon_title:
                case R.id.me_user_name_tv:
                    Intent intent2 = new Intent(context, UserCenterActivity.class);
                    intent2.putExtra(KeyConstant.employee, employee);
                    startActivity(intent2);
                    break;
            }
        }
    };

    private void getUserData() {
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.user_data;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            ToastUtil.show(context, getString(R.string.no_data));
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            employee = jsonObject.getString(KeyConstant.employee);
                        } catch (JSONException e) {
                            employee = "";
                        }
                        try {
                            JSONObject j = new JSONObject(employee);
                            String employeeName = j.getString(KeyConstant.employeeName);
                            String employeeMobile = j.getString(KeyConstant.employeePhone);
                            String employeeSex = j.getString(KeyConstant.employeeSex).equals("1") ? "女" : "男";
                            mMenuNameTv.setText(Html.fromHtml(
                                    "<font color='#333333' ><big><big>" + employeeName + "</big></big></font>" +
                                            "&emsp;<font color='#999999' >" + employeeSex + "</font>"
                                            + "<br/><font color='#999999' >" + ("null".equals(employeeMobile) ? "" : employeeMobile) + "</font>"
                            ));

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };

        App.requestQueue.add(jsonObjRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserData();
    }
    public void setCurrentMenu(int currentMenu) {
        tabBt0.setSelected(false);
        tabBt1.setSelected(false);
        tabBt2.setSelected(false);
        tabBt4.setSelected(false);
        tabBt3.setSelected(false);
        tabTv0.setTextColor(colorNormal);
        tabTv1.setTextColor(colorNormal);
        tabTv3.setTextColor(colorNormal);
        tabTv4.setTextColor(colorNormal);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (null == buildSiteFragment) {
            buildSiteFragment = new BuildSiteFragment(chooseId);
            transaction.add(R.id.main_fragments, buildSiteFragment);
        }
        if (null == alarmFragment) {
            alarmFragment = new AlarmFragment(chooseId);
            transaction.add(R.id.main_fragments, alarmFragment);
        }
        if (null == msgFragment) {
            msgFragment = new MsgFragment(chooseId);
            transaction.add(R.id.main_fragments, msgFragment);
        }
        switch (currentMenu) {
            case 0:
                meLayout.setVisibility(View.GONE);
                transaction.show(buildSiteFragment).hide(alarmFragment).hide(msgFragment);
                tabBt0.setSelected(true);
                tabTv0.setTextColor(colorDark);
                break;
            case 1:
                meLayout.setVisibility(View.GONE);
                transaction.show(alarmFragment).hide(msgFragment).hide(buildSiteFragment);
                tabBt1.setSelected(true);
                tabTv1.setTextColor(colorDark);
                break;
            case 2:
                //弹框
                showReportAddDialog();
                break;
            case 3:
                transaction.show(msgFragment).hide(alarmFragment).hide(buildSiteFragment);
                tabBt3.setSelected(true);
                tabTv3.setTextColor(colorDark);
                meLayout.setVisibility(View.GONE);
                break;
            case 4:
                transaction.hide(msgFragment).hide(alarmFragment).hide(buildSiteFragment);
                tabBt4.setSelected(true);
                tabTv4.setTextColor(colorDark);
                meLayout.setVisibility(View.VISIBLE);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void showReportAddDialog() {
        final Dialog popupWindow = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        BubbleLayout bubbleLayout = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.layout_frgment_item_3,
                null);
        View processbt = bubbleLayout.findViewById(R.id.frgmnet0_item3_process_tv);
        AuthsUtils.setViewAuth(processbt, AuthsConstant.BIZ_PROCESSOR_FIN_REPORT);
        processbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProgressAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) contractsInfoList);//序列化,要注意转化(Serializable)
                i.putExtras(bundle);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });
        View safyBt = bubbleLayout.findViewById(R.id.frgmnet0_item3_safy_tv);
        AuthsUtils.setViewAuth(safyBt, AuthsConstant.BIZ_SECURITY_REPORT);
        safyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SafyAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) contractsInfoList);//序列化,要注意转化(Serializable)
                i.putExtras(bundle);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });
        View qualityBt = bubbleLayout.findViewById(R.id.frgmnet0_item3_quailty_tv);

        AuthsUtils.setViewAuth(qualityBt, AuthsConstant.BIZ_QUALITY_REPORT);
        qualityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, QualityAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) contractsInfoList);//序列化,要注意转化(Serializable)
                i.putExtras(bundle);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();

            }
        });
        View startWorkBt = bubbleLayout.findViewById(R.id.frgmnet0_item4_start_work_tv);
        startWorkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WorkStartAddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyConstant.LIST_OBJECT, (Serializable) contractsInfoList);//序列化,要注意转化(Serializable)
                i.putExtras(bundle);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });
        View finishedWorkBt = bubbleLayout.findViewById(R.id.frgmnet0_item5_finished_work_tv);
        finishedWorkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WorkFinishAddActivity.class);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });
        View changedWorkBt = bubbleLayout.findViewById(R.id.frgmnet0_changed_work_tv);
        changedWorkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WorkChangeAddActivity.class);
                i.putExtra(KeyConstant.id, chooseId);
                context.startActivity(i);
                popupWindow.dismiss();
            }
        });

        popupWindow.setContentView(bubbleLayout);//将布局设置给Dialog
        setDialogWindow(popupWindow);
    }

    private void setDialogWindow(Dialog dialog) {
        Window dialogWindow = dialog.getWindow(); //获取当前Activity所在的窗体
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);//设置Dialog从窗体底部弹出
        WindowManager.LayoutParams params = dialogWindow.getAttributes();   //获得窗体的属性
        params.y = 150;  //Dialog距离底部的距离
        params.width = ImageUtil.getScreenWidth(context) - 80;
        dialogWindow.setAttributes(params); //将属性设置给窗体
        dialog.show();//显示对话框
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            buildSiteFragment.exitBy2Click();

        }
        return false;
    }

    public void onMeTopPhoneClick(View view) {
        Intent intent = new Intent(context, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.PHONE);
        startActivity(intent);
    }

    public void onMeTopEmailClick(View view) {
        Intent intent = new Intent(context, SendBindCodeActivity.class);
        intent.putExtra(KeyConstant.EDIT_TYPE, Constant.EMAIL);
        startActivity(intent);
    }

}