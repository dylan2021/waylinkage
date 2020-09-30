package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.ToastUtil;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 *  @author Gool Lee
 */
public class MaterailAddActivity extends BaseFgActivity {

    private String buildSiteId;
    private MaterailAddActivity context;
    private TextView storeManNameTv, storeManPhoneTv;
    private TextView planInDateTv;
    private TextView planInNumbersTv;
    private TextView specTv;
    private TextView factoryTv;
    private TextView nameTv;
    private TextView unitTv;
    private long planInDate;
    private TextView qualityTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_materail_add);
        context = this;
        initTitleBackBt("新增材料");
        buildSiteId = getIntent().getStringExtra(KeyConstant.id);

        nameTv = (TextView) findViewById(R.id.add_materail_name_tv);
        planInDateTv = (TextView) findViewById(R.id.add_materail_plan_in_date_tv);
        planInNumbersTv = (TextView) findViewById(R.id.add_materail_in_numbers_tv);
        specTv = (TextView) findViewById(R.id.add_materail_spec_tv);

        storeManNameTv = (TextView) findViewById(R.id.add_materail_store_man_name_tv);
        storeManPhoneTv = (TextView) findViewById(R.id.add_materail_store_man_phone_tv);
        factoryTv = (TextView) findViewById(R.id.add_materail_factory_tv);
        unitTv = (TextView) findViewById(R.id.add_materail_unit_tv);
        qualityTv = (TextView) findViewById(R.id.add_materail_quality_tv);

        planInDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                planInDate = millseconds;
                                planInDateTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                            }
                        })
                        .setTitleStringId("")//标题
                        .setCyclic(false)
                        .setCancelStringId(getString(R.string.time_dialog_title_cancel))
                        .setSureStringId(getString(R.string.time_dialog_title_sure))
                        .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                        .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                        .setThemeColor(context.getResources().getColor(R.color.mainColorDrak))
                        .setType(Type.YEAR_MONTH_DAY)
                        .setWheelItemTextSize(16)
                        .build();
                mDialogAll.show(context.getSupportFragmentManager(), "");
            }
        });
    }

    private void addMaterailPost() {
        String name = nameTv.getText().toString();
        String planInNumber = planInNumbersTv.getText().toString();
        String spec = specTv.getText().toString();
        String storeManName = storeManNameTv.getText().toString();
        String unit = unitTv.getText().toString();
        String storeManPhone = storeManPhoneTv.getText().toString();
        String factory = factoryTv.getText().toString();
        String quality = qualityTv.getText().toString();
        if (TextUtil.isEmpty(name)) {
            ToastUtil.show(context, "材料名称不能为空");
            return;
        }
        if (planInDate == 0) {
            ToastUtil.show(context, "请选择计划进场日期");
            return;
        }
        if (TextUtil.isEmpty(planInNumber)) {
            ToastUtil.show(context, "请填写计划进场数量");
            return;
        }
        if (TextUtil.isEmpty(spec)) {
            ToastUtil.show(context, "请填写材料规格");
            return;
        }
        if (TextUtil.isEmpty(unit)) {
            ToastUtil.show(context, "请填写材料的单位");
            return;
        }
        JSONObject idObj = new JSONObject();
        try {
            idObj.put(KeyConstant.id, buildSiteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizBuildSite, idObj);
        map.put(KeyConstant.name, name);
        map.put(KeyConstant.spec, spec);
        map.put(KeyConstant.planInDate, DateUtil.millonsToUTC(planInDate));
        map.put(KeyConstant.factory, factory);
        map.put(KeyConstant.quality, quality);
        map.put(KeyConstant.unit, unit);

        map.put(KeyConstant.keeperName, storeManName);
        map.put(KeyConstant.keeperPhone, storeManPhone);
        //添加
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.biz_materials;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(map),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "设备添加失败");
                            return;
                        }
                        ToastUtil.show(context, "设备添加成功");
                        DialogHelper.hideWaiting(fm);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    public void onReportCommitClick(View view) {
        addMaterailPost();
    }
}
