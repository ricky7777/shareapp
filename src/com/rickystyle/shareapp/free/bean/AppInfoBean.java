package com.rickystyle.shareapp.free.bean;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * game的info bean
 * 
 * @author Ricky
 * 
 */
public class AppInfoBean {
    private final String appName;
    private final String packageName;
    private Date         lastLaunchtime;
    private byte[]       icon;
    private Bitmap       iconBitmap;

    public AppInfoBean(String appName, String packageName, Date lastLaunchtime, byte[] icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.lastLaunchtime = lastLaunchtime;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setLastLaunchTime(Date lastLaunchtime) {
        this.lastLaunchtime = lastLaunchtime;
    }

    public Date getLastLaunchtime() {
        return lastLaunchtime;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public Bitmap getIconBitmap() {
        if (iconBitmap == null) {
            iconBitmap = convertByteArrayToBitmap(icon);
        }
        return iconBitmap;
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(byteArrayToBeCOnvertedIntoBitMap, 0, byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }

}
