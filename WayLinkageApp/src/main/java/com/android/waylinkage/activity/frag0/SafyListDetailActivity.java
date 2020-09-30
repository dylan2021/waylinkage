package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.bean.ItemInfo;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 * 安全详情
 */

public class SafyListDetailActivity extends BaseFgActivity {
    private SafyListDetailActivity context;
    private int TYPE;
    private LinearLayout overviewItemLayout;
    private List<ItemInfo> processList = new ArrayList<>();
    private String id = "", TITLE = "";
    private JSONObject jsonObject;
    private TextView titleTv;
    private TextView belowContractTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_overview_detail);

        context = this;

        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv = (TextView) findViewById(R.id.center_tv);
        belowContractTv = (TextView) findViewById(R.id.below_contract_tv);
        TYPE = getIntent().getIntExtra(KeyConstant.TYPE, 0);
        id = getIntent().getStringExtra(KeyConstant.id);
        TITLE = getIntent().getStringExtra(KeyConstant.TITLE);
        titleTv.setText(TITLE);
        initInfoLv();

    }

    private void initInfoLv() {
        overviewItemLayout = ((LinearLayout) findViewById(R.id.overview_item_detail_list_layout));
        processList.clear();
        String url_type = "";
        if (TYPE == 0) {//安全教育
            url_type = "trains/";
        } else if (TYPE == 1) {//事故记录
            url_type = "accidents/";
        } else if (TYPE == 2) {
            url_type = "materials/";
        } else if (TYPE == 3) {
            url_type = "flags/";
        } else {
            url_type = "checks/";
        }
        //获取数据
        getData(url_type);
    }

    private void getData(String url_type) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + UrlConstant.url_biz_security + "/" + url_type + id;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            ToastUtil.show(context, getString(R.string.no_data));
                            return;
                        }

                        if (TYPE == 0) {//安全教育
                            setTrainsData(result);
                        } else if (TYPE == 1) {//事故记录
                            setAccidentsData(result);
                        } else if (TYPE == 2) {//防护用品
                            setMaterailData(result);
                        } else if (TYPE == 3) {//警示标志
                            setMaterailData(result);
                        } else {//检查
                            setChecksData(result);
                        }

                        //添加Item
                        for (ItemInfo itemInfo : processList) {
                            View itemView = View.inflate(context, R.layout.activity_overview_detail_item_detail, null);
                            TextView leftTv = (TextView) itemView.findViewById(R.id.overview_detail_item_left_tv);
                            leftTv.setText(itemInfo.str0);
                            TextView rightTv = (TextView) itemView.findViewById(R.id.overview_detail_item_right_tv);
                            String str1 = itemInfo.str1;
                            rightTv.setText(Html.fromHtml(str1));

                            overviewItemLayout.addView(itemView);
                        }

                        //附件
                        setFileListData(result);

                        try {
                            JSONObject resultObj = new JSONObject(result);
                            JSONObject buildSiteObj = resultObj.getJSONObject("bizBuildSite");
                            String buildSiteName = buildSiteObj.getString("name");
                            JSONObject bizContractObj = buildSiteObj.getJSONObject("bizContract");
                            String contractName = bizContractObj.getString("name");
                            JSONObject bizProjectObj = bizContractObj.getJSONObject("bizProject");
                            String projectName = bizProjectObj.getString("name");
                            belowContractTv.setText(projectName + "-" + contractName + "-" + buildSiteName);
                        } catch (JSONException e) {
                            Log.d(TAG, "异常" + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(KeyConstant.Authorization, KeyConstant.Bearer + App.token);
                return params;
            }
        };

        App.requestQueue.add(jsonObjRequest);
    }

    //防护用品
    private void setMaterailData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("result");
            if (array == null || array.length() == 0) {
                ToastUtil.show(context, "检查项为空");
                return;
            }
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObj = array.getJSONObject(i);
                String name = itemObj.getString("typeDesc");

                String completed = itemObj.getString("completed");//齐全:  0  不   1  是
                String completeStr = ("0".equals(completed) || "2".equals(completed)
                        || completed == null) ? "<font color='#fab52a' >不齐全</font>" : "齐全";

                if (TYPE == 3) {
                    String standanded = itemObj.getString("standard");//规范
                    String standandedStr = ("0".equals(standanded) || "2".equals(standanded))
                            ? "<font color='#fab52a' >不规范</font>" : "规范";
                    completeStr = completeStr + "<font color='#ffffff'>&ensp|&ensp</font>" + standandedStr;
                }
                processList.add(new ItemInfo(name, completeStr));
            }
        } catch (JSONException e) {
            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
        }
    }


    private void setTrainsData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            processList.add(new ItemInfo("培训地点", jsonObject.getString("location")));
            processList.add(new ItemInfo("培训时间", TextUtil.substringTime(jsonObject.getString("time"))));
            processList.add(new ItemInfo("培训类型", jsonObject.getString("typeDesc")));
            processList.add(new ItemInfo("培训对象", jsonObject.getString("target")));
            processList.add(new ItemInfo("应到人数", jsonObject.getString("planPeoples")));
            processList.add(new ItemInfo("实到人数", jsonObject.getString("realPeoples")));
            processList.add(new ItemInfo("课时", jsonObject.getInt("period") + ""));
            processList.add(new ItemInfo("授课人", jsonObject.getString("trainer")));
            processList.add(new ItemInfo("确认人", jsonObject.getString("confirmor")));
            processList.add(new ItemInfo("内容", jsonObject.getString("content")));
        } catch (JSONException e) {
            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
        }
    }

    private void setChecksData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            processList.add(new ItemInfo("检查区域", jsonObject.getString("areaDesc")));
            String status = jsonObject.getString("status");
            processList.add(new ItemInfo("检查情况", jsonObject.getString("result")));
            processList.add(new ItemInfo("是否存在安全隐患", "1".equals(status) ? "不存在" : "存在"));
            String checkTime = jsonObject.getString("checkTime");
            String confirmTime = jsonObject.getString("confirmTime");
            processList.add(new ItemInfo("检查人", jsonObject.getString("checker")));
            processList.add(new ItemInfo("检查时间", TextUtil.substringTime(checkTime)));
            processList.add(new ItemInfo("确认人", jsonObject.getString("confirmor")));
        } catch (JSONException e) {
            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
        }

    }

    private void setAccidentsData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            processList.add(new ItemInfo("事发地点", jsonObject.getString("location")));
            processList.add(new ItemInfo("事发时间", TextUtil.substringTimeHHMM(
                    jsonObject.getString("time"))));
            processList.add(new ItemInfo("处理完成时间", TextUtil.substringTimeHHMM(
                    jsonObject.getString("handleTime"))));
            processList.add(new ItemInfo("事发原因", jsonObject.getString("reason")));
            processList.add(new ItemInfo("事故责任人", jsonObject.getString("accidentOwner")));
            processList.add(new ItemInfo("安全责任人", jsonObject.getString("securityOwner")));

            processList.add(new ItemInfo("伤亡程度", jsonObject.getString("lossLevel")));
            processList.add(new ItemInfo("确认人", jsonObject.getString("confirmor")));
            processList.add(new ItemInfo("直接经济损失", jsonObject.getString("directMoneyLoss") + getString(R.string.money_unit)));
            processList.add(new ItemInfo("间接经济损失", jsonObject.getString("indirectMoneyLoss") + getString(R.string.money_unit)));
            processList.add(new ItemInfo("应急措施", jsonObject.getString("emergency")));
        } catch (JSONException e) {
            ToastUtil.show(context, getString(R.string.request_failed_retry_later));
        }
    }

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    private void setFileListData(String result) {
        //附件
        TextView linkTv = (TextView) findViewById(R.id.file_link_iv);
        ((TextView) findViewById(R.id.card_detail_file_title)).setText(R.string.file_link_list);
        listView = (ScrollListView) findViewById(R.id.horizontal_gridview);
        try {
            jsonObject = new JSONObject(result);
            JSONArray picArr = jsonObject.getJSONArray(KeyConstant.pic);
            if (picArr != null && picArr.length() > 0) {
                for (int i = 0; i < picArr.length(); i++) {
                    JSONObject obj = picArr.getJSONObject(i);
                    String name = obj.getString(KeyConstant.name);
                    String url = obj.getString(KeyConstant.url);
                    fileData.add(new FileListInfo(name, url, Constant.TYPE_SEE));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONArray attArr = jsonObject.getJSONArray(KeyConstant.attachment);
            if (attArr != null && attArr.length() > 0) {
                for (int i = 0; i < attArr.length(); i++) {
                    JSONObject obj = attArr.getJSONObject(i);
                    String name = obj.getString(KeyConstant.name);
                    String url = attArr.getJSONObject(i).getString(KeyConstant.url);
                    fileData.add(new FileListInfo(name, url, Constant.TYPE_SEE));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "文件数据erre" + e);
        }


        if (fileData == null || fileData.size() == 0) {
            findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
            findViewById(R.id.card_detail_file_layout).setVisibility(View.VISIBLE);
        }
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
    }

}
