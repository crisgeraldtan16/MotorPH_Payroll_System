package motorph.util;

import java.util.Set;

public class AdminAccessPolicy extends BaseAccessPolicy {

    /*
     * These are the screens that an Admin user
     * is allowed to open inside the system.
     */
    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD",
            "EMPLOYEE",
            "PAYROLL",
            "LEAVE_APPROVAL"
    );

    /*
     * This method checks if the requested screen
     * is included in the admin's allowed screens.
     */
    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    /*
     * Admin users are allowed to manage employees.
     */
    @Override
    public boolean canManageEmployees() {
        return true;
    }

    /*
     * Admin users are allowed to compute payroll.
     */
    @Override
    public boolean canComputePayroll() {
        return true;
    }

    /*
     * Admin users are allowed to manage timecard records.
     */
    @Override
    public boolean canManageTimecard() {
        return true;
    }

    /*
     * Admin users are allowed to approve leave requests.
     */
    @Override
    public boolean canApproveLeave() {
        return true;
    }
}