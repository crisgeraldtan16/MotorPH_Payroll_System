package motorph.dao;

import motorph.model.User;
import motorph.util.UserIOUtil;

import java.util.List;

/*
 * This DAO class handles user data access.
 * It loads users from the CSV file using UserIOUtil.
 */
public class UserDao {

    public List<User> findAll() {
        return UserIOUtil.loadUsers();
    }

    /*
     * This method searches for a user based on the username.
     * It compares usernames ignoring uppercase or lowercase.
     */
    public User findByUsername(String username) {
        if (username == null) return null;

        for (User u : findAll()) {
            if (username.equalsIgnoreCase(u.getUsername())) {
                return u;
            }
        }
        return null;
    }
}