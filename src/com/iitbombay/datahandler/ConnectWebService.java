package com.iitbombay.datahandler;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.CookieStore;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.SharedSettings;
import support.Utils;
import android.widget.Toast;

import com.iitbombay.clicker.ApplicationContext;
import com.iitbombay.clicker.MainActivity;


// This class connects to the Ping Servlet and sends a String and gets a String.
public class ConnectWebService {
	String classname = "ConnectWebService";

	// reference  to the calling activity
	private MainActivity _activity;
	private GetDataFromWebServer data;

	public void execute(MainActivity activity) {
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
			jsonreq.put("ping", 0);
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
		
		data = new GetDataFromWebServer(classname);
		new Thread() {
			@Override public void run() {
				ApplicationContext.uiThreadCallback.post(runInUIThread1);
				data.doInBackgroundPost(AppSettings.LoginServiceUri+SharedSettings.ping, req_entity);
				ApplicationContext.uiThreadCallback.post(runInUIThread2);
			}
		}.start();
		//}
	}
	
	// this method is called in the "edt"
	private void _showInUI(int status) {
		if(status==0){
			_activity.updateUI("Trying to connect to Server..");
		}else{

			if (data.dataFromServlet != null){
				try {
					if((int)data.dataFromServlet.get("ping")==1){
						Toast.makeText(_activity,"Connected to Server",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Connected");
						_activity.gotoLoginPage();
					}else{
						Toast.makeText(_activity,"Server is not ready",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Server is not ready");
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

