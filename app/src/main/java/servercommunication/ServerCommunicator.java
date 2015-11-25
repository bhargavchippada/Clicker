package servercommunication;

import android.app.Activity;
import android.os.Handler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is for handling sending and receiving data using post method to the specified url
 *
 * @author bhargav
 */
public abstract class ServerCommunicator {

    protected final Logger LOGGER = Logger.getLogger(getClass().getName());
    private Exception ex;
    private JSONObject dataFromServlet;
    protected final static int BEFORE = 0;
    protected final static int AFTER = 1;

    protected final static int SUCCESS = 1;
    protected final static int FAIL = 0;

    protected Exception getException() {
        return ex;
    }

    protected JSONObject getResponse() {
        return dataFromServlet;
    }

    /**
     * This method is called in a non-"edt" thread. This method sends the request (req_json) to the url path
     * using post method, it sets the dataFromServlet variable to the response received (which is in json format).
     * <br>If there is some exception then dataFromServlet is NULL and ex variable is set to the exception
     * occurred.
     */
    protected void doInBackgroundPost(String path, JSONObject req_json) {
        LOGGER.info("background task - start");
        long startTime = System.currentTimeMillis();

        try {
            // create the post method
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            String out_req = req_json.toString();
            out.write(out_req.getBytes());
            out.flush();
            out.close();

            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            in.close();

            dataFromServlet = new JSONObject(responseStrBuilder.toString());
            LOGGER.info("json data from servlet=" + dataFromServlet);

        } catch (Exception e) {
            dataFromServlet = null;
            ex = e;
            LOGGER.log(Level.SEVERE, "Error processing the request/response", e);
            e.printStackTrace();
        }

        LOGGER.info("background task - end");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        LOGGER.info("Time taken: " + elapsedTime + "ms");
    }

    protected void executeRequest(final String urlpath, final JSONObject req_json) {
        // performs rendering in the "edt" thread, before background operation starts
        final Runnable runInUIThread1 = new Runnable() {
            public void run() {
                _showInUI(BEFORE);
            }
        };

        // performs rendering in the "edt" thread, after background operation is complete
        final Runnable runInUIThread2 = new Runnable() {
            public void run() {
                _showInUI(AFTER);
            }
        };

        // link: http://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
        // allows non-"edt" thread to be re-inserted into the "edt" queue
        final Handler uiThreadCallback = new Handler();

        new Thread() {
            @Override
            public void run() {
                uiThreadCallback.post(runInUIThread1);
                doInBackgroundPost(urlpath, req_json);
                uiThreadCallback.post(runInUIThread2);
            }
        }.start();
    }

    protected abstract void execute(Activity activity);

    protected abstract void _showInUI(int uiStatus);
}
