package clickr;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.logging.Logger;

import support.Question;
import support.UserSession;

public class ApplicationContext extends Application {
    private static String CLASSNAME = "ApplicationContext";
    private final static Logger LOGGER = Logger.getLogger(CLASSNAME);

    @Override
    public void onCreate() {
        super.onCreate();

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * clears usersession and question objects
     */
    public synchronized static void invalidateSession() {
        UserSession.clear();
        LOGGER.info("usersession wiped");
        Question.clear();
        LOGGER.info("question wiped");
    }
}
