package motorph.util;

/*
 * This interface defines the access control rules of the system.
 * It is used to check what screens and actions a user is allowed to access.
 */
public interface AccessPolicy {

    /*
     * This checks if the user can open a specific screen.
     */
    boolean canOpenScreen(String screen);

    // These methods check specific permissions in the system
    boolean canManageEmployees();   // CRUD employees
    boolean canComputePayroll();    // compute/save payroll
    boolean canManageTimecard();    // add/edit/delete attendance
    boolean canApproveLeave();      // approve/deny leaves
    boolean canFileLeave();         // file leave request
    boolean canViewPayslip();       // view own payslip
}