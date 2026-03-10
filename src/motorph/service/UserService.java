package motorph.service;

import motorph.dao.UserDao;
import motorph.model.User;
import motorph.util.UserIOUtil;

/*
 * This service class handles user-related operations.
 * It centralises authentication and account management
 * so the UI does not directly touch DAOs or utilities.
 */
public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    /*
     * This checks the username and password against stored accounts.
     * Returns the matching User or null if credentials are invalid.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;

        User user = userDao.findByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
        }
        return null;
    }

    /*
     * This checks whether a username is already taken.
     */
    public boolean usernameExists(String username) {
        return userDao.findByUsername(username) != null;
    }

    /*
     * This saves a newly created user account to the CSV file.
     */
    public void createUser(User user) {
        UserIOUtil.appendUser(user);
    }
}
