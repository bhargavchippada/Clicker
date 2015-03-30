package com.iitbombay.clicker;

import java.util.HashMap;

import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;

import support.Question;
import support.UserSession;
import support.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.iitbombay.datahandler.SubmitAnswerToWS;

public class QuizPage extends Activity{
	String classname = "QuizPage";

	TextView txtvw_username;
	TextView txtvw_quizContent;
	TextView txtvw_status;
	RadioGroup rg_options;
	Button btn_submit;
	Button btn_exit;

	HashMap<Integer,Integer> optionIds;

	UserSession userSession;
	Question question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_page);

		userSession = ApplicationContext.getThreadSafeUserSession();
		question = ApplicationContext.getThreadSafeQuestion();

		optionIds = new HashMap<Integer, Integer>();

		txtvw_username = (TextView) findViewById(R.id.txtvw_username);
		txtvw_quizContent = (TextView) findViewById(R.id.txtvw_quizContent);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		rg_options = (RadioGroup) findViewById(R.id.rg_options);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		txtvw_username.setText(userSession.username);
		txtvw_quizContent.setText(question.question);
		for(int i=0;i<question.options.length();i++){
			RadioButton row = (RadioButton) getLayoutInflater().inflate(R.layout.singleoption_radiobtn, rg_options, false);
			try {
				row.setText(question.options.get(i).toString());
			} catch (JSONException e) {
				Utils.logv(classname,"JSon Error while setting the options", e);
				e.printStackTrace();
			}
			rg_options.addView(row);
			optionIds.put(row.getId(), i+1);
		}

		rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Utils.logv(classname, checkedId+" is checked now");
				userSession.answers=new JSONArray();
				userSession.answers.put(optionIds.get(checkedId));
			}
		});

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(userSession.answers.length()!=0){
					new SubmitAnswerToWS().execute(QuizPage.this);
				}else{
					Toast.makeText(getBaseContext(), "Please Select an Option...", Toast.LENGTH_SHORT).show();
				}
			}
		});

		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), LoginPage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

	}

	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}

	public void disableBtns(){
		btn_submit.setEnabled(false);
		for (int i = 0; i < rg_options.getChildCount(); i++) {
			rg_options.getChildAt(i).setEnabled(false);
		}
	}

	public void gotoLoginPage(){
		Intent intent = new Intent(this,LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
