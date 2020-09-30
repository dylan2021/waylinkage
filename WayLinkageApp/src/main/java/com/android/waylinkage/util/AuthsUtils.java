package com.android.waylinkage.util;

import android.util.Log;
import android.view.View;

import com.android.waylinkage.App;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

/**
 * Gool
 */
public class AuthsUtils {
    //有权限->显示   无->隐藏
    public static void setViewAuth(View view, String viewAuthStr) {
        Log.d("解析", App.token);
        boolean isContains = false;
        if (App.authsArr != null) {
            for (int i = 0; i < App.authsArr.length; i++) {
                if (viewAuthStr.equals(App.authsArr[i])) {
                    isContains = true;
                    break;
                }
            }
            view.setVisibility(isContains ? View.VISIBLE : View.GONE);
        }
    }

    //解析token
    public static void resolveToken() {
        JWT jwt = new JWT(App.token);
        Claim claim = jwt.getClaim(KeyConstant.authorities);
        try {
            JsonObject[] jsonArr = claim.asArray(JsonObject.class);
            if (jsonArr == null) {
                return;
            }
            int length = jsonArr.length;
            App.authsArr = new String[length];
            for (int i = 0; i < length; i++) {
                JsonObject authObj = jsonArr[i];
                if (null != authObj & authObj.get(KeyConstant.authority) != null) {
                    App.authsArr[i] = authObj.get(KeyConstant.authority).getAsString();
                }
            }
        } catch (Exception e) {
            //Log.d(TAG, "解析,异常");
        }
    }
}
