package com.rickystyle.shareapp.free.tool;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 顯示時用到的工具
 * 
 * @author Ricky Chen
 * 
 */
public class DisplayUtils {
    /**
     * 取得螢幕寬度
     * 
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 取得螢幕高度
     * 
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 自動依據手機螢幕解析度轉換 DP 到 PX
     * 
     * @param context
     * @param px
     * @return
     */
    public static int dip2Px(Context context, float px) {
        return (int) (px * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
