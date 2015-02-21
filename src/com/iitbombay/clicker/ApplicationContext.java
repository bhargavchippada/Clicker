package com.iitbombay.clicker;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import support.UserSession;
import support.Utils;
import android.app.Application;

public class ApplicationContext extends Application{
	public static String classname = "ApplicationContext";
	
	private static int NetworkConnectionTimeout_ms = 5000;
	private static DefaultHttpClient httpClient;
	private static CookieStore cookieStore;
	private static UserSession usersession;
	//private HashMap<String,String> cookiesMap;
	
	
	// link: http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	public synchronized static DefaultHttpClient getThreadSafeClient() {
		if(httpClient!=null) return httpClient;
		Utils.logv(classname, "New httpClient is created");
		
		// set params for connection...
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    ConnManagerParams.setMaxTotalConnections(params, 5);
	    
	    //creating cookie
	    if(cookieStore == null)  cookieStore = new BasicCookieStore();
	    
	    httpClient = new DefaultHttpClient(params);
	    ClientConnectionManager mgr = httpClient.getConnectionManager();
	    httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
	    		mgr.getSchemeRegistry()), params);
	    httpClient.setCookieStore(cookieStore);
        return httpClient;
    }
	
	public synchronized static UserSession getThreadSafeUserSession(){
		if(usersession==null) usersession = new UserSession();
		return usersession;
	}

	/*
	public synchronized HashMap<String, String> getThreadSafeCookiesMap(){
		if(cookiesMap==null){
			cookiesMap = new HashMap<String, String>();
		}
		updateThreadSafeCookiesMap();
		return cookiesMap;
	}
	
	public synchronized void updateThreadSafeCookiesMap(){
		if(cookiesMap==null){
			cookiesMap = new HashMap<String, String>();
		}
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		
		if(cookies != null)
        {
            for(Cookie cookie : cookies)
            {
            	cookiesMap.put(cookie.getName(), cookie.getValue());
            }
        }
	}
	*/
	public synchronized static void invalidateSession(){
		httpClient.getCookieStore().clear();
		usersession.clear();
		//cookiesMap.clear();
	}
	
	
}
