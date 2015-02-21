package com.iitbombay.datahandler;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.Question;
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

		data = new GetDataFromWebServer(classname);
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
					synchronized (ApplicationContext.class) {
						Question question = ApplicationContext.getThreadSafeQuestion();
						question.questionContent = (String)data.dataFromServlet.get("questionContent");
						question.quesType = (int)data.dataFromServlet.get("quesType");
						question.options = (JSONArray) data.dataFromServlet.get("options");
					}
					_activity.gotoQuizPage();
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

