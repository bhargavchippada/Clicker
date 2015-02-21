package support;

public class AppSettings{
	public static String classname = "AppSettings";
	
	//default server service uri
	public static String LoginServiceUri = "";
	
	//Android shared preference key 
	public static String preference_file_key = "clickerSP";
	
	//format of sending data
	public static String JSON_TYPE = "application/json";
	
	public static String authentication = "authentication";
	public static String loadquiz = "pushquiz";
	public static String submitanswer = "receiveanswer";
	
	public static synchronized void updateUrl(String ip, String p, String urlpath){
		LoginServiceUri = "http://"+ip+":"+p+"/"+urlpath+"/";
		Utils.logv(classname,LoginServiceUri, null);
	}
}
