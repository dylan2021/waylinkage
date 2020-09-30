package com.android.waylinkage.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.LoginActivity;
import com.android.waylinkage.activity.main.MainActivity;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.widget.dialogfragment.SimpleDialogFragment;
import com.android.waylinkage.util.ToastUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 修改密码
 * Gool Lee
 */
public class ChangePwdActivity extends BaseFgActivity {

    private Button bt_find_pwd;
    private EditText et_old_pwd, newPwdET1, ensurePwdEt;
    private SharedPreferences.Editor editor;
    private ChangePwdActivity context;
    private String tokenSp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        this.setContentView(R.layout.activity_change_pwd);

        initTitleBackBt("修改密码");
        SharedPreferences preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME,
                MODE_PRIVATE);
        tokenSp = preferences.getString(Constant.SP_TOKEN, "");
        editor = preferences.edit();
        context = ChangePwdActivity.this;


        bt_find_pwd = (Button) findViewById(R.id.bt_find_pwd);
        et_old_pwd = (EditText) findViewById(R.id.old_pwd_et);
        newPwdET1 = (EditText) findViewById(R.id.new_pwd_et1);

        ensurePwdEt = (EditText) findViewById(R.id.ensure_pwd_et);

        bt_find_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwdStr = et_old_pwd.getText().toString().trim();
                String newPwdETStr1 = newPwdET1.getText().toString().trim();
                String ensurePwdStr = ensurePwdEt.getText().toString().trim();

                if (oldPwdStr == null || oldPwdStr.length() <= 0) {
                    ToastUtil.show(context, "旧密码不能为空");
                    return;
                }
                if (oldPwdStr.equals(newPwdETStr1)) {
                    ToastUtil.show(context, "新密码和旧密码相同");
                    return;
                }
                if (newPwdETStr1 == null || newPwdETStr1.length() <= 0) {
                    ToastUtil.show(context, "请输入新密码");
                    return;
                }
                if (newPwdETStr1.length() < 6) {
                    ToastUtil.show(context, "新密码不能少于六位");
                    return;
                }
                if (ensurePwdStr == null || ensurePwdStr.length() <= 0) {
                    ToastUtil.show(context, "请确认新密码");
                    return;
                }
                if (!newPwdETStr1.equals(ensurePwdStr)) {
                    ToastUtil.show(context, "两次输入的新密码不一致");
                    return;
                }

                doFindPwd(oldPwdStr, newPwdETStr1);
            }
        });
    }

    private void doFindPwd(final String oldPwdStr, final String newPwdETStr1) {
        String url = Constant.WEB_SITE + UrlConstant.url_system_change_pwd;
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final Map<String, String> map = new HashMap<>();
        map.put(KeyConstant.newPassword, newPwdETStr1);
        map.put(KeyConstant.oldPassword, oldPwdStr);
        JSONObject mapObj = new JSONObject(map);
        Log.d(TAG, "密码修改失败:" + mapObj.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                mapObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "密码修改失败");
                            return;
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogHelper.hideWaiting(fm);
                if (error.toString().contains("End of input at character 0 of")) {
                    editor.putString(Constant.sp_pwd, newPwdETStr1);
                    editor.commit();
                    showDialog(getString(R.string.pwd_change_success_reLogin_msg));
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


    /**
     * 显示注册结果对话框
     */
    private void showDialog(String msg) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(220);

        TextView tv = new TextView(ChangePwdActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(msg);
        tv.setTextColor(getResources().getColor(R.color.color666666));
        dialogFragment.setContentView(tv);

        dialogFragment.setNegativeButton(R.string.reLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                MainActivity.context.finish();
                finish();
            }
        });
        dialogFragment.show(ft, "successDialog");
    }
}
