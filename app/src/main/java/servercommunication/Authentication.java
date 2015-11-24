package servercommunication;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

import clickr.LoginPage;
import support.AppSettings;
import support.UserSession;


/**
 * This class is for authentication
 *
 * @author bhargav
 */
public class Authentication extends ServerCommunicator {
    String ServletName = "Authentication";

    private static final int SERVEROFF = 0;
    private static final int LOGINSUCCESS = 1;
    private static final int LOGINFAIL = 2;
    private static final int INVALIDSERVERNAME = 3;

    // reference  to the calling activity
    private LoginPage _activity;

    @Override
    public void execute(Activity activity) {
        _activity = (LoginPage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            req_json.put("uid", _activity.getUsername());
            req_json.put("pwd", _activity.getPassword());
            req_json.put("servername", _activity.getServerName());
            LOGGER.info("client request: " + req_json.toString());
            executeRequest(AppSettings.LoginServiceUri + ServletName, req_json);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "JSON object creation error!", e);
            e.printStackTrace();
            return;
        }
    }

    /**
     * Handles UI changes and response handling. This method is called in the "edt" thread.
     */
    @Override
    protected void _showInUI(int uiStatus) {
        if (uiStatus == BEFORE) {
            _activity.updateUI("Trying to connect to Server..");
        } else if (uiStatus == AFTER) {
            if (getResponse() != null) {
                try {
                    Integer status = (Integer) getResponse().get("status");
                    if (status == SUCCESS) {
                        Integer statuscode = (Integer) getResponse().get("statuscode");
                        if (statuscode == SERVEROFF) {
                            Toast.makeText(_activity, "Server is not ready, Wait!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Server is not ready, Wait!");
                        } else if (statuscode == LOGINSUCCESS) {
                            Toast.makeText(_activity, "Login success", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Login success");
                            synchronized (UserSession.class) {
                                UserSession.clear();
                                UserSession.username = _activity.getUsername();
                                UserSession.name = getResponse().getString("name");
                                UserSession.classname = getResponse().getString("classname");
                                UserSession.ip = getResponse().getString("ip");
                                UserSession.servername = getResponse().getString("servername");
                                UserSession.classid = getResponse().getInt("classid");
                            }
                            _activity.gotoHomePage();
                        } else if (statuscode == LOGINFAIL) {
                            Toast.makeText(_activity, "Incorrect username or password!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Incorrect username or password!");
                        } else if (statuscode == INVALIDSERVERNAME) {
                            Toast.makeText(_activity, "Invalid servername!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Invalid servername!");
                        } else {
                            Toast.makeText(_activity, "Invalid status code!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Invalid status code!");
                        }
                    } else if (status == FAIL) {
                        Toast.makeText(_activity, "Server: error processing request!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Server: error processing request!");
                    } else {
                        Toast.makeText(_activity, "Invalid status!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Invalid status!");
                    }
                } catch (JSONException e) {
                    LOGGER.log(Level.SEVERE, "dataFromServlet retrieval error!", e);
                    e.printStackTrace();
                }
            } else if (getException() != null) {
                Toast.makeText(_activity,
                        getException().getMessage() == null ? "Error" : "Error - " + getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                _activity.updateUI("Connection failed");
            }
        }
    }
}

