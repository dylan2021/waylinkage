package com.android.waylinkage.activity.frag0;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

/**
 * Gool Lee
 * 项目规划
 */

public class EdmListActivity extends BaseFgActivity {

    private String id;
    private int mInfoTypeBrigeRoadTunnel;
    private int tabType;
    private EdmListBuildSiteFragment projectFragment0;
    private EdmListContractFragment contractFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_employee_list);

        id = getIntent().getStringExtra(KeyConstant.id);
        mInfoTypeBrigeRoadTunnel = getIntent().getIntExtra(KeyConstant.project_plan_info_type, 0);
        tabType = getIntent().getIntExtra(KeyConstant.type, 0);
        String stringExtra = getIntent().getStringExtra(KeyConstant.project_plan_title);

        String typeEnd = App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? "管理" : "统计";
        String typeTitle = tabType == 3 ? "人员" : tabType == 4 ? "设备" : "材料";
        initTitleBackBt(TextUtil.remove_N(stringExtra) + typeTitle + typeEnd);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE) {
            projectFragment0 = new EdmListBuildSiteFragment(tabType, mInfoTypeBrigeRoadTunnel, id);
            transaction.add(R.id.main_fragments, projectFragment0);
        } else {
            contractFragment = new EdmListContractFragment(id,tabType);
            transaction.add(R.id.main_fragments, contractFragment);
        }

        transaction.commitAllowingStateLoss();

        Button addBt = (Button) findViewById(R.id.title_right_bt);

        addBt.setVisibility(App.CHOOSE_AUTH_TYPE == Constant.BUILDSITE ? View.VISIBLE : View.GONE);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_add);
        addBt.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        addBt.setPadding(0, 0, 60, 0);
        addBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projectFragment0.onAddBtClick();
            }
        });
        //AuthsUtils.setViewAuth(addBt, AuthsConstant.BIZ_GROUP_BTN_ADD);
    }


}
