package motorph.service;

import motorph.dao.EmployeeDao;
import motorph.model.Employee;

import java.util.List;

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

    public Employee getEmployeeByNumber(String empNo) {
        return employeeDao.findByEmployeeNumber(empNo);
    }

    public void saveEmployees(List<Employee> employees) {
        employeeDao.saveAll(employees);
    }
}