
package com.android.waylinkage.util;

/**
 * @author Gool Lee
 */
public class Constant {

    //public static final String WEB_SITE = "http://api.waylinkage.com";
    public static final String WEB_SITE = "http://47.106.196.166:8803";

    public static final String CONFIG_FILE_NAME = "waylinkage.config";
    public static final String WEB_SITE_FILE = "http://api.waylinkage.com";
    public static final String SP_TOKEN = "Token";
    public static final String SP_USER_NAME = "UserName";
    public static final String CONFIG_NICK_NAME = "NickName";
    public static final String sp_pwd = "PassWord";
    public static final String CONFIG_HEAD_PHONE = "HeadUrl";
    public static final String CFG_RECEIVE_MSG = "ReceiveMsg";
    public static final String CFG_DELETE_APK = "DeleteApk";
    public static final String CFG_ALLOW_4G_LOAD = "AllowLoadBy4G";
    public static final String PUSH_API_KEY = "LUQUlTLy7fybX0oZOVeg9Pwh";
    public static final String URL_APP_UPDATE = "/app/queryCurrentAppVersion";
    public static final String URL_GAME_CENSUS = "/game/censusGameDownload";
    public static final String URL_USER_LOGIN = "/authorization/oauth/token";
    public static final String URL_USER_REGISTER = "/user/userRegistration";
    public static final String URL_FEEDBACK = "/complaint/submitFeedback";
    public static final String URL_FEEDBACK_FILE = "/complaint/uploadFeedbackPhoto";
    public static final String URL_POSTS_LIST = "/gameCircle/getShowPostCategoryList";
    public static final String URL_CIRCLE_POSTS_LIST = "/gameCircle/getShowPostList";
    public static final String URL_PUSH_MSG_DETAIL = "/message/queryMessageById";
    public static final String URL_WATCH_RECORD_QUERY = "/video/queryVideoPlayRecordList";
    public static final String URL_WATCH_RECORD_ADD = "/video/insertVideoPlayRecord";
    public static final String URL_WATCH_RECORD_DELETE = "/video/deletePlayRecord";
    public static final int NET_STATUS_DISCONNECT = 0x0010;//网络未连接
    public static final int NET_STATUS_4G = 0x0011;// 4G状态连接
    public static final int NET_STATUS_WIFI = 0x0012;//WIFI状态
    public static final String APP_TYPE_ID_0_ANDROID = "0";
    public static final String URL_GET_AUTH_CODE = "/user/getAuthCode";
    public static final String URL_GET_USER_BY_TOKEN = "/user/getUserByToken";
    public static final String url_system_employees = "/system/employees";
    public static final String CONFIG_LOGIN_TYPE = "loginType";
    public static final String CONFIG_USER_CODE = "config_user_code";
    public static final String loginMode_Phone = "0";
    public static final String loginMode_Email = "1";
    public static final String authType_Find_Pwd = "1";
    public static final String authType_Register = "0";

    public static final String PHONE = "0";
    public static final String EMAIL = "1";
    public static final String accident = "accident";
    public static final String flag = "flag";
    public static final String material = "material";
    public static final String train = "train";
    public static final String check = "check";//（1手机，2QQ，3微信，4新浪微博）

    public static final String FILE_NAME_SD_CRAD_APP_PKGNAME = "file_name_sd_crad_app_pkgname";
    public static final String CONFIG_USER_EMAIL = "config_user_email";
    public static final String application_form = "application/x-www-form-urlencoded";
    public static final String application_json = "application/json";
    public static final String authorization= "Basic d2ViOjEyMzQ1Ng==";
    public static final String FILE_TYPE_IMG = "img";
    public static final String FILE_TYPE_DOC = "doc";
    public static final String buildSite= "buildSite";
    public static final String TYPE_SEE="TYPE_SEE";
    public static final String TYPE_ADD="TYPE_ADD";
    public static final int BUILDSITE = 0;
    public static final int CONTRACT = -2;
    public static final int PROJECT = -1;
}
