package support;

import java.util.ArrayList;

public class UserSession {
	String classname = "UserSession";
	
	public String rollnumber;
	public String name;
	public String clsnm;
	public String SESSIONID;
	public ArrayList<String> answers = new ArrayList<String>();
	
	public void initialize(){
		rollnumber = null;
		name= null;
		clsnm= null;
		SESSIONID = null;
		answers.clear();
	}
	
	void print(){
		Utils.logv(classname, rollnumber+","+name+","+clsnm+","+SESSIONID+","+answers);
	}
}
