package com.rickystyle.shareapp.free.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * ShareApp專用tool,透過此tool,分享app出去
 * 
 * @author Ricky
 */
public class ShareAppTool {

    /**
     * 分享此ap
     * 
     * @param res
     * @param act
     * @param packageName
     * @param labelName
     */
    public static void shareapp(Resources res, Activity act, String packageName, String labelName) {
        Uri uri = BarcodeManager.getInstance().getBarcodeURI(packageName);
        String appMarketURL = BarcodeManager.getInstance().getBarcodeURLInHttp(packageName);
        DebugTool.printVLog("packageName:" + packageName + ",uri:" + uri);
        Intent intent = new Intent(Intent.ACTION_SEND);
        String textBody = res.getString(R.string.shareapp_main_body) + " " + labelName + "\n" + res.getString(R.string.shareapp_main_body2) + "\n"
                + appMarketURL;
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TITLE, res.getString(R.string.shareapp_main_title) + labelName);
        intent.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.shareapp_main_title) + labelName);
        intent.putExtra(Intent.EXTRA_TEXT, textBody);
        intent.putExtra(ShareAppConsts.KEY_EXTRA_SMS_BODY, textBody);

        IntentTool.launchChooser(act, intent, res.getString(R.string.app_share_title), ShareAppConsts.ACT_RESULT_CODE_SHARE_SUCCESS);
    }

    /**
     * shareQR code
     * 
     * @param act
     * @param shareAlert
     * @param packageName
     * @param labelName
     */
    public static void shareQR(Activity act, ShareAppDialog.Builder shareAlert, String packageName, String labelName) {
        LayoutInflater factory = LayoutInflater.from(act);
        View textEntryView = factory.inflate(R.layout.showqritem, null);
        ImageView appQRView = (ImageView) textEntryView.findViewById(R.id.IV_qr_img);
        Drawable qrImg = BarcodeManager.getInstance().getBarcodeDrawable(packageName);
        appQRView.setBackgroundDrawable(qrImg);
        shareAlert.setTitle(labelName);
        shareAlert.setContentView(textEntryView);
        shareAlert.show();
    }

    public static List<Map<String, Object>> getShareAppOptionData(Resources res, int arrayText) {
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        int[] appOptionIcon = ShareAppConsts.SHAREAPP_OPTION_ICONS;
        String[] optionDesc = res.getStringArray(arrayText);
        for (int i = 0; i < optionDesc.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(ShareAppConsts.KEY_SHAREAPP_OPTION_ICONID, appOptionIcon[i]);
            map.put(ShareAppConsts.KEY_SHAREAPP_OPTION_DESC, optionDesc[i]);
            data.add(map);
        }
        return data;
    }
}
