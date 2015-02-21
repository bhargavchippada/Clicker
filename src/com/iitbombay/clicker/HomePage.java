package com.iitbombay.clicker;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import support.Question;
import support.UserSession;
import support.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iitbombay.datahandler.LoadQuizFromWS;

public class HomePage extends Activity{
	String classname = "HomePage";

	TextView txtvw_roll_number;
	TextView txtvw_name;
	TextView txtvw_ipaddress;
	TextView txtvw_clsnm;
	TextView txtvw_status;

	Button btn_startquiz;
	ProgressBar pbar_startquiz;

	UserSession usersession;

	int status; //0 means start button can ask server, 1 means it can't ask

	double lastTime = -5.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		txtvw_roll_number = (TextView) findViewById(R.id.txtvw_roll_number);
		txtvw_name = (TextView) findViewById(R.id.txtvw_name);
		txtvw_ipaddress = (TextView) findViewById(R.id.txtvw_ipaddress);
		txtvw_clsnm = (TextView) findViewById(R.id.txtvw_clsnm);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);

		usersession = ApplicationContext.getThreadSafeUserSession();
		if(!usersession.isSessionValid()){
			Utils.logv(classname, "UserSession is invalid");
			gotoLoginPage();
		}

		String ipaddress = Utils.getIpAddress(getBaseContext());
		txtvw_roll_number.setText(usersession.username);
		txtvw_name.setText(usersession.name);
		txtvw_clsnm.setText(usersession.clsnm);
		txtvw_ipaddress.setText(ipaddress);

		btn_startquiz = (Button) findViewById(R.id.btn_startquiz);
		pbar_startquiz = (ProgressBar) findViewById(R.id.pbar_startquiz);

		btn_startquiz.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				double present_time  = System.currentTimeMillis()/1000;
				int diff_time = (int)(present_time-lastTime);
				if(diff_time<5){
					updateUI("Wait for "+(5-diff_time)+" secs before trying", View.INVISIBLE);
				}

				lastTime = present_time;
				if(status==0){
					new LoadQuizFromWS().execute(HomePage.this);
					pbar_startquiz.setVisibility(View.VISIBLE);
				}else{
					pbar_startquiz.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	public void updateUI(String msg, int pbar_state){
		txtvw_status.setText(msg);
		pbar_startquiz.setVisibility(pbar_state);
	}

	public String getUsername(){
		return txtvw_roll_number.getText().toString();
	}

	public void gotoQuizPage(JSONObject dataFromServlet) throws JSONException{
		Question.questionContent = (String)dataFromServlet.get("questionContent");
		Question.quesType = Integer.parseInt((String) dataFromServlet.get("quesType"));
		Question.options = (JSONArray) dataFromServlet.get("options");
		startActivity(new Intent(this,QuizPage.class));
	}

	public void gotoLoginPage(){
		Intent intent = new Intent(this,LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
