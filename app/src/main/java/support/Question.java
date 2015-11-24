package support;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author bhargav
 */
public class Question {
    private static String CLASSNAME = "Question";
    private final static Logger LOGGER = Logger.getLogger(CLASSNAME);

    public static Integer quizid;
    public static JSONObject question;
    public static JSONArray options;
    public static Boolean feedback;
    public static Boolean timedquiz;
    public static Integer quiztime;

    public static long startTime; //quiz start time
    public static long timeTook; //time taken to submit answer (submitTime - startTime)
    public static long submitTime; //quiz submit time

    public static JSONArray answers;

    /**
     * Clear the Question info
     */
    public static void clear() {
        quizid = null;
        question = null;
        options = null;
        feedback = null;
        timedquiz = null;
        quiztime = null;
        startTime = -1;
        timeTook = -1;
        submitTime = -1;
        answers = null;
    }

    /**
     * Print the question
     */
    public static void print() {
        LOGGER.info("quizid: " + quizid);
        LOGGER.info("question: " + question);
        LOGGER.info("options: " + options);
        LOGGER.info("feedback: " + feedback);
        LOGGER.info("timedquiz: " + timedquiz);
        LOGGER.info("quiztime: " + quiztime);
    }
}
