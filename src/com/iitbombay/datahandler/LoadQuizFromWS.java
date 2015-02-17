package com.iitbombay.datahandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import support.SharedSettings;
import support.Utils;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.iitbombay.clicker.HomePage;

/**
 * This class connects to the Ping Servlet and sends a String and gets a String.
 */
public class LoadQuizFromWS {
	String ClassName = "LoadQuizFromWS";
	
	int NetworkConnectionTimeout_ms = 5000;
	
	// data
	
	/* ref to the calling activity */
	private HomePage _activity;
	private Exception ex;
	private HashMap<String, Serializable> dataFromServlet;
	String uid;
	// methods
	
	public void execute(HomePage activity) {
	
	  _activity = activity;
	
	  // allows non-"edt" thread to be re-inserted into the "edt" queue
	  final Handler uiThreadCallback = new Handler();
	
	  // performs rendering in the "edt" thread, before background operation starts
	  final Runnable runInUIThread1 = new Runnable() {
	    public void run() {
	      _showInUI(0);
	    }
	  };
	  
	  // performs rendering in the "edt" thread, after background operation is complete
	  final Runnable runInUIThread2 = new Runnable() {
	    public void run() {
	      _showInUI(1);
	    }
	  };
	
	  new Thread() {
	    @Override public void run() {
	      uiThreadCallback.post(runInUIThread1);
	      _doInBackgroundPost();
	      uiThreadCallback.post(runInUIThread2);
	    }
	  }.start();
	
	  //Toast.makeText(_activity, "Getting data from servlet", Toast.LENGTH_SHORT).show();
	
	}

	/** this method is called in a non-"edt" thread */
	private void _doInBackgroundPost() {
	  Utils.logv(ClassName, "background task - start",null);
	  long startTime = System.currentTimeMillis();
	  
	  uid =  _activity.getUsername();
	  HashMap<String, String> request_map = new HashMap<String,String>();
	  request_map.put("uid",uid);
	  try {
	    HttpParams params = new BasicHttpParams();
	
	    // set params for connection...
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    DefaultHttpClient httpClient = new DefaultHttpClient(params);
	
	    // create post method
	    HttpPost postMethod = new HttpPost(AppSettings.LoginServiceUri+SharedSettings.pushquiz);
	
	    // create request entity
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(request_map);
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
	            dataFromServlet = (HashMap<String, Serializable>) ois.readObject();
	            Utils.logv(ClassName,"data size from servlet=" + data.length,null);
	            Utils.logv(ClassName,"data hashmap from servlet=" + dataFromServlet.toString(),null);
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
	
	/** this method is called in the "edt" */
	private void _showInUI(int status) {
	  if(status==0){
		  _activity.updateUI("Trying to start quiz..",View.VISIBLE);
	  }else{
		
		  if (dataFromServlet != null){
			if(((String)dataFromServlet.get("status")).equals("1")){
			    Toast.makeText(_activity,"Quiz retrievel Success!",Toast.LENGTH_SHORT).show();
			    _activity.updateUI("Quiz retrievel Success!",View.INVISIBLE);
			    _activity.gotoQuizPage(dataFromServlet);
			}else{
				Toast.makeText(_activity,"Quiz retrieval Failed",Toast.LENGTH_SHORT).show();
			    _activity.updateUI("Quiz retrieval Failed",View.INVISIBLE);
			}
		  }else if (ex != null){
		    Toast.makeText(_activity,
		                   ex.getMessage() == null ? "Error" : "Error - " + ex.getMessage(),
		                   Toast.LENGTH_SHORT).show();
		    _activity.updateUI("Connection failed",View.INVISIBLE);
		    _activity.gotoConnectPage();
		  }
	  
	  }
	}

}

