package motorph.service;

import motorph.dao.EmployeeDao;
import motorph.model.Employee;
import motorph.util.CSVUtil;

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

    /*
     * This generates the next available employee number
     * by finding the current maximum and incrementing by 1.
     */
    public String generateNextEmployeeNumber(List<Employee> employees) {
        return CSVUtil.generateNextEmployeeNumber(employees);
    }
}