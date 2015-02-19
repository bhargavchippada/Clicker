package com.iitbombay.clicker;

import java.util.HashMap;

import support.AppSettings;
import support.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.iitbombay.datahandler.AuthenticateWS;

public class LoginPage extends Activity{
	String ClassName = "LoginPage";
	
	EditText edtxt_username;
	EditText edtxt_password;
	CheckBox cbox_savedetails;
	
	Button btn_login;
	TextView txtvw_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		
		edtxt_username = (EditText) findViewById(R.id.edtxt_username);
		edtxt_password = (EditText) findViewById(R.id.edtxt_password);
		cbox_savedetails = (CheckBox) findViewById(R.id.cbox_savedetails);
		
		btn_login = (Button) findViewById(R.id.btn_login);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		
		initializeViews();
		
		btn_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cbox_savedetails.isChecked()){
					updateSharedPref(1);
				}else{
					updateSharedPref(0);
				}
				
				new AuthenticateWS().execute(LoginPage.this);
				Utils.logv(ClassName, "Login button was pressed",null);
			}
		});
		
	}
	
	//Update the status....
	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}
	
	// bool == 0 means nothing is saved, bool == 1 means the settings are saved
	void updateSharedPref(int bool){
		String user="",pass="";
		boolean check = false;
		if(bool == 1){
			user = edtxt_username.getText().toString();
			pass = edtxt_password.getText().toString();
			check = true;
		}
		SharedPreferences sharedPref = getBaseContext()
				.getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.saved_username),user);
		editor.putString(getString(R.string.saved_password),pass);
		editor.putBoolean(getString(R.string.saved_savedetails), check);
		editor.commit();
	}
	
	void initializeViews(){
		SharedPreferences sharedPref = getBaseContext()
				.getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
		edtxt_username.setText(sharedPref.getString(getString(R.string.saved_username), ""));
		edtxt_password.setText(sharedPref.getString(getString(R.string.saved_password), ""));
		cbox_savedetails.setChecked(sharedPref.getBoolean(getString(R.string.saved_savedetails), false));
	}
	
	public String getUsername(){
		return edtxt_username.getText().toString();
	}
	
	public String getPassword(){
		return edtxt_password.getText().toString();
	}
	
	public void gotoHomePage(HashMap<String, String> dataFromServlet){
		Intent intent = new Intent(this,HomePage.class);
		intent.putExtra("rollnum", dataFromServlet.get("uid"));
		intent.putExtra("password", dataFromServlet.get("pwd"));
		intent.putExtra("name", dataFromServlet.get("name"));
		intent.putExtra("clsnm", dataFromServlet.get("clsnm"));
		startActivity(intent);
	}
	
	public void gotoConnectPage(){
		Intent intent = new Intent(this,MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
