package motorph.util;

import motorph.model.AttendanceEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AttendanceUtil {


    // ProjectRoot/data/attendance.csv
    private static final String ATTENDANCE_CSV_PATH = "data/attendance.csv";

    // Company rules
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final int GRACE_MINUTES = 10; // salary deduction applies if time-in is 08:11 onwards

    public static class AttendanceSummary {
        public final int daysPresent;
        public final int totalLateMinutes;

        public AttendanceSummary(int daysPresent, int totalLateMinutes) {
            this.daysPresent = daysPresent;
            this.totalLateMinutes = totalLateMinutes;
        }
    }

    /**
     * Returns Days Present + Total Late Minutes for the employee/month.
     * Uses the same data as the Timecard loader below.
     */
    public static AttendanceSummary summarizeForEmployeeMonth(String employeeNo, YearMonth ym) {
        List<AttendanceEntry> entries = loadEntriesForEmployeeMonth(employeeNo, ym);

        int daysPresent = entries.size();
        int totalLateMinutes = 0;

        for (AttendanceEntry e : entries) {
            totalLateMinutes += (int) computeLateMinutesWithGrace(e.getTimeIn(), GRACE_MINUTES);
        }

        return new AttendanceSummary(daysPresent, totalLateMinutes);
    }

    /**
     *  Loads full daily logs (timecard) for one employee in a selected month.
     *
     * CSV header:
     * Employee #,Last Name,First Name,Date,Log In,Log Out
     *
     * Column mapping:
     * 0 = Employee #
     * 3 = Date
     * 4 = Log In
     * 5 = Log Out
     *
     * Date formats supported: MM/dd/yyyy OR yyyy-MM-dd
     * Time formats supported: H:mm, HH:mm, H:mm:ss, HH:mm:ss
     */
    public static List<AttendanceEntry> loadEntriesForEmployeeMonth(String employeeNo, YearMonth ym) {
        List<AttendanceEntry> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_PATH))) {
            String line = br.readLine(); // header
            if (line == null) return list;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Simple CSV split (ok if your fields don't contain commas)
                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                String emp = p[0].trim();
                if (!emp.equals(employeeNo)) continue;

                String dateText = p[3].trim();
                String inText = p[4].trim();
                String outText = p[5].trim();

                LocalDate date = parseDateFlexible(dateText);
                LocalTime timeIn = parseTimeFlexible(inText);
                LocalTime timeOut = parseTimeFlexible(outText);

                // If missing log-in/log-out, skip (you can change this if needed)
                if (date == null || timeIn == null || timeOut == null) continue;

                // Only same selected month
                if (!YearMonth.from(date).equals(ym)) continue;

                list.add(new AttendanceEntry(emp, date, timeIn, timeOut));
            }
        } catch (Exception e) {
            // Keep quiet to avoid UI spam if file missing.
            // System.out.println("Attendance load error: " + e.getMessage());
        }

        return list;
    }

    /**
     * Late minutes rule (10-minute grace):
     * - If Time In is 08:00 to 08:10 => no late deduction
     * - If Time In is 08:11 onwards => late minutes counted from 08:00
     */
    public static long computeLateMinutesWithGrace(LocalTime timeIn, int graceMinutes) {
        LocalTime graceCutoff = START_TIME.plusMinutes(graceMinutes);
        if (timeIn.isAfter(graceCutoff)) {
            return Duration.between(START_TIME, timeIn).toMinutes();
        }
        return 0;
    }

    /**
     * Worked hours = TimeOut - TimeIn (in hours)
     */
    public static double computeWorkedHours(LocalTime in, LocalTime out) {
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes < 0) return 0; // safety
        return minutes / 60.0;
    }

    // ---------- Helpers ----------
    private static LocalDate parseDateFlexible(String s) {
        if (s == null || s.isBlank()) return null;

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE
        };

        for (DateTimeFormatter f : formats) {
            try { return LocalDate.parse(s, f); } catch (Exception ignored) {}
        }
        return null;
    }

    private static LocalTime parseTimeFlexible(String s) {
        if (s == null || s.isBlank()) return null;

        DateTimeFormatter[] formats = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("H:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm:ss")
        };

        for (DateTimeFormatter f : formats) {
            try { return LocalTime.parse(s, f); } catch (Exception ignored) {}
        }
        return null;
    }
}
