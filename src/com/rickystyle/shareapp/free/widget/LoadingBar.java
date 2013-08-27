package com.rickystyle.shareapp.free.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.rickystyle.shareapp.free.R;

/**
 * 載入的loading bar
 * 
 * @author Ricky
 * 
 */
public class LoadingBar extends ProgressDialog {
    public static LoadingBar instance;
    private ProgressDialog   progressDialog;

    private static Activity  act;

    private LoadingBar(Activity context) {
        super(context);
    }

    public static void init(Activity activity) {
        if (instance == null) {
            act = activity;
            instance = new LoadingBar(activity);
        }
    }

    // public void dialogShow() {
    // Context context = getContext();
    //
    // String loadingText = context.getString(R.string.loading_prompt);
    // LogUtils.d(this, "dialogShow:%1$s,isFinish:%2$s", progressDialog == null, act.isFinishing());
    // if (progressDialog == null) {
    // if (!act.isFinishing()) {
    // progressDialog = show(context, null, loadingText);
    // }
    // } else {
    // LogUtils.d(this, "progressDialog.isShowing():%1$s", progressDialog.isShowing());
    // if (!progressDialog.isShowing()) {
    // // show完了,再new一個給他
    // if (!act.isFinishing()) {
    // progressDialog = show(context, null, loadingText);
    // }
    // }
    // }
    //
    // progressDialog.setOnKeyListener(new MyKeyListener());
    //
    // }

    public void dialogShow() {
        Context context = getContext();

        if (progressDialog == null) {
            if (!act.isFinishing()) {
                // progressDialog = show(context, null, loadingText);
                progressDialog = new ProgressDialog(act);
            }
        } else {
            if (!progressDialog.isShowing()) {
                // show完了,再new一個給他
                if (!act.isFinishing()) {
                    progressDialog = new ProgressDialog(act);
                    // progressDialog = show(context, null, loadingText);
                }
            }
        }

        // progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        String loadingText = context.getString(R.string.loading_prompt);
        progressDialog.setMessage(loadingText);
        // progressDialog.setMax(100);
        // progressDialog.show(context, null, loadingText);

        if (!act.isFinishing()) {
            progressDialog.show();
        }
        progressDialog.setOnKeyListener(new MyKeyListener());

    }

    public void dialogShowProgress() {
        Context context = getContext();

        if (progressDialog == null) {
            if (!act.isFinishing()) {
                // progressDialog = show(context, null, loadingText);
                progressDialog = new ProgressDialog(act);
            }
        } else {
            if (!progressDialog.isShowing()) {
                // show完了,再new一個給他
                if (!act.isFinishing()) {
                    progressDialog = new ProgressDialog(act);
                    // progressDialog = show(context, null, loadingText);
                }
            }
        }

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        String loadingText = context.getString(R.string.loading_init);
        progressDialog.setMessage(loadingText);
        progressDialog.setMax(100);
        // progressDialog.show(context, null, loadingText);

        if (!act.isFinishing()) {
            progressDialog.show();
        }
        progressDialog.setOnKeyListener(new MyKeyListener());
    }

    public void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void setDialogPercent(int value) {
        if (progressDialog != null) {
            progressDialog.setProgress(value);
        }
    }

    private class MyKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            int action = event.getAction();
            if (keyCode == KeyEvent.KEYCODE_BACK && action == KeyEvent.ACTION_UP) {
                dialog.dismiss();
            }
            return false;
        }
    }

}
