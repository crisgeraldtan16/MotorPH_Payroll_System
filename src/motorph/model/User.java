package motorph.model;

import motorph.util.*;

public class User {

    public enum Role {
        REGULAR,
        PROBATIONARY,
        HR,
        IT,
        FINANCE,
        ADMIN
    }

    private String username;
    private String password;
    private Role role;
    private String employeeNumber;

    // ✅ Polymorphism support (not stored in CSV)
    private transient AccessPolicy accessPolicy;

    public User() {}

    public User(String username, String password, Role role, String employeeNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeNumber = employeeNumber;
    }

    // ---------- Getters ----------
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEmployeeNumber() { return employeeNumber; }

    // ---------- Setters ----------
    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    public void setRole(Role role) {
        this.role = role;
        this.accessPolicy = null; // ✅ reset cached policy if role changes
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    // ---------- Convenience checks ----------
    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isHr() { return role == Role.HR; }
    public boolean isFinance() { return role == Role.FINANCE; }
    public boolean isIt() { return role == Role.IT; }

    public boolean isEmployee() {
        return role == Role.REGULAR || role == Role.PROBATIONARY;
    }

    // ✅ Polymorphism entry point (role decides which policy object is used)
    public AccessPolicy getAccessPolicy() {
        if (accessPolicy != null) return accessPolicy;

        if (role == null) {
            accessPolicy = new BaseAccessPolicy() {};
            return accessPolicy;
        }

        switch (role) {
            case REGULAR:
                accessPolicy = new RegularEmployeeAccessPolicy();
                break;
            case PROBATIONARY:
                accessPolicy = new ProbationaryEmployeeAccessPolicy();
                break;
            case HR:
                accessPolicy = new HrAccessPolicy();
                break;
            case FINANCE:
                accessPolicy = new FinanceAccessPolicy();
                break;
            case IT:
                accessPolicy = new ItAccessPolicy();
                break;
            case ADMIN:
                accessPolicy = new AdminAccessPolicy();
                break;
            default:
                accessPolicy = new BaseAccessPolicy() {};
                break;
        }
        return accessPolicy;
    }
}