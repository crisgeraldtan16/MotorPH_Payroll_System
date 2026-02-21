package motorph.util;

public class HrAccessPolicy extends BaseAccessPolicy {

    @Override
    public boolean canOpenScreen(String screen) {
        return "DASHBOARD".equals(screen)
                || "EMPLOYEE".equals(screen)
                || "PAYROLL".equals(screen)
                || "LEAVE_APPROVAL".equals(screen);
    }

    @Override
    public boolean canManageEmployees() { return true; }

    @Override
    public boolean canComputePayroll() { return true; }

    @Override
    public boolean canApproveLeaves() { return true; }
}