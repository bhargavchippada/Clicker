package clicker;

import java.util.HashMap;

import support.Question;
import support.UserSession;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clicker.R;

public class QuizPage extends FragmentActivity{
	String classname = "QuizPage";

	TextView txtvw_username;
	TextView txtvw_question;
	TextView txtvw_status;
	Button btn_submit;
	Button btn_exit;

	UserSession userSession;
	Question question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_quiz);

		userSession = ApplicationContext.getThreadSafeUserSession();
		question = ApplicationContext.getThreadSafeQuestion();

		txtvw_username = (TextView) findViewById(R.id.txtvw_username);
		txtvw_question = (TextView) findViewById(R.id.txtvw_question);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		txtvw_username.setText(userSession.username);
		txtvw_question.setText(question.question);
		
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(question.answers.length()!=0){
					//new SubmitAnswerToWS().execute(QuizPage.this);
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
		/*
		for (int i = 0; i < rg_options.getChildCount(); i++) {
			rg_options.getChildAt(i).setEnabled(false);
		}
		*/
	}

	public void gotoLoginPage(){
		Intent intent = new Intent(this,LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
