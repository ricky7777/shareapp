package com.rickystyle.shareapp.free.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;

import com.rickystyle.shareapp.free.R;
import com.rickystyle.shareapp.free.tool.LogUtils;

/**
 * 控制讀取中的畫面
 * 
 * @author syuechenglu
 * 
 */
public class LoadingProcess extends Dialog {

    private static LoadingProcess progressDialog;

    // private static String dialogTitle;
    // private static String dialogBody;
    // private final static LodingDialogDissmiss lodingDialogDissmiss = new LodingDialogDissmiss();

    public LoadingProcess(Activity activity) {
        super(activity, R.style.NewDialog);
    }

    public static LoadingProcess show(Activity activity, CharSequence title, CharSequence message) {
        return show(activity, title, message, false);
    }

    public static LoadingProcess show(Activity activity, CharSequence title, CharSequence message, boolean indeterminate) {
        return show(activity, title, message, indeterminate, false, null);
    }

    public static LoadingProcess show(Activity activity, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(activity, title, message, indeterminate, cancelable, null);
    }

    public static LoadingProcess show(Activity activity, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable,
            OnCancelListener cancelListener) {
        LoadingProcess dialog = new LoadingProcess(activity);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        /* The next line will add the ProgressBar to the dialog. */
        dialog.addContentView(new ProgressBar(activity), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    /**
     * 顯示讀取中
     * 
     * @param act
     * @param strDialogTitle
     * @param strDialogBody
     */
    public static void dialogShow(Activity activity) {
        progressDialog = show(activity, null, null, true, true);

        LogUtils.d("log", "show 出 loading process");
    }

    /**
     * 關閉讀取中
     * 
     * @param act
     */
    public static void dialogDismiss() {
        progressDialog.dismiss();
    }

    private static class LodingDialogDissmiss implements DialogInterface.OnCancelListener {
        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
        }
    }
}
