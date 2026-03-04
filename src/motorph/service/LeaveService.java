package motorph.service;

import motorph.dao.LeaveDao;
import motorph.model.LeaveRequest;

import java.util.List;

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

    public List<LeaveRequest> getRequestsForEmployee(String empNo) {
        return leaveDao.findForEmployee(empNo);
    }

    public void saveAllRequests(List<LeaveRequest> requests) {
        leaveDao.saveAll(requests);
    }
}