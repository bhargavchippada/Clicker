package com.iitbombay.datahandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import support.AppSettings;
import support.MIMETypeConstantsIF;
import support.Utils;
import android.os.Handler;

import com.iitbombay.clicker.MainActivity;

/**
 * This class connects to the Ping Servlet and sends a String and gets a String.
 */

public class PingWebService {
	String ClassName = "PingWebService";
	
	int NetworkConnectionTimeout_ms = 5000;
	
	/* ref to the calling activity */
	private MainActivity _activity;
	private Exception ex;
	private HashMap<String,String> dataFromServlet;
	
	// methods
	
	public void execute(MainActivity activity, final String path) {
	
	  _activity = activity;
	
	  // allows non-"edt" thread to be re-inserted into the "edt" queue
	  final Handler uiThreadCallback = new Handler();
	
	  // performs rendering in the "edt" thread, after background operation ends
	  final Runnable runInUIThread = new Runnable() {
	    public void run() {
	      _showInUI(path);
	    }
	  };
	
	  new Thread() {
	    @Override public void run() {
	      _doInBackgroundPost(path);
	      uiThreadCallback.post(runInUIThread);
	    }
	  }.start();
	
	  //Toast.makeText(_activity, "Getting data from servlet", Toast.LENGTH_SHORT).show();
	
	}
	
	public void _showInUI(String path){
		if (dataFromServlet != null){
			if(dataFromServlet.get("ping").equals("1")){
				Utils.logv(ClassName,path+" : 1",null);
			}else{
				Utils.logv(ClassName,path+" : 0",null);
			}
		}else{
			Utils.logv(ClassName,path+" : 0",null);
		}
	}

	/** this method is called in a non-"edt" thread */
	private void _doInBackgroundPost(String path) {
	  Utils.logv(ClassName, "background task - start",null);
	  long startTime = System.currentTimeMillis();
	
	  try {
	    HttpParams params = new BasicHttpParams();
	
	    // set params for connection...
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    DefaultHttpClient httpClient = new DefaultHttpClient(params);
	
	    // create post method
	    HttpPost postMethod = new HttpPost(AppSettings.LoginServiceUri+path);
	    
	    HashMap<String,String> req = new HashMap<String,String>();
	    req.put("ping", "0");
	    // create request entity
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(req);
	    ByteArrayEntity req_entity = new ByteArrayEntity(baos.toByteArray());
	    req_entity.setContentType(MIMETypeConstantsIF.BINARY_TYPE);
	
	    // associating entity with method
	    postMethod.setEntity(req_entity);
	
	    // RESPONSE
	    httpClient.execute(postMethod, new ResponseHandler<Void>() {
	      public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
	        HttpEntity resp_entity = response.getEntity();
	        if (resp_entity != null) {
	
	          try {
	            byte[] data = EntityUtils.toByteArray(resp_entity);
	            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
	            dataFromServlet = (HashMap<String,String>) ois.readObject();
	            Utils.logv(ClassName,"data size from servlet=" + data.length,null);
	            Utils.logv(ClassName,"data hashtable from servlet=" + dataFromServlet.toString(),null);
	          }
	          catch (Exception e) {
	        	  ex = e;
	        	  //e.printStackTrace();
	        	  Utils.logv(ClassName,"problem processing post response",e);
	          }
	
	        }
	        else {
	          Utils.logv(ClassName,"No response entity",null);
	          throw new IOException(
	              new StringBuffer()
	                  .append("HTTP response : ").append(response.getStatusLine())
	                  .toString());
	        }
	        return null;
	      }
	    });
	
	  }
	  catch (Exception e) {
	    ex = e;
	    Utils.logv(ClassName,"Error Establishing Connection to Server",e);
	  }
	
	 	Utils.logv(ClassName,"background task - end",null);
	 	long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    Utils.logv(ClassName,elapsedTime+"ms",null);
	}

}

