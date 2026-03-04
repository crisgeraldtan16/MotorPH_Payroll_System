package motorph.dao;

import motorph.model.User;
import motorph.util.UserIOUtil;

import java.util.List;

public class UserDao {

    public List<User> findAll() {
        return UserIOUtil.loadUsers();
    }

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