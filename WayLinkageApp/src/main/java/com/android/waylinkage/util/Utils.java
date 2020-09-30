package com.android.waylinkage.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.lang.reflect.Field;

/**
 * 工具类
 */
public class Utils {
    /*
     *  是不是奇数
     */
    public static boolean isEvenNum(int num) {
        return !((num & 1) == 1);
    }

    /**
     * 调用第三方浏览器打开
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            // 打印Log   ComponentName到底是什么
            L.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setLoadHeaderFooter(Activity context, RefreshLayout refreshLayout) {
        refreshLayout.setPrimaryColors(Color.WHITE);
        // Header
        final ClassicsHeader header = new ClassicsHeader(context);
        header.setTextSizeTitle(14);
        TextView headerLastUpdateTv = header.getLastUpdateText();
        headerLastUpdateTv.setVisibility(View.GONE);
        header.setDrawableProgressSizePx(62);
        header.setDrawableArrowSizePx(57);
        header.setEnableLastTime(false);
        refreshLayout.setRefreshHeader(header, ImageUtil.getScreenWidth(context), 200);
        // Footer
        ClassicsFooter footer = new ClassicsFooter(context);
        footer.setPrimaryColor(Color.WHITE);
        footer.setTextSizeTitle(14);
        footer.setDrawableArrowSizePx(57);
        footer.setDrawableProgressSizePx(62);
        refreshLayout.setRefreshFooter(footer, ImageUtil.getScreenWidth(context), 200);
        refreshLayout.setEnableFooterFollowWhenLoadFinished(true);
    }

    public static  void setIndicator(final TabLayout tabLayout,final int leftRightMargin ){
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);

                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width ;
                        params.leftMargin = leftRightMargin;
                        params.rightMargin = leftRightMargin;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
