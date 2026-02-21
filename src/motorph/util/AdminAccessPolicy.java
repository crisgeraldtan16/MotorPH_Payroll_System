package motorph.util;

import java.util.Set;

public class AdminAccessPolicy extends BaseAccessPolicy {

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

    @Override public boolean canManageEmployees() { return true; }
    @Override public boolean canComputePayroll() { return true; }
    @Override public boolean canManageTimecard() { return true; }
    @Override public boolean canApproveLeave() { return true; }
}