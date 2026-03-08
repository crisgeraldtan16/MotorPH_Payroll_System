package motorph.util;

import motorph.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*
 * This utility class handles reading and writing
 * user account data in users.csv.
 */
public class UserIOUtil {

    private static final String PATH = "data/users.csv";
    private static final String HEADER = "Username,Password,Role,Employee #";

    /*
     * This loads all users from the CSV file
     * and converts them into User objects.
     */
    public static List<User> loadUsers() {
        ensureFile();

        List<User> users = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String username = get(p, 0);
                String password = get(p, 1);
                String roleText = get(p, 2).toUpperCase();
                String employeeNo = get(p, 3);

                if (username.isEmpty() || password.isEmpty()) continue;

                User.Role role;
                try {
                    role = User.Role.valueOf(roleText);
                } catch (Exception ex) {
                    role = User.Role.REGULAR;
                }

                users.add(new User(username, password, role, employeeNo));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /*
     * This checks if the username already exists
     * to avoid duplicate user accounts.
     */
    public static boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) return false;

        for (User u : loadUsers()) {
            if (username.trim().equalsIgnoreCase(u.getUsername())) {
                return true;
            }
        }
        return false;
    }

    /*
     * This adds a new user account to users.csv.
     */
    public static void appendUser(User user) {
        ensureFile();

        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH, true))) {
            pw.println(csv(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole() == null ? "" : user.getRole().name(),
                    user.getEmployeeNumber()
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user account.", e);
        }
    }

    /*
     * This makes sure the users.csv file exists.
     * If not, it creates the file with the correct header.
     */
    private static void ensureFile() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();

            File f = new File(PATH);
            if (!f.exists()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println(HEADER);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize users.csv", e);
        }
    }

    private static String get(String[] p, int idx) {
        if (idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }

    /*
     * This makes values safer before writing them to CSV
     * by removing commas and line breaks.
     */
    private static String csv(String... vals) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            String v = vals[i] == null ? "" : vals[i];
            v = v.replace(",", " ").replace("\n", " ").replace("\r", " ");
            if (i > 0) sb.append(",");
            sb.append(v);
        }
        return sb.toString();
    }
}