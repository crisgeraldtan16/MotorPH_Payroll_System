package motorph.model;

import motorph.util.AccessPolicy;
import motorph.util.AccessPolicyFactory;

public class User {

    public enum Role { ADMIN, HR, EMPLOYEE }

    private String username;
    private String password;
    private Role role;
    private String employeeNumber; // only used if role is EMPLOYEE

    // 🔹 Polymorphism support (not stored in CSV)
    private transient AccessPolicy accessPolicy;

    public User() {
        // empty constructor for flexibility
    }

    public User(String username, String password, Role role, String employeeNumber) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeNumber = employeeNumber;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) {
        this.role = role;
        this.accessPolicy = null; // reset policy if role changes
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public boolean isAdmin() { return role == Role.ADMIN; }
    public boolean isHr() { return role == Role.HR; }
    public boolean isEmployee() { return role == Role.EMPLOYEE; }

    // 🔹 Polymorphic access control
    public AccessPolicy getAccessPolicy() {
        if (accessPolicy == null) {
            accessPolicy = AccessPolicyFactory.forUser(this);
        }
        return accessPolicy;
    }
}