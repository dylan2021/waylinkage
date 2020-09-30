package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.ToastUtil;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.HashMap;
import java.util.Map;


/**
 * Gool Lee
 */
public class EmployeeAddActivity extends BaseFgActivity {
    private EmployeeAddActivity content;
    private TextView nameTv, idNumbTv, sexTv;
    private String[] sexItems = {"男", "女"};
    private String[] degreeItems = {"小学", "初中", "中专/高中", "专科", "本科", "硕士", "博士"};
    private String[] itemsWorkYears = {"1年", "2年", "3年", "4年", "5年",
            "6年", "7年", "8年", "9年", "10年", "11年", "12年", "13年","14年", "15年", "16年",
            "17年","18年","19年","20年","21年","22年","23年","24年","25年","26年","27年","28年",
            "29年","30年",
    };
    private String[] itemsAges = new String[40];
    private EmployeeAddActivity context;
    private TextView workYearTv;
    private TextView certificationTv;
    private TextView realInDateTv;
    private TextView addressTv;
    private TextView ageTv;
    private TextView phoneTv;
    private TextView degreeTv;
    private TextView emergencyNameTv;
    private TextView emergencyPhoneTv;
    private Map<String, String> map;
    private String groupId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples_add);
        context = this;

        groupId = getIntent().getStringExtra(KeyConstant.groupId);
        initStatusBar();
        initView();
        content = this;
    }

    private void initView() {
        for (int i = 0; i < 40; i++) {
            itemsAges[i] = String.valueOf(20 + i);
        }
        nameTv = (TextView) findViewById(R.id.name_tv);
        sexTv = (TextView) findViewById(R.id.sex_tv);

        sexTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeleteDialog(sexItems, sexTv);
            }
        });
        idNumbTv = (TextView) findViewById(R.id.id_number_tv);
        idNumbTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String idStr = s.toString().replace(" ", "");
                if (idStr.length() > 9) {
                    Integer age = TextUtil.getAgeFromIDCard(idStr);
                    ageTv.setText(age + "");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneTv = (TextView) findViewById(R.id.phone_tv);
        ageTv = (TextView) findViewById(R.id.age_tv);

        addressTv = (TextView) findViewById(R.id.address_tv);

        realInDateTv = (TextView) findViewById(R.id.real_in_date);
        //资历证明
        certificationTv = (TextView) findViewById(R.id.certification_tv);
        workYearTv = (TextView) findViewById(R.id.work_years_tv);
        degreeTv = (TextView) findViewById(R.id.degree_tv);

        emergencyNameTv = (TextView) findViewById(R.id.emergency_name_tv);
        emergencyPhoneTv = (TextView) findViewById(R.id.emergency_phone_tv);

        workYearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeleteDialog(itemsWorkYears, workYearTv);
            }
        });

        //学历
        degreeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeleteDialog(degreeItems, degreeTv);
            }
        });

        realInDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                realInDateTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");

                            }
                        })
                        .setTitleStringId("")
                        .setCyclic(false)
                        .setCancelStringId(getString(R.string.time_dialog_title_cancel))
                        .setSureStringId(getString(R.string.time_dialog_title_sure))
                        .setWheelItemTextSelectorColorId(context.getResources().getColor(R.color.mainColor))
                        .setWheelItemTextNormalColorId(context.getResources().getColor(R.color.time_nomal_text_color))
                        .setThemeColor(context.getResources().getColor(R.color.mainColorDrak))
                        .setWheelItemTextSize(16)
                        .setType(Type.YEAR_MONTH_DAY)
                        .build();
                mDialogAll.show(context.getSupportFragmentManager(), "");
            }
        });


    }

    private void showSeleteDialog(final String[] items, final TextView currTv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(content);
        // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                currTv.setText(items[i]);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void peopleAddRightSaveClick(View view) {
        String sex = sexTv.getText().toString();

        String postedName = nameTv.getText().toString();
        String postedIdNo = idNumbTv.getText().toString();
        String postedAge = ageTv.getText().toString();

        String postedPhone = phoneTv.getText().toString();
        String postedAddress = addressTv.getText().toString();
        String postedRealInDate = realInDateTv.getText().toString();

        String postedCertification = certificationTv.getText().toString();
        String postedWorkYear = workYearTv.getText().toString();

        String postedDegree = degreeTv.getText().toString();
        String postedEmergencyName = emergencyNameTv.getText().toString();
        String postedEmergencyPhone = emergencyPhoneTv.getText().toString();

        if (TextUtil.isEmpty(postedName)) {
            ToastUtil.show(content, "姓名不能为空");
            return;
        }
        if (TextUtil.isEmpty(sex)) {
            ToastUtil.show(content, "性别不能为空");
            return;
        }
        if (!TextUtil.isIdCardNum(postedIdNo)) {
            ToastUtil.show(content, "身份证号不合法哦");
            return;
        }
        if (!TextUtil.isMobile(postedPhone)) {
            ToastUtil.show(content, "手机号不正确哦");
            return;
        }

        if (TextUtil.isEmpty(postedWorkYear)) {
            ToastUtil.show(content, "工作年限不能为空哦");
            return;
        }
        if (TextUtil.isEmpty(postedRealInDate)) {
            ToastUtil.show(content, "请选择实际进场日期");
            return;
        }
        String postedSex = sex.equals("男") ? "1" : "0";

        map = new HashMap<>();
        map.put(KeyConstant.name, postedName);
        map.put(KeyConstant.age, postedAge);
        map.put(KeyConstant.sex, postedSex);

        map.put(KeyConstant.cardNo, postedIdNo);
        map.put(KeyConstant.phone, postedPhone);
        map.put(KeyConstant.address, postedAddress);
        map.put(KeyConstant.workYears, postedWorkYear.substring(0, postedWorkYear.length() - 1));
        map.put(KeyConstant.currentGroupId, groupId);
        map.put(KeyConstant.realInDate, postedRealInDate);

        map.put(KeyConstant.degree, postedDegree);//学历
        map.put(KeyConstant.certification, postedCertification);//资质证明

        map.put(KeyConstant.emergyName, postedEmergencyName);
        map.put(KeyConstant.emergyPhone, postedEmergencyPhone);

        String url = Constant.WEB_SITE + UrlConstant.url_employees;

        Response.Listener<JsonObject> successListener = new Response
                .Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject result) {
                if (result == null) {
                    DialogHelper.hideWaiting(getSupportFragmentManager());
                    ToastUtil.show(context, "人员添加失败");
                    return;
                }
                ToastUtil.show(context, "人员添加成功");
                DialogHelper.hideWaiting(getSupportFragmentManager());
                context.finish();
            }
        };

        Request<JsonObject> versionRequest = new
                GsonRequest<JsonObject>(
                        Request.Method.POST, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        DialogHelper.hideWaiting(getSupportFragmentManager());
                        Log.d(TAG, "HTTP提交返回" + volleyError.toString());
                        ToastUtil.show(context, getString(R.string.server_exception));
                    }
                }, new TypeToken<JsonObject>() {
                }.getType()) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return map;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(KeyConstant.Content_Type, Constant.application_json);
                        params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                        return params;
                    }
                };
        App.requestQueue.add(versionRequest);
    }

    public void onBackClick(View view) {
        finish();
    }

}