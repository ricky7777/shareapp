package com.rickystyle.shareapp.free.bean;

import java.util.Locale;

import com.rickystyle.shareapp.free.consts.ShareAppConsts;

public class DeviceBean {

    // 能使用的寬
    int canUseScreenWidth;

    // 能使用的高
    int canUseScreenHeight;

    int deviceId;
    int deviceScreenMode;
    String deviceModel;
    String deviceCode;

    private static DeviceBean instance = new DeviceBean();

    public static DeviceBean getInstance() {
	return instance;
    }

    public int getCanUseScreenWidth() {
	return canUseScreenWidth;
    }

    public void setCanUseScreenWidth(int canUseScreenWidth) {
	this.canUseScreenWidth = canUseScreenWidth;
    }

    public int getCanUseScreenHeight() {
	return canUseScreenHeight;
    }

    public void setCanUseScreenHeight(int canUseScreenHeight) {
	this.canUseScreenHeight = canUseScreenHeight;
    }

    public int getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(int deviceId) {
	this.deviceId = deviceId;
    }

    public String getDeviceModel() {
	return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
	this.deviceModel = deviceModel;
    }

    public String getDeviceCode() {
	return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
	this.deviceCode = deviceCode;
    }

    public String getLanguage() {
	return Locale.getDefault().getLanguage();
    }

    public int getDeviceScreenMode() {
	if (canUseScreenWidth == ShareAppConsts.DEVICE_LARGE_WIDTH) {
	    this.deviceScreenMode = ShareAppConsts.DEVICE_SCREEN_LARGE;
	} else if (canUseScreenWidth == ShareAppConsts.DEVICE_NORMAL_WIDTH) {
	    this.deviceScreenMode = ShareAppConsts.DEVICE_SCREEN_NORMAL;
	} else if (canUseScreenWidth == ShareAppConsts.DEVICE_SMALL_WIDTH) {
	    this.deviceScreenMode = ShareAppConsts.DEVICE_SCREEN_SMALL;
	} else {
	    this.deviceScreenMode = ShareAppConsts.DEVICE_SCREEN_NORMAL;
	}
	return this.deviceScreenMode;
    }

}
