package com.android.waylinkage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间类型的转换
 */
public class DateUtil {

    public static int dayForWeek(String pTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    public static boolean isRegisteringTime() {
        Calendar time = Calendar.getInstance();
        if (time.get(Calendar.HOUR_OF_DAY) < 23) {
            return true;
        }
        return false;
    }

    //    public static SimpleDateFormat getDateFormat() {
//        if (null == DateLocal.get()) {
//            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
//        }
//        return DateLocal.get();
//    }
    private static SimpleDateFormat sdf_YYYY_mm_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf_YYYYmmdd = new SimpleDateFormat("yyyyMMdd");

    public static String getStrTime_YMD(long cc_time) {
        String re_StrTime = null;
        sdf_YYYY_mm_dd.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        re_StrTime = sdf_YYYY_mm_dd.format(new Date(cc_time));
        return re_StrTime;
    }

    public static String getStrTime_Y_M_D(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        re_StrTime = sdf.format(new Date(cc_time));
        return re_StrTime;
    }

    public static String getStrTime_Y_M_D_HHMMss(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        re_StrTime = sdf.format(new Date(cc_time));
        return re_StrTime;
    }

    public static String getWeek(int i) {
        String week = "";
        switch (i) {
            case 1:
                week = "周一";
                break;
            case 2:
                week = "周二";
                break;
            case 3:
                week = "周三";
                break;
            case 4:
                week = "周四";
                break;
            case 5:
                week = "周五";
                break;
            case 6:
                week = "周六";
                break;
            case 7:
                week = "周日";
                break;
        }
        return week;
    }


    /**
     * local时间转换成UTC时间
     *
     * @return
     */
    public static String millonsToUTC(long millons) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(new Date(millons));
    }

    public static SimpleDateFormat getFormat() {
        return sdf_YYYY_mm_dd;
    }
    public static SimpleDateFormat getFormatYYYYmmdd() {
        return sdf_YYYYmmdd;
    }
}
