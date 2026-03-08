package motorph.util;

/*
 * This abstract class provides the default access rules of the system.
 * By default, all permissions are denied unless a child class overrides them.
 */
public abstract class BaseAccessPolicy implements AccessPolicy {

    // Default access is denied for all screens
    @Override public boolean canOpenScreen(String screen) { return false; }

    // These are default permission methods that return false
    @Override public boolean canManageEmployees() { return false; }
    @Override public boolean canComputePayroll() { return false; }
    @Override public boolean canManageTimecard() { return false; }
    @Override public boolean canApproveLeave() { return false; }
    @Override public boolean canFileLeave() { return false; }
    @Override public boolean canViewPayslip() { return false; }
}