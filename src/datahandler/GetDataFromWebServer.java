package datahandler;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import support.AppSettings;
import support.Utils;
import clickr.ApplicationContext;

/**This class is for handling sending and receiving data using post method to the specified url
 * @author bhargav
 *
 */
public class GetDataFromWebServer {
	String classname;
	Exception ex;
	JSONObject dataFromServlet;

	/**Initializes the classname of this class to the name of the class that created this class's object,
	 *  this is used for debugging purposes
	 * @param String clsnm
	 */
	public GetDataFromWebServer(String clsnm) {
		classname = clsnm;
	}

	/**This method is called in a non-"edt" thread. This method sends the request (req_entity) to the url path
	 * using post method, it sets the dataFromServlet variable to the response received (which is in json format).
	 * <br>If there is some exception then dataFromServlet is NULL and ex variable is set to the exception
	 * occurred.
	 * @param String path
	 * @param StringEntity req_entity
	 */
	public void doInBackgroundPost(String path, StringEntity req_entity) {
		Utils.logv(classname, "background task - start");
		long startTime = System.currentTimeMillis();

		try {
			// create the post method
			HttpPost postMethod = new HttpPost(path);

			req_entity.setContentType(AppSettings.JSON_TYPE);
			// associating request entity with post method
			postMethod.setEntity(req_entity);

			final DefaultHttpClient httpClient = ApplicationContext.getThreadSafeClient();
			// response
			httpClient.execute(postMethod, new ResponseHandler<Void>() {
				public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					HttpEntity resp_entity = response.getEntity();

					if (resp_entity != null) {

						try {
							String resp_str = EntityUtils.toString(resp_entity);
							dataFromServlet = new JSONObject(resp_str);
							Utils.logv(classname,"data size from servlet=" + resp_entity.getContentLength());
							Utils.logv(classname,"json data from servlet=" + dataFromServlet);
						}catch (Exception e) {
							ex = e;
							Utils.logv(classname,"problem processing post response",e);
							e.printStackTrace();
						}
					}else {
						Utils.logv(classname,"No response entity");
						throw new IOException(
								new StringBuffer()
								.append("HTTP response : ").append(response.getStatusLine())
								.toString());
					}
					return null;
				}
			});

		}catch (Exception e) {
			ex = e;
			Utils.logv(classname,"Error processing the request/response",e);
			e.printStackTrace();
		}

		Utils.logv(classname,"background task - end");
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		Utils.logv(classname,elapsedTime+"ms");
	}
}
