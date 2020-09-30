package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.util.ToastUtil;


/**
 * @author Gool Lee
 * 申请管理
 */

public class ApplyDetailActivity extends BaseFgActivity {
    private ApplyDetailActivity content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initStatusBar();
        setContentView(R.layout.activity_todo_detail);
        content = this;
        findViewById(R.id.left_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleTv = (TextView) findViewById(R.id.center_tv);
        titleTv.setText("申请单详情");
    }

    public void onLinkTv0Click(View view) {
        ToastUtil.show(content, "暂时无法查看");
    }

    public void onLinkTv1Click(View view) {
        ToastUtil.show(content, "暂时无法查看");
    }

    public void onLinkTv2Click(View view) {

        ToastUtil.show(content, "暂时无法查看");
    }
}
