package motorph.util;

import java.util.Set;

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

    @Override public boolean canFileLeave() { return true; }
    @Override public boolean canViewPayslip() { return true; }
}