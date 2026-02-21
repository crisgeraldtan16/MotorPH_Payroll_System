package motorph.util;

public interface AccessPolicy {
    boolean canOpenScreen(String screen);

    // Permissions (optional checks for UI panels/buttons)
    boolean canManageEmployees();   // CRUD employees
    boolean canComputePayroll();    // compute/save payroll
    boolean canManageTimecard();    // add/edit/delete attendance
    boolean canApproveLeave();      // approve/deny leaves
    boolean canFileLeave();         // file leave request
    boolean canViewPayslip();       // view own payslip
}