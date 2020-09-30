package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.Contact;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.view.ExRadioGroup;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 * @Date 监理旁站记录
 */
public class BuildSiteListActivity extends BaseFgActivity {

    public final String TAG = BuildSiteListActivity.class.getSimpleName();
    private String title;
    private String id;
    private BuildSiteListActivity context;
    private LinearLayout.LayoutParams lp;
    private ExRadioGroup topLayout;
    private String url_counter;
    private SharedPreferences.Editor spEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_buildsite_list);
        title = getIntent().getStringExtra(KeyConstant.TITLE);
        id = getIntent().getStringExtra(KeyConstant.id);


        spEdit = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE).edit();

        context = this;
        emptyTv =  findViewById(R.id.empty_tv);
        topLayout = (ExRadioGroup) findViewById(R.id.road_map_layout);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(55, 0, 0, 0);
        if (App.CHOOSE_AUTH_TYPE == Constant.CONTRACT) {
            initTitleBackBt("选择工地");
            url_counter = Constant.WEB_SITE + UrlConstant.url_biz_buildSites + "/all?contractId=" + id;
        } else if (App.CHOOSE_AUTH_TYPE == Constant.PROJECT) {
            initTitleBackBt("选择标段");
            url_counter = Constant.WEB_SITE + UrlConstant.url_biz_contracts + "/all?projectId=" + id;
        }
        getListData();

      findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              backBtfinish();
          }
      });
    }

    //获取标段列表
    private void getListData() {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("获取中...", true);
        Response.Listener<List<Contact>> successListener = new Response
                .Listener<List<Contact>>() {
            @Override
            public void onResponse(List<Contact> result) {
                if (result == null || result.size() == 0) {
                    if (null != context && !context.isFinishing()) {
                        dialogHelper.hideAlert();
                    }
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);

                    return;
                }
                setView(result);

                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
            }
        };

        Request<List<Contact>> versionRequest = new
                GsonRequest<List<Contact>>(
                        Request.Method.GET, url_counter,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (null != context && !context.isFinishing()) {
                            emptyTv.setText(context.getString(R.string.server_exception));
                            emptyTv.setVisibility(View.VISIBLE);
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<Contact>>() {
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

    private void setView(List<Contact> contractsInfoList) {
        for (final Contact Contact : contractsInfoList) {
            final String name = Contact.getName();
            final int id = Contact.getId();

            TextView itemTv = new TextView(context);
            itemTv.setGravity(Gravity.CENTER);
            itemTv.setTextSize(15.5f);
            itemTv.setPadding(35, 8, 35, 10);
            itemTv.setText(name);
            itemTv.setSingleLine();
            itemTv.setTextColor(context.getResources().getColor(R.color.color212121));
            itemTv.setBackgroundResource(R.drawable.selector_fragment_2_dialog_tab);
            itemTv.setLayoutParams(lp);
            topLayout.addView(itemTv);
            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spEdit.putInt(KeyConstant.SP_CHOOSE_ID, id);
                    spEdit.putInt(AuthsConstant.CHOOSE_AUTH_TYPE, App.CHOOSE_AUTH_TYPE == Constant.CONTRACT ? Constant.BUILDSITE : Constant.CONTRACT);
                    spEdit.putString(KeyConstant.SP_PROJECT_IMG_URL, "");
                    spEdit.putBoolean(KeyConstant.IS_FIRST_LUNCHER_SP, false);
                    spEdit.commit();

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    context.finish();
                    //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        backBtfinish();
    }

    private void backBtfinish() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
