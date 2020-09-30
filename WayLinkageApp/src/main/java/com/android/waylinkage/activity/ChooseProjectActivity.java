/*
 * 	Flan.Zeng 2011-2016	http://git.oschina.net/signup?inviter=flan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.waylinkage.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.main.MainActivity;
import com.android.waylinkage.adapter.ChooseDataAdapter;
import com.android.waylinkage.bean.ChooseDataInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.AuthsConstant;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DialogUtils;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gool Lee
 */
public class ChooseProjectActivity extends BaseFgActivity {

    private Button title_bar;
    private TextView tv_content;
    private TextView guideContentTv, titleTv;
    private LinearLayout guildeLayout;
    private TabLayout mTopTab0, mTopTab1;
    private RecyclerView recyclerView;
    private List<ChooseDataInfo.DataBean> dataBean = new ArrayList<>();
    private ArrayList<String> pinYinList = new ArrayList<>();
    private ChooseDataAdapter adapter;
    private SharedPreferences.Editor spEd;
    private ChooseProjectActivity context;
    private int AUTH_TYPE = 0;
    private View finishBt;
    private SharedPreferences sp;
    private boolean isFirstLuncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        context = this;
        setContentView(R.layout.activity_choose_project);

        finishBt = findViewById(R.id.left_bt);
        titleTv = (TextView) findViewById(R.id.center_tv);

        sp = getSharedPreferences(Constant.CONFIG_FILE_NAME, MODE_PRIVATE);
        spEd = sp.edit();
        isFirstLuncher = sp.getBoolean(KeyConstant.IS_FIRST_LUNCHER_SP, true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChooseDataAdapter(dataBean, context, isFirstLuncher);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChooseDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int id, String name) {
                spEd.putInt(KeyConstant.SP_CHOOSE_ID, id);
                spEd.putInt(AuthsConstant.CHOOSE_AUTH_TYPE, AUTH_TYPE);
                spEd.putInt(AuthsConstant.AUTH_TYPE, AUTH_TYPE);
                spEd.putString(KeyConstant.SP_PROJECT_IMG_URL, "");
                spEd.putString(KeyConstant.SP_MAIN_NAME, name);
                spEd.putBoolean(KeyConstant.IS_FIRST_LUNCHER_SP, false);
                spEd.commit();

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                context.finish();
                if (!isFirstLuncher) {
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });

        getData();
        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtfinish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backBtfinish();
    }

    private void backBtfinish() {
        //不是第一次启动
        if (!isFirstLuncher) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            finish();
        }
    }

    //获取权限数据列表
    private void getData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_data;
        Response.Listener<ChooseDataInfo> successListener = new Response
                .Listener<ChooseDataInfo>() {
            @Override
            public void onResponse(ChooseDataInfo result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null) {
                    ToastUtil.show(context, "暂无权限");
                    return;
                }
                String type = result.getType();
                AUTH_TYPE = type.equals(AuthsConstant.buildSite) ? 0 :
                        type.equals(AuthsConstant.contract) ? -2 : -1;
                App.CHOOSE_AUTH_TYPE = AUTH_TYPE;
                titleTv.setText(AUTH_TYPE == Constant.BUILDSITE ?
                        "选择工地" : AUTH_TYPE == Constant.CONTRACT ?
                        "选择标段" : "选择项目");
                //设置数据
                adapter.setData(result.getData());
            }
        };

        Request<ChooseDataInfo> versionRequest = new
                GsonRequest<ChooseDataInfo>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                        if (volleyError != null && volleyError.networkResponse != null &&
                                volleyError.networkResponse.statusCode == 403) {
                            DialogUtils.showTipDialog(context, "该账号暂无任何数据查看权限");
                        } else {
                            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        }
                    }
                }, new TypeToken<ChooseDataInfo>() {
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
}
