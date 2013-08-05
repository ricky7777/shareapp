package com.rickystyle.shareapp.free.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.net.NetManager;
import com.rickystyle.shareapp.free.tool.LogUtils;
import com.rickystyle.shareapp.free.tool.StringUtils;

/**
 * background service<br/>
 * user按分享時會跑background去server記share次數一筆
 * 
 * @author Ricky
 */
public class ShareAppService extends Service {
    Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (intent != null) {
            String appName = intent.getStringExtra(ShareAppConsts.KEY_SERVICE_APPNAME);
            String packageName = intent.getStringExtra(ShareAppConsts.KEY_SERVICE_PACKAGENAME);
            LogUtils.d("AppName:%1$s,packageName:%2$s", appName, packageName);
            if (!(StringUtils.isBlankorNull(appName) || StringUtils.isBlankorNull(packageName))) {
                NetManager.getInstance().uploadShareAppInfo(handler, appName, packageName);
            }
        }
    }

    @Override
    public void onDestroy() {

    }

}
