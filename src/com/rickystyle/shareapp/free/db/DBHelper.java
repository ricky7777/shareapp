package com.rickystyle.shareapp.free.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rickystyle.shareapp.free.bean.AppInfoBean;
import com.rickystyle.shareapp.free.db.table.TableApp;
import com.rickystyle.shareapp.free.tool.LogUtils;

/**
 * db helper
 * 
 * @author Ricky
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
    private final ArrayList<AppInfoBean> appList;
    private final static String          DBNAME     = "shareapp.db";
    private final static int             DB_VERSION = 1;

    private final SimpleDateFormat       sdf;

    public DBHelper(Context context) {
        super(context, DBNAME, null, DB_VERSION);
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        appList = new ArrayList<AppInfoBean>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // craete table
        db.execSQL(TableApp.CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TableApp.DROP_SQL);
    }

    /**
     * 插入一筆遊戲資料
     * 
     * @param tableName
     * @param appInfo
     */
    public void insertApp(AppInfoBean appInfo) {
        String packageName = appInfo.getPackageName();
        // 先檢查table內有無此app,沒有才插入
        AppInfoBean appInfoBean = queryApp(packageName);
        if (appInfoBean != null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableApp.PACKAGENAME, packageName);

        String appName = appInfo.getAppName();
        values.put(TableApp.APPNAME, appName);

        String launchTime = sdf.format(appInfo.getLastLaunchtime());
        values.put(TableApp.LAST_LAUNCHTIME, launchTime);

        values.put(TableApp.IMAGE_ICON, appInfo.getIcon());

        db.insert(TableApp.TABLE_NAME, null, values);
    }

    /**
     * 用packageName query app,若此table沒有此筆資料,回傳null
     * 
     * @param packageName
     * @return
     */
    public AppInfoBean queryApp(String packageName) {
        SQLiteDatabase db = getReadableDatabase();

        String selection = TableApp.PACKAGENAME + "= '" + packageName + "'";
        // return db.rawQuery(selection, null);
        Cursor appCursor = db.query(TableApp.TABLE_NAME, null, selection, null, null, null, null);
        if (appCursor.getCount() == 0) {
            return null;
        }

        // return a appInfo bean
        appCursor.moveToFirst();
        return dataToAppInfoBean(appCursor);
    }

    public boolean updateApp(AppInfoBean appInfo) {
        // AppInfoBean app = queryApp(packageName);

        // String table, ContentValues values, String whereClause, String[] whereArgs
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        String launchTime = sdf.format(appInfo.getLastLaunchtime());
        values.put(TableApp.APPNAME, appInfo.getAppName());
        values.put(TableApp.PACKAGENAME, appInfo.getPackageName());
        values.put(TableApp.LAST_LAUNCHTIME, launchTime);

        String where = "packageName=\"" + appInfo.getPackageName() + "\"";
        int rowIndex = db.update(TableApp.TABLE_NAME, values, where, null);
        LogUtils.d(this, "update %1$s, row:%2$s", appInfo.getPackageName(), rowIndex);
        return false;
    }

    /**
     * 取回appTable的所有遊戲,並且轉成appInfoBean
     * 
     * @return
     */
    public ArrayList<AppInfoBean> queryAllApp() {
        appList.clear();

        SQLiteDatabase db = getReadableDatabase();
        String orderBy = TableApp.LAST_LAUNCHTIME + " desc";

        Cursor appCursor = db.query(TableApp.TABLE_NAME, null, null, null, null, null, orderBy);

        while (appCursor.moveToNext()) {
            AppInfoBean appInfo = dataToAppInfoBean(appCursor);

            appList.add(appInfo);
        }

        return appList;
    }

    public void deleteApp(String packageName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = TableApp.PACKAGENAME + "='" + packageName + "'";
        db.delete(TableApp.TABLE_NAME, where, null);
    }

    /**
     * 看一下app table的數量
     * 
     * @return
     */
    public int queryAppCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor appCursor = db.query(TableApp.TABLE_NAME, null, null, null, null, null, null);
        return appCursor.getCount();
    }

    private AppInfoBean dataToAppInfoBean(Cursor appRow) {
        String appName = appRow.getString(appRow.getColumnIndex(TableApp.APPNAME));
        String packageName = appRow.getString(appRow.getColumnIndex(TableApp.PACKAGENAME));
        String time = appRow.getString(appRow.getColumnIndex(TableApp.LAST_LAUNCHTIME));
        byte[] iconByte = appRow.getBlob(appRow.getColumnIndex(TableApp.IMAGE_ICON));
        Date launchTime = new Date();
        try {
            launchTime = sdf.parse(time);
        } catch (ParseException e) {
            LogUtils.d(this, "query app parse date tiem:%1$s,error:%2$s", time, e.getMessage());
        }

        return new AppInfoBean(appName, packageName, launchTime, iconByte);
    }

}
