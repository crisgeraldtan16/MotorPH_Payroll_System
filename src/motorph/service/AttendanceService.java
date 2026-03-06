package motorph.service;

import motorph.dao.AttendanceDao;
import motorph.model.AttendanceEntry;
import motorph.model.AttendanceRecord;
import motorph.model.Employee;
import motorph.util.AttendanceUtil;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public List<AttendanceRecord> getRecordsForEmployeeMonth(String empNo, YearMonth ym) {
        return attendanceDao.findRecordsForEmployeeMonth(empNo, ym);
    }

    public AttendanceUtil.AttendanceSummary summarizeForEmployeeMonth(String empNo, YearMonth ym) {
        return attendanceDao.summarizeForEmployeeMonth(empNo, ym);
    }

    public AttendanceRecord getTodayRecord(String empNo) {
        YearMonth ym = YearMonth.now();
        LocalDate today = LocalDate.now();

        List<AttendanceRecord> records = attendanceDao.findRecordsForEmployeeMonth(empNo, ym);
        for (AttendanceRecord r : records) {
            if (r.getDate() != null && r.getDate().equals(today)) {
                return r;
            }
        }
        return null;
    }

    public void timeIn(Employee emp) {
        if (emp == null || emp.getEmployeeNumber() == null || emp.getEmployeeNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee information is incomplete.");
        }

        AttendanceRecord today = getTodayRecord(emp.getEmployeeNumber());
        if (today != null && today.getLogIn() != null) {
            throw new IllegalStateException("You already timed in today.");
        }

        AttendanceRecord record = new AttendanceRecord(
                emp.getEmployeeNumber(),
                emp.getLastName(),
                emp.getFirstName(),
                LocalDate.now(),
                nowNoSeconds(),
                null
        );

        attendanceDao.add(record);
    }

    public void timeOut(Employee emp) {
        if (emp == null || emp.getEmployeeNumber() == null || emp.getEmployeeNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee information is incomplete.");
        }

        AttendanceRecord today = getTodayRecord(emp.getEmployeeNumber());

        if (today == null || today.getLogIn() == null) {
            throw new IllegalStateException("You have not timed in yet today.");
        }

        if (today.getLogOut() != null) {
            throw new IllegalStateException("You already timed out today.");
        }

        AttendanceRecord updated = new AttendanceRecord(
                today.getEmployeeNumber(),
                today.getLastName(),
                today.getFirstName(),
                today.getDate(),
                today.getLogIn(),
                nowNoSeconds()
        );

        attendanceDao.update(today, updated);
    }

    private LocalTime nowNoSeconds() {
        return LocalTime.now().withSecond(0).withNano(0);
    }
}