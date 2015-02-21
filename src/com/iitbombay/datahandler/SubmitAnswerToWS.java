package com.iitbombay.datahandler;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.Question;
import support.UserSession;
import support.Utils;
import android.os.Handler;
import android.widget.Toast;

import com.iitbombay.clicker.ApplicationContext;
import com.iitbombay.clicker.QuizPage;

//This class submits answer to the server and receives the response
public class SubmitAnswerToWS {
	String classname = "SubmitAnswerToWS";

	// reference  to the calling activity
	private QuizPage _activity;
	private GetDataFromWebServer data;

	public void execute(QuizPage activity) {

		_activity = activity;

		// performs rendering in the "edt" thread, before background operation starts
		final Runnable runInUIThread1 = new Runnable() {
			public void run() {
				try {
					_showInUI(0);
				} catch (JSONException e) {
					Utils.logv(classname, "Json exception",e);
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
					Utils.logv(classname, "Json exception",e);
					e.printStackTrace();
				}
			}
		};

		// create the request object
		JSONObject jsonreq = new JSONObject();
		final StringEntity req_entity;
		try {
			UserSession userSession = ApplicationContext.getThreadSafeUserSession();
			jsonreq.put("uid", userSession.username);
			jsonreq.put("answer", new JSONArray(userSession.answers));
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
				data.doInBackgroundPost(AppSettings.LoginServiceUri+AppSettings.submitanswer, req_entity);
				uiThreadCallback.post(runInUIThread2);
			}
		}.start();
	}

	/** this method is called in the "edt" 
	 * @throws JSONException */
	private void _showInUI(int uiStatus) throws JSONException {
		if(uiStatus==0){
			_activity.updateUI("Trying to submit answer..");
		}else{
			if (data.dataFromServlet != null){
				int status = (int)data.dataFromServlet.get("status");
				if(status==0){
					Toast.makeText(_activity,"Failed to submit answer",Toast.LENGTH_SHORT).show();
					_activity.updateUI("Failed to submit answer");
				}else if(status==1){
					Toast.makeText(_activity,"Answer submitted!",Toast.LENGTH_SHORT).show();
					_activity.updateUI("Answer submitted!");
					JSONArray answer = (JSONArray) data.dataFromServlet.get("answer");
					String eval;
					if((int) data.dataFromServlet.get("correct")==1) eval="correct";
					else eval="wrong";
					String output="Your answer is "+eval+"\nCorrect answer:";
					int op;
					synchronized (ApplicationContext.class) {
						Question question = ApplicationContext.getThreadSafeQuestion();
						for(int i=0;i<answer.length();i++){
							op = Integer.parseInt((String)answer.get(i));
							output+="\n"+op+": "+question.options.get(op-1);
						}
						_activity.updateUI(output);
					}
					_activity.disableBtns();
				}else if(status==2){
					Toast.makeText(_activity,"You have already submitted",Toast.LENGTH_SHORT).show();
					_activity.updateUI("You have already submitted");
				}
			}else if (data.ex != null){
				Toast.makeText(_activity,
						data.ex.getMessage() == null ? "Error" : "Error - " + data.ex.getMessage(),
								Toast.LENGTH_SHORT).show();
				_activity.updateUI("Connection failed");
				_activity.gotoLoginPage();
			}
		}
	}

}

