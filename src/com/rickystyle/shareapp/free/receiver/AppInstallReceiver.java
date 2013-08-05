package com.rickystyle.shareapp.free.receiver;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rickystyle.shareapp.free.db.DBHelper;

/**
 * 當有app安裝或移除時的receiver
 * 
 * @author Ricky
 * 
 */
public class AppInstallReceiver extends BroadcastReceiver {
    private final static String tag            = AppInstallReceiver.class.getSimpleName();

    private final static String PACKAGE_PREFIX = "package:";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) { // install
            String packageName = intent.getDataString();
            packageName = packageName.substring(PACKAGE_PREFIX.length());
            Log.i(tag, "安裝了 :" + packageName);
            DBHelper dbHelper = new DBHelper(context);
            GameInfoBean game = dbHelper.queryGame(packageName);
            if (game == null) {
                // 表示第一次安裝此game,跑web query流程
                Thread t = new Thread(new QueryRunnable(dbHelper, packageName));
                t.start();
            }
        }

        if (action.equals(Intent.ACTION_UNINSTALL_PACKAGE) || action.equals(Intent.ACTION_PACKAGE_REMOVED)) { // uninstall
            String packageName = intent.getDataString();
            packageName = packageName.substring(PACKAGE_PREFIX.length());
            Log.i(tag, "卸載了 :" + packageName);

            ApplayDbHelper dbHelper = new ApplayDbHelper(context);
            GameInfoBean game = dbHelper.queryGame(packageName);
            if (game != null) {
                // 從table delet此筆game
                dbHelper.deleteGame(packageName);
            } else {
                // 跑到這表示bug,沒抓到此game

            }
        }
    }

    /**
     * query web,問看此packageName是否為遊戲
     * 
     * @author Ricky
     * 
     */
    private class QueryRunnable implements Runnable {
        private final ApplayDbHelper dbHelper;
        private final String         packageName;

        public QueryRunnable(ApplayDbHelper dbHelper, String packageName) {
            this.dbHelper = dbHelper;
            this.packageName = packageName;
        }

        @Override
        public void run() {
            queryCategory(dbHelper, packageName);
        }

        private void queryCategory(ApplayDbHelper dbHelper, String packageName) {
            String queryAppUrl = String.format(ApplayConsts.URL_APPAWARD, packageName);
            Log.i(tag, "queryAppUrl:" + queryAppUrl);
            String appData = HttpUtils.getData(queryAppUrl);
            JSONObject resultJSONObj = HttpUtils.getJSONObject(appData);

            // 判斷結果
            try {
                int resultCount = resultJSONObj.getInt(AppInfoRunnable.JSON_KEY_NUMBER_RESULT);

                if (resultCount > 0) {
                    // 直接取出第一個
                    JSONObject appObj = resultJSONObj.getJSONArray(AppInfoRunnable.JSON_KEY_ITEMS).getJSONObject(0);
                    String appCategory = appObj.getString(AppInfoRunnable.JSON_KEY_CATEGORY);
                    for (String category : AppInfoRunnable.GAME_CATEGORYS) {
                        if (appCategory.equals(category)) {
                            // insert進db,時間用現在的
                            String gameName = appObj.getString(AppInfoRunnable.JSON_KEY_NAME);

                            Date launchTime = new Date();

                            Log.i(tag, "準備插入資料 name:" + gameName + "packageName:" + packageName + "time:" + launchTime);
                            GameInfoBean bean = new GameInfoBean(gameName, packageName, launchTime);
                            bean.setCategory(category);
                            dbHelper.insertGame(bean);
                            break;
                        }
                    }
                }

            } catch (JSONException e) {
                Log.i(tag, "解析安裝的遊戲時發生錯誤");
            }
        }

    }

}
