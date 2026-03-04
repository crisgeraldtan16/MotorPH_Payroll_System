package motorph.service;

import motorph.dao.AttendanceDao;
import motorph.model.AttendanceEntry;
import motorph.model.AttendanceRecord;
import motorph.util.AttendanceUtil;

import java.time.YearMonth;
import java.util.List;

public class AttendanceService {

    private final AttendanceDao attendanceDao;

    public AttendanceService() {
        this.attendanceDao = new AttendanceDao();
    }

    public List<AttendanceEntry> getEntriesForEmployeeMonth(String empNo, YearMonth ym) {
        return attendanceDao.findEntriesForEmployeeMonth(empNo, ym);
    }

    public AttendanceUtil.AttendanceSummary summarizeForEmployeeMonth(String empNo, YearMonth ym) {
        return attendanceDao.summarizeForEmployeeMonth(empNo, ym);
    }

    public void addRecord(AttendanceRecord record) {
        attendanceDao.add(record);
    }

    public void updateRecord(AttendanceRecord oldKey, AttendanceRecord updated) {
        attendanceDao.update(oldKey, updated);
    }

    public void deleteRecord(AttendanceRecord key) {
        attendanceDao.delete(key);
    }
}