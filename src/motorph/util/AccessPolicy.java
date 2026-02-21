package motorph.util;

public interface AccessPolicy {
    boolean canOpenScreen(String screen);
    boolean canManageEmployees();      // CRUD employees
    boolean canComputePayroll();       // payroll module access
    boolean canApproveLeaves();        // leave approvals
}