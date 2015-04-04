package clicker;

import support.Question;
import support.UserSession;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iitbombay.clicker.R;

import datahandler.SubmitAnswerToWS;

public class QuizPage extends FragmentActivity {
	String classname = "QuizPage";

	TextView txtvw_username;
	TextView txtvw_title;
	TextView txtvw_question;
	TextView txtvw_timer;
	TextView txtvw_status;
	Button btn_submit;
	Button btn_exit;

	UserSession userSession;
	Question question;

	QuestionFragment  fragment_question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_quiz);

		userSession = ApplicationContext.getThreadSafeUserSession();
		question = ApplicationContext.getThreadSafeQuestion();

		txtvw_username = (TextView) findViewById(R.id.txtvw_username);
		txtvw_title = (TextView) findViewById(R.id.txtvw_title);
		txtvw_question = (TextView) findViewById(R.id.txtvw_question);
		txtvw_timer = (TextView) findViewById(R.id.txtvw_timer);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_exit = (Button) findViewById(R.id.btn_exit);

		fragment_question = (QuestionFragment) getSupportFragmentManager().findFragmentById(R.id.frag_question);

		if(!question.title.equals("")){
			txtvw_title.setVisibility(View.VISIBLE);
			txtvw_title.setText(question.title);
		}

		txtvw_username.setText(userSession.username);
		txtvw_question.setText(Html.fromHtml("<b>Q) </b>"+question.question));

		if(question.timed){
			txtvw_timer.setText(question.time+"");
			new CountDownTimer(question.time*1000, 1000) {

				public void onTick(long millisUntilFinished) {
					txtvw_timer.setText(""+millisUntilFinished / 1000);
				}

				public void onFinish() {
					txtvw_timer.setText("Up!");
				}
			}.start();
		}

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(question.answers.length()!=0){
					new SubmitAnswerToWS().execute(QuizPage.this);
				}else{
					Toast.makeText(getBaseContext(), "Please answer...", Toast.LENGTH_SHORT).show();
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
		fragment_question.disableBtns();
	}

	public void gotoLoginPage(){
		Intent intent = new Intent(this,LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
