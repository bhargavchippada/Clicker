package support;

import java.util.ArrayList;

import org.json.JSONArray;

public class UserSession {
	String classname = "UserSession";
	
	public String username;
	public String password;
	public String name;
	public String clsnm;
	public JSONArray answers = new JSONArray();
	
	public void clear(){
		username = null;
		password = null;
		name= null;
		clsnm= null;
		answers=new JSONArray();
	}
	
	public boolean isSessionValid(){
		if(username==null || password==null || name==null){
			return false;
		}else return true;
	}
	
	public void print(){
		if(isSessionValid()){
			Utils.logv(classname, username+","+password+","+name+","+clsnm+","+answers);
		}
	}
}
