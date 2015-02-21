package com.iitbombay.datahandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.MIMETypeConstantsIF;
import support.SharedSettings;
import support.Utils;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.iitbombay.clicker.ApplicationContext;
import com.iitbombay.clicker.HomePage;

//This class is for loading the quiz from the server
public class LoadQuizFromWS {
	String classname = "LoadQuizFromWS";

	// reference  to the calling activity
	private HomePage _activity;
	private GetDataFromWebServer data;

	public void execute(HomePage activity) {

		_activity = activity;

		// performs rendering in the "edt" thread, before background operation starts
		final Runnable runInUIThread1 = new Runnable() {
			public void run() {
				try {
					_showInUI(0);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		// performs rendering in the "edt" thread, after background operation is complete
		final Runnable runInUIThread2 = new Runnable() {
			public void run() {
				try {
					_showInUI(1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};

		// create the request object
		JSONObject jsonreq = new JSONObject();
		final StringEntity req_entity;
		try {
			jsonreq.put("uid", ApplicationContext.getThreadSafeUserSession().username);
			req_entity = new StringEntity(jsonreq.toString());
			Utils.logv(classname, "client request: "+jsonreq.toString());
		} catch (JSONException e1) {
			Utils.logv(classname, "JSON object creation error!",e1);
			e1.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e) {
			Utils.logv(classname, "JSON object creation error: UnsupportedEncodingException!",e);
			e.printStackTrace();
			return;
		}

		// link: http://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
		// allows non-"edt" thread to be re-inserted into the "edt" queue
		final Handler uiThreadCallback = new Handler();

		new Thread() {
			@Override public void run() {
				uiThreadCallback.post(runInUIThread1);
				data.doInBackgroundPost(AppSettings.LoginServiceUri+SharedSettings.pushquiz, req_entity);
				uiThreadCallback.post(runInUIThread2);
			}
		}.start();
	}

	/** this method is called in the "edt" 
	 * @throws JSONException */
	private void _showInUI(int uiStatus) throws JSONException {
		if(uiStatus==0){
			_activity.updateUI("Trying to start quiz..",View.VISIBLE);
		}else{
			if (data.dataFromServlet != null){
				int status = (int)data.dataFromServlet.get("status");
				if(status==1){
					Toast.makeText(_activity,"Quiz retrievel Success!",Toast.LENGTH_SHORT).show();
					_activity.updateUI("Quiz retrievel Success!",View.INVISIBLE);
					_activity.gotoQuizPage(data.dataFromServlet);
				}else{
					Toast.makeText(_activity,"Quiz retrieval Failed",Toast.LENGTH_SHORT).show();
					_activity.updateUI("Quiz retrieval Failed",View.INVISIBLE);
				}
			}else if (data.ex != null){
				Toast.makeText(_activity,
						data.ex.getMessage() == null ? "Error" : "Error - " + data.ex.getMessage(),
								Toast.LENGTH_SHORT).show();
				_activity.updateUI("Connection failed",View.INVISIBLE);
				_activity.gotoLoginPage();
			}
		}
	}

}

