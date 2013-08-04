package com.rickystyle.shareapp.free.net;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.os.Message;

import com.rickystyle.shareapp.free.adapter.ShareAppAdapter;
import com.rickystyle.shareapp.free.consts.ShareAppConsts;
import com.rickystyle.shareapp.free.tool.StringUtils;

/**
 * 掌管網路相關的manager<br/>
 * 由他去呼叫底層的httpclient
 * @author Ricky
 */
public class NetManager implements Runnable {
    private static NetManager instance;
    Connector connector;
    private boolean isAlive;
    private final Vector<Hashtable<String, Object>> workQueue = new Vector<Hashtable<String, Object>>();

    private NetManager() {
	connector = new Connector();
    }

    public static synchronized NetManager getInstance() {
	if (instance == null) {
	    instance = new NetManager();
	}
	return instance;
    }

    public void start() {
	if (!this.isAlive) {
	    this.isAlive = true;
	    Thread t = new Thread(this);
	    t.start();
	}
    }

    public void run() {
	while (this.workQueue.size() > 0) {
	    Hashtable<String, Object> bag = this.workQueue.remove(0);
	    boolean isSuccess = connector.execute(bag);
	    Handler handler = (Handler) bag.get(ShareAppConsts.HANDLER);
	    if (isSuccess) {
		String source = (String) bag.get(ShareAppConsts.RESPONSE_BODY);
		// Log.v("log", "success source:" + source);
		if (!StringUtils.isBlankorNull(source)) {
		    ShareAppAdapter.getInstance().parse(source);
		}

		int successState = (Integer) bag.get(ShareAppConsts.KEY_STATE_SUCCESS);
		sendMsg(successState, handler);
	    } else {
		int failState = (Integer) bag.get(ShareAppConsts.KEY_STATE_FAIL);
		sendMsg(failState, handler);
	    }
	}
	this.isAlive = false;
    }

    /**
     * 通知handler用
     * @param what
     * @param notifyHandler
     */
    public static void sendMsg(int what, Handler notifyHandler) {
	if (notifyHandler != null) {
	    Message m = new Message();
	    m.what = what;
	    notifyHandler.sendMessage(m);
	}
    }

    /**
     * 將shareapp info上傳至 dreamplatform
     * @param handler
     * @param appName
     * @param packageName
     */
    public void uploadShareAppInfo(Handler handler, String appName, String packageName) {
	// filter掉shareApp
	if (packageName.indexOf(ShareAppConsts.FILTER_NAME_SHAREAPP) != -1)
	    return;

	Hashtable<String, Object> bag = new Hashtable<String, Object>();
	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_AP, ShareAppConsts.AP_NAME));
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_CMD, ShareAppConsts.CMD_UPLOAD_SHAREAPP));
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_SERVICE_APPNAME, appName));
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_SERVICE_PACKAGENAME, packageName));

	bag.put(ShareAppConsts.KEY_REQUEST_URL, ShareAppConsts.API_URL);
	bag.put(ShareAppConsts.PARAMS, params);
	bag.put(ShareAppConsts.HANDLER, handler);

	// 將成功和失敗的state放入bag
	bag.put(ShareAppConsts.KEY_STATE_SUCCESS, ShareAppConsts.NET_UPLOAD_SHAREAPP_SUCCESS);
	bag.put(ShareAppConsts.KEY_STATE_FAIL, ShareAppConsts.NET_UPLOAD_SHAREAPP_FAIL);
	this.workQueue.add(bag);
	this.start();
    }

    public void getShareAppRankBoard(Handler handler, String appType, String range) {
	Hashtable<String, Object> bag = new Hashtable<String, Object>();
	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_AP, ShareAppConsts.AP_NAME));
	params.add(new BasicNameValuePair(ShareAppConsts.KEY_CMD, ShareAppConsts.CMD_GET_SHAREAPPRANKBOARD));
	params.add(new BasicNameValuePair(ShareAppConsts.PARAM_APPTYPE, appType));
	params.add(new BasicNameValuePair(ShareAppConsts.PARAM_RANGE, range));

	bag.put(ShareAppConsts.KEY_REQUEST_URL, ShareAppConsts.API_URL);
	bag.put(ShareAppConsts.PARAMS, params);
	bag.put(ShareAppConsts.HANDLER, handler);

	// 將成功和失敗的state放入bag
	bag.put(ShareAppConsts.KEY_STATE_SUCCESS, ShareAppConsts.NET_GET_SHAREAPPBOARD_SUCCESS);
	bag.put(ShareAppConsts.KEY_STATE_FAIL, ShareAppConsts.NET_GET_SHAREAPPBOARD_FAIL);
	this.workQueue.add(bag);
	this.start();
    }
}
