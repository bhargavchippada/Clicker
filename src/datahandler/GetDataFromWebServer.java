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
import clicker.ApplicationContext;

public class GetDataFromWebServer {
	String classname;
	Exception ex;
	JSONObject dataFromServlet;

	public GetDataFromWebServer(String clsnm) {
		classname = clsnm;
	}

	// this method is called in a non-"edt" thread
	public void doInBackgroundPost(String path, StringEntity req_entity) {
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
						}catch (Exception e) {
							ex = e;
							e.printStackTrace();
						}
					}else {
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
			e.printStackTrace();
		}

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
	}
}
