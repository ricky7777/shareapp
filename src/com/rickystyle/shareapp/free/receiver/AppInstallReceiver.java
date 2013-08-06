package com.rickystyle.shareapp.free.receiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.rickystyle.shareapp.free.bean.AppInfoBean;
import com.rickystyle.shareapp.free.db.DBHelper;
import com.rickystyle.shareapp.free.tool.LogUtils;

/**
 * 當有app安裝或移除時的receiver
 * 
 * @author Ricky
 * 
 */
public class AppInstallReceiver extends BroadcastReceiver {
    private final static String tag            = AppInstallReceiver.class.getSimpleName();

    private final static String PACKAGE_PREFIX = "package:";
    private Context             context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        Log.i(tag, "share app action:" + action);
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) { // install
            String packageName = intent.getDataString();
            packageName = packageName.substring(PACKAGE_PREFIX.length());
            Log.i(tag, "安裝了 :" + packageName);
            DBHelper dbHelper = new DBHelper(context);
            AppInfoBean app = dbHelper.queryApp(packageName);
            if (app == null) {
                // 表示第一次安裝此app,insert app
                // Thread t = new Thread(new QueryRunnable(dbHelper, packageName));
                // t.start();
                PackageManager packageMgr = context.getPackageManager();
                ApplicationInfo appInfo;
                try {
                    appInfo = packageMgr.getApplicationInfo(packageName, 0);

                    String appName = appInfo.loadLabel(packageMgr).toString();
                    String lastLaunchTime = String.valueOf(getLastLaunchTime(packageName));
                    Date currentTime = new Date();
                    currentTime.setTime(Long.valueOf(lastLaunchTime));

                    Drawable icon = appInfo.loadIcon(packageMgr);
                    Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] iconByte = stream.toByteArray();

                    AppInfoBean newApp = new AppInfoBean(appName, packageName, currentTime, iconByte);
                    dbHelper.insertApp(newApp);
                } catch (NameNotFoundException e) {
                    Log.e(tag, "insert app error", e);
                }
            }
        }

        if (action.equals(Intent.ACTION_UNINSTALL_PACKAGE) || action.equals(Intent.ACTION_PACKAGE_REMOVED)) { // uninstall
            String packageName = intent.getDataString();
            packageName = packageName.substring(PACKAGE_PREFIX.length());
            Log.i(tag, "卸載了 :" + packageName);

            DBHelper dbHelper = new DBHelper(context);
            AppInfoBean app = dbHelper.queryApp(packageName);
            if (app != null) {
                // 從table delet此筆game
                dbHelper.deleteApp(packageName);
            } else {
                // 跑到這表示bug,沒抓到此game

            }
        }
    }

    /**
     * 取得此app的lastLaunchTime<br/>
     * 目前規則<br/>
     * 1.取cache time<br/>
     * 2.取data source
     * 
     * @param packageName
     * @return
     */
    private long getLastLaunchTime(String packageName) {
        // cache time
        try {
            Context otherAppContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            File cacheFile = otherAppContext.getCacheDir();
            if (cacheFile != null) {
                return cacheFile.lastModified();
            } else {
                // 換取info dataSource
                PackageManager packageMgr = context.getPackageManager();
                ApplicationInfo info = packageMgr.getApplicationInfo(packageName, 0);
                File dataDirFile = new File(info.dataDir);
                return dataDirFile.lastModified();
            }
        } catch (Exception ex) {
            LogUtils.d(this, "getLaunchTime error:%1$s", ex);
        }
        return 0;
    }

    /**
     * query web,問看此packageName是否為遊戲
     * 
     * @author Ricky
     * 
     */
    // private class QueryRunnable implements Runnable {
    // private final DbHelper dbHelper;
    // private final String packageName;
    //
    // public QueryRunnable(DbHelper dbHelper, String packageName) {
    // this.dbHelper = dbHelper;
    // this.packageName = packageName;
    // }
    //
    // @Override
    // public void run() {
    // queryCategory(dbHelper, packageName);
    // }
    //
    // private void queryCategory(DbHelper dbHelper, String packageName) {
    // String queryAppUrl = String.format(ApplayConsts.URL_APPAWARD, packageName);
    // Log.i(tag, "queryAppUrl:" + queryAppUrl);
    // String appData = HttpUtils.getData(queryAppUrl);
    // JSONObject resultJSONObj = HttpUtils.getJSONObject(appData);
    //
    // // 判斷結果
    // try {
    // int resultCount = resultJSONObj.getInt(AppInfoRunnable.JSON_KEY_NUMBER_RESULT);
    //
    // if (resultCount > 0) {
    // // 直接取出第一個
    // JSONObject appObj = resultJSONObj.getJSONArray(AppInfoRunnable.JSON_KEY_ITEMS).getJSONObject(0);
    // String appCategory = appObj.getString(AppInfoRunnable.JSON_KEY_CATEGORY);
    // for (String category : AppInfoRunnable.GAME_CATEGORYS) {
    // if (appCategory.equals(category)) {
    // // insert進db,時間用現在的
    // String gameName = appObj.getString(AppInfoRunnable.JSON_KEY_NAME);
    //
    // Date launchTime = new Date();
    //
    // Log.i(tag, "準備插入資料 name:" + gameName + "packageName:" + packageName + "time:" + launchTime);
    // GameInfoBean bean = new GameInfoBean(gameName, packageName, launchTime);
    // bean.setCategory(category);
    // dbHelper.insertGame(bean);
    // break;
    // }
    // }
    // }
    //
    // } catch (JSONException e) {
    // Log.i(tag, "解析安裝的遊戲時發生錯誤");
    // }
    // }
    //
    // }

}
