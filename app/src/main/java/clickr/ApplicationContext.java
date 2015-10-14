package clickr;

import android.app.Application;

import java.util.logging.Logger;

import support.Question;
import support.UserSession;
import support.Utils;

public class ApplicationContext extends Application {
    private static String CLASSNAME = "ApplicationContext";
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static UserSession usersession;
    private static Question question;

    /**
     * creates a UserSession object if one doesn't exist for the app, makes sure only a single UserSession object is defined for a single run of the app
     *
     * @return UserSession
     */
    public synchronized static UserSession getThreadSafeUserSession() {
        if (usersession == null) usersession = new UserSession();
        return usersession;
    }

    /**
     * creates a Question object if one doesn't exist for the app, makes sure only a single Question object is defined for a single run of the app
     * <br> Since the app supports only a single question this is enough
     *
     * @return Question
     */
    public synchronized static Question getThreadSafeQuestion() {
        if (question == null) question = new Question();
        return question;
    }

    /**
     * clears usersession and question objects
     */
    public synchronized static void invalidateSession() {
        if (usersession != null) {
            usersession.clear();
            Utils.logv(CLASSNAME, "usersession wiped");
        }
        if (question != null) {
            question.clear();
            Utils.logv(CLASSNAME, "question wiped");
        }
    }
}
