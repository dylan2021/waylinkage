package com.android.waylinkage.activity.user;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.main.MainActivity;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DataCleanManager;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;

/**
 * Gool Lee
 */
public class SystemSettingsActivity extends BaseFgActivity {

    private ToggleButton but_load;
    private int delayMillis = 100;
    private SystemSettingsActivity content;
    private TextView tv_clear;
    private SharedPreferences.Editor preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_me_settings);
        content = this;
        Button left_but = (Button) findViewById(R.id.left_bt);
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText("设置");
        left_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_clear = (TextView) findViewById(R.id.tv_clear);
        RelativeLayout layout_1 = (RelativeLayout) findViewById(R.id.layout_1);
        layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = tv_clear.getText().toString();
                if ("0KB".equals(text)) {
                    ToastUtil.show(SystemSettingsActivity.this, "没有缓存了~");
                    return;
                }

                if (text.endsWith("MB")) {
                    delayMillis = 1000;
                } else if (text.endsWith("KB")) {
                    delayMillis = 200;
                } else {
                    delayMillis = 1000;
                }
                showLogoutDialog();
            }
        });

        try {
            String cacheSize = DataCleanManager.getTotalCacheSize(this);
            tv_clear.setText(cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //清理缓存
    public void showLogoutDialog() {
        final Dialog dialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_logout, null);

        TextView title_tv = (TextView) inflate.findViewById(R.id.dialog_top_title_tv);
        title_tv.setText("确定清除缓存数据吗?");
        TextView clearBt = (TextView) inflate.findViewById(R.id.logout_yes_bt);
        clearBt.setText("清除数据");
        clearBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                DataCleanManager.clearAllCache(SystemSettingsActivity.this);
                final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), SystemSettingsActivity.this);
                dialogHelper.showAlert("清理中...", true);

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogHelper.hideAlert();
                        ToastUtil.show(content, "缓存已清除~");
                        if (null != tv_clear) {
                            tv_clear.setText("0KB");
                        }
                    }
                }, delayMillis);
            }
        });
        inflate.findViewById(R.id.logout_cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(inflate);//将布局设置给Dialog
        DialogUtils.setDialogWindow(content, dialog, Gravity.BOTTOM);
    }

    public void onLogoutClick(View view) {
        showLogout();
    }

    private void showLogout() {

        final Dialog dialog = new Dialog(this, R.style.Dialog_From_Bottom_Style);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_dialog_logout, null);

        inflate.findViewById(R.id.logout_yes_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE).edit();
                preferences.putString(Constant.sp_pwd, "").commit();
                MainActivity.context.finish();
                finish();

                dialog.cancel();
            }
        });
        inflate.findViewById(R.id.logout_cancel_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(inflate);

        DialogUtils.setDialogWindow(content, dialog, Gravity.BOTTOM);
    }

}
