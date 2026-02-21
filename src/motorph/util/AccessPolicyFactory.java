package motorph.util;

import motorph.model.User;

public class AccessPolicyFactory {

    public static AccessPolicy forUser(User u) {
        if (u == null) return new EmployeeAccessPolicy(); // safest default
        if (u.isAdmin()) return new AdminAccessPolicy();
        if (u.isHr()) return new HrAccessPolicy();
        return new EmployeeAccessPolicy();
    }
}