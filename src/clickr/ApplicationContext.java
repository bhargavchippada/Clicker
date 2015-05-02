package clickr;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import support.Question;
import support.UserSession;
import support.Utils;
import android.app.Application;

public class ApplicationContext extends Application{
	private static String classname = "ApplicationContext";

	private static int NetworkConnectionTimeout_ms = 5000;
	private static DefaultHttpClient httpClient;
	private static UserSession usersession;
	private static Question question;	

	// link: http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	/**creates a httpclient if one doesn't exist for the app, makes sure only a single httpclient is defined for a single run of the app
	 * @return DefaultHttpClient
	 */
	public synchronized static DefaultHttpClient getThreadSafeClient() {
		if(httpClient!=null) return httpClient;
		Utils.logv(classname, "New httpClient is created");

		// set params for connection...
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
		HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
		//ConnManagerParams.setMaxTotalConnections(params, 5);

		httpClient = new DefaultHttpClient(params);
		ClientConnectionManager mgr = httpClient.getConnectionManager();
		httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
				mgr.getSchemeRegistry()), params);
		return httpClient;
	}

	
	/**creates a UserSession object if one doesn't exist for the app, makes sure only a single UserSession object is defined for a single run of the app
	 * @return UserSession
	 */
	public synchronized static UserSession getThreadSafeUserSession(){
		if(usersession==null) usersession = new UserSession();
		return usersession;
	}
	
	/**creates a Question object if one doesn't exist for the app, makes sure only a single Question object is defined for a single run of the app
	 * <br> Since the app supports only a single question this is enough
	 * @return Question
	 */
	public synchronized static Question getThreadSafeQuestion(){
		if(question==null) question = new Question();
		return question;
	}

	/** clears usersession and question objects  
	 * 
	 */
	public synchronized static void invalidateSession(){
		if(usersession!=null) {
			usersession.clear();
			Utils.logv(classname, "usersession wiped");
		}
		if(question!=null) {
			question.clear();
			Utils.logv(classname, "question wiped");
		}
	}
}
