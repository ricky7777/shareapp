package com.rickystyle.shareapp.free.adapter;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

/**
 * 將接收下來的GSON物件轉成排行榜list的adapter
 * @author Ricky
 */
public class ShareAppAdapter {
    private static ShareAppAdapter instance;
    private JSONArray billBoardData;

    private ShareAppAdapter() {
    }

    public static ShareAppAdapter getInstance() {
	if (instance == null) {
	    instance = new ShareAppAdapter();
	}
	return instance;
    }

    public void parse(String source) {
	try {
	    // Log.v("log", "parse source:" + source);
	    // JSONObject jsonObj = new JSONObject("{\"" + HeaderConsts.KEY_BILLBOARD + "\": " + source + "}");
	    JSONArray jsonAry = new JSONArray(source);
	    setBillBoardData(jsonAry);
	    // Log.v("log", "parse test:" + jsonObj);
	} catch (JSONException e) {
	    Log.v("log", "parse error:" + e.getMessage());
	}
    }

    public JSONArray getBillBoardData() {
	return this.billBoardData;
    }

    public void setBillBoardData(JSONArray billBoardData) {
	this.billBoardData = billBoardData;
    }
}
