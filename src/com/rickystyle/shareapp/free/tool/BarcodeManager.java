package com.rickystyle.shareapp.free.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.rickystyle.shareapp.free.bean.AppInfoBean;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;

/**
 * Shareapp內掌管barcode各項事物的男人<br/>
 * singleton
 * 
 * @author Ricky
 */
public class BarcodeManager {
    private static BarcodeManager instance;
    private String                packageName;

    private BarcodeManager() {
    }

    public static BarcodeManager getInstance() {
        if (instance == null)
            instance = new BarcodeManager();
        return instance;
    }

    /**
     * 取得barcode的uri
     * 
     * @param packageName
     * @return
     */
    public Uri getBarcodeURI(String packageName) {
        this.packageName = packageName;
        String barcodeContentString = getBarcodeContentString();
        return Uri.parse(barcodeContentString);
    }

    /**
     * 取得text型式的Uri,省略過一些驗證
     * 
     * @param packageName
     * @param text
     * @return
     */
    public Uri getBarcodeURIforText(String text) {
        saveBarcodeImg(text);
        return Uri.parse(ShareAppConsts.PREF_BARCODE_PATH + getBarcodeTextInFolderURL());
    }

    /**
     * 取得barcode的content string,會是file://開頭
     * 
     * @param packageName
     * @return
     */
    public String getBarcodeContentString() {
        if (!isBarcodeExists()) {
            saveBarcodeImg(null);
        }

        return getBarcodeContentURL();
    }

    public void saveBarcodeImg(String text) {
        try {
            Bitmap bm = getBarcodeImg(text);
            String filePath = getBarcodeInFolderURL();
            if (!StringUtils.isBlankorNull(text)) {
                filePath = getBarcodeTextInFolderURL();
            }
            File barcodeFile = new File(filePath);
            FileOutputStream fOut = new FileOutputStream(barcodeFile);
            boolean isSuccess = bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            if (fOut != null)
                fOut.close();

        } catch (Exception e) {
            LogUtils.d("get url fail:%1$s", packageName);
        }
    }

    public Bitmap getBarcodeImg(String text) {
        Bitmap bm = null;
        try {
            URL url = new URL(getBarcodeHttpURL(null));
            if (!StringUtils.isBlankorNull(text)) {
                url = new URL(getBarcodeHttpURL(text));
            }
            final URLConnection conn = url.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception ex) {
            LogUtils.d(this, "get barcode Img fail:%1$s", ex.getMessage());
        }
        return bm;
    }

    /**
     * 檢查barcode是否存在資料夾內
     * 
     * @param packageName
     * @return
     */
    public boolean isBarcodeExists() {
        boolean isInFolder = false;
        File f = new File(getBarcodeInFolderURL());
        if (f.exists()) {
            isInFolder = true;
        }
        return isInFolder;
    }

    /**
     * bookmark flow,check barcode icon is in sdcard
     * 
     * @return
     */
    public boolean isBarcodeIconExists() {
        boolean isInFolder = false;
        File f = new File(getBarcodeIconURL());
        if (f.exists()) {
            isInFolder = true;
        }
        return isInFolder;
    }

    /**
     * file開頭
     * 
     * @return
     */
    public String getBarcodeContentURL() {
        return ShareAppConsts.PREF_BARCODE_PATH + ShareAppConsts.BARCODE_FOLDER_PATH + "/" + packageName + ".png";
    }

    /**
     * barcode in folder url /sdcard/.shareapp開頭
     * 
     * @return
     */
    public String getBarcodeInFolderURL() {
        return ShareAppConsts.BARCODE_FOLDER_PATH + "/" + packageName + ".png";
    }

    public String getBarcodeTextInFolderURL() {
        return ShareAppConsts.BARCODE_FOLDER_PATH + "/secret.png";
    }

    public String getBarcodeIconURL() {
        return ShareAppConsts.BARCODE_FOLDER_PATH + "/" + packageName + "_icon.png";
    }

    /**
     * barcode httpurl
     * 
     * @return
     */
    public String getBarcodeHttpURL(String text) {
        String url = "";
        if (StringUtils.isBlankorNull(text)) {
            url = ShareAppConsts.BARCODE_GEN_URL + ShareAppConsts.BARCODE_SIZE_NORMAL + ShareAppConsts.BARCODE_PREF_EXTRA_PARAM + packageName;
        } else {
            url = ShareAppConsts.BARCODE_GEN_URL + ShareAppConsts.BARCODE_SIZE_NORMAL + ShareAppConsts.BARCODE_PREF_EXTRA_PARAM_CHL + Uri.encode(text);
        }
        return url;
    }

    public Drawable getBarcodeDrawable(String packageName) {
        this.packageName = packageName;
        if (!isBarcodeExists()) {
            saveBarcodeImg(null);
        }
        return Drawable.createFromPath(getBarcodeInFolderURL());
    }

    public String getBarcodeURLInMarket(String packageName) {
        return ShareAppConsts.BARCODE_PREF_MARKET_URL + packageName;
    }

    public String getBarcodeURLInHttp(String packageName) {
        return ShareAppConsts.BARCODE_PREF_MARKET_HTTP_URL + packageName;
    }

    /**
     * 將app bookmark<br/>
     * 1.save icon to sdcard<br/>
     * 2.save app info to xml
     * 
     * @param info
     * @param res
     * @param mPm
     */
    public void addAppBookmark(AppInfoBean info, Context context, PackageManager mPm) {
        long start = System.currentTimeMillis();
        this.packageName = info.getPackageName();

        // save icon to sdcard
        // Drawable drawable = info.loadIcon(mPm);
        // Bitmap bitmap = ImageTools.DrawableToBitmap(drawable);
        Bitmap bitmap = info.getIconBitmap();

        if (!isBarcodeIconExists()) {
            File barcodeFile = new File(getBarcodeIconURL());
            try {
                FileOutputStream fOut = new FileOutputStream(barcodeFile);
                boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                if (fOut != null)
                    fOut.close();
            } catch (Exception e) {
                LogUtils.d("save bookmark error:%1$s", e.getMessage());
            }
        }

        // save app info to xml
        PreferenceUtil.saveToBookmark(context, mPm, info);

    }

}
