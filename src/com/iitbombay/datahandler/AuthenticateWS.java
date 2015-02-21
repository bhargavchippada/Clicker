package com.iitbombay.datahandler;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.SharedSettings;
import support.UserSession;
import support.Utils;
import android.os.Handler;
import android.widget.Toast;

import com.iitbombay.clicker.ApplicationContext;
import com.iitbombay.clicker.LoginPage;


// This class is for authentication and connecting to the server
public class AuthenticateWS {
	String classname = "AuthenticateWS";

	// reference  to the calling activity
	private LoginPage _activity;
	private GetDataFromWebServer data;

	public void execute(LoginPage activity) {
		_activity = activity;

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

		// create the request object
		JSONObject jsonreq = new JSONObject();
		final StringEntity req_entity;
		try {
			jsonreq.put("uid", _activity.getUsername());
			jsonreq.put("pwd", _activity.getPassword());		
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

		data = new GetDataFromWebServer(classname);
		new Thread() {
			@Override public void run() {
				uiThreadCallback.post(runInUIThread1);
				data.doInBackgroundPost(AppSettings.LoginServiceUri+SharedSettings.authentication, req_entity);
				uiThreadCallback.post(runInUIThread2);
			}
		}.start();
	}

	// this method is called in the "edt"
	private void _showInUI(int uiStatus) {
		if(uiStatus==0){
			_activity.updateUI("Trying to connect to Server..");
		}else{

			if (data.dataFromServlet != null){
				try {
					int status = (int)data.dataFromServlet.get("status");
					if(status==0){
						Toast.makeText(_activity,"Server is not ready, Wait!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Server is not ready, Wait!");
					}else if(status==1){
						Toast.makeText(_activity,"Incorrect username or password",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Incorrect username or password");
					}else if(status==2){
						Toast.makeText(_activity,"Login success",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Login success");
						synchronized (AuthenticateWS.class) {
							UserSession usersession=ApplicationContext.getThreadSafeUserSession();
							usersession.clear();
							usersession.username = _activity.getUsername();
							usersession.name = data.dataFromServlet.getString("name");
							usersession.clsnm = data.dataFromServlet.getString("clsnm");
						}
						_activity.gotoHomePage(data.dataFromServlet);
					}else if(status==3){
						Toast.makeText(_activity,"Server: error processing request",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Server: error processing request");
					}else{
						Toast.makeText(_activity,"Invalid satus code",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Invalid satus code");
					}
				} catch (JSONException e) {
					Utils.logv(classname, "dataFromServlet retrieval error!",e);
					e.printStackTrace();
				}
			}else if (data.ex != null){
				Toast.makeText(_activity,
						data.ex.getMessage() == null ? "Error" : "Error - " + data.ex.getMessage(),
								Toast.LENGTH_SHORT).show();
				_activity.updateUI("Connection failed");
			}
		}
	}

}

