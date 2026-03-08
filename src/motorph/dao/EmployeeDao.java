package motorph.dao;

import motorph.model.Employee;
import motorph.util.CSVUtil;

import java.util.List;

/*
 * This DAO class handles employee data access.
 * It uses CSVUtil to load, save, and find employee records.
 */
public class EmployeeDao {

    public List<Employee> findAll() {
        return CSVUtil.loadEmployees();
    }

    public void saveAll(List<Employee> employees) {
        CSVUtil.saveEmployees(employees);
    }

    /*
     * This method finds a specific employee using the employee number.
     */
    public Employee findByEmployeeNumber(String empNo) {
        return CSVUtil.findEmployeeByNumber(empNo);
    }
}