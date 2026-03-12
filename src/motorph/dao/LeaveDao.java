package motorph.dao;

import motorph.model.LeaveRequest;
import motorph.util.LeaveIOUtil;

import java.util.List;

/*
 * This DAO class handles leave request data.
 * It uses LeaveIOUtil to load and save leave records from the CSV file.
 */
public class LeaveDao {

    public List<LeaveRequest> findAll() {
        return LeaveIOUtil.loadAll();
    }

    /*
     * This method gets all leave requests for a specific employee.
     */
    public List<LeaveRequest> findForEmployee(String empNo) {
        return LeaveIOUtil.loadForEmployee(empNo);
    }

    public void saveAll(List<LeaveRequest> requests) {
        LeaveIOUtil.overwriteAll(requests);
    }

    /*
     * This appends a single new leave request to the CSV file.
     */
    public void append(LeaveRequest r) {
        LeaveIOUtil.append(r);
    }
}