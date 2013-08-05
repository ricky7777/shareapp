package com.rickystyle.shareapp.free.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.net.NetManager;
import com.rickystyle.shareapp.free.tool.BarcodeManager;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.ShareAppTool;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * ShareApp APP Rank
 * 
 * @author Ricky
 */
public class ShareRankAppActivity extends BaseRankActivity {
    private AlertDialog.Builder appChooseAlert;
    private SimpleAdapter       shareOptionAdapter;
    private int                 currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sharerank);

        // ready shareOptionAdapter
        String[] optionDataFrom = new String[] { ShareAppConsts.KEY_SHAREAPP_OPTION_ICONID, ShareAppConsts.KEY_SHAREAPP_OPTION_DESC };
        int[] optionDataTo = new int[] { R.id.IV_saoption_icon, R.id.TV_saoption_text };
        // bookmark 的選項和rank的選項一樣,所以共用array
        List<Map<String, Object>> optionData = ShareAppTool.getShareAppOptionData(getResources(), R.array.shareapp_bookmark_option_text);
        shareOptionAdapter = new SimpleAdapter(this, optionData, R.layout.shareappoptionitem, optionDataFrom, optionDataTo);

        // makeApp choose Alert
        makeAppChooseAlert();

        appList = (ListView) findViewById(R.id.LV_appList);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
                currentPosition = position;
                appChooseAlert.show();
            }
        });
        NetManager.getInstance().getShareAppRankBoard(handler, ShareAppConsts.APPTYPE_APPLICATION, ShareAppConsts.DEFALT_RANGE_VALUE);
    }

    public void makeAppChooseAlert() {
        appChooseAlert = new AlertDialog.Builder(this);
        appChooseAlert.setAdapter(shareOptionAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShareAppDialog.Builder shareAlert = new ShareAppDialog.Builder(ShareRankAppActivity.this);
                HashMap<String, Object> info = (HashMap<String, Object>) appData.get(currentPosition);
                String packageName = info.get(ShareAppConsts.KEY_RANK_PACKAGENAME) + "";
                String labelName = info.get(ShareAppConsts.KEY_RANK_APPNAME) + "";
                switch (which) {
                case ShareAppConsts.OPTION_BOOKMARK_SHAREAPP:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHARERANK, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SHAREAPP,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareapp(getResources(), ShareRankAppActivity.this, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_BOOKMARK_SHOWQRCODE:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHARERANK, ShareAppConsts.ANALYTICS_ACTION_CLICK,
                            ShareAppConsts.ANALYTICS_LABEL_DISPLAYQRCODE, ShareAppConsts.ANALYTICS_VALUE_YES);
                    ShareAppTool.shareQR(ShareRankAppActivity.this, shareAlert, packageName, labelName);
                    break;
                case ShareAppConsts.OPTION_BOOKMARK_SEARCHMARKET:
                    trackEvent(ShareAppConsts.ANALYTICS_CATEGORY_SHARERANK, ShareAppConsts.ANALYTICS_ACTION_CLICK, ShareAppConsts.ANALYTICS_LABEL_SEARCHMARKET,
                            ShareAppConsts.ANALYTICS_VALUE_YES);
                    String url = BarcodeManager.getInstance().getBarcodeURLInMarket(packageName);
                    IntentTool.launchWeb(ShareRankAppActivity.this, url);
                    break;
                }
            }
        });
    }

    @Override
    protected String getGoogleAnalyticsAccountId() {
        return ShareAppConsts.ANALYTICS_ID;
    }

}
