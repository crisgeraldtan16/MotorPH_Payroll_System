package motorph.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AttendanceUtil {

    // Put your file here:
    private static final String ATTENDANCE_FILE = "data/attendance.csv";

    // Policy:
    private static final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private static final int GRACE_MINUTES = 10; // 10-minute grace
    private static final LocalTime LATE_CUTOFF = SHIFT_START.plusMinutes(GRACE_MINUTES); // 08:10

    // Accepted formats (we try multiple)
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    private static final List<DateTimeFormatter> TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("H:mm:ss"),
            DateTimeFormatter.ofPattern("HH:mm:ss"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mm a")
    );

    public static AttendanceSummary summarizeForEmployeeMonth(String employeeNo, YearMonth month) {
        File file = new File(ATTENDANCE_FILE);
        if (!file.exists()) {
            // No attendance file: return empty summary (no crash)
            return new AttendanceSummary(0, 0);
        }

        int daysPresent = 0;
        int totalLateMinutes = 0;

        // Track unique dates present
        Set<LocalDate> presentDates = new HashSet<>();
        Map<LocalDate, Integer> lateMinutesByDate = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new AttendanceSummary(0, 0);

            String[] headers = headerLine.split(",", -1);
            Map<String, Integer> idx = headerIndex(headers);

            Integer empIdx = findIndex(idx, List.of("employee #", "employee no", "employee number", "emp no", "emp#", "employeeid"));
            Integer dateIdx = findIndex(idx, List.of("date", "log date", "attendance date"));
            Integer timeInIdx = findIndex(idx, List.of("time in", "login", "log in", "in", "clock in", "timein"));

            if (empIdx == null || dateIdx == null || timeInIdx == null) {
                // If your headers are different, adjust the names above.
                return new AttendanceSummary(0, 0);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] c = line.split(",", -1);
                if (c.length <= Math.max(empIdx, Math.max(dateIdx, timeInIdx))) continue;

                String emp = c[empIdx].trim();
                if (!emp.equals(employeeNo)) continue;

                LocalDate date = parseDate(c[dateIdx].trim());
                if (date == null) continue;

                if (!YearMonth.from(date).equals(month)) continue;

                LocalTime timeIn = parseTime(c[timeInIdx].trim());
                if (timeIn == null) continue;

                presentDates.add(date);

                int lateMin = calculateLateMinutes(timeIn);
                // If there are multiple entries per day, keep the highest late minutes (worst case)
                lateMinutesByDate.merge(date, lateMin, Math::max);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new AttendanceSummary(0, 0);
        }

        daysPresent = presentDates.size();
        for (int v : lateMinutesByDate.values()) totalLateMinutes += v;

        return new AttendanceSummary(daysPresent, totalLateMinutes);
    }

    private static int calculateLateMinutes(LocalTime timeIn) {
        // Grace period: no deduction if <= 08:10
        if (!timeIn.isAfter(LATE_CUTOFF)) return 0;

        // Deduct from 08:10 onward; at 08:11 it's 1 minute late (beyond grace)
        return (int) java.time.Duration.between(LATE_CUTOFF, timeIn).toMinutes();
    }

    private static Map<String, Integer> headerIndex(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim().toLowerCase(), i);
        }
        return map;
    }

    private static Integer findIndex(Map<String, Integer> idx, List<String> candidates) {
        for (String c : candidates) {
            Integer i = idx.get(c.toLowerCase());
            if (i != null) return i;
        }
        return null;
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        for (DateTimeFormatter f : DATE_FORMATS) {
            try { return LocalDate.parse(s, f); } catch (Exception ignored) {}
        }
        return null;
    }

    private static LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        for (DateTimeFormatter f : TIME_FORMATS) {
            try { return LocalTime.parse(s.toUpperCase(), f); } catch (Exception ignored) {}
        }
        return null;
    }

    // Simple summary record
    public static class AttendanceSummary {
        public final int daysPresent;
        public final int totalLateMinutes;

        public AttendanceSummary(int daysPresent, int totalLateMinutes) {
            this.daysPresent = daysPresent;
            this.totalLateMinutes = totalLateMinutes;
        }
    }
}
