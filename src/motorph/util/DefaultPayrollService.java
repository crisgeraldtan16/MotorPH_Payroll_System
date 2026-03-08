package motorph.util;

import motorph.model.Employee;
import motorph.model.PayrollRecord;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/*
 * This is the default implementation of the PayrollService interface.
 * It handles payroll computation and saving of payroll records.
 */
public class DefaultPayrollService implements PayrollService {

    /*
     * This computes payroll for one employee for the selected month.
     * If the employee has no attendance, no payroll record is created.
     */
    @Override
    public PayrollRecord computeForEmployeeMonth(Employee emp, YearMonth ym) {
        AttendanceUtil.AttendanceSummary summary =
                AttendanceUtil.summarizeForEmployeeMonth(emp.getEmployeeNumber(), ym);

        // If there is no attendance, payroll should not be computed.
        if (summary.daysPresent <= 0) {
            return null;
        }

        return PayrollCalculator.computeMonthlyPayroll(
                emp, ym, summary.daysPresent, summary.totalLateMinutes
        );
    }

    /*
     * This computes payroll for all employees for the selected month.
     * Only employees with attendance are included.
     */
    @Override
    public List<PayrollRecord> computeForAllEmployeesMonth(List<Employee> employees, YearMonth ym) {
        List<PayrollRecord> out = new ArrayList<>();
        for (Employee e : employees) {
            PayrollRecord pr = computeForEmployeeMonth(e, ym);
            if (pr != null) out.add(pr);
        }
        return out;
    }

    /*
     * This saves a payroll record to the payroll file.
     */
    @Override
    public void saveRecord(PayrollRecord record) {
        PayrollIOUtil.appendPayrollRecord(record);
    }

    /*
     * This gets the latest payroll record of a specific employee.
     */
    @Override
    public PayrollRecord findLatestForEmployee(String employeeNumber) {
        return PayrollIOUtil.findLatestForEmployee(employeeNumber);
    }
}