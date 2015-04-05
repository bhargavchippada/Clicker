package datahandler;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import support.AppSettings;
import support.Question;
import support.UserSession;
import support.Utils;
import android.os.Handler;
import android.widget.Toast;
import clicker.ApplicationContext;
import clicker.QuizPage;

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
			UserSession userSession = ApplicationContext.getThreadSafeUserSession();
			Question question = ApplicationContext.getThreadSafeQuestion();
			jsonreq.put("uid", userSession.username);
			jsonreq.put("myanswer", question.answers);
			jsonreq.put("qid", question.ID);
			question.submitTime = new Date().getTime();
			question.timeTook = question.submitTime - question.startTime;
			jsonreq.put("starttime", (long) question.startTime/1000);
			jsonreq.put("submittime", (long) question.submitTime/1000);
			jsonreq.put("timetook", (long) question.timeTook/1000);
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
	private void _showInUI(int uiStatus) {
		if(uiStatus==0){
			_activity.updateUI("Trying to submit answer..");
		}else{
			if (data.dataFromServlet != null){
				try{
					Integer status = (Integer)data.dataFromServlet.get("status");
					if(status==-1){
						Toast.makeText(_activity,"Failed to submit answer",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Failed to submit answer");
					}else if(status==0){
						Toast.makeText(_activity,"Your are not authorized!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Your are not authorized!");
						_activity.gotoLoginPage();
					}else if(status==1){
						Toast.makeText(_activity,"Quiz has changed!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Quiz has changed!");
						_activity.gotoLoginPage();
					}else if(status==2){
						Toast.makeText(_activity,"You have already submitted",Toast.LENGTH_SHORT).show();
						_activity.updateUI("You have already submitted");
					}else if(status==3){
						Toast.makeText(_activity,"Answer submitted!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Answer submitted!");
						String feedback = (String) data.dataFromServlet.get("feedback");
						_activity.updateUI(feedback);
						_activity.disableBtns();
					}else if(status==4){
						Toast.makeText(_activity,"Sorry, this quiz was over!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Sorry, this quiz was over!");
						_activity.gotoLoginPage();
					}else {
						Toast.makeText(_activity,"Invalid status code!",Toast.LENGTH_SHORT).show();
						_activity.updateUI("Invalid status code!");
						_activity.gotoLoginPage();
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
				_activity.gotoLoginPage();
			}
		}
	}

}

