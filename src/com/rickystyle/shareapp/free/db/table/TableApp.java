package com.rickystyle.shareapp.free.db.table;

import android.provider.BaseColumns;

/**
 * 使用者手機內的遊戲列表table
 * 
 * @author Ricky
 * 
 */
public class TableApp implements BaseColumns {

    public final static String TABLE_NAME      = "appInfo";

    public final static String APPNAME         = "appName";
    public final static String PACKAGENAME     = "packageName";
    public final static String LAST_LAUNCHTIME = "lastLaunchTime";
    public final static String IMAGE_ICON      = "iconImage";
    public final static String IMAGE_BARCODE   = "barcodeImage";

    public static final String CREATE_SQL      = "CREATE TABLE " + TABLE_NAME + //
                                                       "(" + //
                                                       _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //
                                                       APPNAME + " varchar, " + //
                                                       PACKAGENAME + " varchar, " + //
                                                       IMAGE_ICON + " image," + //
                                                       IMAGE_BARCODE + " image," + //
                                                       LAST_LAUNCHTIME + " Smalldatetime" + //
                                                       ")";

    public static final String DROP_SQL        = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
