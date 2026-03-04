package motorph.dao;

import motorph.model.LeaveRequest;
import motorph.util.LeaveIOUtil;

import java.util.List;

public class LeaveDao {

    public List<LeaveRequest> findAll() {
        return LeaveIOUtil.loadAll();
    }

    public List<LeaveRequest> findForEmployee(String empNo) {
        return LeaveIOUtil.loadForEmployee(empNo);
    }

    public void saveAll(List<LeaveRequest> requests) {
        LeaveIOUtil.overwriteAll(requests);
    }
}