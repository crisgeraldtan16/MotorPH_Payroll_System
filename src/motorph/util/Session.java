package motorph.util;

import motorph.model.User;

/*
 * This class manages the current logged-in user
 * during the application's runtime.
 */
public class Session {

    private static User currentUser;

    /*
     * Stores the user who has successfully logged in.
     */
    public static void setCurrentUser(User u) {
        currentUser = u;
    }

    /*
     * Returns the currently logged-in user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /*
     * Clears the session when the user logs out.
     */
    public static void clear() {
        currentUser = null;
    }
}