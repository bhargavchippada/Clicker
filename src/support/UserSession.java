package support;

import org.json.JSONArray;

public class UserSession {
	String classname = "UserSession";
	
	public String username;
	public String password;
	public String name;
	public String clsnm;
	public String ip;
	
	public void clear(){
		username = null;
		password = null;
		name= null;
		clsnm= null;
	}
	
	public boolean isSessionValid(){
		if(username==null || password==null || name==null){
			return false;
		}else return true;
	}
	
	public void print(){
		if(isSessionValid()){
			Utils.logv(classname, username+","+password+","+name+","+clsnm);
		}
	}
}
