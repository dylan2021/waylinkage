package com.android.waylinkage.activity.frag0;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.bean.StatInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.DialogHelper;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.UrlConstant;
import com.android.waylinkage.view.MyExpandableListView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 */
@SuppressLint("ValidFragment")
public class EdmListContractFragment extends Fragment {

    private int tabType;
    private List<StatInfo> statInfos = new ArrayList<>();
    private BaseFgActivity context;
    private View view;
    private String id;
    private String[] tabArr = {"入库", "出库", "剩余"};
    private String url_auth_type = "", url_tab_type = "";
    private MyExpandableListView expendList;
    private EdmListContratAdapter edmListContratAdapter;
    private TabLayout mTopTab;
    private String materailUrl = "?materialType=enter";
    private TextView emptyTv;

    public EdmListContractFragment(String id, int tabType) {
        this.id = id;
        this.tabType = tabType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        context = (BaseFgActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_edm_contrat, null);
        emptyTv=view.findViewById(R.id.empty_tv);
        switch (App.CHOOSE_AUTH_TYPE) {
            case Constant.CONTRACT:
                url_auth_type = UrlConstant.url_biz_contracts;
                break;
            case Constant.PROJECT:
                url_auth_type = UrlConstant.url_biz_projects;
                break;
        }
        expendList = (MyExpandableListView) view.findViewById(R.id.expand_list);
        edmListContratAdapter = new EdmListContratAdapter(tabType);
        expendList.setAdapter(edmListContratAdapter);
        //设置分组的监听
        expendList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        //设置子项布局监听
        expendList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // childString[groupPosition][childPosition]
                return true;

            }
        });

        if (tabType == 3) {//人员
            url_tab_type = "/employee/stat";
        } else if (tabType == 4) {//设备
            url_tab_type = "/device/stat";
        } else {//材料
            initMateraiTabLayout();
            url_tab_type = "/material/stat";
        }

        getData();
        return view;
    }

    private void getData() {
        TextUtil.initEmptyTv(context, emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            return;
        }
        String url = Constant.WEB_SITE + url_auth_type + "/" + id + url_tab_type;
        if (tabType == 5) {
            url = url + materailUrl;//入库/出库/剩余
        }
        final DialogHelper dialogHelper = new DialogHelper(context.getSupportFragmentManager(), context);
        dialogHelper.showAlert("加载中...", true);

        Response.Listener<List<StatInfo>> successListener = new Response
                .Listener<List<StatInfo>>() {
            @Override
            public void onResponse(List<StatInfo> result) {
                if (null != context && !context.isFinishing()) {
                    dialogHelper.hideAlert();
                }
                if (result == null || result.size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    statInfos.clear();
                    edmListContratAdapter.setData(statInfos);
                    return;
                }
                statInfos = result;
                edmListContratAdapter.setData(statInfos);
            }
        };

        Request<List<StatInfo>> versionRequest = new
                GsonRequest<List<StatInfo>>(Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                        if (null != context && !context.isFinishing()) {
                            dialogHelper.hideAlert();
                        }
                    }
                }, new TypeToken<List<StatInfo>>() {
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

    //材料统计:TabLayout
    private void initMateraiTabLayout() {
        mTopTab = (TabLayout) view.findViewById(R.id.top_tab);
        mTopTab.setVisibility(View.VISIBLE);
        for (String tabTitle : tabArr) {
            TabLayout.Tab tab = mTopTab.newTab();
            tab.setTag(tabTitle);
            tab.setText(tabTitle);
            mTopTab.addTab(tab);
        }
        mTopTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                materailUrl = tabPosition == 0 ? "?materialType=enter" :
                        tabPosition == 1 ? "?materialType=exit" :
                                "?materialType=rest";
                getData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}
