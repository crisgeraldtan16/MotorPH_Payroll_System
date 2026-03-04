package motorph.service;

import motorph.dao.PayrollDao;
import motorph.model.Employee;
import motorph.model.PayrollRecord;
import motorph.util.DefaultPayrollService;
import motorph.util.PayrollService;

import java.time.YearMonth;
import java.util.List;

public class PayrollAppService {

    private final PayrollService payrollService; // uses your existing interface
    private final PayrollDao payrollDao;

    public PayrollAppService() {
        this.payrollService = new DefaultPayrollService();
        this.payrollDao = new PayrollDao();
    }

    public PayrollRecord computeForEmployeeMonth(Employee emp, YearMonth ym) {
        return payrollService.computeForEmployeeMonth(emp, ym);
    }

    public List<PayrollRecord> computeForAllEmployeesMonth(List<Employee> employees, YearMonth ym) {
        return payrollService.computeForAllEmployeesMonth(employees, ym);
    }

    public void saveRecord(PayrollRecord record) {
        payrollDao.append(record);
    }

    public PayrollRecord findLatestForEmployee(String empNo) {
        return payrollDao.findLatestForEmployee(empNo);
    }
}