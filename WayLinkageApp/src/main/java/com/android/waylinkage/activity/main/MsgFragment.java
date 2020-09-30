package com.android.waylinkage.activity.main;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.adapter.MsgFragmentAdapter;
import com.android.waylinkage.base.fragment.BaseSearchFragment;
import com.android.waylinkage.bean.MsgInfo;
import com.android.waylinkage.core.net.GsonRequest;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.NetUtil;
import com.android.waylinkage.util.TextUtil;
import com.android.waylinkage.util.Utils;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gool Lee
 */
@SuppressLint({"WrongConstant", "ValidFragment"})
public class MsgFragment extends BaseSearchFragment {
    private int id;
    private String[] tabArr = {"公告", "通知", "公示"};
    private MainActivity context;
    private RefreshLayout mRefreshLayout;
    private MsgFragmentAdapter msgAdapter;
    private TabLayout tabLayout;
    private int TYPE = 1;
    private ListView lv;
    private TextView emptyTv;

    public MsgFragment(int chooseId) {
        id = chooseId;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initViewsAndEvents(View view) {
        context = (MainActivity) getActivity();
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        Utils.setLoadHeaderFooter(context, mRefreshLayout);
        mRefreshLayout.autoRefresh();

        emptyTv = (TextView) view.findViewById(R.id.empty_tv);
        tabLayout = (TabLayout) view.findViewById(R.id.msg_tab_layout);
        for (String tabTitle : tabArr) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitle).setTag(tabTitle));
        }
        //修改TabLayout的下划线宽度
        Utils.setIndicator(tabLayout, 55);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                msgAdapter.setDate(new ArrayList<MsgInfo>());
                TYPE = tab.getPosition() + 1;
                getMsgData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        lv = (ListView) view.findViewById(R.id.listView);
        msgAdapter = new MsgFragmentAdapter(context);
        lv.setAdapter(msgAdapter);
        lv.setEmptyView((TextView) view.findViewById(R.id.empty_tv));

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getMsgData();
            }
        });

    }

    //获取消息数据
    private void getMsgData() {
        TextUtil.initEmptyTv(context,emptyTv);
        if (!NetUtil.isNetworkConnected(context)) {
            mRefreshLayout.finishRefresh(0);
            return;
        }
        String url = Constant.WEB_SITE + "/biz/contents/all?type=" + TYPE + "&status=2";
        Response.Listener<List<MsgInfo>> successListener = new Response
                .Listener<List<MsgInfo>>() {
            @Override
            public void onResponse(List<MsgInfo> result) {
                mRefreshLayout.finishRefresh(0);
                if (result == null || result.size() == 0) {
                    emptyTv.setText(context.getString(R.string.no_data));
                    emptyTv.setVisibility(View.VISIBLE);
                    return;
                }
                Collections.reverse(result);
                msgAdapter.setDate(result);
            }
        };

        Request<List<MsgInfo>> versionRequest = new
                GsonRequest<List<MsgInfo>>(
                        Request.Method.GET, url,
                        successListener, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mRefreshLayout.finishRefresh(0);
                        emptyTv.setText(context.getString(R.string.server_exception));
                        emptyTv.setVisibility(View.VISIBLE);
                    }
                }, new TypeToken<List<MsgInfo>>() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onFirstUserVisible() {

    }

    @Override
    protected void onUserVisible() {
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    protected View getLoadView(View view) {
        return null;
    }


}
