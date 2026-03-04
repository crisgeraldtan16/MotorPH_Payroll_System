package motorph.dao;

import motorph.model.Employee;
import motorph.util.CSVUtil;

import java.util.List;

public class EmployeeDao {

    public List<Employee> findAll() {
        return CSVUtil.loadEmployees();
    }

    public void saveAll(List<Employee> employees) {
        CSVUtil.saveEmployees(employees);
    }

    public Employee findByEmployeeNumber(String empNo) {
        return CSVUtil.findEmployeeByNumber(empNo);
    }
}