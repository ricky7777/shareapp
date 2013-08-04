package com.rickystyle.shareapp.free.activity;

import java.lang.reflect.Field;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.bean.DeviceBean;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.tool.DisplayUtils;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.PreferenceUtil;

/**
 * ShareApp的進入點,採用tab做分頁
 * 
 * @author Ricky
 */
public class MainActivity extends ActivityGroup {
    private DeviceBean deviceBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);

        PreferenceUtil.addPlayTime(this);
        // AdManager.setTestDevices(new String[] { "2C96FDE80DFC8A28EB37B666BCB68033" });

        initiationDevice();
        Resources res = getResources();
        final TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
        final TabWidget tabWidget = (TabWidget) findViewById(android.R.id.tabs);

        Field mBottomLeftStrip;
        Field mBottomRightStrip;

        tabs.setup(getLocalActivityManager());
        tabs.addTab(tabs.newTabSpec("tab1")//
                .setIndicator(getString(R.string.tab_text_myapp), res.getDrawable(R.drawable.tab_myapp))//
                .setContent(new Intent(this, ShareAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabs.addTab(tabs.newTabSpec("tab2")//
                .setIndicator(getString(R.string.tab_text_sharerank), res.getDrawable(R.drawable.tab_rank))//
                .setContent(new Intent(this, ShareRankAppActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabs.addTab(tabs.newTabSpec("tab3")//
                .setIndicator(getString(R.string.tab_text_bookmark), res.getDrawable(R.drawable.tab_bookmark))//
                .setContent(new Intent(this, BookmarkActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        tabs.setCurrentTab(0);

        String version = Build.VERSION.RELEASE;

        for (int i = 0; i < tabWidget.getChildCount(); i++) {

            // int height = 48;int width = 45;
            // 设置高度、宽度，不过宽度由于设置为fill_parent，在此对它没效果
            // tabWidget.getChildAt(i).getLayoutParams().height = height;
            // tabWidget.getChildAt(i).getLayoutParams().width = width;

            /**
             * 此方法是為了去掉系統默認底角的顏色<br/>
             * 在 TabWidget中mBottomLeftStrip、mBottomRightStrip 都是私有變數<br/>
             * 但是我们可以通过反射来获取<br/>
             * 2.2版中並沒有這兩個變數 ,所以要分開來處理
             */
            // if (version.equals("2.2")) {
            try {
                mBottomLeftStrip = tabWidget.getClass().getDeclaredField("mBottomLeftStrip");
                mBottomRightStrip = tabWidget.getClass().getDeclaredField("mBottomRightStrip");

                if (!mBottomLeftStrip.isAccessible()) {
                    mBottomLeftStrip.setAccessible(true);
                }
                if (!mBottomRightStrip.isAccessible()) {
                    mBottomRightStrip.setAccessible(true);
                }

                // mBottomLeftStrip.set(tabWidget, Color.BLACK);
                // mBottomRightStrip.set(tabWidget, Color.BLACK);
                mBottomLeftStrip.set(tabWidget, getResources().getDrawable(Color.TRANSPARENT));
                mBottomRightStrip.set(tabWidget, getResources().getDrawable(Color.YELLOW));

            } catch (Exception e) {

            }

            // } else {
            /**
             * Froyo is implementing setters for the bottom tab strip so you will call that setter here
             */
            // tabWidget.setStripEnabled(false);
            // }

            View tabView = tabWidget.getChildAt(i);
            tabView.setBackgroundColor(Color.BLACK);
            // if (tabs.getCurrentTab() == i) {
            // tabView.setBackgroundResource(R.drawable.tab_btn_clk);
            // } else {
            // tabView.setBackgroundResource(R.drawable.tab_btn);
            // }
            ImageView icon = (ImageView) tabView.findViewById(android.R.id.icon);
            // TextView title = (TextView) tabView.findViewById(android.R.id.title);
            LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
            iconParams.addRule(RelativeLayout.ABOVE, android.R.id.title);
            icon.setLayoutParams(iconParams);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = DisplayUtils.dip2Px(getApplicationContext(), 80);
            params.topMargin = 0;
            tabView.setLayoutParams(params);
        }

        /**
         * 當前tab改變時,也把tab background換掉
         */
        // tabs.setOnTabChangedListener(new OnTabChangeListener() {
        // @Override
        // public void onTabChanged(String tabId) {
        // for (int i = 0; i < tabWidget.getChildCount(); i++) {
        // View tabView = tabWidget.getChildAt(i);
        // if (tabs.getCurrentTab() == i) {
        // // tabView.setBackgroundResource(R.drawable.tab_btn_clk);
        // } else {
        // // tabView.setBackgroundResource(R.drawable.tab_btn);
        // }
        // }
        // }
        // });

        // 如果尚未rating,且遊戲次數大於5次,則出現請他評價
        if (PreferenceUtil.getPlayTime(this) > 5) {
            if (!PreferenceUtil.getRatingState(this)) {
                AlertDialog.Builder ratingAlert = new AlertDialog.Builder(this);
                ratingAlert.setMessage(R.string.rating_msg);
                ratingAlert.setPositiveButton(R.string.rating_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtil.saveRatingState(MainActivity.this);
                        String url = ShareAppConsts.RATING_URL_DEMO;
                        if (!ShareAppConsts.DEMO) {
                            url = ShareAppConsts.RATING_URL_FULL;
                        }
                        IntentTool.launchWeb(MainActivity.this, url);
                    }
                });
                ratingAlert.setNegativeButton(R.string.rating_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceUtil.saveRatingState(MainActivity.this);
                    }
                });

                ratingAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // 將返回鎖住 return true
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }).show();
            }
        }

        // Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        // intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        // startActivityForResult(intent, 0);
    }

    /**
     * 初始化Device,將資訊裝進deviceBean內
     */
    private void initiationDevice() {
        deviceBean = DeviceBean.getInstance();
        WindowManager wManager = getWindowManager();
        int screenWidth = wManager.getDefaultDisplay().getWidth();
        int screenHeight = wManager.getDefaultDisplay().getHeight();

        String deviceModel = android.os.Build.MODEL;
        String deviceCode = android.os.Build.DEVICE;

        // 目前螢幕高都與getHeight一樣~所以暫不特別處理
        deviceBean.setCanUseScreenHeight(screenHeight);
        deviceBean.setCanUseScreenWidth(screenWidth);
        deviceBean.setDeviceCode(deviceCode);
        deviceBean.setDeviceModel(deviceModel);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int action = event.getAction();
        // DebugTool.printVLog("main keyCode:" + keyCode + ",action:" + action);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
