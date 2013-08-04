package com.rickystyle.shareapp.free.tool;

import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.rickystyle.shareapp.free.bean.BookMarks;
import com.rickystyle.shareapp.free.bean.BookmarkInfo;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;

/**
 * 偏好設定Utils
 * @author Ricky
 */
public class PreferenceUtil {

    // 取得預設的偏好設定
    private static SharedPreferences getSharedPreferences(Context context) {
	return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveToBookmark(Context context, PackageManager mPm, ApplicationInfo info) {
	Editor editor = getSharedPreferences(context).edit();
	String packageName = ShareAppConsts.PREF_BOOKMARK_KEY + info.packageName;
	String appName = info.loadLabel(mPm).toString();
	editor.putString(packageName, appName);
	editor.commit();
    }

    @SuppressWarnings("unchecked")
    public static List<BookmarkInfo> getBookmarks(Context context) {
	BookMarks bookmark = new BookMarks();
	SharedPreferences shareP = getSharedPreferences(context);
	Map<String, String> map = (Map<String, String>) shareP.getAll();
	Set<String> set = map.keySet();
	for (String key : set) {
	    if (key.startsWith(ShareAppConsts.PREF_BOOKMARK_KEY)) {
		BookmarkInfo appInfo = new BookmarkInfo();
		String appPackageName = key.replace(ShareAppConsts.PREF_BOOKMARK_KEY, "");
		String appName = map.get(key);
		String iconFilePath = ShareAppConsts.BARCODE_FOLDER_PATH + "/" + appPackageName + "_icon.png";
		appInfo.setAppPackageName(appPackageName);
		appInfo.setAppName(appName);
		appInfo.setAppIconFilePath(iconFilePath);
		bookmark.addBookmarkInfo(appInfo);
	    }
	}
	return bookmark.getBookmarkList();
    }

    public static void removeBookmark(Context context, String packageName) {
	packageName = ShareAppConsts.PREF_BOOKMARK_KEY + packageName;
	Editor editor = getSharedPreferences(context).edit();
	editor.remove(packageName);
	editor.commit();
    }

    public static int getSharedInt(Context context, String key) {
	return getSharedPreferences(context).getInt(key, 0);
    }

    public static String getSharedString(Context context, String key) {
	return getSharedPreferences(context).getString(key, "");
    }

    public static Boolean getSharedBoolean(Context context, String key) {
	return getSharedPreferences(context).getBoolean(key, false);
    }

    public static void saveSharedInt(Context context, String key, int value) {
	Editor editor = getSharedPreferences(context).edit();
	editor.putInt(key, value);
	editor.commit();
    }

    public static void saveSharedString(Context context, String key, String value) {
	Editor editor = getSharedPreferences(context).edit();
	editor.putString(key, value);
	editor.commit();
    }

    public static void saveSharedBoolean(Context context, String key) {
	Editor editor = getSharedPreferences(context).edit();
	editor.putBoolean(key, true);
	editor.commit();
    }

    /**
     * 取得是否給評價
     * @param context
     * @return
     */
    public static boolean getRatingState(Context context) {
	return getSharedPreferences(context).getBoolean(ShareAppConsts.PREF_KEY_RATINGSTATE, false);
    }

    /**
     * 記錄已經rating
     * @param context
     */
    public static void saveRatingState(Context context) {
	Editor editor = getSharedPreferences(context).edit();
	editor.putBoolean(ShareAppConsts.PREF_KEY_RATINGSTATE, true);
	editor.commit();
    }

    // 取得遊戲次數
    public static int getPlayTime(Context context) {
	return getSharedPreferences(context).getInt(ShareAppConsts.PREF_KEY_PLAYTIME, 0);
    }

    /**
     * 增加遊戲次數
     * @param context
     */
    public static void addPlayTime(Context context) {
	int playtime = getPlayTime(context) + 1;
	Editor editor = getSharedPreferences(context).edit();
	editor.putInt(ShareAppConsts.PREF_KEY_PLAYTIME, playtime);
	editor.commit();
    }

}
