package com.rickystyle.shareapp.free.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.tool.BarcodeManager;
import com.rickystyle.shareapp.free.tool.IntentTool;
import com.rickystyle.shareapp.free.tool.StringUtils;
import com.rickystyle.shareapp.free.widget.ShareAppDialog;

/**
 * Shareapp的baseActivity,共用的method會放在此
 * 
 * @author Ricky
 */
public abstract class BaseActivity extends AnalyticsActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, ShareAppConsts.MENU_SHARETEXT, 0, R.string.shareapp_menu_sharetext).setIcon(R.drawable.menu_icon_text);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case ShareAppConsts.MENU_SHARETEXT:
            ShareAppDialog.Builder shareAlert = new ShareAppDialog.Builder(this);
            final EditText edit = new EditText(this);
            shareAlert.setTitle(R.string.shareapp_sharetext_title);
            shareAlert.setContentView(edit);
            shareAlert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String text = edit.getText().toString();
                    if (!StringUtils.isBlankorNull(text)) {
                        Uri uri = BarcodeManager.getInstance().getBarcodeURIforText(text);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.shareapp_sharetext_subject));
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.shareapp_sharetext_subject));
                        // intent.putExtra(Intent.EXTRA_TEXT, textBody);
                        // intent.putExtra(ShareAppConsts.KEY_EXTRA_SMS_BODY, textBody);

                        IntentTool.launchChooser(BaseActivity.this, intent, getString(R.string.app_share_title),
                                ShareAppConsts.ACT_RESULT_CODE_SHARETEXT_SUCCESS);
                    }
                    dialog.dismiss();
                }
            }).show();
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected abstract String getGoogleAnalyticsAccountId();
}
