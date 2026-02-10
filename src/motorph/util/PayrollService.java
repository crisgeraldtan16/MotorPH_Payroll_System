package motorph.util;

import motorph.model.Employee;
import motorph.model.PayrollRecord;

import java.time.YearMonth;
import java.util.List;

public interface PayrollService {

    PayrollRecord computeForEmployeeMonth(Employee emp, YearMonth ym);

    List<PayrollRecord> computeForAllEmployeesMonth(List<Employee> employees, YearMonth ym);

    void saveRecord(PayrollRecord record);

    PayrollRecord findLatestForEmployee(String employeeNumber);
}
