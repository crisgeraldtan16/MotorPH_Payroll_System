package motorph.service;

import motorph.dao.EmployeeDao;
import motorph.model.Employee;

import java.util.List;

/*
 * This service class handles employee-related operations.
 * It acts as a bridge between the UI and the EmployeeDao.
 */
public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService() {
        this.employeeDao = new EmployeeDao();
    }

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    /*
     * This method gets a specific employee using the employee number.
     */
    public Employee getEmployeeByNumber(String empNo) {
        return employeeDao.findByEmployeeNumber(empNo);
    }

    public void saveEmployees(List<Employee> employees) {
        employeeDao.saveAll(employees);
    }
}