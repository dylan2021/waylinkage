package com.android.waylinkage.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.android.waylinkage.R;
import com.android.waylinkage.activity.BaseFgActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 * Gool
 */
public class TextUtil {

    /**
     * @param str 被判的字符串
     * @return 如果任何一个字符串为null, 则返回true
     */
    public static boolean isAnyEmpty(String... str) {

        for (String s : str) {
            if (s == null || s.length() <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @return 如果为空则返回 true
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.trim().length() <= 0) {
            return true;
        }
        return false;
    }


    /**
     * 是否是合法字符串
     *
     * @param str 被校验的字符串
     * @param reg 正则表达式
     * @return
     */
    public static boolean isLegal(String str, String reg) {

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }

    /**
     * 检测是否是合法的手机号
     *
     * @param phone
     * @return
     */
    public static boolean isMobile(String phone) {
        return isLegal(phone, "^1[3|4|5|7|8]\\d{9}$");
    }

    /**
     * 文件单位转换
     *
     * @param size 文件大小，单位字节（Byte）
     * @return 最小单位KB
     */
    public static String formatFileSize(long size) {

        String sizeStr;
        if ((size = size / 1024) > 1024) {

            sizeStr = Math.round(size / 1024) + "M";
            sizeStr = size / 1024 + "M";
        } else {
            sizeStr = Math.round(size) + "K";
            sizeStr = size + "K";
        }

        return sizeStr;
    }

    /**
     * 格式化下载数值
     *
     * @param count 数值
     * @return 格式化后的字符串
     */
    public static String formatCount(long count) {

        String countStr;

        if (count > 1000) {
            countStr = Math.round(count / 1000) + "千";
        } else if (count > 10000) {
            countStr = Math.round(count / 10000) + "万";
        } else if (count > 100000) {
            countStr = Math.round(count / 100000) + "十万";
        } else if (count > 1000000) {
            countStr = Math.round(count / 1000000) + "百万";
        } else {
            countStr = count + "";
        }
        return countStr;
    }

    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    public static String getTxtString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String substringTime(String time) {
        if (time == null || time.length() < 10) {
            return "未知";
        } else {
            return time.substring(0, 10);
        }

    }

    public static String substringTimeHHMM(String time) {
        if (time == null || time.length() < 16) {
            return "未知";
        } else {
            return time.substring(0, 16);
        }
    }

    public static String substringTimeMMDD_HHMM(String time) {
        if (time == null || time.length() < 16) {
            return "未知";
        } else {
            return time.substring(6, 16);
        }
    }

    /**
     * 某时间距离现在
     *
     * @return
     */
    public static int differentDaysByMillisecond(Date date2) {
        Date date = new Date();
        int days = (int) ((date2.getTime() - date.getTime()) / (1000 * 3600 * 24));
        return days + 1;
    }

    /**
     * 两个时间的间隔
     *
     * @return
     */
    public static int differentDaysByMillisecond2(Date endDate, Date startDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
        return days + 1;
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toAllSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    public static String remove_N(String str) {
        return str == null ? "" : str;
    }

    public static String removeBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 身份证号校验
     */
    public static boolean isIdCardNum(String idCard) {
        String reg = "^\\d{15}$|^\\d{17}[0-9Xx]$";
        if (!idCard.matches(reg)) {
            return false;
        }
        return true;
    }

    public static Integer getAgeFromIDCard(String idCardNo) {

        int length = idCardNo.length();

        String dates = "";

        if (length > 9) {
            dates = idCardNo.substring(6, 10);

            SimpleDateFormat df = new SimpleDateFormat("yyyy");

            String year = df.format(new Date());

            int u = Integer.parseInt(year) - Integer.parseInt(dates);

            return u > 150 ? 0 : u < 0 ? 0 : u;

        } else {
            return 0;
        }

    }

    public static String parseArrayToString(CharSequence[] arr) {
        if (arr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int offset = arr.length - 1;
        for (int i = 0; i < offset; i++) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[offset]);

        return sb.toString();
    }

    public static String parseArrayToString(String[] arr) {
        if (arr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int offset = arr.length - 1;
        for (int i = 0; i < offset; i++) {
            sb.append(arr[i]).append(", ");
        }
        sb.append(arr[offset]).append("]");

        return sb.toString();
    }

    public static void initEmptyTv(BaseFgActivity context, TextView emptyTv) {
        emptyTv.setText(!NetUtil.isNetworkConnected(context) ? context.getString(R.string.no_network) : "");
        emptyTv.setVisibility(!NetUtil.isNetworkConnected(context) ? View.VISIBLE : View.GONE);
        Drawable noNetWork = context.getResources().getDrawable(!NetUtil.isNetworkConnected(context) ?
                R.drawable.ic_bg_no_network : R.drawable.ic_bg_no_data);
        emptyTv.setCompoundDrawablesWithIntrinsicBounds(null, noNetWork, null, null);
    }
}
