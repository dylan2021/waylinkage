package com.android.waylinkage.activity.frag0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.EmployeeInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;


/**
 * Gool Lee
 * 人员详情
 */
public class EmployeeDetailActivity extends BaseFgActivity {
    private EmployeeDetailActivity context;
    private TextView nameTv, phoneTv, idNumbTv, sexTv;
    private String name;
    private TextView titleTv;
    private String employeeId = "";
    private TextView ageTv;
    private TextView workYearTv;
    private TextView certificationTv;
    private TextView realInDateTv;
    private TextView addressTv;
    private TextView degreeTv;
    private TextView emergencyNameTv;
    private TextView emergencyPhoneTv;
    private TextView workStatusTv;
    private TextView realOutDateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_detail);

        context = this;
        initStatusBar();

        name = getIntent().getStringExtra(KeyConstant.name);
        employeeId = getIntent().getStringExtra(KeyConstant.id);
        titleTv = ((TextView) findViewById(R.id.tv_title));
        initView();
    }

    private void initView() {
        nameTv = (TextView) findViewById(R.id.name_tv);
        nameTv.setText(name);

        sexTv = (TextView) findViewById(R.id.sex_tv);
        idNumbTv = (TextView) findViewById(R.id.id_number_tv);
        phoneTv = (TextView) findViewById(R.id.phone_tv);


        phoneTv = (TextView) findViewById(R.id.phone_tv);
        ageTv = (TextView) findViewById(R.id.age_tv);
        addressTv = (TextView) findViewById(R.id.address_tv);

        realInDateTv = (TextView) findViewById(R.id.real_in_date);
        realOutDateTv = (TextView) findViewById(R.id.real_out_date);
        //资历证明
        certificationTv = (TextView) findViewById(R.id.certification_tv);
        workYearTv = (TextView) findViewById(R.id.work_year_tv);
        degreeTv = (TextView) findViewById(R.id.study_tv);

        workStatusTv = (TextView) findViewById(R.id.work_status_tv);

        emergencyNameTv = (TextView) findViewById(R.id.emergency_name_tv);
        emergencyPhoneTv = (TextView) findViewById(R.id.emergency_phone_tv);

        getEmployee();
    }


    private void setData(EmployeeInfo result) {
        String sex = result.getSex();
        sexTv.setText("1".equals(sex) ? "男" : "女");

        idNumbTv.setText(result.getCardNo());
        ageTv.setText(result.getAge()+"");
        addressTv.setText(TextUtil.removeBlank(result.getAddress()));
        workYearTv.setText(result.getWorkYears()+"");
        degreeTv.setText(result.getDegree());
        certificationTv.setText(result.getCertification());
        phoneTv.setText(result.getPhone());

        String status = result.getStatus();
        workStatusTv.setText("1".equals(status) ? "正常" : "已调拨");

        //realInDateTv.setText(result.getRealInDate());
        realInDateTv.setText(result.getCreateTime());
        emergencyNameTv.setText(result.getEmergyName());
        emergencyPhoneTv.setText(result.getEmergyPhone());

    }

    private void getEmployee() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        String url = Constant.WEB_SITE + UrlConstant.url_employees + "/" + employeeId;

        Response.Listener<EmployeeInfo> successListener = new Response.Listener<EmployeeInfo>() {
            @Override
            public void onResponse(EmployeeInfo result) {
                if (result == null) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                setData(result);
            }

        };

        Request<EmployeeInfo> versionRequest = new GsonRequest<EmployeeInfo>(
                Request.Method.GET, url,
                successListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }, new TypeToken<EmployeeInfo>() {
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

    public void onBackClick(View view) {
        finish();
    }

    public void onPeopleAllotRecordClick(View view) {
        Intent intent = new Intent(context, EmployeeRecordActivity.class);
        intent.putExtra(KeyConstant.id, employeeId);
        intent.putExtra(KeyConstant.TITLE, name);

        startActivity(intent);
    }

}