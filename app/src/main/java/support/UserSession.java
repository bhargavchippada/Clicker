package support;


import java.util.logging.Logger;

public class UserSession {
    private static String CLASSNAME = "UserSession";
    private final static Logger LOGGER = Logger.getLogger(CLASSNAME);

    public static String username; // username of the user
    public static String name; //name of the username
    public static String classname; // class name
    public static String ip; // ip-address of the user
    public static String servername; // servername
    public static Integer classid;


    /**
     * Clear the user info
     */
    public static void clear() {
        username = null;
        name = null;
        classname = null;
        ip = null;
        servername = null;
        classid = null;
    }

    /**
     * @return whether or not the userSession is valid or not
     */
    public static boolean isSessionValid() {
        if (username == null || name == null || servername == null) {
            return false;
        } else return true;
    }

    /**
     * Print the userSession details
     */
    public static void print() {
        if (isSessionValid()) {
            LOGGER.info(username + "," + name + "," + classname + "," + servername);
        }
    }
}
