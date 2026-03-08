package motorph.dao;

import motorph.model.AttendanceEntry;
import motorph.model.AttendanceRecord;
import motorph.util.AttendanceUtil;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

/*
 * This DAO class handles attendance-related data operations.
 * It uses AttendanceUtil to load and manage attendance records.
 */
public class AttendanceDao {

    public List<AttendanceEntry> findEntriesForEmployeeMonth(String empNo, YearMonth ym) {
        return AttendanceUtil.loadEntriesForEmployeeMonth(empNo, ym);
    }

    public List<AttendanceRecord> findRecordsForEmployeeMonth(String empNo, YearMonth ym) {
        return AttendanceUtil.loadRecordsForEmployeeMonth(empNo, ym);
    }

    public AttendanceUtil.AttendanceSummary summarizeForEmployeeMonth(String empNo, YearMonth ym) {
        return AttendanceUtil.summarizeForEmployeeMonth(empNo, ym);
    }

    /*
     * These methods perform add, update, and delete operations.
     * IOException is caught and converted to a runtime exception.
     */
    public void add(AttendanceRecord record) {
        try {
            AttendanceUtil.addRecord(record);
        } catch (IOException e) {
            throw new RuntimeException("Failed to add attendance record.", e);
        }
    }

    public void update(AttendanceRecord oldKey, AttendanceRecord updated) {
        try {
            AttendanceUtil.updateRecord(oldKey, updated);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update attendance record.", e);
        }
    }

    public void delete(AttendanceRecord key) {
        try {
            AttendanceUtil.deleteRecord(key);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete attendance record.", e);
        }
    }
}