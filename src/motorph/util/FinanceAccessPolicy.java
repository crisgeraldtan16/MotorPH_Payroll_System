package motorph.util;

import java.util.Set;

/*
 * Access policy for Finance users.
 * Finance can access the dashboard and payroll system.
 * They are also allowed to compute payroll and manage timecards.
 */
public class FinanceAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD",
            "PAYROLL"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    @Override
    public boolean canComputePayroll() {
        return true;
    }

    @Override
    public boolean canManageTimecard() {
        return true;
    }
}