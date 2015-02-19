package com.iitbombay.clicker;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import support.Utils;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

public class ApplicationContext extends Application{
	public static String classname = "ApplicationContext";
	
	private static int NetworkConnectionTimeout_ms = 5000;
	private static DefaultHttpClient httpClient;
	private static CookieStore cookieStore;
	
	// link: http://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
	// allows non-"edt" thread to be re-inserted into the "edt" queue
	public static final Handler uiThreadCallback = new Handler();
	
	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Welcome to Clicker", Toast.LENGTH_SHORT).show();
	}
	
	
	// link: http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	public synchronized static DefaultHttpClient getThreadSafeClient() {
		if(httpClient!=null) return httpClient;
		Utils.logv(classname, "New httpClient is created");
		
		// set params for connection...
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    
	    //creating cookie
	    if(cookieStore == null)  cookieStore = new BasicCookieStore();
	    
	    httpClient = new DefaultHttpClient(params);
	    ClientConnectionManager mgr = httpClient.getConnectionManager();
	    httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
	    		mgr.getSchemeRegistry()), params);
	    httpClient.setCookieStore(cookieStore);
        return httpClient;
    }
	
	public synchronized static CookieStore getThreadSafeCookieStore(){
		if(cookieStore==null) {
			Utils.logv(classname, "New CookieStore is created");
			cookieStore = new BasicCookieStore();
		}
		return cookieStore;
	}
	
}
