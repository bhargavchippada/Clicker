package com.iitbombay.clicker;

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

import com.iitbombay.datahandler.ConnectWebService;
import com.iitbombay.datahandler.PingWebService;

public class MainActivity extends Activity{
	String ClassName = "MainActivity";
	
	EditText edtxt_ipaddress;
	EditText edtxt_port;
	EditText edtxt_url;
	CheckBox cbox_savesettings;
	TextView txtvw_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		edtxt_ipaddress = (EditText) findViewById(R.id.edtxt_ipaddress);
		edtxt_port = (EditText) findViewById(R.id.edtxt_port);
		edtxt_url = (EditText) findViewById(R.id.edtxt_url);
		cbox_savesettings = (CheckBox) findViewById(R.id.cbox_savesettings);
		
		Button btn_connect = (Button) findViewById(R.id.btn_connect);
		txtvw_status = (TextView) findViewById(R.id.txtvw_status);
		
		initializeViews();
		
		btn_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cbox_savesettings.isChecked()){
					updateSharedPref(1);
				}else{
					updateSharedPref(0);
				}
				
				new ConnectWebService().execute(MainActivity.this);
				//new PingWebService().execute(MainActivity.this, "ping");
				Utils.logv(ClassName, "connect button was pressed",null);
			}
		});
		
	}
	
	public void updateUI(String msg){
		txtvw_status.setText(msg);
	}
	
	void updateSharedPref(int bool){
		String ip="",p="",urlp="";
		boolean check = false;
		if(bool == 1){
			ip = edtxt_ipaddress.getText().toString();
			p = edtxt_port.getText().toString();
			urlp = edtxt_url.getText().toString();
			check = true;
		}
		SharedPreferences sharedPref = getBaseContext()
				.getSharedPreferences(AppSettings.preference_file_key, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.saved_ipadress),ip);
		editor.putString(getString(R.string.saved_port),p);
		editor.putString(getString(R.string.saved_url),urlp);
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
		cbox_savesettings.setChecked(sharedPref.getBoolean(getString(R.string.saved_savesettings), false));
	}

	public void gotoLoginPage(){
		startActivity(new Intent(this,LoginPage.class));
	}
}
