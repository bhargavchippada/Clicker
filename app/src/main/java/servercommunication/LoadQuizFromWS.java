package servercommunication;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

import clickr.ApplicationContext;
import clickr.HomePage;
import support.AppSettings;
import support.Question;
import support.Utils;

/**
 * This class is for loading the quiz from the server
 *
 * @author bhargav
 */
public class LoadQuizFromWS extends ServerCommunicator {
    String CLASSNAME = "LoadQuizFromWS";

    // reference  to the calling activity
    private HomePage _activity;

    @Override
    protected String getCLASSNAME() {
        return CLASSNAME;
    }

    @Override
    public void execute(Activity activity) {

        _activity = (HomePage) activity;

        //initialize request parameters
        try {
            JSONObject req_json = new JSONObject();
            req_json.put("uid", ApplicationContext.getThreadSafeUserSession().username);
            LOGGER.info("client request: " + req_json.toString());
            executeRequest(AppSettings.LoginServiceUri + AppSettings.loadquiz, req_json);
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
                    if (status == 0) {
                        Toast.makeText(_activity, "Not authorized!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Authorization failed!", View.INVISIBLE);
                        _activity.gotoLoginPage();
                    } else if (status == 1) {
                        Toast.makeText(_activity, "Quiz hasn't started!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Quiz hasn't started!", View.INVISIBLE);
                    } else if (status == 2) {
                        Toast.makeText(_activity, "Quiz retrievel Success!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Quiz retrievel Success!", View.INVISIBLE);
                        //initialize the Question object
                        synchronized (Question.class) {
                            Question question = ApplicationContext.getThreadSafeQuestion();
                            question.clear();
                            question.ID = (String) getResponse().get("qid");
                            question.title = (String) getResponse().get("title");
                            question.question = (String) getResponse().get("question");
                            question.type = (Integer) getResponse().get("type");
                            question.options = (JSONArray) getResponse().get("options");
                            question.feedback = (boolean) getResponse().get("feedback");
                            question.timed = (boolean) getResponse().get("timed");
                            question.time = (Integer) getResponse().get("time");
                            question.print();
                        }
                        _activity.gotoQuizPage();
                    } else if (status == 3) {
                        Toast.makeText(_activity, "You already attempted the quiz!", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("You already attempted the quiz!", View.INVISIBLE);
                    } else {
                        Toast.makeText(_activity, "Invalid satus code", Toast.LENGTH_SHORT).show();
                        _activity.updateUI("Invalid satus code", View.INVISIBLE);
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
                _activity.updateUI("Connection failed!", View.INVISIBLE);
                _activity.gotoLoginPage();
            }
        }
    }

}

