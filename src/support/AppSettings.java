package support;

public class AppSettings{
	public static String classname = "AppSettings";
	
	/**Default server service uri
	 * 
	 */
	public static String LoginServiceUri = "";
	
	/**Android shared preference key 
	 * 
	 */
	public static String preference_file_key = "clickerSP";
	
	//
	/**Json format to send data
	 * 
	 */
	public static String JSON_TYPE = "application/json";
	
	/**authentication servlet url path
	 * 
	 */
	public static String authentication = "authentication";
	
	/**load quiz servlet url path
	 * 
	 */
	public static String loadquiz = "pushquiz";
	
	/**submit answer servlet url path
	 * 
	 */
	public static String submitanswer = "receiveanswer";
	
	/**update url
	 * @param String ip-address
	 * @param String port
	 */
	public static synchronized void updateUrl(String ip, String p){
		LoginServiceUri = "http://"+ip+":"+p+"/"+"ClickrServer/";
		Utils.logv(classname,LoginServiceUri, null);
	}
}
