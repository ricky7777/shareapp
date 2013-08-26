package com.rickystyle.shareapp.free.consts;

import com.rickystyle.shareapp.free.R;

/**
 * ShareApp常用的常數
 * 
 * @author Ricky
 */
public class ShareAppConsts {
    public static boolean      DEBUG                             = true;
    public static boolean      DEMO                              = true;
    public static String       LOGPREF                           = "log";

    public final static String API_URL                           = "http://rickyplatform.appspot.com/dreamplatform";
    public static final String RATING_URL_DEMO                   = "market://details?id=com.rickystyle.shareapp.free";
    public static final String RATING_URL_FULL                   = "market://details?id=com.rickystyle.shareapp";

    // shareapp option adapter key
    public final static String KEY_SHAREAPP_OPTION_ICONID        = "shareoptioniconid";
    public final static String KEY_SHAREAPP_OPTION_DESC          = "shareoptiondesc";
    public final static String KEY_BOOKMARK_ICONPATH             = "bookmarkiconpath";
    public final static String KEY_BOOKMARK_PACKAGENAME          = "bookmarkpackagename";
    public final static String KEY_BOOKMARK_DESC                 = "bookmarkdesc";

    // activity result code
    public final static int    ACT_RESULT_CODE_SHARE_SUCCESS     = 0;
    public final static int    ACT_RESULT_CODE_SHARETEXT_SUCCESS = 1;

    // shareapp 選項
    public final static int    OPTION_SHAREAPP                   = 0;
    public final static int    OPTION_SHOWQRCODE                 = 1;
    public final static int    OPTION_SEARCHMARKET               = 2;
    // public final static int OPTION_ADDTOBOOKMARK = 3;
    public final static int    OPTION_LAUNCHAPP                  = 4;
    public final static int    OPTION_UNINSTALLAPP               = 5;

    // shareapp for bookmark 選項
    public final static int    OPTION_BOOKMARK_SHAREAPP          = 0;
    public final static int    OPTION_BOOKMARK_SHOWQRCODE        = 1;
    public final static int    OPTION_BOOKMARK_SEARCHMARKET      = 2;

    // menu key
    public final static int    MENU_SHARETEXT                    = 0;

    // share app extra key
    public final static String KEY_EXTRA_SMS_BODY                = "sms_body";

    // service key
    public final static String KEY_SERVICE_PACKAGENAME           = "packagename";
    public final static String KEY_SERVICE_APPNAME               = "name";

    // cmd
    public final static String CMD_UPLOAD_SHAREAPP               = "uploadshareapp";
    public final static String CMD_GET_SHAREAPPRANKBOARD         = "getboard";

    // 與server一樣的key
    public final static String PARAM_APPTYPE                     = "apptype";
    public final static String PARAM_RANGE                       = "range";
    public final static String APPTYPE_APPLICATION               = "APPLICATION";
    public final static String APPTYPE_GAME                      = "GAME";

    public final static String DEFALT_RANGE_VALUE                = "0";

    // rank key,從server接回的資料,資料內的key
    public final static String KEY_RANK_APPNAME                  = "ApName";
    public final static String KEY_RANK_PACKAGENAME              = "PackageName";
    public final static String KEY_RANK_SHARETIME                = "ShareTime";
    public final static String KEY_RANK_ICONDATA                 = "IconData";
    public final static String KEY_RANK_CREATOR                  = "Creator";
    public final static String KEY_RANK_ICONDATA_BYTES           = "bytes";
    public final static String KEY_RANK_RANK                     = "Rank";

    // handler state
    public final static int    NET_UPLOAD_SHAREAPP_SUCCESS       = 0;
    public final static int    NET_UPLOAD_SHAREAPP_FAIL          = 1;
    public final static int    NET_GET_SHAREAPPBOARD_SUCCESS     = 2;
    public final static int    NET_GET_SHAREAPPBOARD_FAIL        = 3;

    // net 用
    public final static String KEY_STATE_SUCCESS                 = "success";
    public final static String KEY_STATE_FAIL                    = "fail";
    public final static String KEY_AP                            = "ap";
    public final static String KEY_CMD                           = "cmd";
    public final static String KEY_REQUEST_URL                   = "url";
    public final static String AP_NAME                           = "shareapp";
    public final static String HANDLER                           = "handler";
    public final static String PARAMS                            = "params";
    public static final String HTTP_ACCEPT_ENCODING              = "Accept-Encoding";
    public static final String RESPONSE_BODY                     = "source";
    public static final String KEY_ERROR_MSG                     = "msg";

    // shareapp barcode folder path
    public final static String BARCODE_FOLDER_PATH               = "/sdcard/.shareapp";
    public final static String PREF_BARCODE_PATH                 = "file://";

    public final static String BARCODE_GEN_URL                   = "http://chart.apis.google.com/chart?cht=qr&";
    public final static String BARCODE_SIZE_NORMAL               = "chs=200x200&";
    public final static String BARCODE_PREF_EXTRA_PARAM          = "chl=market://details?id=";
    public final static String BARCODE_PREF_EXTRA_PARAM_CHL      = "chl=";
    public final static String BARCODE_PREF_MARKET_URL           = "market://details?id=";
    public final static String BARCODE_PREF_MARKET_HTTP_URL      = "http://market.android.com/search?q=pname:";

    // http://chart.apis.google.com/chart?cht=qr&chs=200x200&chl=market://details?id=com.rickystyle.crazycaller.free

    // bookmark preference prefkey
    public final static String PREF_BOOKMARK_KEY                 = "bookmark_";

    public static final int    DEVICE_LARGE_WIDTH                = 854;
    public static final int    DEVICE_NORMAL_WIDTH               = 480;
    public static final int    DEVICE_SMALL_WIDTH                = 320;
    public static final int    DEVICE_SCREEN_LARGE               = 1;
    public static final int    DEVICE_SCREEN_NORMAL              = 2;
    public static final int    DEVICE_SCREEN_SMALL               = 3;

    // share app option選項對應的圖,有新增選項要在此和array新增
    public final static int[]  SHAREAPP_OPTION_ICONS             = { R.drawable.icon_shareapp, R.drawable.icon_qrcode, R.drawable.icon_market,
            R.drawable.icon_addbookmark, R.drawable.icon_launchapp, R.drawable.icon_uninstallapp };

    // share app icon的底圖,有新增底圖要在此新增
    public final static int[]  SHAREAPP_ICONS_BG                 = { R.drawable.icon_bg1, R.drawable.icon_bg2, R.drawable.icon_bg3, R.drawable.icon_bg4,
            R.drawable.icon_bg5, R.drawable.icon_bg6, R.drawable.icon_bg7 };

    // Ad info
    public static final String AD_APP_NAME                       = "ShareApp";
    public static final String AD_COMPANY_NAME                   = "DreamPlatform";
    public static final String AD_CLIENT_ID                      = "ca-mb-app-pub-8328208634671238";
    public static final String AD_KEYWORD                        = "game";
    public static final String AD_CHANNELID                      = "3383304965";

    // rating
    public final static String PREF_KEY_RATINGSTATE              = "rating";
    public final static String PREF_KEY_PLAYTIME                 = "playtime";

    // filter 掉 分享快樂的資料
    public final static String FILTER_NAME_SHAREAPP              = "com.rickystyle.shareapp";

    // analytics
    public final static String ANALYTICS_ID                      = "UA-17805573-4";

    public final static String ANALYTICS_CATEGORY_SHAREAPP       = "ShareApp";
    public final static String ANALYTICS_CATEGORY_SHARERANK      = "ShareRank";
    public final static String ANALYTICS_CATEGORY_BOOKMARK       = "Bookmark";
    public final static String ANALYTICS_ACTION_CLICK            = "Click";

    public final static String ANALYTICS_LABEL_SHAREAPP          = "Shareapp";
    public final static String ANALYTICS_LABEL_DISPLAYQRCODE     = "DisplayQR";
    public final static String ANALYTICS_LABEL_SEARCHMARKET      = "SearchMarket";
    public final static String ANALYTICS_LABEL_ADDBOOKMARK       = "AddBookmark";
    public final static String ANALYTICS_LABEL_LAUNCHAPP         = "LaunchApp";
    public final static String ANALYTICS_LABEL_REMOVEAPP         = "RemoveApp";

    public final static int    ANALYTICS_VALUE_YES               = 1;
    public final static int    ANALYTICS_VALUE_NO                = 0;
}
