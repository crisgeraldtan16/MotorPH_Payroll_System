package motorph.util;

public abstract class BaseAccessPolicy implements AccessPolicy {

    // Default: deny everything
    @Override public boolean canOpenScreen(String screen) { return false; }

    @Override public boolean canManageEmployees() { return false; }
    @Override public boolean canComputePayroll() { return false; }
    @Override public boolean canManageTimecard() { return false; }
    @Override public boolean canApproveLeave() { return false; }
    @Override public boolean canFileLeave() { return false; }
    @Override public boolean canViewPayslip() { return false; }
}