package com.rickystyle.shareapp.free.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.service.ShareAppService;
import com.rickystyle.shareapp.free.tool.BarcodeManager;
import com.rickystyle.shareapp.free.tool.FileComparator;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.ShareAppTool;
import com.rickystyle.shareapp.free.tool.StringUtils;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * ShareApp主要邏輯<br/>
 * 1.list install app<br/>
 * 2.share app<br/>
 * 
 * @author Ricky
 */
public class ShareAppActivity extends BaseActivity {
    private GridView              shareappGrid;
    private PackageManager        mPm;
    private LayoutInflater        mInflater;
    private List<ApplicationInfo> mApps;
    private FileComparator        pnComparator;
    private AlertDialog.Builder   appChooseAlert;
    private SimpleAdapter         shareOptionAdapter;
    private int                   currentPosition;
    private AppsAdapter           appsAdapter;
    private File                  path;
    private TextView              imTextView;
    private String                internalMemoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.shareapp);
        imTextView = (TextView) findViewById(R.id.TV_internal_memory);
        path = Environment.getDataDirectory();
        internalMemoryText = getString(R.string.im_text);

        // make shareapp data
        pnComparator = new FileComparator();
        mPm = getPackageManager();
        mInflater = LayoutInflater.from(this);

        // load appdata
        loadApps();

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

        // anim
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(150);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        shareappGrid.setLayoutAnimation(controller);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInternalMemory();
        loadApps();
        appsAdapter.notifyDataSetChanged();
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
     * 載入app data,並且sort
     */
    private void loadApps() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ApplicationInfo> appList = mPm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        mApps = new ArrayList<ApplicationInfo>();
        // filter
        for (ApplicationInfo appInfo : appList) {
            boolean flag = false;
            if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                // Updated system app
                flag = true;
            } else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // Non-system app
                flag = true;
            }
            if (flag) {
                mApps.add(appInfo);
            }
        }

        // sort
        Collections.sort(mApps, pnComparator);

        // DebugTool.printVLog("total app:" + appList.size() + ",filter after:" + mApps.size());
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
                ShareAppDialog.Builder shareAlert = new ShareAppDialog.Builder(ShareAppActivity.this);
                ApplicationInfo info = mApps.get(currentPosition);
                String packageName = info.packageName;
                String labelName = info.loadLabel(mPm) + "";
                switch (which) {
                case ShareAppConsts.OPTION_SHAREAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SHAREAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareapp(getResources(), ShareAppActivity.this, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_SHOWQRCODE:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_DISPLAYQRCODE,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareQR(ShareAppActivity.this, shareAlert, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_SEARCHMARKET:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SEARCHMARKET,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    String url = BarcodeManager.getInstance().getBarcodeURLInMarket(packageName);
                    IntentTool.launchWeb(ShareAppActivity.this, url);
                    break;
                case ShareAppConsts.OPTION_ADDTOBOOKMARK:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_ADDBOOKMARK,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    // BarcodeManager.getInstance().addAppBookmark(info, ShareAppActivity.this, mPm);
                    Toast.makeText(getApplicationContext(), R.string.bookmark_add_success, Toast.LENGTH_SHORT).show();
                    break;
                case ShareAppConsts.OPTION_LAUNCHAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_LAUNCHAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    IntentTool.launchAnotherApp(ShareAppActivity.this, packageName, mPm);
                    break;
                case ShareAppConsts.OPTION_UNINSTALLAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHAREAPP, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_REMOVEAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    IntentTool.deleteAnotherApp(ShareAppActivity.this, packageName);
                    break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ShareAppConsts.ACT_RESULT_CODE_SHARE_SUCCESS) {
            ApplicationInfo info = mApps.get(currentPosition);
            Intent intent = new Intent(ShareAppActivity.this, ShareAppService.class);
            intent.putExtra(ShareAppConsts.KEY_SERVICE_PACKAGENAME, info.packageName);
            intent.putExtra(ShareAppConsts.KEY_SERVICE_APPNAME, info.loadLabel(mPm).toString());
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

            ApplicationInfo info = mApps.get(position);
            Drawable icon = info.loadIcon(mPm);
            String label = info.loadLabel(mPm).toString();

            holder.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.icon.setImageDrawable(icon);
            holder.icon.setBackgroundResource(ShareAppConsts.SHAREAPP_ICONS_BG[(int) (Math.random() * 7)]);
            if (StringUtils.isBlankorNull(label)) {
                label = info.name;
            }
            holder.text.setText(label);
            return convertView;
        }

        public final int getCount() {
            return mApps.size();
        }

        public final Object getItem(int position) {
            return mApps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    static class ViewHolder {
        TextView  text;
        ImageView icon;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean keyDown = super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyDown = false;
        }

        return keyDown;
    }

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
