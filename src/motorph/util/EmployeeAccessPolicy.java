package motorph.util;

public class EmployeeAccessPolicy extends BaseAccessPolicy {

    @Override
    public boolean canOpenScreen(String screen) {
        return "EMPLOYEE_DASHBOARD".equals(screen)
                || "MY_PAYSLIP".equals(screen)
                || "LEAVE_REQUEST".equals(screen);
    }
}