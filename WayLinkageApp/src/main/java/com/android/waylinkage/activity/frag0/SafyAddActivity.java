package com.android.waylinkage.activity.frag0;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.other.CommonBaseActivity;
import com.android.waylinkage.bean.PictureBean;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.BuildSiteInfo;
import com.android.waylinkage.bean.ContractInfo;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.ImageUtil;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.RetrofitUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.FileTypeUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ExRadioGroup;
import com.android.waylinkage.view.ScrollListView;
import com.android.waylinkage.widget.mulpicture.MulPictureActivity;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 * 新增汇报
 */

public class SafyAddActivity extends CommonBaseActivity {
    private SafyAddActivity context;
    private TextView gongDiTv, heTongDuanTv;
    private TextView trainerTv, periodTv, emergencyTv, lossLevelTv, contentTv, confirmorTv;
    private TextView eventTv, checkerTv, checkResultTv;
    private String projectId = "";
    private int contractId;
    private List<BuildSiteInfo> buildSiteInfo = new ArrayList<>();
    private int buildSiteId, eventPosition = 0;
    private TextView trainTypeTv, areaTv, addressTv, accidentAddressTv, realPeoplesTv,
            passedTv, targetTv, planPeoplesTv, accidentplanPeoplesTv, accidentrealPeoplesTv;
    private FragmentManager fm;
    private List<ContractInfo> contractList = new ArrayList<>();
    private String URL_TYPE = "trains";
    private String confirmorStr;
    private int trainType = 1, areaType = 1;
    private TextView layoutTitle0Tv;
    private TextView confirmTimeTv;
    private ExRadioGroup mMaterailItemGroup, mFlag1ItemGroup, mFlag2ItemGroup;
    private TextView accidenttrainerTv, accidentcontentTv, accidenttargetTv;
    private TextView hasDangerTv;
    private int hasDanger = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_safy_add);
        fm = getSupportFragmentManager();
        context = this;
        buildSiteId = getIntent().getIntExtra(KeyConstant.id, 0);
        contractList = (List<ContractInfo>) getIntent().getSerializableExtra(KeyConstant.LIST_OBJECT);

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText("安全汇报");

        gongDiTv = (TextView) findViewById(R.id.report_add_gongqu_tv);
        heTongDuanTv = (TextView) findViewById(R.id.report_add_hetongduan_tv);
        eventTv = (TextView) findViewById(R.id.report_add_gongxu_tv);
        confirmorTv = (TextView) findViewById(R.id.confirmor_tv);//确认人
        setInputDialog(confirmorTv, InputType.TYPE_CLASS_TEXT);

        trainTypeTv = (TextView) findViewById(R.id.type_tv);
        layoutTitle0Tv = (TextView) findViewById(R.id.layout_title_0_tv);

        addressTv = (TextView) findViewById(R.id.address_tv);
        checkResultTv = (TextView) findViewById(R.id.check_result_tv);
        setInputDialog(addressTv, InputType.TYPE_CLASS_TEXT);
        setInputDialog(checkResultTv, InputType.TYPE_CLASS_TEXT);

        areaTv = (TextView) findViewById(R.id.area_tv);
        areaTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeletedDialog(area, areaTv, 4);
            }
        });

        accidentAddressTv = (TextView) findViewById(R.id.accident_address_tv);
        setInputDialog(accidentAddressTv, InputType.TYPE_CLASS_TEXT);

        targetTv = (TextView) findViewById(R.id.target_tv);
        hasDangerTv = (TextView) findViewById(R.id.has_danger_tv);
        accidenttargetTv = (TextView) findViewById(R.id.accident_target_tv);
        setInputDialog(targetTv, InputType.TYPE_CLASS_TEXT);
        setInputDialog(accidenttargetTv, InputType.TYPE_CLASS_TEXT);

        planPeoplesTv = (TextView) findViewById(R.id.plan_in_people_tv);
        accidentplanPeoplesTv = (TextView) findViewById(R.id.accident_plan_in_people_tv);
        setInputDialog(planPeoplesTv, InputType.TYPE_CLASS_NUMBER);
        setInputDialog(accidentplanPeoplesTv, InputType.TYPE_CLASS_NUMBER);

        realPeoplesTv = (TextView) findViewById(R.id.real_in_people_tv);
        accidentrealPeoplesTv = (TextView) findViewById(R.id.accident_real_in_people_tv);
        setInputDialog(accidentrealPeoplesTv, InputType.TYPE_CLASS_NUMBER);
        setInputDialog(realPeoplesTv, InputType.TYPE_CLASS_NUMBER);
        passedTv = (TextView) findViewById(R.id.passed_tv);//实际工期
        setInputDialog(passedTv, InputType.TYPE_CLASS_NUMBER);//授课人

        trainerTv = (TextView) findViewById(R.id.trainer_tv);
        setInputDialog(trainerTv, InputType.TYPE_CLASS_TEXT);

        accidenttrainerTv = (TextView) findViewById(R.id.accident_trainer_tv);
        setInputDialog(accidenttrainerTv, InputType.TYPE_CLASS_TEXT);

        periodTv = (TextView) findViewById(R.id.period_tv);
        setInputDialog(periodTv, InputType.TYPE_CLASS_NUMBER);

        contentTv = (TextView) findViewById(R.id.content_tv);
        setInputDialog(contentTv, InputType.TYPE_CLASS_TEXT);

        accidentcontentTv = (TextView) findViewById(R.id.accident_content_tv);
        setInputDialog(accidentcontentTv, InputType.TYPE_CLASS_TEXT);

        lossLevelTv = (TextView) findViewById(R.id.lossLevel_tv);
        setInputDialog(lossLevelTv, InputType.TYPE_CLASS_TEXT);

        emergencyTv = (TextView) findViewById(R.id.emergency_tv);
        checkerTv = (TextView) findViewById(R.id.checker_tv);
        setInputDialog(checkerTv, InputType.TYPE_CLASS_TEXT);
        setInputDialog(emergencyTv, InputType.TYPE_CLASS_TEXT);

        mMaterailItemGroup = ((ExRadioGroup) findViewById(R.id.saft_accident_material));
        mFlag1ItemGroup = ((ExRadioGroup) findViewById(R.id.saft_flag1_erg));
        mFlag2ItemGroup = ((ExRadioGroup) findViewById(R.id.saft_flag2_erg));

        findViewById(R.id.confirm_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet(R.id.confirm_time_tv);
            }
        });
        findViewById(R.id.accident_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet(R.id.accident_time_tv);
            }
        });
        findViewById(R.id.check_real_start_time_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeSet(R.id.check_real_start_time_tv);
            }
        });


        trainTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeletedDialog(trainTypeArr, trainTypeTv, 3);
            }
        });
        hasDangerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSeletedDialog(hasDangerArr, hasDangerTv, 5);
            }
        });

        initFileView();
    }

    private void setInputDialog(final TextView tvClick, final int inputType) {
        tvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.layout_dialog_update_process, null);
                Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);
                final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id
                        .dialog_add_card_title);
                etContent.setInputType(inputType);
                final Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setGravity(Gravity.TOP);//可以设置显示的位置
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        String title = etContent.getText().toString();
                        if (TextUtil.isEmpty(title)) {

                            ToastUtil.show(context, context.getString(R.string.enter_cannot_empty));
                            return;
                        }
                        tvClick.setText(title);
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void postReport() {
        confirmorStr = confirmorTv.getText().toString();
   /*     if (0 == buildSiteId) {
            ToastUtil.show(context, "请选择工地");
            return;
        }*/
        if (TextUtil.isEmpty(confirmorStr)) {
            ToastUtil.show(context, "请填写汇报人");
            return;
        }
        DialogHelper.showWaiting(fm, "加载中...");

        String url = Constant.WEB_SITE + UrlConstant.url_biz_security + "/" + URL_TYPE;

        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
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
        map.put(KeyConstant.confirmor, confirmorStr);

        //附件
        if (fileData != null) {
            try {
                JSONArray picArr = new JSONArray();
                JSONArray attachmentArr = new JSONArray();
                for (FileListInfo fileInfo : fileData) {
                    String fileUrl = fileInfo.fileUrl;
                    String fileName = fileInfo.fileName;
                    JSONObject fileObj = new JSONObject();
                    fileObj.put(KeyConstant.name, fileName);
                    fileObj.put(KeyConstant.url, fileUrl);
                    picArr.put(fileObj);
                }
                map.put(KeyConstant.pic, picArr);
                map.put(KeyConstant.attachment, attachmentArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        switch (eventPosition) {
            //安全教育
            case 0:
                map.put(KeyConstant.type, trainType);
                map.put(KeyConstant.time, DateUtil.millonsToUTC(time));
                map.put(KeyConstant.location, addressTv.getText().toString());//培训地点
                map.put(KeyConstant.target, targetTv.getText().toString());//培训对象
                map.put(KeyConstant.planPeoples, planPeoplesTv.getText().toString());
                map.put(KeyConstant.realPeoples, realPeoplesTv.getText().toString());
                map.put(KeyConstant.passed, passedTv.getText().toString());
                map.put(KeyConstant.trainer, trainerTv.getText().toString());//授课人
                map.put(KeyConstant.period, periodTv.getText().toString());//授课人
                map.put(KeyConstant.content, contentTv.getText().toString());//授课人

                break;

            //安全事故
            case 1:
                Log.d(TAG, time + "事故数据:" + handleTime);
                map.put(KeyConstant.time, DateUtil.millonsToUTC(time));//事故时间
                map.put(KeyConstant.location, accidentAddressTv.getText().toString());//事故地点

                map.put(KeyConstant.accidentOwner, accidenttrainerTv.getText().toString());
                map.put(KeyConstant.securityOwner, accidentcontentTv.getText().toString());

                map.put(KeyConstant.reason, accidenttargetTv.getText().toString());
                map.put(KeyConstant.lossLevel, lossLevelTv.getText().toString());

                map.put(KeyConstant.directMoneyLoss, accidentplanPeoplesTv.getText().toString());//直接经济损失
                map.put(KeyConstant.indirectMoneyLoss, accidentrealPeoplesTv.getText().toString());//间接经济损失

                map.put(KeyConstant.emergency, emergencyTv.getText().toString());

                map.put(KeyConstant.handleTime, DateUtil.millonsToUTC(handleTime));//处理完成时间
                break;

            //警示标识
            case 2:
                JSONArray resultArrFlag = new JSONArray();
                for (int i = 0; i < flagArr.length; i++) {
                    JSONObject resultObj = new JSONObject();
                    try {
                        resultObj.put(KeyConstant.type, i + 1);//哪种用品
                        resultObj.put(KeyConstant.completed, flagCompletedArr[i]);//是否齐全
                        resultObj.put(KeyConstant.standard, flagStandardArr[i]);//是否规范
                        resultArrFlag.put(resultObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                map.put(KeyConstant.result, resultArrFlag);
                break;

            //防护用品  -  防护
            case 3:
                JSONArray resultArr = new JSONArray();
                for (int i = 0; i < materialArr.length; i++) {
                    JSONObject resultObj = new JSONObject();
                    try {
                        resultObj.put(KeyConstant.type, i + 1);//哪种用品
                        resultObj.put(KeyConstant.completed, materialCompletedArr[i]);//是否齐全
                        resultArr.put(resultObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                map.put(KeyConstant.result, resultArr);

                break;
            //安全排查
            case 4:
                map.put(KeyConstant.checkTime, DateUtil.millonsToUTC(time));
                map.put(KeyConstant.confirmTime, DateUtil.millonsToUTC(System.currentTimeMillis()));
                map.put(KeyConstant.area, areaType);//区域
                map.put(KeyConstant.status, hasDanger);//是否存在隐患
                map.put(KeyConstant.checker, checkerTv.getText().toString());//事故地点
                map.put(KeyConstant.result, checkResultTv.getText().toString());//事故地点

                break;
        }


        JSONObject jsonObject = new JSONObject(map);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        if (result == null) {
                            DialogHelper.hideWaiting(fm);
                            ToastUtil.show(context, "汇报提交失败");
                            return;
                        }
                        ToastUtil.show(context, "汇报提交成功");
                        DialogHelper.hideWaiting(fm);
                        context.finish();
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

    String materialArr[] = new String[]{"劳保手套", "安全带", "安全帽", "安全鞋",
            "防护手套", "防尘口罩", "焊接面罩", "电焊手套", "坠落防护用品"};
    int materialCompletedArr[] = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};

    String flagArr[] = new String[]{"生活区", "施工通道", "施工场地", "钢筋加工区", "拌合站", "石料加工厂"};
    int flagCompletedArr[] = new int[]{1, 1, 1, 1, 1, 1};//警示标识 --齐全
    int flagStandardArr[] = new int[]{1, 1, 1, 1, 1, 1};//警示标识 --合格
    //安全教育
    int[] layoutArr = new int[]{R.id.saft_train_layout, R.id.saft_accident_layout,
            R.id.saft_accident_flag, R.id.saft_accident_material, R.id.saft_accident_check
    };

    public void showSeletedDialog(final String[] arr, final TextView titleTv, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置了标题就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                titleTv.setText(arr[i]);//汇报事项 TextView

                if (type == 0) {
                    contractId = contractList.get(i).getId();
                } else if (type == 1) {
                    buildSiteId = buildSiteInfo.get(i).getId();
                } else if (type == 2) {
                    layoutTitle0Tv.setText(title0Arr[i]);//底部layout标题
                    eventPosition = i;
                    //设置地址
                    URL_TYPE = urlArr[i];
                    //更换UI
                    for (int idPosition = 0; idPosition < layoutArr.length; idPosition++) {
                        findViewById(layoutArr[idPosition]).setVisibility(i == idPosition ? View.VISIBLE : View.GONE);
                    }
                    switch (i) {
                        //安全教育
                        case 0:

                            break;
                        //安全事故
                        case 1:
                            break;
                        //警示标识
                        case 2:
                            mFlag1ItemGroup.removeAllViews();
                            mFlag2ItemGroup.removeAllViews();
                            int length = flagArr.length;
                            for (int position = 0; position < length; position++) {
                                //不齐全
                                setFlagItem(mFlag1ItemGroup, position, flagArr, flagCompletedArr);
                                //不规范
                                setFlagItem(mFlag2ItemGroup, position, flagArr, flagStandardArr);
                            }


                            break;
                        //防护用品
                        case 3:
                            mMaterailItemGroup.removeAllViews();

                            for (int position = 0; position < materialCompletedArr.length; position++) {
                                setFlagItem(mMaterailItemGroup, position, materialArr, materialCompletedArr);
                            }

                            break;

                        case 4:     //安全排查
                            break;
                    }

                } else if (type == 3) {
                    trainType = i + 1;
                } else if (type == 4) {
                    areaType = i + 1;
                } else if (type == 5) {
                    hasDanger = i == 0 ? 2 : 1;//2  存在隐患   1  不存在
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, 1000);

    }

    public void setFlagItem(ExRadioGroup radioGroup, final int position, String[] titleArr, final int[] seletedArr) {
        final TextView itemTv = new TextView(context);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.setMargins(40, 0, 0, 30);
        itemTv.setGravity(Gravity.CENTER_VERTICAL);
        itemTv.setPadding(25, 8, 25, 10);
        itemTv.setTextSize(14);
        itemTv.setText(titleArr[position]);
        itemTv.setSingleLine();
        itemTv.setTextColor(context.getResources().getColor(R.color.color999999));
        itemTv.setBackgroundResource(R.drawable.selector_safy_report_item_bg);
        itemTv.setLayoutParams(lp2);

        itemTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTv.setSelected(!itemTv.isSelected());
                boolean selected = itemTv.isSelected();
                itemTv.setTextColor(context.getResources().getColor(selected
                        ? R.color.white :
                        R.color.color999999));
                seletedArr[position] = selected ? 0 : 1;
            }
        });

        radioGroup.addView(itemTv);
    }

    //选择标段
    public void seleteContracts(View view) {
        int size = contractList.size();
        String[] array = new String[size];
        for (int i = 0; i < size; i++) {
            array[i] = contractList.get(i).getName();
        }
        showSeletedDialog(array, heTongDuanTv, 0);
    }

    //选择工地
    public void seleteGongQu(View view) {
        if (contractId == 0) {
            ToastUtil.show(context, "请选择标段");
            return;
        }
        getBuildSiteListData();
    }

    //获取工地列表
    private void getBuildSiteListData() {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        final DialogHelper dialogHelper = new DialogHelper(getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);
        String url = Constant.WEB_SITE + UrlConstant.url_biz_contracts + "/"
                + contractId + UrlConstant.buildSites;

        Response.Listener<List<BuildSiteInfo>> successListener = new Response
                .Listener<List<BuildSiteInfo>>() {
            @Override
            public void onResponse(List<BuildSiteInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null || result.size() == 0) {
                    ToastUtil.show(context, getString(R.string.no_data));
                    return;
                }
                buildSiteInfo.clear();
                buildSiteInfo = result;

                int size = buildSiteInfo.size();
                String[] array = new String[size];
                for (int i = 0; i < size; i++) {
                    array[i] = buildSiteInfo.get(i).getName();
                }

                //工地
                showSeletedDialog(array, gongDiTv, 1);
            }
        };

        Request<List<BuildSiteInfo>> versionRequest = new
                GsonRequest<List<BuildSiteInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        ToastUtil.show(context, getString(R.string.request_failed_retry_later));
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<BuildSiteInfo>>() {
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

    String array[] = new String[]{"安全教育", "安全事故", "警示标识", "安全防护用品", "安全隐患排查"};
    String title0Arr[] = new String[]{"汇报内容", "汇报内容", "选择不齐全项",
            "选择不齐全项", "汇报内容"};
    String trainTypeArr[] = new String[]{"入场教育", "用电安全", "工地施工", "行车安全", "机械操作安全"};
    String hasDangerArr[] = new String[]{"是", "否"};
    String area[] = new String[]{"生活区", "施工现场", "钢筋加工场", "制梁厂",
            "石料加工厂", "拌合站", "施工通道", "仓库"};
    String urlArr[] = new String[]{"trains", "accidents", "flags", "materials", "checks"};

    //工序
    public void seleteProcessors(View view) {
        showSeletedDialog(array, eventTv, 2);
    }

    long handleTime, time;

    public void onRealStartTimeClick(View view) {
        showTimeSet(R.id.real_start_time_tv);
    }

    private void showTimeSet(final int id) {
        final TextView curTv = (TextView) findViewById(id);
        Type type = eventPosition == 1 ? Type.ALL : Type.YEAR_MONTH_DAY;
        TimePickerDialog mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                        if (millseconds > System.currentTimeMillis()) {
                            ToastUtil.show(context, "时间" + getString(R.string.cannot_later_than_today));
                            return;
                        }
                        Log.d(TAG, "事故时间选择" + millseconds);

                        if (id == R.id.confirm_time_tv) {
                            handleTime = millseconds;//事故--> 处理完成时间
                        } else {
                            time = millseconds;
                        }
                        if (eventPosition == 1) {
                            curTv.setText(DateUtil.getStrTime_Y_M_D_HHMMss(millseconds));
                        } else {
                            curTv.setText(DateUtil.getStrTime_Y_M_D(millseconds));
                        }
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
                .setType(type)
                .build();
        mDialogAll.show(context.getSupportFragmentManager(), "");
    }

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    private void initFileView() {
        //附件
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
        fileListAdapter.setCallBack(new FileListAdapter.DataRemoveCallBack() {
            @Override
            public void finish(List<FileListInfo> data) {
                fileData = data;
            }
        });

        findViewById(R.id.card_detail_file_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseFileDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //上传附件
        String fileType = "";
        String path = "";
        if (data != null && data.getData() != null) {
            path = FileTypeUtil.getPath(context, data.getData());
            //不是合格的类型
            if (!FileTypeUtil.isFileType(path) && !ImageUtil.isImageSuffix(path)) {
                ToastUtil.show(context, "暂不支持该文件类型");
                return;
            }
            fileType = ImageUtil.isImageSuffix(path) ? Constant.FILE_TYPE_IMG : Constant.FILE_TYPE_DOC;
        }
        //上传图片
        if (requestCode == 101 && data != null) {
            setIntent(data);
            getBundleP();
            if (pictures != null && pictures.size() > 0) {
                fileType = Constant.FILE_TYPE_IMG;
                for (int i = 0; i < pictures.size(); i++) {
                    path = pictures.get(i).getLocalURL();
                    fileType = Constant.FILE_TYPE_IMG;
                }
            }
        }
        File file = new File(path);
        uploadPictureThread(file, fileType);
    }

    private void uploadPictureThread(final File file, final String fileType) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put(KeyConstant.fileType, fileType);
        final String url = Constant.WEB_SITE_FILE + UrlConstant.url_biz_files;

        new Thread() {
            @Override
            public void run() {
                try {
                    RetrofitUtil.upLoadByCommonPost(url, file, map,
                            new RetrofitUtil
                                    .FileUploadListener() {
                                @Override
                                public void onProgress(long pro, double precent) {
                                    Log.d(TAG, "上传附件...." + pro);
                                }

                                @Override
                                public void onFinish(int code, final String responseUrl, Map<String, List<String>> headers) {
                                    if (200 == code && responseUrl != null) {
                                        final String finalResponseUrl = responseUrl;
                                        context.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileData.add(new FileListInfo(
                                                        file.getName(), finalResponseUrl, file.length(), finalResponseUrl));
                                                fileListAdapter.setDate(fileData);
                                                ImageUtil.reSetLVHeight(context, listView);
                                            }
                                        });
                                    }
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    public void getBundleP() {
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
            if (bundle != null) {
                pictures = (List<PictureBean>) bundle.getSerializable("pictures") != null ?
                        (List<PictureBean>) bundle.getSerializable("pictures") : new
                        ArrayList<PictureBean>();
            }
        }
    }

    private void choisePicture() {
        int choose = 9 - pictures.size();
        Intent intent = new Intent(context, MulPictureActivity.class);
        bundle = setBundle();
        bundle.putInt("imageNum", choose);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivityForResult(intent, 101);
    }

    //选择文件
    private void choiseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    private void showChooseFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style
                .dialog_appcompat_theme);
        //    指定下拉列表的显示数据
        final String[] chooseFileArr = {"图片", "手机文件"};
        //    设置一个下拉的列表选择项
        builder.setItems(chooseFileArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    //文件
                    case 0:
                        choisePicture();
                        break;
                    //图片
                    case 1:
                        choiseFile();
                        break;
                }
            }
        });
        builder.show();
    }

    public void onReportCommitClick(View view) {
        //保存
        postReport();
    }
}
