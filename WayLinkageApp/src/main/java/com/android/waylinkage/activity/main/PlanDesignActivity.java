package com.android.waylinkage.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.util.KeyConstant;

/**
 * Gool Lee
 * 项目规划
 */

public class PlanDesignActivity extends BaseFgActivity {

    private String id;
    private int mInfoTypeBrigeRoadTunnel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_plan_design_info);

        id = getIntent().getStringExtra(KeyConstant.id);
        mInfoTypeBrigeRoadTunnel = getIntent().getIntExtra(KeyConstant.project_plan_info_type, 0);
        String stringExtra = getIntent().getStringExtra(KeyConstant.project_plan_title);
        initTitleBackBt(stringExtra + "规划信息");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PlanDesignFragment projectFragment0 = new PlanDesignFragment(mInfoTypeBrigeRoadTunnel, id);
        transaction.add(R.id.main_fragments, projectFragment0);
        transaction.commitAllowingStateLoss();
    }


}
