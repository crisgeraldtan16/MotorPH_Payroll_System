package motorph.dao;

import motorph.model.PayrollRecord;
import motorph.util.PayrollIOUtil;

import java.time.YearMonth;
import java.util.List;

/*
 * This DAO class handles payroll data access.
 * It uses PayrollIOUtil to store and retrieve payroll records from the CSV file.
 */
public class PayrollDao {

    /*
     * This method adds a new payroll record to the payroll file.
     */
    public void append(PayrollRecord record) {
        PayrollIOUtil.appendPayrollRecord(record);
    }

    /*
     * This method retrieves the latest payroll record of an employee.
     */
    public PayrollRecord findLatestForEmployee(String empNo) {
        return PayrollIOUtil.findLatestForEmployee(empNo);
    }

    public List<PayrollRecord> findForEmployeeMonth(String empNo, YearMonth ym) {
        return PayrollIOUtil.loadPayrollRecordsForEmployeeMonth(empNo, ym);
    }
}