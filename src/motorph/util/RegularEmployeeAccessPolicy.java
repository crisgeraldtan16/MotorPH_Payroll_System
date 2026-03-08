package motorph.util;

import java.util.Set;

/*
 * This access policy is for regular employees.
 * They can access employee dashboard, view payslips,
 * and submit leave requests.
 */
public class RegularEmployeeAccessPolicy extends BaseAccessPolicy {

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
     * Regular employees are allowed to file leave requests.
     */
    @Override
    public boolean canFileLeave() {
        return true;
    }

    /*
     * Regular employees are allowed to view their payslip.
     */
    @Override
    public boolean canViewPayslip() {
        return true;
    }
}