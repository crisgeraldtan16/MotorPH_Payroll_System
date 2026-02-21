package motorph.util;

import java.util.Set;

public class FinanceAccessPolicy extends BaseAccessPolicy {

    private static final Set<String> ALLOWED = Set.of(
            "DASHBOARD",
            "PAYROLL"
    );

    @Override
    public boolean canOpenScreen(String screen) {
        return ALLOWED.contains(screen);
    }

    @Override public boolean canComputePayroll() { return true; }
    @Override public boolean canManageTimecard() { return true; }
}