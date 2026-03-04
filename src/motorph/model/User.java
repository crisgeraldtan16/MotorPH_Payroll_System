package motorph.model;

import motorph.util.*;

public class User {

    /*
     * This enum defines the different roles in the system.
     * Each role has different access permissions.
     */
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

    /*
     * This field is used for polymorphism.
     * It is transient because it is not stored in the CSV.
     * The access behavior is decided at runtime based on role.
     */
    private transient AccessPolicy accessPolicy;

    public User() {}

    public User(String username, String password, Role role, String employeeNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeNumber = employeeNumber;
    }

    /*
     * Getters allow other classes to read user data.
     */
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getEmployeeNumber() { return employeeNumber; }

    /*
     * Setters allow updating user information when needed.
     */
    public void setUsername(String username) { this.username = username; }

    public void setPassword(String password) { this.password = password; }

    /*
     * When the role changes, we reset the access policy
     * to ensure the correct permissions are applied.
     */
    public void setRole(Role role) {
        this.role = role;
        this.accessPolicy = null;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isHr() { return role == Role.HR; }
    public boolean isFinance() { return role == Role.FINANCE; }
    public boolean isIt() { return role == Role.IT; }

    public boolean isEmployee() {
        return role == Role.REGULAR || role == Role.PROBATIONARY;
    }

    /*
     * This method is the polymorphism entry point.
     * It returns the correct AccessPolicy implementation
     * depending on the user's role.
     */
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