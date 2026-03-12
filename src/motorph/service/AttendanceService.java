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

/*
 * This service class handles the attendance-related business logic.
 * It connects the UI to the DAO and manages time in, time out,
 * and attendance summary functions.
 */
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

    /*
     * This method checks the employee's attendance record for today.
     */
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

    /*
     * This method records the employee's time in for the current day.
     * It also checks if the employee already timed in.
     */
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

    /*
     * This method records the employee's time out for the current day.
     * It makes sure that the employee already timed in first.
     */
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

    /*
     * This adds a raw attendance record directly through the DAO.
     * Used by the payroll timecard manager for manual entries.
     */
    public void addRecord(AttendanceRecord record) {
        attendanceDao.add(record);
    }

    /*
     * This updates an existing attendance record through the DAO.
     */
    public void updateRecord(AttendanceRecord oldKey, AttendanceRecord updated) {
        attendanceDao.update(oldKey, updated);
    }

    /*
     * This deletes an attendance record through the DAO.
     */
    public void deleteRecord(AttendanceRecord key) {
        attendanceDao.delete(key);
    }

    /*
     * This computes the number of minutes an employee was late
     * after the grace period is applied.
     */
    public long computeLateMinutesWithGrace(LocalTime timeIn, int graceMinutes) {
        return AttendanceUtil.computeLateMinutesWithGrace(timeIn, graceMinutes);
    }

    /*
     * This computes total worked hours based on log in and log out.
     */
    public double computeWorkedHours(LocalTime in, LocalTime out) {
        return AttendanceUtil.computeWorkedHours(in, out);
    }

    /*
     * This helper method returns the current time without seconds and nanoseconds.
     * This keeps the recorded time cleaner and more consistent.
     */
    private LocalTime nowNoSeconds() {
        return LocalTime.now().withSecond(0).withNano(0);
    }
}