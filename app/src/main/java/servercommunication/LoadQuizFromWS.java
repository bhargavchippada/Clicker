package servercommunication;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

import clickr.HomePage;
import support.AppSettings;
import support.Question;
import support.UserSession;

/**
 * This class is for loading the quiz from the server
 *
 * @author bhargav
 */
public class LoadQuizFromWS extends ServerCommunicator {
    String ServletName = "PushQuiz";

    private static final int SERVEROFF = 0;
    private static final int LOGGEDOFF = 1;
    private static final int QUIZOFF = 2;
    private static final int ATTEMPTED = 3;
    private static final int RETRIEVED = 4;

    // reference  to the calling activity
    private HomePage _activity;

    @Override
    public void execute(Activity activity) {

        _activity = (HomePage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            req_json.put("uid", UserSession.username);
            req_json.put("servername", UserSession.servername);
            req_json.put("classid", UserSession.classid);
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
            _activity.updateUI("Trying to start quiz..", View.VISIBLE);
        } else if (uiStatus == AFTER) {
            if (getResponse() != null) {
                try {
                    Integer status = (Integer) getResponse().get("status");
                    if (status == SUCCESS) {
                        Integer statuscode = (Integer) getResponse().get("statuscode");
                        if (statuscode == SERVEROFF) {
                            Toast.makeText(_activity, "Server is not ready, Wait!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Server is not ready, Wait!", View.INVISIBLE);
                            _activity.gotoLoginPage();
                        } else if (statuscode == LOGGEDOFF) {
                            Toast.makeText(_activity, "Please Login!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Login failed!!", View.INVISIBLE);
                            _activity.gotoLoginPage();
                        } else if (statuscode == QUIZOFF) {
                            Toast.makeText(_activity, "Quiz hasn't started yet!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Quiz hasn't started yet!!", View.INVISIBLE);
                        } else if (statuscode == ATTEMPTED) {
                            Toast.makeText(_activity, "Quiz already attempted!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Login failed!!", View.INVISIBLE);
                            _activity.gotoLoginPage();
                        } else if (statuscode == RETRIEVED) {
                            Toast.makeText(_activity, "Quiz retrievel Success!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Quiz retrievel Success!", View.INVISIBLE);

                            synchronized (Question.class) {
                                Question.clear();
                                Question.quizid = Integer.parseInt((String) getResponse().get("quizid"));
                                Question.question = (JSONObject) getResponse().get("question");
                                Question.options = (JSONArray) getResponse().get("options");
                                Question.feedback = Boolean.parseBoolean((String) getResponse().get("feedback"));
                                Question.timedquiz = Boolean.parseBoolean((String) getResponse().get("timedquiz"));
                                Question.quiztime = Integer.parseInt((String) getResponse().get("quiztime"));
                                Question.print();
                            }
                            //_activity.gotoQuizPage();
                        } else {
                            Toast.makeText(_activity, "Invalid status code!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Invalid status code!", View.INVISIBLE);
                        }
                    } else if (status == FAIL) {
                        Toast.makeText(_activity, "Server: error processing request!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Server: error processing request!", View.INVISIBLE);
                    } else {
                        Toast.makeText(_activity, "Invalid status!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Invalid status!", View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    LOGGER.log(Level.SEVERE, "dataFromServlet retrieval error!", e);
                    e.printStackTrace();
                }
            } else if (getException() != null) {
                Toast.makeText(_activity,
                        getException().getMessage() == null ? "Error" : "Error - " + getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
                _activity.updateUI("Connection failed!", View.INVISIBLE);
                _activity.gotoLoginPage();
            }
        }
    }

}

