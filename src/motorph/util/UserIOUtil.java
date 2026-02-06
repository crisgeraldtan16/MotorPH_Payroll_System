package motorph.util;

import motorph.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserIOUtil {

    private static final String PATH = "data/users.csv";

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(PATH);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String username = get(p, 0);
                String password = get(p, 1);
                String roleText = get(p, 2).toUpperCase();
                String empNo = get(p, 3);

                User.Role role;
                try {
                    role = User.Role.valueOf(roleText);
                } catch (Exception e) {
                    role = User.Role.EMPLOYEE;
                }

                users.add(new User(username, password, role, empNo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    private static String get(String[] p, int idx) {
        if (idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }
}
