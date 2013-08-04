package com.rickystyle.shareapp.free.bean;

/**
 * 每則bookmark 的info
 * @author Ricky
 */
public class BookmarkInfo {
    public String appName;
    public String appPackageName;
    public String appIconFilePath;

    public BookmarkInfo() {
    }

    public String getAppName() {
	return this.appName;
    }

    public void setAppName(String appName) {
	this.appName = appName;
    }

    public String getAppPackageName() {
	return this.appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
	this.appPackageName = appPackageName;
    }

    public String getAppIconFilePath() {
	return this.appIconFilePath;
    }

    public void setAppIconFilePath(String appIconFilePath) {
	this.appIconFilePath = appIconFilePath;
    }
}
