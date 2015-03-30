package support;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class Utils {

	public static void println(String s){
		System.out.println(s);
	}

	public static void logv(String classname, String s, Exception e){
		if(e!=null) Log.v("Clicker", classname+": "+s, e);
		else{
			Log.v("Clicker", classname+": "+s);
		}
	}

	public static void logv(String classname, String msg){
		Log.v("Clicker",classname+" : "+msg);
	}

	public static String getIpAddress(Context context){
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		int ipAddress = wifiInf.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
		return ip;
	}
}
