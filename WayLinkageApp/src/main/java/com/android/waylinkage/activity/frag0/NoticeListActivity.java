package com.android.waylinkage.activity.frag0;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;
import com.android.waylinkage.util.KeyConstant;
import com.android.waylinkage.util.TextUtil;

import java.io.InputStream;

/**
 * Gool Lee
 * 公告通知
 */
public class NoticeListActivity extends BaseFgActivity {
    private Button title_bar;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
        setContentView(R.layout.activity_affiche);
        String afficheId = getIntent().getStringExtra(KeyConstant.afficheTitle);
        initTitleBackBt(afficheId);

        TextView guideContentTv = (TextView) findViewById(R.id.guide_content_tv);
        InputStream inputStream = getResources().openRawResource(R.raw.qiao_liang_sgyd_txt);
        guideContentTv.setText(TextUtil.getTxtString(inputStream));

    }

}
