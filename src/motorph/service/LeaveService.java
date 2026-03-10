package motorph.service;

import motorph.dao.LeaveDao;
import motorph.model.LeaveRequest;
import motorph.util.LeaveIOUtil;

import java.util.List;

/*
 * This service class handles leave-related operations.
 * It connects the UI to the LeaveDao.
 */
public class LeaveService {

    /*
     * This value object holds the counts of leave requests
     * by status for a specific employee.
     */
    public static class LeaveSummary {
        public final int pending;
        public final int approved;
        public final int denied;

        public LeaveSummary(int pending, int approved, int denied) {
            this.pending = pending;
            this.approved = approved;
            this.denied = denied;
        }
    }

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

    /*
     * This submits a new leave request using the DAO append operation.
     */
    public void submitRequest(LeaveRequest r) {
        leaveDao.append(r);
    }

    /*
     * This counts pending, approved, and denied leave requests
     * for a specific employee and returns a summary object.
     */
    public LeaveSummary summarizeForEmployee(String empNo) {
        int pending = 0, approved = 0, denied = 0;
        for (LeaveRequest r : getRequestsForEmployee(empNo)) {
            if (r.getStatus() == LeaveRequest.Status.PENDING) pending++;
            else if (r.getStatus() == LeaveRequest.Status.APPROVED) approved++;
            else if (r.getStatus() == LeaveRequest.Status.DENIED) denied++;
        }
        return new LeaveSummary(pending, approved, denied);
    }

    /*
     * This generates a unique leave request ID.
     */
    public String newRequestId() {
        return LeaveIOUtil.newRequestId();
    }

    /*
     * This returns the current date/time formatted for leave records.
     */
    public String now() {
        return LeaveIOUtil.now();
    }
}