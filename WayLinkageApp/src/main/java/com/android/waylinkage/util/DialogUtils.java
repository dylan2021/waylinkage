package com.android.waylinkage.util;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.widget.dialogfragment.SimpleDialogFragment;

/**
 * Gool Lee
 */

public class DialogUtils {
    public static void setDialogWindow(Activity context, Dialog dialog, int gravity) {
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(gravity);
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        //params.y = 20;  Dialog距离底部的距离
        params.width = ImageUtil.getScreenWidth(context);
        dialogWindow.setAttributes(params);
        dialog.show();
    }

    public static void showTipDialog(FragmentActivity activity, String content) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();

        final SimpleDialogFragment dialogFragment = new SimpleDialogFragment();
        dialogFragment.setDialogWidth(250);
        TextView tv = new TextView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        params.setMargins(30, 66, 20, 40);
        params.gravity = Gravity.LEFT;
        tv.setLayoutParams(params);
        tv.setLineSpacing(0f, 1.5f);
        tv.setText(content);
        tv.setTextColor(ContextCompat.getColor(activity,R.color.color666666));
        tv.setTextSize(15.5F);
        dialogFragment.setContentView(tv);

        dialogFragment.setNegativeButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.dismiss();
            }
        });
        dialogFragment.show(ft, "successDialog");
    }

}
