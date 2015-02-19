package com.iitbombay.clicker;

import org.json.JSONException;
import org.json.JSONObject;

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

//connection settings page, uses ConnectWebService class 

public class LoginPage extends Activity{
	public static String ClassName = "LoginPage";
	
	EditText edtxt_ipaddress;
	EditText edtxt_port;
	EditText edtxt_url;
	EditText edtxt_username;
	EditText edtxt_password;
	
	CheckBox cbox_savesettings;
	
	Button btn_connect;
	TextView txtvw_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationContext appcontext = (ApplicationContext)getApplicationContext();
		setContentView(R.layout.login_page);
		
		edtxt_ipaddress = (EditText) findViewById(R.id.edtxt_ipaddress);
		edtxt_port = (EditText) findViewById(R.id.edtxt_port);
		edtxt_url = (EditText) findViewById(R.id.edtxt_url);
		edtxt_username = (EditText) findViewById(R.id.edtxt_username);
		edtxt_password = (EditText) findViewById(R.id.edtxt_password);
		cbox_savesettings = (CheckBox) findViewById(R.id.cbox_savesettings);
		
		btn_connect = (Button) findViewById(R.id.btn_connect);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		
		//initializes view values
		initializeViews();
		
		btn_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cbox_savesettings.isChecked()){
					updateSharedPref(1);
				}else{
					updateSharedPref(0);
				}
				
				new AuthenticateWS().execute(LoginPage.this);
				Utils.logv(ClassName, "Login button os pressed",null);
			}
		});
		
	}
	
	//Update the status....
	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}
	
	// bool == 0 means nothing is saved, bool == 1 means the settings are saved
	void updateSharedPref(int bool){
		String ip="",p="",urlp="";
		String user="",pass="";
		boolean check = false;
		if(bool == 1){
			ip = edtxt_ipaddress.getText().toString();
			p = edtxt_port.getText().toString();
			urlp = edtxt_url.getText().toString();
			user = edtxt_username.getText().toString();
			pass = edtxt_password.getText().toString();
			check = true;
		}
		SharedPreferences sharedPref = getBaseContext()
				.getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.saved_ipadress),ip);
		editor.putString(getString(R.string.saved_port),p);
		editor.putString(getString(R.string.saved_url),urlp);
		editor.putString(getString(R.string.saved_username),user);
		editor.putString(getString(R.string.saved_password),pass);
		editor.putBoolean(getString(R.string.saved_savesettings), check);
		editor.commit();
		AppSettings.updateUrl(edtxt_ipaddress.getText().toString(), 
				edtxt_port.getText().toString(), 
				edtxt_url.getText().toString());
	}
	
	void initializeViews(){
		SharedPreferences sharedPref = getBaseContext()
				.getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
		edtxt_ipaddress.setText(sharedPref.getString(getString(R.string.saved_ipadress), ""));
		edtxt_port.setText(sharedPref.getString(getString(R.string.saved_port), ""));
		edtxt_url.setText(sharedPref.getString(getString(R.string.saved_url), ""));
		edtxt_username.setText(sharedPref.getString(getString(R.string.saved_username), ""));
		edtxt_password.setText(sharedPref.getString(getString(R.string.saved_password), ""));
		cbox_savesettings.setChecked(sharedPref.getBoolean(getString(R.string.saved_savesettings), false));
	}
	
	public String getUsername(){
		return edtxt_username.getText().toString();
	}
	
	public String getPassword(){
		return edtxt_password.getText().toString();
	}
	
	public void gotoHomePage(JSONObject dataFromServlet) throws JSONException{
		Intent intent = new Intent(this,HomePage.class);
		intent.putExtra("uid", dataFromServlet.getString("uid"));
		intent.putExtra("name", dataFromServlet.getString("name"));
		intent.putExtra("clsnm", dataFromServlet.getString("clsnm"));
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		txtvw_status.setText("Click to login");
	}
}
