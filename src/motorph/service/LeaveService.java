package motorph.service;

import motorph.dao.LeaveDao;
import motorph.model.LeaveRequest;

import java.util.List;

/*
 * This service class handles leave-related operations.
 * It connects the UI to the LeaveDao.
 */
public class LeaveService {

    private final LeaveDao leaveDao;

    public LeaveService() {
        this.leaveDao = new LeaveDao();
    }

    public LeaveService(LeaveDao leaveDao) {
        this.leaveDao = leaveDao;
    }

    public List<LeaveRequest> getAllRequests() {
        return leaveDao.findAll();
    }

    /*
     * This method gets all leave requests of a specific employee.
     */
    public List<LeaveRequest> getRequestsForEmployee(String empNo) {
        return leaveDao.findForEmployee(empNo);
    }

    public void saveAllRequests(List<LeaveRequest> requests) {
        leaveDao.saveAll(requests);
    }
}