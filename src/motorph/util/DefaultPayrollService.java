package motorph.util;

import motorph.model.Employee;
import motorph.model.PayrollRecord;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DefaultPayrollService implements PayrollService {

    @Override
    public PayrollRecord computeForEmployeeMonth(Employee emp, YearMonth ym) {
        AttendanceUtil.AttendanceSummary summary =
                AttendanceUtil.summarizeForEmployeeMonth(emp.getEmployeeNumber(), ym);

        // IMPORTANT RULE:
        // If there is no attendance, we return null (so UI will NOT compute salary).
        if (summary.daysPresent <= 0) {
            return null;
        }

        return PayrollCalculator.computeMonthlyPayroll(
                emp, ym, summary.daysPresent, summary.totalLateMinutes
        );
    }

    @Override
    public List<PayrollRecord> computeForAllEmployeesMonth(List<Employee> employees, YearMonth ym) {
        List<PayrollRecord> out = new ArrayList<>();
        for (Employee e : employees) {
            PayrollRecord pr = computeForEmployeeMonth(e, ym);
            if (pr != null) out.add(pr); // only include employees with attendance
        }
        return out;
    }

    @Override
    public void saveRecord(PayrollRecord record) {
        PayrollIOUtil.appendPayrollRecord(record);
    }

    @Override
    public PayrollRecord findLatestForEmployee(String employeeNumber) {
        return PayrollIOUtil.findLatestForEmployee(employeeNumber);
    }
}
