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
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    users.add(new User(data[0].trim(), data[1].trim(), data[2].trim()));
                }
            }
        } catch (IOException e) {
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
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] c = line.split(",", -1); // keep empty values
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
        int max = 10000; // base number so first generated is 10001

        for (Employee e : employees) {
            String raw = e.getEmployeeNumber();
            if (raw == null) continue;

            raw = raw.trim();

            // Extract numeric part only (handles "10001", "EMP-10001", etc.)
            String digits = raw.replaceAll("\\D+", "");
            if (digits.isEmpty()) continue;

            try {
                int num = Integer.parseInt(digits);
                if (num > max) {
                    max = num;
                }
            } catch (NumberFormatException ignored) {}
        }

        return String.valueOf(max + 1);
    }


    private static String toEmployeeCsvRow(Employee e) {
        // Keep the same order as the required CSV header
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
        // Avoid breaking CSV by removing commas (simple academic-safe approach)
        return s.replace(",", " ").trim();
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int tryParseInt(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return -1;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
