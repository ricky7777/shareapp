package com.rickystyle.shareapp.free.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * <pre>
 * 1.extends MoaibotAnalyticsActivity 時需實作 getGoogleAnalyticsAccountId
 * 2.使用時需至https://www.google.com/analytics/
 *   申請自己project的domain,Ex: http://xxx.moaicity.com/ (xxx為project name)
 * 3.申請完後 報表欄位 即是網頁資源編號(GoogleAnalyticsAccountId)
 * </pre>
 * 
 * @author Jack Chiang
 * 
 */
abstract public class AnalyticsActivity extends Activity {
    protected static final String  KEYWORD_FREE = "Free";
    protected static final String  KEYWORD_PAID = "Paid";
    private GoogleAnalyticsTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 Google Analytics
        final String googleAnalyticsAccountId = getGoogleAnalyticsAccountId();
        if (!TextUtils.isEmpty(googleAnalyticsAccountId)) {
            tracker = GoogleAnalyticsTracker.getInstance();
            tracker.start(getGoogleAnalyticsAccountId(), 60, this);

            // 追蹤
            StringBuilder page = new StringBuilder("/").append(getClass().getSimpleName()).append("_");
            // if (SysUtils.isFreeEdition(getApplicationContext())) {
            // page.append(KEYWORD_FREE);
            // } else {
            // page.append(KEYWORD_PAID);
            // }
            tracker.trackPageView(page.toString());
        }
    }

    /**
     * 回傳 Google Analytics 中的 "網頁資源編號", ex: "UA-20011933-2"
     * 
     * @return
     */
    protected abstract String getGoogleAnalyticsAccountId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tracker != null) {
            tracker.stop();
            tracker = null;
        }
    }

    public void trackEvent(String category, String action, String label, int value) {
        if (tracker != null) {
            tracker.trackEvent(category, action, label, value);
        }
    }

    public void trackPageView(String pageUrl) {
        if (tracker != null) {
            tracker.trackPageView(pageUrl);
        }
    }

}
