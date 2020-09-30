package com.android.waylinkage.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.android.waylinkage.App;
import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.activity.frag0.EdmListActivity;
import com.android.waylinkage.activity.frag0.RecordActivity;
import com.android.waylinkage.bean.LinkInfo;
import com.android.waylinkage.util.Constant;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.view.ScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gool Lee
 */

public class MoreActivity extends BaseFgActivity {

    private MoreActivity context;
    private int type;
    private String buildSiteName, chooseId;
    private int pendingApproval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_more);
        context = this;
        initTitleBackBt("更多");

        Intent intent = getIntent();
        chooseId = intent.getStringExtra(KeyConstant.id);
        buildSiteName = intent.getStringExtra(KeyConstant.project_plan_title);
        type = intent.getIntExtra(KeyConstant.type, 0);
        pendingApproval = intent.getIntExtra(KeyConstant.numbers, 0);

        initMenuBt();

    }

    private void initMenuBt() {
        View.OnClickListener onMenuClickLister = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, EdmListActivity.class);
                intent1.putExtra(KeyConstant.project_plan_title, buildSiteName);
                intent1.putExtra(KeyConstant.id, chooseId);
                intent1.putExtra(KeyConstant.project_plan_info_type, Integer.valueOf(type));

                switch (v.getId()) {
                    case R.id.confirm_things_bt:
                        Intent intent = new Intent(context, ReportMenuActivity.class);
                        intent.putExtra(KeyConstant.id, chooseId);
                        context.startActivity(intent);
                        break;
                    case R.id.employee_manager_bt:
                        intent1.putExtra(KeyConstant.type, 3);//人员管理
                        context.startActivity(intent1);

                        break;
                    case R.id.device_manager_bt:
                        intent1.putExtra(KeyConstant.type, 4);//设备管理
                        context.startActivity(intent1);
                        break;
                    case R.id.materail_manager_bt:
                        intent1.putExtra(KeyConstant.type, 5);//材料管理
                        context.startActivity(intent1);
                        break;
                    case R.id.plans_bt:
                        Intent i = new Intent(context, ProcessorListActivity.class);
                        i.putExtra(KeyConstant.id, chooseId);
                        i.putExtra(KeyConstant.TITLE, buildSiteName);
                        context.startActivity(i);
                        break;
                    //日志管理
                    case R.id.note_manager_bt:
                        Intent i2 = new Intent(context, RecordActivity.class);
                        i2.putExtra(KeyConstant.id, chooseId);
                        i2.putExtra(KeyConstant.TITLE, context.getString(R.string.note_manager));
                        context.startActivity(i2);
                        break;
                    //资料管理
                    case R.id.data_manager_bt:
                        Intent i1 = new Intent(context, RecordActivity.class);
                        i1.putExtra(KeyConstant.id, chooseId);
                        i1.putExtra(KeyConstant.TITLE, context.getString(R.string.data_manager));
                        context.startActivity(i1);
                        break;
                    //更多
                    case R.id.more_bt:
                        Intent i3 = new Intent(context, MoreActivity.class);
                        i3.putExtra(KeyConstant.id, chooseId);
                        i3.putExtra(KeyConstant.project_plan_title, buildSiteName);
                        i3.putExtra(KeyConstant.type, Integer.valueOf(type));
                        i3.putExtra(KeyConstant.TITLE, context.getString(R.string.more));
                        context.startActivity(i3);
                        break;
                }
            }
        };
        View bt1 = findViewById(R.id.confirm_things_bt);
        TextView bt2 = findViewById(R.id.employee_manager_bt);
        TextView bt3 = findViewById(R.id.device_manager_bt);
        TextView bt4 = findViewById(R.id.materail_manager_bt);

        TextView  thingsNumTv = (TextView) findViewById(R.id.confirm_things_num_red_tv);

        if (pendingApproval > 0) {
            thingsNumTv.setVisibility(View.VISIBLE);
            thingsNumTv.setText(pendingApproval + "");
        }

        TextView dataBt =findViewById(R.id.data_manager_bt);
        View noteBt =findViewById(R.id.note_manager_bt);

        View emptyBt = findViewById(R.id.empty_bt);
        if (App.CHOOSE_AUTH_TYPE != Constant.BUILDSITE) {
            if (App.CHOOSE_AUTH_TYPE == Constant.PROJECT) {//项目
                bt2.setText("项目人员");
                bt3.setText("项目设备");
                bt4.setText("项目材料");

                bt2.setVisibility(View.VISIBLE);
                bt3.setVisibility(View.VISIBLE);
                bt4.setVisibility(View.VISIBLE);
                noteBt.setVisibility(View.VISIBLE);
            } else {//标段
                dataBt.setText("标段资料");

                bt1.setVisibility(View.VISIBLE);
                noteBt.setVisibility(View.VISIBLE);
                dataBt.setVisibility(View.VISIBLE);
                emptyBt.setVisibility(View.VISIBLE);
            }
        } else {//工地
            bt2.setText("工地人员");
            bt3.setText("工地设备");
            bt4.setText("工地材料");

            bt1.setVisibility(View.VISIBLE);
            bt2.setVisibility(View.VISIBLE);
            bt3.setVisibility(View.VISIBLE);
            bt4.setVisibility(View.VISIBLE);
        }

        bt1.setOnClickListener(onMenuClickLister);
        bt2.setOnClickListener(onMenuClickLister);
        bt3.setOnClickListener(onMenuClickLister);
        bt4.setOnClickListener(onMenuClickLister);
        dataBt.setOnClickListener(onMenuClickLister);
        noteBt.setOnClickListener(onMenuClickLister);
    }

}
