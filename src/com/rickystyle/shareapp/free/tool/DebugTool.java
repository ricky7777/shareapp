package com.rickystyle.shareapp.free.tool;

import android.util.Log;

import com.rickystyle.shareapp.free.consts.ShareAppConsts;

/**
 * Debug Tool,不直接使用Log.x<br/>
 * 透過DebugTool去印Log
 * @author Ricky
 */
public class DebugTool {

    /**
     * 印verboselog
     * @param msg
     */
    public static void printVLog(String msg) {
	if (ShareAppConsts.DEBUG)
	    Log.v(ShareAppConsts.LOGPREF, msg);
    }

    public static void printDLog(String msg) {
	if (ShareAppConsts.DEBUG)
	    Log.d(ShareAppConsts.LOGPREF, msg);
    }

    public static void printILog(String msg) {
	if (ShareAppConsts.DEBUG)
	    Log.i(ShareAppConsts.LOGPREF, msg);
    }

    public static void printWLog(String msg) {
	if (ShareAppConsts.DEBUG)
	    Log.w(ShareAppConsts.LOGPREF, msg);
    }

    public static void printELog(String msg) {
	if (ShareAppConsts.DEBUG)
	    Log.e(ShareAppConsts.LOGPREF, msg);
    }

}
