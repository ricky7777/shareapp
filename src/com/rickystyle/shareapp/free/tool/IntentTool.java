package com.rickystyle.shareapp.free.tool;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * intent launch的method封裝在此
 * @author Ricky
 */
public class IntentTool {
    public static void launchActivity(Activity act, int flag, Class<?> launchClass) {
	launchActivity(act, flag, launchClass, null);
    }

    public static void launchActivity(Activity act, int flag, Class<?> launchClass, Intent intent) {
	if (intent == null)
	    intent = new Intent();

	intent.setFlags(flag);
	intent.setClass(act, launchClass);
	act.startActivity(intent);
    }

    /**
     * 啟動chooser
     * @param act
     * @param intent
     * @param chooseTitle
     */
    public static void launchChooser(Activity act, Intent intent, String chooseTitle, int requestCode) {
	act.startActivityForResult(Intent.createChooser(intent, chooseTitle.toString()), requestCode);
    }

    /**
     * 啟動web
     * @param act
     * @param url
     */
    public static void launchWeb(Activity act, String url) {
	Intent intent = new Intent();
	intent.setData(Uri.parse(url));
	intent.setAction(Intent.ACTION_VIEW);
	act.startActivity(intent);
    }

    /**
     * 啟動另一個ap
     * @param act
     * @param packageName
     * @param mPm
     */
    public static void launchAnotherApp(Activity act, String packageName, PackageManager mPm) {
	// Intent launchIntent = new Intent(Intent.ACTION_MAIN);
	// launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	// launchIntent.setPackage(packageName); //1.5沒有這個method= =,改用下面的方式
	// ResolveInfo anotherAppInfo = mPm.resolveActivity(launchIntent, 0);
	// DebugTool.printVLog("start another app classname => " + anotherAppInfo.activityInfo.name);
	// ComponentName componentName = new ComponentName(packageName, anotherAppInfo.activityInfo.name);
	// launchIntent.setComponent(componentName);
	// act.startActivity(launchIntent);

	Intent mainApps = new Intent(Intent.ACTION_MAIN);
	mainApps.addCategory(Intent.CATEGORY_LAUNCHER);
	List<ResolveInfo> activities = act.getPackageManager().queryIntentActivities(mainApps, 0);
	Iterator<ResolveInfo> it = activities.iterator();
	while (it.hasNext()) {
	    ResolveInfo info = it.next();
	    ActivityInfo activity = info.activityInfo;
	    if (activity.packageName.equalsIgnoreCase(packageName)) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ComponentName name = new ComponentName(activity.packageName, activity.name);
		intent.setComponent(name);
		act.startActivity(intent);
		break;
	    }
	}

    }

    /**
     * 啟動刪除介面
     * @param act
     * @param packageName
     */
    public static void deleteAnotherApp(Activity act, String packageName) {
	Intent intent = new Intent();
	intent.setAction(Intent.ACTION_DELETE);
	intent.setData(Uri.parse("package:" + packageName));
	act.startActivity(intent);
    }

    public static void startService(Activity act, Intent intent) {
	if (intent == null)
	    intent = new Intent();
	act.startService(intent);
    }

}
