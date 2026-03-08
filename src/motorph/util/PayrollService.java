package motorph.util;

import motorph.model.Employee;
import motorph.model.PayrollRecord;

import java.time.YearMonth;
import java.util.List;

/*
 * This interface defines the payroll service contract.
 * It lists the payroll operations that must be implemented
 * by any payroll service class.
 */
public interface PayrollService {

    /*
     * This computes payroll for one employee
     * for the selected month.
     */
    PayrollRecord computeForEmployeeMonth(Employee emp, YearMonth ym);

    /*
     * This computes payroll for all employees
     * for the selected month.
     */
    List<PayrollRecord> computeForAllEmployeesMonth(List<Employee> employees, YearMonth ym);

    /*
     * This saves a payroll record.
     */
    void saveRecord(PayrollRecord record);

    /*
     * This gets the latest payroll record
     * of a specific employee.
     */
    PayrollRecord findLatestForEmployee(String employeeNumber);
}