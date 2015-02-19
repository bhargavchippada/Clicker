package com.iitbombay.clicker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import support.Question;
import support.UserProfile;
import support.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.datahandler.SubmitAnswerToWS;

public class QuizPage extends Activity{
	String ClassName = "QuizPage";
	
	TextView txtvw_username;
	TextView txtvw_quizContent;
	TextView txtvw_status;
	RadioGroup rg_options;
	Button btn_submit;
	Button btn_exit;
	
	HashMap<Integer,Integer> optionIds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz_page);
		
		UserProfile.answers.clear();
		optionIds = new HashMap<Integer, Integer>();
		
		txtvw_username = (TextView) findViewById(R.id.txtvw_username);
		txtvw_quizContent = (TextView) findViewById(R.id.txtvw_quizContent);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		rg_options = (RadioGroup) findViewById(R.id.rg_options);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		
		txtvw_username.setText(UserProfile.rollnumber);
		txtvw_quizContent.setText(Question.questionContent);
		for(int i=0;i<Question.options.size();i++){
			RadioButton row = (RadioButton) getLayoutInflater().inflate(R.layout.singleoption_radiobtn, rg_options, false);
			row.setText(Question.options.get(i));
			rg_options.addView(row);
			optionIds.put(row.getId(), i+1);
		}
		
		rg_options.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Utils.logv(ClassName, checkedId+" is checked now");
				UserProfile.answers.clear();
				UserProfile.answers.add(optionIds.get(checkedId)+"");
			}
		});
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(UserProfile.answers.size()!=0){
					new SubmitAnswerToWS().execute(QuizPage.this);
				}else{
					Toast.makeText(getBaseContext(),"Please Select an Option...", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		btn_exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
	}
	
	public String getUsername(){
		return txtvw_username.getText().toString();
	}
	
	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}
	
	public void displayAnswer(HashMap<String, Serializable> dataFromServlet){
		ArrayList<String> answer = (ArrayList<String>) dataFromServlet.get("answer");
		String eval;
		if(dataFromServlet.get("correct").equals("1")) eval="correct";
		else eval="wrong";
		String output="Your answer is "+eval+"\nCorrect answer:";
		int op;
		for(int i=0;i<answer.size();i++){
			op = Integer.parseInt(answer.get(i));
			output+="\n"+op+": "+Question.options.get(op-1);
		}
		txtvw_status.setText(output);
	}
	
	public void disableBtns(){
		btn_submit.setEnabled(false);
		for (int i = 0; i < rg_options.getChildCount(); i++) {
			rg_options.getChildAt(i).setEnabled(false);
		}
	}
	
	public void gotoConnectPage(){
		Intent intent = new Intent(this,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
