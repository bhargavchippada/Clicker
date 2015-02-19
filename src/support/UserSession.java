package support;

import java.util.ArrayList;

public class UserSession {
	String classname = "UserSession";
	
	public String username;
	public String name;
	public String clsnm;
	public ArrayList<String> answers = new ArrayList<String>();
	
	public void clear(){
		username = null;
		name= null;
		clsnm= null;
		answers.clear();
	}
	
	public boolean isSessionValid(){
		if(username==null || name==null || clsnm==null){
			return false;
		}else return true;
	}
	
	public void print(){
		if(isSessionValid()){
			Utils.logv(classname, username+","+name+","+clsnm+","+answers);
		}
	}
}
