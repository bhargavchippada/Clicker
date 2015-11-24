package servercommunication;

import android.app.Activity;
import android.text.Html;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.logging.Level;

import clickr.QuizPage;
import support.AppSettings;
import support.Question;
import support.UserSession;

/**
 * This class submits answer to the server and receives the response
 *
 * @author bhargav
 */
public class SubmitAnswerToWS extends ServerCommunicator {
    String ServletName = "ReceiveAnswer";

    private static final int SERVEROFF = 0;
    private static final int LOGGEDOFF = 1;
    private static final int QUIZOFF = 2;
    private static final int ATTEMPTED = 3;
    private static final int SUBMITTED = 4;

    // reference  to the calling activity
    private QuizPage _activity;

    @Override
    public void execute(Activity activity) {

        _activity = (QuizPage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            req_json.put("uid", UserSession.username);
            req_json.put("servername", UserSession.servername);
            req_json.put("classid", UserSession.classid);
            req_json.put("myanswer", Question.answers);
            req_json.put("quizid", Question.quizid);
            Question.submitTime = new Date().getTime();
            Question.timeTook = Question.submitTime - Question.startTime;
            req_json.put("timetook", Question.timeTook / 1000);
            LOGGER.info("client request: " + req_json.toString());
            executeRequest(AppSettings.LoginServiceUri + ServletName, req_json);
        } catch (JSONException e) {
            LOGGER.log(Level.SEVERE, "JSON object creation error!", e);
            e.printStackTrace();
        }
    }

    /**
     * Handles UI changes and response handling. This method is called in the "edt" thread.
     */
    @Override
    protected void _showInUI(int uiStatus) {
        if (uiStatus == BEFORE) {
            _activity.updateUI("Trying to submit answer..");
        } else if (uiStatus == AFTER) {
            if (getResponse() != null) {
                try {
                    Integer status = (Integer) getResponse().get("status");
                    if (status == SUCCESS) {
                        Integer statuscode = (Integer) getResponse().get("statuscode");
                        if (statuscode == SERVEROFF) {
                            Toast.makeText(_activity, "Server is stopped!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Server is stopped!");
                            _activity.gotoLoginPage();
                        } else if (statuscode == LOGGEDOFF) {
                            Toast.makeText(_activity, "Please Login!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Login failed!!");
                            _activity.gotoLoginPage();
                        } else if (statuscode == QUIZOFF) {
                            Toast.makeText(_activity, "Quiz has stopped!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Quiz has stopped!!");
                            _activity.disableBtns();
                        } else if (statuscode == ATTEMPTED) {
                            Toast.makeText(_activity, "Quiz already attempted!!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Quiz already attempted!!");
                            _activity.disableBtns();
                        } else if (statuscode == SUBMITTED) {
                            Toast.makeText(_activity, "Answers submitted!", Toast.LENGTH_SHORT).show();
                            _activity.updateUI("Answer submitted!");
                            if(getResponse().has("feedback")){
                                String feedback = (String) getResponse().get("feedback");
                                _activity.updateUI(feedback);
                            }
                            _activity.disableBtns();
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
                _activity.gotoLoginPage();
            }
        }
    }

}

