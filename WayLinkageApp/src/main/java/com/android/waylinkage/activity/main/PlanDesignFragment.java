package com.android.waylinkage.activity.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.adapter.FileListAdapter;
import com.android.waylinkage.bean.FileListInfo;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.util.DateUtil;
import com.android.waylinkage.util.ToastUtil;
import com.android.waylinkage.view.ScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Gool Lee
 */
@SuppressLint("ValidFragment")
public class PlanDesignFragment extends Fragment {
    private String mId = "";
    private int mTnfoType;
    private FragmentActivity context;
    private TextView tv_summary;
    private LinearLayout layoutProject, layoutContractSection, layoutBrige, layoutRoad, layoutTunnel;
    private View view;
    private String planBeginTime;
    private String planEndTime;

    // 项目 / 标段/ 桥梁 /路基 /隧道
    public PlanDesignFragment(int infoType, String id) {
        mTnfoType = infoType;
        mId = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_project_0, null);

        initTop();

        return view;
    }

    private void initTop() {
        tv_summary = (TextView) view.findViewById(R.id.tv_summary);
        layoutProject = (LinearLayout) view.findViewById(R.id.layout_info_project);
        layoutContractSection = (LinearLayout) view.findViewById(R.id.layout_info_contract_section);
        layoutBrige = (LinearLayout) view.findViewById(R.id.layout_info_brige);
        layoutRoad = (LinearLayout) view.findViewById(R.id.layout_info_road);
        layoutTunnel = (LinearLayout) view.findViewById(R.id.layout_info_tunnel);

        if (mTnfoType == -1) {//项目
            layoutProject.setVisibility(View.VISIBLE);
            getData(UrlConstant.url_biz_projects);
        } else if (mTnfoType == -2) {//标段
            layoutContractSection.setVisibility(View.VISIBLE);
            getData(UrlConstant.url_biz_contracts);
        } else if (mTnfoType == 2) {//桥梁
            layoutBrige.setVisibility(View.VISIBLE);
            //mId = "1";
            getData(UrlConstant.url_biz_buildSites);
        } else if (mTnfoType == 1) {//路基
            //mId = "2";
            getData(UrlConstant.url_biz_buildSites);
            layoutRoad.setVisibility(View.VISIBLE);
        } else if (mTnfoType == 3) {//隧道
            //mId = "3";
            getData(UrlConstant.url_biz_buildSites);
            layoutTunnel.setVisibility(View.VISIBLE);
        }

    }


    //标段
    int[] contractIdArr = new int[]{R.id.proj_fragment0_top_xmmc_tv, R.id.proj_fragment0_bianhao_tv,
            R.id.proj_fragment0_kg_date_tv, R.id.proj_fragment0_zc_tv, R.id.proj_fragment0_wg_date_tv,
            R.id.proj_fragment0_jsbz_tv, R.id.proj_fragment0_ljwf_tv, R.id.proj_fragment0_0_tv,
            R.id.proj_fragment0_l_tv, R.id.proj_fragment0_3_tv,
            R.id.proj_fragment0_hdsl_tv, R.id.proj_fragment0_gsbj_tv, R.id.proj_fragment0_ljkd_tv,
            R.id.proj_fragment0_5_tv,

    };
    String[] contractKeyArr = new String[]{KeyConstant.name, KeyConstant.invest, KeyConstant.code,
            KeyConstant.length, KeyConstant.length, KeyConstant.planBeginDate,
            KeyConstant.planEndDate, KeyConstant.startChainage, KeyConstant.endChainage,
            KeyConstant.realStartDate, KeyConstant.realEndDate,
            KeyConstant.description,
            KeyConstant.contractCorporation, KeyConstant.supervisionCorporation
    };


    //桥梁
    int[] brigeIdArr = new int[]{R.id.bright_name_tv, R.id.fragment0_bright_tv_0,
            R.id.fragment0_bright_tv_1, R.id.fragment0_bright_tv_2, R.id.fragment0_bright_tv_3,
            R.id.fragment0_bright_tv_4, R.id.fragment0_bright_tv_5,
            R.id.fragment0_bright_tv_6, R.id.fragment0_bright_tv_7, R.id.fragment0_bright_tv_8,
            R.id.fragment0_bright_tv_9, R.id.fragment0_bright_tv_10, R.id.fragment0_bright_tv_11,
            R.id.fragment0_bright_tv_12, R.id.fragment0_bright_tv_13, R.id.fragment0_bright_tv_14,
            R.id.fragment0_bright_tv_15, R.id.fragment0_bright_tv_16,
    };
    String[] brigeKeyArr = new String[]{KeyConstant.name, KeyConstant.invest, KeyConstant.code,
            KeyConstant.period, KeyConstant.BRIDGE_LENGTH, KeyConstant.roadLevel, KeyConstant.speed,
            KeyConstant.BRIDGE_WIDTH, KeyConstant.BRIDGE_CENTER_CHAINAGE, KeyConstant.planBeginDate,
            KeyConstant.planEndDate, KeyConstant.loadLevel, KeyConstant.BRIDGE_WATER_FREQUENCY,
            KeyConstant.realStartDate, KeyConstant.realEndDate, KeyConstant.designUnit,
            KeyConstant.constructionUnit, KeyConstant.description
    };

    private void initBrigeView(JSONObject jsonObject) {
        int length = brigeIdArr.length;
        for (int i = 0; i < length; i++) {
            try {
                if (i == 1) {
                    setView(brigeIdArr[i], jsonObject.getInt(brigeKeyArr[i]) + "");
                } else if (i == 4 || i == 7 || i == 8 || i == 12) {
                    String specification = jsonObject.getString(KeyConstant.specification);
                    JSONObject specificationObj = new JSONObject(specification);
                    String string = specificationObj.getString(brigeKeyArr[i]);
                    setView(brigeIdArr[i], string);
                } else {
                    String name = brigeKeyArr[i];
                    String string = jsonObject.getString(name);
                    if (i == 9 || i == 10 || i == 13 || i == 14) {
                        string = TextUtil.substringTime(string);
                        if (i == 9) {
                            planBeginTime = string;
                        } else if (i == 10) {
                            planEndTime = string;
                        }
                    } else if (i == 15 || i == 16) {
                        JSONObject unitObj = new JSONObject(string);
                        string = unitObj.getString("nameCn");
                    } else if (i == length - 1) {
                        string = "\t\t\t\t\t\t" + TextUtil.remove_N(string);
                    }
                    setView(brigeIdArr[i], string);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            Date beginTime = DateUtil.getFormat().parse(planBeginTime);
            Date endTime = DateUtil.getFormat().parse(planEndTime);
            int periodDay = TextUtil.differentDaysByMillisecond2(endTime, beginTime);
            setView(brigeIdArr[3], periodDay + "");
        } catch (ParseException e) {
        }

    }

    //隧道
    int[] tunnelIdArr = new int[]{R.id.tunnel_name_tv, R.id.fragment0_tunnel_tv_0,
            R.id.fragment0_tunnel_tv_2, R.id.fragment0_tunnel_tv_1,
            R.id.fragment0_tunnel_tv_4, R.id.fragment0_tunnel_tv_5, R.id.fragment0_tunnel_tv_6,
            R.id.fragment0_tunnel_tv_7, R.id.fragment0_tunnel_tv_8, R.id.fragment0_tunnel_tv_9,
            R.id.fragment0_tunnel_tv_10, R.id.fragment0_tunnel_tv_11, R.id.fragment0_tunnel_tv_12,
            R.id.fragment0_tunnel_tv_13, R.id.fragment0_tunnel_tv_14, R.id.fragment0_tunnel_tv_15,
            R.id.fragment0_tunnel_tv_16, R.id.fragment0_tunnel_tv_17, R.id.fragment0_tunnel_tv_18,
            R.id.fragment0_tunnel_tv_19, R.id.fragment0_tunnel_tv_20, R.id.fragment0_tunnel_tv_21,
            R.id.fragment0_tunnel_tv_22, R.id.fragment0_tunnel_tv_23, R.id.fragment0_tunnel_tv_24,
            R.id.fragment0_tunnel_tv_25, R.id.fragment0_tunnel_tv_26, R.id.fragment0_tunnel_tv_27,
            R.id.fragment0_tunnel_tv_28
    };
    String[] tunnelKeyArr = new String[]{KeyConstant.name,
            KeyConstant.invest, KeyConstant.period, KeyConstant.code,
            KeyConstant.roadLevel, KeyConstant.loadLevel,
            "TUNNEL_LEFT_LENGTH", "TUNNEL_RIGHT_LENGTH",//右洞长度
            "TUNNEL_LEFT_HEIGHT", "TUNNEL_RIGHT_HEIGHT",//右洞高度
            "TUNNEL_LEFT_WIDTH", "TUNNEL_RIGHT_WIDTH",//宽度
            "TUNNEL_LEFT_ENTRY", "TUNNEL_RIGHT_ENTRY",//进口
            "TUNNEL_LEFT_EXIT", "TUNNEL_RIGHT_EXIT",//出口
            "TUNNEL_LEFT_START_CHAINAGE", "TUNNEL_RIGHT_START_CHAINAGE",//起点
            "TUNNEL_LEFT_END_CHAINAGE", "TUNNEL_RIGHT_END_CHAINAGE",//终点
            "TUNNEL_MAX_HEIGHT",//最大高度
            "TUNNEL_LIGHT", //照明
            KeyConstant.planBeginDate, KeyConstant.planEndDate,
            KeyConstant.realStartDate, KeyConstant.realEndDate,
            KeyConstant.designUnit, KeyConstant.constructionUnit,
            KeyConstant.description
    };

    //隧道
    private void initTunnelView(JSONObject jsonObject) {
        int length = tunnelKeyArr.length;
        for (int i = 0; i < length; i++) {
            try {
                if (i == 1) {
                    setView(tunnelIdArr[i], jsonObject.getInt(tunnelKeyArr[i]) + "");
                } else if (i >= 6 && i <= 21) {
                    String specification = jsonObject.getString(KeyConstant.specification);
                    JSONObject specificationObj = new JSONObject(specification);
                    String string = specificationObj.getString(tunnelKeyArr[i]);
                    setView(tunnelIdArr[i], string);
                } else {
                    String string = jsonObject.getString(tunnelKeyArr[i]);
                    if (i >= 22 && i <= 25) {
                        string = TextUtil.substringTime(string);
                        if (i == 22) {
                            planBeginTime = string;
                        } else if (i == 23) {
                            planEndTime = string;
                        }
                    } else if (i == 26 || i == 27) {
                        JSONObject unitObj = new JSONObject(string);
                        string = unitObj.getString("nameCn");
                    } else if (i == length - 1) {
                        string = "\t\t\t\t\t\t" + TextUtil.remove_N(string);
                    }
                    setView(tunnelIdArr[i], string);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            Date beginTime = DateUtil.getFormat().parse(planBeginTime);
            Date endTime = DateUtil.getFormat().parse(planEndTime);
            int periodDay = TextUtil.differentDaysByMillisecond2(endTime, beginTime);
            setView(tunnelIdArr[2], periodDay + "");
        } catch (ParseException e) {

        }
    }

    //隧道
    int[] roadIdArr = new int[]{R.id.road_name_tv, R.id.road_tv_0,
            R.id.road_tv_1, R.id.road_tv_2, R.id.road_tv_3,
            R.id.road_tv_4, R.id.road_tv_5, R.id.road_tv_6,
            R.id.road_tv_7, R.id.road_tv_8, R.id.road_tv_9,
            R.id.road_tv_10, R.id.road_tv_11, R.id.road_tv_12,
            R.id.road_tv_13, R.id.road_tv_14, R.id.road_tv_15,
            R.id.road_tv_16,
    };
    String[] roadKeyArr = new String[]{KeyConstant.name,
            KeyConstant.invest, KeyConstant.code,
            KeyConstant.period, "SUBGRADE_LENGTH",
            KeyConstant.speed, "SUBGRADE_WIDTH",
            KeyConstant.planBeginDate, KeyConstant.planEndDate,
            "SUBGRADE_START_CHAINAGE", "SUBGRADE_END_CHAINAGE",
            KeyConstant.roadLevel, KeyConstant.loadLevel,
            KeyConstant.realStartDate, KeyConstant.realEndDate,

            KeyConstant.designUnit, KeyConstant.constructionUnit,
            KeyConstant.description
    };

    //路基
    private void initRoadView(JSONObject jsonObject) {
        int length = roadKeyArr.length;
        for (int i = 0; i < length; i++) {
            try {
                if (i == 1) {
                    setView(roadIdArr[i], jsonObject.getInt(roadKeyArr[i]) + "");
                } else if (i == 4 || i == 6 || i == 9 || i == 10) {
                    String specification = jsonObject.getString(KeyConstant.specification);
                    JSONObject specificationObj = new JSONObject(specification);
                    String string = specificationObj.getString(roadKeyArr[i]);
                    setView(roadIdArr[i], string);
                } else {
                    String string = jsonObject.getString(roadKeyArr[i]);
                    if (i == 7 || i == 8 || i == 13 || i == 14) {
                        string = TextUtil.substringTime(string);
                        if (i == 7) {
                            planBeginTime = string;
                        } else if (i == 8) {
                            planEndTime = string;
                        }
                    } else if (i == 15 || i == 16) {
                        JSONObject unitObj = new JSONObject(string);
                        string = unitObj.getString("nameCn");
                    } else if (i == length - 1) {
                        string = "\t\t\t\t\t\t" + TextUtil.remove_N(string);
                    }
                    setView(roadIdArr[i], string);
                }

                Date beginTime = DateUtil.getFormat().parse(planBeginTime);
                Date endTime = DateUtil.getFormat().parse(planEndTime);
                int periodDay = TextUtil.differentDaysByMillisecond2(endTime, beginTime);
                setView(roadIdArr[3], periodDay + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //标段
    private void initContractView(JSONObject jsonObject) {

        int length = contractIdArr.length;
        for (int i = 0; i < length; i++) {
            try {
                String string = jsonObject.getString(contractKeyArr[i]);
                if (i == 5 || i == 6 || i == 9 || i == 10) {
                    string = TextUtil.substringTime(string);
                    if (i == 5) {
                        planBeginTime = string;
                    } else if (i == 6) {
                        planEndTime = string;
                    }
                }
                if (i == 1) {
                    setView(contractIdArr[i], jsonObject.getInt(contractKeyArr[i]) + "");
                } else if (i < length - 2) {
                    if (i == length - 3) {
                        string = "\t\t\t\t\t\t" + TextUtil.remove_N(string);
                    }
                    setView(contractIdArr[i], string);

                } else {
                    JSONObject corporationObj = new JSONObject(string);
                    setView(contractIdArr[i], corporationObj.getString(KeyConstant.nameCn));
                }
            } catch (Exception e) {
            }
        }
        try {
            Date beginTime = DateUtil.getFormat().parse(planBeginTime);
            Date endTime = DateUtil.getFormat().parse(planEndTime);
            int periodDay = TextUtil.differentDaysByMillisecond2(endTime, beginTime);
            setView(contractIdArr[3], periodDay + "");
        } catch (Exception e) {
        }
    }

    //项目
    private void initProjectView(JSONObject jsonObject) {
        try {
            setView(R.id.project_fragment0_xmmc_tv, jsonObject.getString(KeyConstant.name));
            setView(R.id.project_fragment0_ztz_tv, jsonObject.getInt(KeyConstant.invest) + "");
            setView(R.id.project_bianhao_tv, jsonObject.getString(KeyConstant.code));
            setView(R.id.project_lenth_tv, jsonObject.getString(KeyConstant.length));

            planBeginTime = jsonObject.getString(KeyConstant.planBeginDate);
            planEndTime = jsonObject.getString(KeyConstant.planEndDate);

            Date beginTime = DateUtil.getFormat().parse(planBeginTime);
            Date endTime = DateUtil.getFormat().parse(planEndTime);
            int periodDay = TextUtil.differentDaysByMillisecond2(endTime, beginTime);
            setView(R.id.project_zgq_tv, periodDay + "");

            setView(R.id.project_planBeginDate_tv, TextUtil.substringTime(planBeginTime));
            setView(R.id.project_planEndDate_tv, TextUtil.substringTime(planEndTime));

            setView(R.id.project_startLocation_tv, jsonObject.getString(KeyConstant.startLocation));
            setView(R.id.project_endLocation_tv, jsonObject.getString(KeyConstant.endLocation));
            String desc = TextUtil.remove_N(jsonObject.getString(KeyConstant.description));
            setView(R.id.project_desc_tv, "\t\t\t\t\t\t" + desc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setView(int viewId, String text) {
        ((TextView) view.findViewById(viewId)).setText(text==null?"":text);
    }

    //获取标段列表
    private void getData(String URL_POSTFIX) {
        if (!NetUtil.isNetworkConnected(context)) {
            ToastUtil.show(context, getString(R.string.no_network));
            return;
        }
        String url = Constant.WEB_SITE + URL_POSTFIX + "/" + mId;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        if (result == null) {
                            ToastUtil.show(context, getString(R.string.no_data));
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (mTnfoType == -1) {//项目
                                initProjectView(jsonObject);
                            } else if (mTnfoType == -2) {//标段
                                initContractView(jsonObject);
                            } else if (mTnfoType == 2) {//桥梁
                                initBrigeView(jsonObject);
                            } else if (mTnfoType == 1) {//路基
                                initRoadView(jsonObject);
                            } else if (mTnfoType == 3) {//隧道
                                initTunnelView(jsonObject);
                            }

                            setFileListData(jsonObject);
                        } catch (JSONException e) {
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

    private List<FileListInfo> fileData = new ArrayList<>();
    private ScrollListView listView;
    private FileListAdapter fileListAdapter;

    //附件
    private void setFileListData(JSONObject jsonObject) {
        TextView linkTv = (TextView) view.findViewById(R.id.file_link_iv);
        listView = (ScrollListView) view.findViewById(R.id.horizontal_gridview);
        try {
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
        }

        if (fileData == null || fileData.size() == 0) {
            view.findViewById(R.id.card_detail_file_layout).setVisibility(View.GONE);
        } else {
            linkTv.setVisibility(View.GONE);
            view.findViewById(R.id.card_detail_file_layout).setVisibility(View.VISIBLE);
        }
        fileListAdapter = new FileListAdapter(context, fileData);
        listView.setAdapter(fileListAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
