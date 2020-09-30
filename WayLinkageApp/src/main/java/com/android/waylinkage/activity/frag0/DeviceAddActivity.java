package com.android.waylinkage.activity.frag0;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
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

/**
 * Gool Lee
 */
public class DeviceAddActivity extends BaseFgActivity {
    private DeviceAddActivity context;
    private TextView deviceCodeTv, idNumbTv, realInDateTv, belongPeopleNameTv;
    private TextView sourceTv, workStatusTv;
    private int source, workStatus;
    private long realInDate;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add);
        groupId = getIntent().getStringExtra(KeyConstant.groupId);

        initStatusBar();
        initView();
        context = this;
    }

    private String[] workStatusItems = {"正常", "待维保", "已损坏"};
    private String[] sourceItems = {"自购", "租赁"};

    private void initView() {
        deviceCodeTv = (TextView) findViewById(R.id.device_code_tv);
        belongPeopleNameTv = (TextView) findViewById(R.id.belong_people_name_tv);
        realInDateTv = (TextView) findViewById(R.id.real_in_date_tv);
        workStatusTv = (TextView) findViewById(R.id.work_status_tv);
        sourceTv = (TextView) findViewById(R.id.source_tv);

        realInDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                        .setCallBack(new OnDateSetListener() {
                            @Override
                            public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                realInDate = millseconds;
                                realInDateTv.setText(DateUtil.getStrTime_Y_M_D(millseconds) + "");
                            }
                        })
                        .setTitleStringId("")//标题
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

        sourceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。

                builder.setItems(sourceItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        sourceTv.setText(sourceItems[i]);
                        source = i + 1;
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });
        workStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。

                builder.setItems(workStatusItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        workStatusTv.setText(workStatusItems[i]);
                        workStatus = i + 1;
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

    }


    public void deviceAddSaveClick(View view) {
        String deviceCode = deviceCodeTv.getText().toString();//编号
        String belongPeopleName = belongPeopleNameTv.getText().toString();
        String deviceName = ((EditText) findViewById(R.id.device_name_tv)).getText().toString();
        String deviceSpec = ((EditText) findViewById(R.id.device_xin_hao_tv)).getText().toString();//型号
        String owner = ((EditText) findViewById(R.id.belong_people_name_tv)).getText().toString();
        String ownerPhone = ((EditText) findViewById(R.id.belong_people_phone_tv)).getText().toString();

        if (TextUtil.isEmpty(deviceCode)) {
            ToastUtil.show(context, "设备编号不能为空哦");
            return;
        }
        if (0 == realInDate) {
            ToastUtil.show(context, "请选择实际进场日期");
            return;
        }
        if (0 == workStatus) {
            ToastUtil.show(context, "请选择设备的运行状态");
            return;
        }

        if (0 == source) {
            ToastUtil.show(context, "请选择设备的来源");
            return;
        }
        if (TextUtil.isEmpty(belongPeopleName)) {
            ToastUtil.show(context, "保管人姓名不能为空哦");
            return;
        }
        JSONObject idObj = new JSONObject();
        try {
            idObj.put(KeyConstant.id, groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();
        map.put(KeyConstant.bizDevice, idObj);
        map.put(KeyConstant.code, deviceCode);
        map.put(KeyConstant.name, deviceName);
        map.put(KeyConstant.spec, deviceSpec);
        map.put(KeyConstant.numbers, 1);
        map.put(KeyConstant.realInDate, DateUtil.millonsToUTC(realInDate));
        map.put(KeyConstant.owner, owner);
        map.put(KeyConstant.source, source + "");
        map.put(KeyConstant.status, workStatus + "");
        map.put(KeyConstant.ownerPhone, ownerPhone);

        //添加
        postData(new JSONObject(map));
    }

    private void postData(JSONObject objParams) {
        final FragmentManager fm = getSupportFragmentManager();
        DialogHelper.showWaiting(fm, "加载中...");
        String url = Constant.WEB_SITE + UrlConstant.url_biz_device_details;
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        Log.d(TAG, "新增设备"+url);
        Log.d(TAG, "新增设备"+objParams.toString());
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                objParams,
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
                Log.d(TAG, "新增设备"+error.getCause());
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

    public void onBackClick(View view) {
        finish();
    }
}