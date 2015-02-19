package support;

import java.util.ArrayList;


public class UserProfile {
	public static String classname = "UserProfile";
	
	public static String rollnumber="null";
	public static String password="null";
	public static String name="null";
	public static String ipaddress="null";
	public static String clsnm="null";
	int status = 0; //0 means hasn't logged in yet, 1 means connected, 2 means logged in
	// 3 means started quiz, 4 means finished quiz, 5 means disconnected after logged in
	public static ArrayList<String> answers = new ArrayList<String>();
	
	public static void initialize(){
		rollnumber = "null";
		password="null";
		name="null";
		ipaddress="null";
		clsnm="null";
		answers.clear();
	}
	
	void print(){
		Utils.logv(classname, rollnumber+","+password+","+name);
	}
	
	public String getPassword(){
		return password;
	}
}
