package motorph.util;

import java.util.Set;

/*
 * This access policy is for probationary employees.
 * They can only access employee-related screens in the system.
 */
public class ProbationaryEmployeeAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "EMPLOYEE_DASHBOARD",
            "MY_PAYSLIP",
            "LEAVE_REQUEST"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    /*
     * Probationary employees are allowed to file leave requests.
     */
    @Override
    public boolean canFileLeave() {
        return true;
    }

    /*
     * Probationary employees are allowed to view their payslip.
     */
    @Override
    public boolean canViewPayslip() {
        return true;
    }
}