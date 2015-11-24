package support;


public class AppSettings {
    /**
     * Default server service uri
     */
    public static String LoginServiceUri = "";

    /**
     * Android shared preference key
     */
    public static String preference_file_key = "clickerSP";

    /**
     * update url
     *
     * @param String ip-address
     * @param String port
     */
    public static synchronized void updateUrl(String ip, String p) {
        LoginServiceUri = "http://" + ip + ":" + p + "/" + "ClickrServer/";
    }
}
