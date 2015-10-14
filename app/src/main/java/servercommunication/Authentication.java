package servercommunication;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

import clickr.ApplicationContext;
import clickr.LoginPage;
import support.AppSettings;
import support.UserSession;
import support.Utils;


/**
 * This class is for authentication
 *
 * @author bhargav
 */
public class Authentication extends ServerCommunicator {
    String CLASSNAME = "Authentication";
    String ServletName = "Authentication";

    // reference  to the calling activity
    private LoginPage _activity;

    @Override
    protected String getCLASSNAME() {
        return CLASSNAME;
    }

    @Override
    public void execute(Activity activity) {
        _activity = (LoginPage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            req_json.put("uid", _activity.getUsername());
            req_json.put("pwd", _activity.getPassword());
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
                    if (status == 0) {
                        Toast.makeText(_activity, "Server is not ready, Wait!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Server is not ready, Wait!");
                    } else if (status == 1) {
                        Toast.makeText(_activity, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Incorrect username or password");
                    } else if (status == 2) {
                        Toast.makeText(_activity, "Login success", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Login success");
                        synchronized (UserSession.class) {
                            UserSession usersession = ApplicationContext.getThreadSafeUserSession();
                            usersession.clear();
                            usersession.username = _activity.getUsername();
                            usersession.password = _activity.getPassword();
                            usersession.name = getResponse().getString("name");
                            usersession.clsnm = getResponse().getString("clsnm");
                            usersession.ip = getResponse().getString("ip");
                        }
                        _activity.gotoHomePage();
                    } else if (status == -1) {
                        Toast.makeText(_activity, "Server: error processing request", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Server: error processing request");
                    } else {
                        Toast.makeText(_activity, "Invalid satus code", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Invalid satus code");
                    }
                } catch (JSONException e) {
                    Utils.logv(CLASSNAME, "dataFromServlet retrieval error!", e);
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

