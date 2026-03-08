package motorph.util;

import motorph.model.User;

public class AccessPolicyFactory {

    /*
     * This factory class returns the correct access policy
     * based on the user's role in the system.
     */
    public static AccessPolicy forUser(User u) {
        if (u == null) return new EmployeeAccessPolicy(); // safest default
        if (u.isAdmin()) return new AdminAccessPolicy();
        if (u.isHr()) return new HrAccessPolicy();
        return new EmployeeAccessPolicy();
    }
}