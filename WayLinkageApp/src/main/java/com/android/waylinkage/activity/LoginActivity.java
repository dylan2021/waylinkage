package com.android.waylinkage.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.main.MainActivity;
import com.android.waylinkage.activity.user.FindPwdActivity;
import com.android.waylinkage.activity.user.RegisterActivity;
import com.android.waylinkage.util.CommonUtil;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.FastClickUtils;
import com.android.waylinkage.util.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Gool Lee
 */
public class LoginActivity extends BaseFgActivity implements View.OnClickListener {

    private String TAG = LoginActivity.class.getSimpleName();
    private MaterialEditText et_pwd, et_user;
    private String userName, password;
    private TextView bt_find_pwd, bt_register;
    private Button bt_login;
    private SharedPreferences preferences;
    private LoginActivity context;
    private DialogHelper dialogHelper;
    private String accessToken;
    private int projectId;
    private boolean isFirstLuncher;
    private String phoneSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);
        context = this;
        CommonUtil.verifyStoragePermissions(context);
        preferences = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        isFirstLuncher = preferences.getBoolean(KeyConstant.IS_FIRST_LUNCHER_SP, true);

        et_user = (MaterialEditText) findViewById(R.id.et_login_user);
        et_pwd = (MaterialEditText) findViewById(R.id.et_login_pwd);
        phoneSp = preferences.getString(Constant.SP_USER_NAME, "");

        bt_find_pwd = (TextView) findViewById(R.id.tv_find_pwd);
        bt_find_pwd.setOnClickListener(this);
        bt_register = (TextView) findViewById(R.id.tv_register);
        bt_register.setOnClickListener(this);
        bt_login = (Button) findViewById(R.id.but_login);
        bt_login.setOnClickListener(this);

        dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        findViewById(R.id.login_qq_bt).setOnClickListener(this);
        findViewById(R.id.login_wechat_bt).setOnClickListener(this);
        findViewById(R.id.login_sina_bt).setOnClickListener(this);

        et_user.setText(phoneSp);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_login:
                if (FastClickUtils.isFastClick()) {
                    // 进行点击事件后的逻辑操作
                    doLogin();
                }
                break;
            case R.id.tv_find_pwd:
                Intent fIntent = new Intent(this, FindPwdActivity.class);
                startActivity(fIntent);
                break;
            case R.id.tv_register:
                Intent rIntent = new Intent(this, RegisterActivity.class);
                startActivity(rIntent);
                break;
        }
    }

    private void doLogin() {
        userName = et_user.getText().toString();
        if (TextUtil.isEmpty(userName)) {
            ToastUtil.show(context, "请输入用户名");
            return;
        }
        password = et_pwd.getText().toString();
        if (TextUtil.isEmpty(password)) {
            ToastUtil.show(context, "请输入密码");
            return;
        }
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        dialogHelper.showAlert("登录中...", false);
        String url = Constant.WEB_SITE + Constant.URL_USER_LOGIN;
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            if (null != context && !context.isFinishing()) {
                                dialogHelper.hideAlert();
                            }
                            ToastUtil.show(context, getString(R.string.server_exception));
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            accessToken = jsonObject.getString(KeyConstant.access_token);
                        } catch (JSONException e) {
                            accessToken = null;
                        }

                        if (TextUtil.isEmpty(accessToken)) {
                            if (null != context && !context.isFinishing()) {
                                dialogHelper.hideAlert();
                            }
                            ToastUtil.show(context, getString(R.string.server_exception));
                            return;
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constant.SP_TOKEN, accessToken);
                        editor.putString(Constant.SP_USER_NAME, userName);
                        editor.putString(Constant.sp_pwd, password);
                        editor.apply();
                        App.token = accessToken;
                        App.passWord = password;
                        App.phone = userName;
                        if (!isFirstLuncher && !phoneSp.equals(userName)) {
                            startActivity(new Intent(context, ChooseProjectActivity.class));
                        } else {
                            startActivity(new Intent(context, isFirstLuncher ? ChooseProjectActivity.class : MainActivity.class));
                        }
                        context.finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtil.show(context, getString(R.string.login_failed));
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Content_Type, Constant.application_form);
                params.put(KeyConstant.Authorization, Constant.authorization);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //参数
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.username, userName);
                params.put(KeyConstant.password, password);
                params.put(KeyConstant.grant_type, KeyConstant.password);
                return params;
            }

        };

        App.requestQueue.add(jsonObjRequest);

     /*   Response.Listener<String> succesListener = new Response
                .Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                Log.d(TAG, "登录返回:" + result);
                if (result == null) {
                    Toast.makeText(context, "服务端异常", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences.Editor editor = preferences.edit();
                String token = (String) result;
                editor.putString(Constant.SP_TOKEN, token);
                editor.putString(Constant.SP_USER_NAME, userName);
                editor.putString(Constant.sp_pwd, password);
                editor.apply();
                    App.token = token;
                    App.passWord = password;
                    App.phone = phone;

                startActivity(new Intent(context, MainActivity.class));
                context.finish();

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.d(TAG, "登录返回错误:" + volleyError.getMessage());
                ToastUtil.show(context, "登录失败,稍后重试");

            }
        };
        Request<String> versionRequest1 = new GsonRequest<String>(Request
                .Method.POST, fileUrl,
                succesListener, errorListener, new TypeToken<String>() {
        }.getType()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Content_Type, Constant.application_form);
                params.put(KeyConstant.Authorization, Constant.authorization);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //设置POST请求参数
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.username, userName);
                params.put(KeyConstant.password, password);
                params.put("grant_type", "password");
                return params;
            }
        };
        App.requestQueue.add(versionRequest1);*/
    }

    private String nicknameStr = "";
    private String URL_HEAD_PHOTO = "";

}
