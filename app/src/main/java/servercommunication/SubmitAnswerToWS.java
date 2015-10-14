package servercommunication;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.logging.Level;

import clickr.ApplicationContext;
import clickr.QuizPage;
import support.AppSettings;
import support.Question;
import support.UserSession;
import support.Utils;

/**
 * This class submits answer to the server and receives the response
 *
 * @author bhargav
 */
public class SubmitAnswerToWS extends ServerCommunicator {
    String CLASSNAME = "SubmitAnswerToWS";

    // reference  to the calling activity
    private QuizPage _activity;

    @Override
    protected String getCLASSNAME() {
        return CLASSNAME;
    }

    @Override
    public void execute(Activity activity) {

        _activity = (QuizPage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            UserSession userSession = ApplicationContext.getThreadSafeUserSession();
            Question question = ApplicationContext.getThreadSafeQuestion();
            req_json.put("uid", userSession.username);
            req_json.put("myanswer", question.answers);
            req_json.put("qid", question.ID);
            question.submitTime = new Date().getTime();
            question.timeTook = question.submitTime - question.startTime;
            req_json.put("starttime", (long) question.startTime / 1000);
            req_json.put("submittime", (long) question.submitTime / 1000);
            req_json.put("timetook", (long) question.timeTook / 1000);
            LOGGER.info("client request: " + req_json.toString());
            executeRequest(AppSettings.LoginServiceUri + AppSettings.submitanswer, req_json);
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
            _activity.updateUI("Trying to submit answer..");
        } else if (uiStatus == AFTER) {
            if (getResponse() != null) {
                try {
                    Integer status = (Integer) getResponse().get("status");
                    if (status == -1) {
                        Toast.makeText(_activity, "Failed to submit answer", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Failed to submit answer");
                    } else if (status == 0) {
                        Toast.makeText(_activity, "Your are not authorized!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Your are not authorized!");
                        _activity.gotoLoginPage();
                    } else if (status == 1) {
                        Toast.makeText(_activity, "Sorry, this quiz was over!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Sorry, this quiz was over!");
                        _activity.gotoLoginPage();
                    } else if (status == 2) {
                        Toast.makeText(_activity, "You have already submitted", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("You have already submitted");
                    } else if (status == 3) {
                        Toast.makeText(_activity, "Answer submitted!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Answer submitted!");
                        String feedback = (String) getResponse().get("feedback");
                        _activity.updateUI(feedback);
                        _activity.disableBtns();
                    } else {
                        Toast.makeText(_activity, "Invalid status code!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Invalid status code!");
                        _activity.gotoLoginPage();
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
                _activity.gotoLoginPage();
            }
        }
    }

}

