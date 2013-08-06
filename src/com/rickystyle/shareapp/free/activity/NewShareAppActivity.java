package com.rickystyle.shareapp.free.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.bean.AppInfoBean;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.db.DBHelper;
import com.rickystyle.shareapp.free.service.ShareAppService;
import com.rickystyle.shareapp.free.tool.BarcodeManager;
import com.rickystyle.shareapp.free.tool.FileComparator;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.LogUtils;
import com.rickystyle.shareapp.free.tool.ShareAppTool;
import com.rickystyle.shareapp.free.tool.StringUtils;
import com.rickystyle.shareapp.free.widget.LoadingProcess;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * ShareApp主要邏輯<br/>
 * 1.list install app<br/>
 * 2.share app<br/>
 * 
 * @author Ricky
 */
public class NewShareAppActivity extends BaseActivity {
    private GridView               shareappGrid;
    private PackageManager         mPm;
    private LayoutInflater         mInflater;
    // private List<ApplicationInfo> mApps;
    // private FileComparator pnComparator;
    private AlertDialog.Builder    appChooseAlert;
    private SimpleAdapter          shareOptionAdapter;
    private int                    currentPosition;
    private AppsAdapter            appsAdapter;
    private File                   path;
    private TextView               imTextView;
    private String                 internalMemoryText;

    private DBHelper               dbHelper;

    private ArrayList<AppInfoBean> appInfos;

    // 優先的app list
    private ArrayList<String>      priorityApplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPm = getPackageManager();
        mInflater = LayoutInflater.from(this);
        appInfos = new ArrayList<AppInfoBean>();
        priorityApplist = new ArrayList<String>();

        LoadingProcess.dialogShow(this);

        setContentView(R.layout.shareapp);

        setMemoryInfo();

        loadApps();

        showApps();

        LoadingProcess.dialogDismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInternalMemory();
        // loadApps();
        // appsAdapter.notifyDataSetChanged();
    }

    private String formatSize(long size) {
        return Formatter.formatFileSize(this, size);
    }

    private void updateInternalMemory() {
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        imTextView.setText(internalMemoryText + formatSize(availableBlocks * blockSize));
    }

    /**
     * 設定memory相關資訊
     */
    private void setMemoryInfo() {
        imTextView = (TextView) findViewById(R.id.TV_internal_memory);
        path = Environment.getDataDirectory();
        internalMemoryText = getString(R.string.im_text);
    }

    /**
     * 載入app data,並且sort
     */
    private void loadApps() {
        dbHelper = new DBHelper(this);

        // SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
        // writeDB.execSQL(TableApp.DROP_SQL);
        // writeDB.execSQL(TableApp.CREATE_SQL);
        int appCount = dbHelper.queryAppCount();

        if (appCount == 0) {
            long start = System.currentTimeMillis();
            loadAppDataFromDevice();
            LogUtils.d(this, "load app from device time:%1$s", (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            recordAppToDB();
            LogUtils.d(this, "record app to db time:%1$s", (System.currentTimeMillis() - start));
        } else {
            // 更新一下app launchtime

            long start = System.currentTimeMillis();
            loadAppFromDB();
            LogUtils.d(this, "load app from db time:%1$s", (System.currentTimeMillis() - start));
        }

        // copy file for debug
        // SQLiteDatabase db = dbHelper.getReadableDatabase();
        // FileUtils.copyFile(db.getPath(), "/sdcard/shareapp.db");
    }

    /**
     * 第一次載入app資料,從device載入
     * 
     * @throws Exception
     */
    private void loadAppDataFromDevice() {
        // make shareapp data
        FileComparator pnComparator = new FileComparator();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ApplicationInfo> appList = mPm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        // mApps = new ArrayList<ApplicationInfo>();
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        // filter
        try {
            refreshTaskApp();

            for (ApplicationInfo appInfo : appList) {
                boolean flag = false;
                if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                    // Updated system app
                    flag = false;
                }

                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // Non-system app
                    flag = true;
                }

                if (flag) {
                    String appName = appInfo.loadLabel(mPm).toString();
                    String packageName = appInfo.packageName;

                    if (packageName.equals(getPackageName())) {
                        continue;
                    }

                    String lastLaunchTime = String.valueOf(getLastLaunchTime(packageName));
                    Date currentTime = new Date();
                    if (!priorityApplist.contains(packageName)) {
                        currentTime.setTime(Long.valueOf(lastLaunchTime));
                    } else {
                        LogUtils.d(this, "%1$s 在task內:%2$s", appName, currentTime);
                    }
                    // String launchTimeStr = sdf.format(currentTime);

                    Drawable icon = appInfo.loadIcon(mPm);
                    Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    AppInfoBean app = new AppInfoBean(appName, packageName, currentTime, bitmapdata);
                    appInfos.add(app);

                    // mApps.add(appInfo);
                }
            }
        } catch (Exception ex) {
            LogUtils.d(this, "parse app exception:%1$s", ex.getMessage());
        }

        // sort
        Collections.sort(appInfos, pnComparator);
    }

    /**
     * 從db載入app
     */
    private void loadAppFromDB() {
        long start = System.currentTimeMillis();
        appInfos = dbHelper.queryAllApp();

        start = System.currentTimeMillis();
        refreshTaskApp();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        // 更新db內所有app的時間
        start = System.currentTimeMillis();
        for (AppInfoBean app : appInfos) {
            String packageName = app.getPackageName();
            if (priorityApplist.contains(packageName)) {
                // 將task app 啟動時間更新為now
                app.setLastLaunchTime(new Date());
                dbHelper.updateApp(app);
            } else {
                // long start2 = System.currentTimeMillis();
                // long lastLaunchTime = getLastLaunchTime(packageName);
                // LogUtils.d(this, "getLastTime:%1$s", (System.currentTimeMillis() - start2));
                //
                // Date date = new Date();
                // date.setTime(lastLaunchTime);
                // String newDateStr = sdf.format(date);
                // try {
                // date = sdf.parse(newDateStr);
                // } catch (ParseException e) {
                // LogUtils.d(this, "load app from db error:%1$s", e.getMessage());
                // }
                //
                // if (!app.getLastLaunchtime().equals(date)) {
                // app.setLastLaunchTime(date);
                // dbHelper.updateApp(app);
                // }
            }
        }
        LogUtils.d(this, "update app time:%1$s", (System.currentTimeMillis() - start));

        FileComparator pnComparator = new FileComparator();
        Collections.sort(appInfos, pnComparator);
    }

    /**
     * 更新task的app
     */
    private void refreshTaskApp() {
        priorityApplist.clear();
        ActivityManager activityMrg = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RecentTaskInfo> taskList = activityMrg.getRecentTasks(20, 0);
        for (RecentTaskInfo rti : taskList) {
            ResolveInfo ri = mPm.resolveActivity(rti.baseIntent, 0);
            if (ri != null) {
                String packageName = ri.activityInfo.packageName;
                // 過濾掉自己
                if (!packageName.equals(getPackageName())) {
                    priorityApplist.add(packageName);
                }
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
            Context otherAppContext = createPackageContext(packageName, CONTEXT_IGNORE_SECURITY);
            File cacheFile = otherAppContext.getCacheDir();
            if (cacheFile != null) {
                return cacheFile.lastModified();
            } else {
                // 換取info dataSource
                ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, 0);
                File dataDirFile = new File(info.dataDir);
                return dataDirFile.lastModified();
            }
        } catch (Exception ex) {
            LogUtils.d(this, "getLaunchTime error:%1$s", ex);
        }

        return 0;
    }

    /**
     * 將app資訊記錄到db
     */
    private void recordAppToDB() {
        for (AppInfoBean app : appInfos) {
            dbHelper.insertApp(app);
        }
    }

    /**
     * 顯示apps
     */
    private void showApps() {
        appsAdapter = new AppsAdapter();
        shareappGrid = (GridView) findViewById(R.id.GV_shareapp);
        shareappGrid.setAdapter(appsAdapter);

        // make shareapp option data
        String[] dataFrom = new String[] { ShareAppConsts.KEY_SHAREAPP_OPTION_ICONID, ShareAppConsts.KEY_SHAREAPP_OPTION_DESC };
        int[] dataTo = new int[] { R.id.IV_saoption_icon, R.id.TV_saoption_text };
        List<Map<String, Object>> optionData = ShareAppTool.getShareAppOptionData(getResources(), R.array.shareapp_option_text);
        shareOptionAdapter = new SimpleAdapter(this, optionData, R.layout.shareappoptionitem, dataFrom, dataTo);

        // make app choose alert
        makeAppChooseAlert();

        shareappGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                currentPosition = position;
                appChooseAlert.show();
            }
        });

        // create barcode save folder
        File folder = new File(ShareAppConsts.BARCODE_FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }

    }

    /**
     * 做出Shareappchoose的Alert<br/>
     * shareapp主邏輯
     */
    public void makeAppChooseAlert() {
        appChooseAlert = new AlertDialog.Builder(this);
        appChooseAlert.setAdapter(shareOptionAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShareAppDialog.Builder shareAlert = new ShareAppDialog.Builder(NewShareAppActivity.this);
                AppInfoBean info = appInfos.get(currentPosition);
                String packageName = info.getPackageName();
                String labelName = info.getAppName();
                switch (which) {
                case ShareAppConsts.OPTION_SHAREAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SHAREAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareapp(getResources(), NewShareAppActivity.this, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_SHOWQRCODE:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_DISPLAYQRCODE,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareQR(NewShareAppActivity.this, shareAlert, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_SEARCHMARKET:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SEARCHMARKET,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    String url = BarcodeManager.getInstance().getBarcodeURLInMarket(packageName);
                    IntentTool.launchWeb(NewShareAppActivity.this, url);
                    break;
                case ShareAppConsts.OPTION_ADDTOBOOKMARK:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_ADDBOOKMARK,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    BarcodeManager.getInstance().addAppBookmark(info, NewShareAppActivity.this, mPm);
                    Toast.makeText(getApplicationContext(), R.string.bookmark_add_success, Toast.LENGTH_SHORT).show();
                    break;
                case ShareAppConsts.OPTION_LAUNCHAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_LAUNCHAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    IntentTool.launchAnotherApp(NewShareAppActivity.this, packageName, mPm);
                    break;
                case ShareAppConsts.OPTION_UNINSTALLAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_REMOVEAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    IntentTool.deleteAnotherApp(NewShareAppActivity.this, packageName);
                    break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ShareAppConsts.ACT_RESULT_CODE_SHARE_SUCCESS) {
            AppInfoBean info = appInfos.get(currentPosition);
            Intent intent = new Intent(NewShareAppActivity.this, ShareAppService.class);
            intent.putExtra(ShareAppConsts.KEY_SERVICE_PACKAGENAME, info.getPackageName());
            intent.putExtra(ShareAppConsts.KEY_SERVICE_APPNAME, info.getAppName());
            IntentTool.startService(this, intent);
            // Toast.makeText(this, R.string.shareapp_success, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 取得apps data
     * 
     * @author Ricky
     */
    public class AppsAdapter extends BaseAdapter {
        ViewHolder holder;

        public AppsAdapter() {
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // DebugTool.printVLog("position:" + position + ",convertView:" + convertView + ",app size:" + mApps.size());

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.shareapp_item, null);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.IV_app_icon);
                holder.text = (TextView) convertView.findViewById(R.id.TV_app_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfoBean info = appInfos.get(position);
            // Drawable icon = info.loadIcon(mPm);
            Bitmap icon = info.getIconBitmap();
            String label = info.getAppName();

            holder.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // holder.icon.setImageDrawable(icon);
            holder.icon.setImageBitmap(icon);
            holder.icon.setBackgroundResource(ShareAppConsts.SHAREAPP_ICONS_BG[(int) (Math.random() * 7)]);
            if (StringUtils.isBlankorNull(label)) {
                label = info.getAppName();
            }
            holder.text.setText(label);
            return convertView;
        }

        public final int getCount() {
            return appInfos.size();
        }

        public final Object getItem(int position) {
            return appInfos.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    static class ViewHolder {
        TextView  text;
        ImageView icon;
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // boolean keyDown = super.onKeyDown(keyCode, event);
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // keyDown = false;
    // }
    //
    // return keyDown;
    // }

    /**
     * shareapp option的adapter<br/>
     * 先備用
     * 
     * @author Ricky
     */
    class ShareAppOptionAdapter extends SimpleAdapter {
        public ShareAppOptionAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView optionDesc = (TextView) view.findViewById(R.id.TV_saoption_text);
            switch (position) {
            }

            return view;
        }
    }

    @Override
    protected String getGoogleAnalyticsAccountId() {
        return ShareAppConsts.ANALYTICS_ID;
    }
}
