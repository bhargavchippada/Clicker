package support;

public class AppSettings {
	public static String ClassName = "AppSettings";
	
	//default server service uri
	public static String LoginServiceUri = "";
	
	//Android shared preference key 
	public static String preference_file_key = "clickerSP";
	
	
	//
	public static void updateUrl(String ip, String p, String urlpath){
		LoginServiceUri = "http://"+ip+":"+p+"/"+urlpath+"/";
		Utils.logv(ClassName,LoginServiceUri, null);
	}
}
