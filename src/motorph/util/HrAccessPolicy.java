package motorph.util;

import java.util.Set;

/*
 * Access policy for HR users.
 * HR can manage employees, approve leave requests,
 * and access payroll-related features.
 */
public class HrAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD",
            "EMPLOYEE",
            "PAYROLL",
            "LEAVE_APPROVAL"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    @Override
    public boolean canManageEmployees() {
        return true;
    }

    @Override
    public boolean canApproveLeave() {
        return true;
    }

    // HR can also compute payroll and manage timecards if allowed by company rules
    @Override
    public boolean canComputePayroll() {
        return true;
    }

    @Override
    public boolean canManageTimecard() {
        return true;
    }
}