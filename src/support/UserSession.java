package support;


public class UserSession {
	String classname = "UserSession";

	public String username; // username of the user
	public String password; //password of the user
	public String name; //name of the username
	public String clsnm; // class name
	public String ip; // ip-address of the user

	
	/**
	 * Clear the user info
	 */
	public void clear(){
		username = null;
		password = null;
		name = null;
		clsnm = null;
		ip = null;
	}

	/**
	 * @return whether or not the userSession is valid or not
	 */
	public boolean isSessionValid(){
		if(username==null || password==null || name==null){
			return false;
		}else return true;
	}

	/**Print the userSession details
	 *
	 */
	public void print(){
		if(isSessionValid()){
			Utils.logv(classname, username+","+password+","+name+","+clsnm);
		}
	}
}
