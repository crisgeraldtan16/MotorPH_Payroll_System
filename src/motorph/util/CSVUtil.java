package motorph.util;

import motorph.model.Employee;
import motorph.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {

    private static final String USER_FILE = "data/users.csv";
    private static final String EMPLOYEE_FILE = "data/employees.csv";

    private static final String EMPLOYEE_HEADER =
            "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,Gross Semi-monthly Rate,Hourly Rate";

    // ---------------- USERS ----------------
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();

        File file = new File(USER_FILE);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String username = p.length > 0 ? p[0].trim() : "";
                String password = p.length > 1 ? p[1].trim() : "";
                String roleText = p.length > 2 ? p[2].trim().toUpperCase() : "REGULAR";
                String employeeNumber = p.length > 3 ? p[3].trim() : "";

                if (username.isEmpty() || password.isEmpty()) continue;

                User.Role role;
                try {
                    role = User.Role.valueOf(roleText);
                } catch (Exception ex) {
                    role = User.Role.REGULAR;
                }

                users.add(new User(username, password, role, employeeNumber));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    // ------------- EMPLOYEES --------------
    public static List<Employee> loadEmployees() {
        ensureEmployeeFileExists();

        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] c = line.split(",", -1);
                if (c.length < 19) continue;

                Employee e = new Employee();
                e.setEmployeeNumber(c[0].trim());
                e.setLastName(c[1].trim());
                e.setFirstName(c[2].trim());
                e.setBirthday(c[3].trim());
                e.setAddress(c[4].trim());
                e.setPhoneNumber(c[5].trim());

                e.setSssNumber(c[6].trim());
                e.setPhilHealthNumber(c[7].trim());
                e.setTinNumber(c[8].trim());
                e.setPagIbigNumber(c[9].trim());

                e.setStatus(c[10].trim());
                e.setPosition(c[11].trim());
                e.setImmediateSupervisor(c[12].trim());

                e.setBasicSalary(parseDoubleSafe(c[13]));
                e.setRiceSubsidy(parseDoubleSafe(c[14]));
                e.setPhoneAllowance(parseDoubleSafe(c[15]));
                e.setClothingAllowance(parseDoubleSafe(c[16]));
                e.setGrossSemiMonthlyRate(parseDoubleSafe(c[17]));
                e.setHourlyRate(parseDoubleSafe(c[18]));

                employees.add(e);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return employees;
    }

    public static void saveEmployees(List<Employee> employees) {
        ensureEmployeeFileExists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(EMPLOYEE_FILE))) {
            pw.println(EMPLOYEE_HEADER);

            for (Employee e : employees) {
                pw.println(toEmployeeCsvRow(e));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String generateNextEmployeeNumber(List<Employee> employees) {
        int max = 10000;

        for (Employee e : employees) {
            String raw = e.getEmployeeNumber();
            if (raw == null) continue;

            raw = raw.trim();
            String digits = raw.replaceAll("\\D+", "");
            if (digits.isEmpty()) continue;

            try {
                int num = Integer.parseInt(digits);
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }

        return String.valueOf(max + 1);
    }

    // ✅ UPDATED METHOD (IMPROVED VERSION)
    public static Employee findEmployeeByNumber(String empNo) {
        if (empNo == null || empNo.trim().isEmpty()) return null;

        List<Employee> employees = loadEmployees();
        for (Employee e : employees) {
            if (empNo.trim().equals(e.getEmployeeNumber())) {
                return e;
            }
        }
        return null;
    }

    private static String toEmployeeCsvRow(Employee e) {
        return safe(e.getEmployeeNumber()) + "," +
                safe(e.getLastName()) + "," +
                safe(e.getFirstName()) + "," +
                safe(e.getBirthday()) + "," +
                safe(e.getAddress()) + "," +
                safe(e.getPhoneNumber()) + "," +
                safe(e.getSssNumber()) + "," +
                safe(e.getPhilHealthNumber()) + "," +
                safe(e.getTinNumber()) + "," +
                safe(e.getPagIbigNumber()) + "," +
                safe(e.getStatus()) + "," +
                safe(e.getPosition()) + "," +
                safe(e.getImmediateSupervisor()) + "," +
                e.getBasicSalary() + "," +
                e.getRiceSubsidy() + "," +
                e.getPhoneAllowance() + "," +
                e.getClothingAllowance() + "," +
                e.getGrossSemiMonthlyRate() + "," +
                e.getHourlyRate();
    }

    private static void ensureEmployeeFileExists() {
        try {
            File file = new File(EMPLOYEE_FILE);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            if (!file.exists()) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                    pw.println(EMPLOYEE_HEADER);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace(",", " ").trim();
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}