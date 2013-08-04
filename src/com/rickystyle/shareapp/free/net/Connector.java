package com.rickystyle.shareapp.free.net;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.Handler;

import com.rickystyle.shareapp.free.consts.ShareAppConsts;

/**
 * connector
 * @author Ricky
 */
public class Connector {

    protected HttpClient httpClient;
    protected Handler notifyHandler;
    private HttpPost httppost;
    private final HttpContext context;

    public Connector() {
	HttpParams baseParams = new BasicHttpParams();
	ConnManagerParams.setMaxTotalConnections(baseParams, 5);
	HttpProtocolParams.setVersion(baseParams, HttpVersion.HTTP_1_1);
	ConnManagerParams.setTimeout(baseParams, 10000);// set timeout
	// Create and initialize scheme registry
	SchemeRegistry schemeRegistry = new SchemeRegistry();
	schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	ClientConnectionManager cm = new ThreadSafeClientConnManager(baseParams, schemeRegistry);

	this.context = new BasicHttpContext();
	this.httpClient = new DefaultHttpClient(cm, baseParams);
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Hashtable<String, Object> bag) {
	boolean isSuccess = false;
	List<NameValuePair> params = (List<NameValuePair>) bag.get(ShareAppConsts.PARAMS);
	String apiURL = (String) bag.get(ShareAppConsts.KEY_REQUEST_URL);

	HttpResponse response = null;
	HttpPost httppost = new HttpPost(apiURL);
	httppost.setHeader(ShareAppConsts.HTTP_ACCEPT_ENCODING, "gzip, deflate");

	String source = "";
	int stateCode = 0;
	try {
	    httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

	    // execute the method
	    response = this.httpClient.execute(httppost, this.context);
	    stateCode = response.getStatusLine().getStatusCode();

	    HttpEntity entity = response.getEntity();

	    if (entity != null) {
		if (entity.getContentEncoding() != null && "gzip".equals(entity.getContentEncoding().getValue())) {
		    GZIPInputStream gzipIs = new GZIPInputStream(entity.getContent());
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();

		    int tmplength = 2048;
		    byte[] tmpfile = new byte[tmplength];

		    int read = 0;
		    while ((read = gzipIs.read(tmpfile)) != -1) {
			bos.write(tmpfile, 0, read);
		    }
		    bos.close();
		    gzipIs = null;
		    source = new String(bos.toByteArray());
		} else {
		    source = EntityUtils.toString(entity);
		}
		// put body in bag
		// DebugTool.printVLog("Rank\n" + source);
		bag.put(ShareAppConsts.RESPONSE_BODY, source);
	    }

	} catch (Exception e) {
	    if (this.httppost != null) {
		this.httppost.abort();
	    }
	    String error = e.getMessage();
	    bag.put(ShareAppConsts.KEY_ERROR_MSG, error);

	} finally {
	    // Log.v("log", "url:" + apiURL + ",stateCode:" + stateCode + ",e:" + error);
	    this.httpClient.getConnectionManager().closeExpiredConnections();
	}

	switch (stateCode) {
	case HttpStatus.SC_OK:
	    isSuccess = true;
	    break;
	case HttpStatus.SC_BAD_REQUEST:
	    isSuccess = false;
	    break;
	}

	return isSuccess;
    }
}
