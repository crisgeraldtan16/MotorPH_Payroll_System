package motorph.util;

import motorph.model.AttendanceEntry;
import motorph.model.AttendanceRecord;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AttendanceUtil {

    private static final String ATTENDANCE_CSV_PATH = "data/attendance.csv";

    // Company rules
    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final int GRACE_MINUTES = 10;

    // Your attendance header (must match exactly)
    private static final String HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    // Date/Time output formats (what we write back to CSV)
    private static final DateTimeFormatter OUT_DATE = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter OUT_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static class AttendanceSummary {
        public final int daysPresent;
        public final int totalLateMinutes;

        public AttendanceSummary(int daysPresent, int totalLateMinutes) {
            this.daysPresent = daysPresent;
            this.totalLateMinutes = totalLateMinutes;
        }
    }

    // ===================== PAYROLL SUMMARY =====================
    public static AttendanceSummary summarizeForEmployeeMonth(String employeeNo, YearMonth ym) {
        List<AttendanceEntry> entries = loadEntriesForEmployeeMonth(employeeNo, ym);

        int daysPresent = entries.size();
        int totalLateMinutes = 0;

        for (AttendanceEntry e : entries) {
            totalLateMinutes += (int) computeLateMinutesWithGrace(e.getTimeIn(), GRACE_MINUTES);
        }

        return new AttendanceSummary(daysPresent, totalLateMinutes);
    }

    // ===================== TIME CARD LOADERS =====================
    /**
     * Loads attendance records (full fields) for one employee in a month.
     * CSV header: Employee #,Last Name,First Name,Date,Log In,Log Out
     */
    public static List<AttendanceRecord> loadRecordsForEmployeeMonth(String employeeNo, YearMonth ym) {
        List<AttendanceRecord> list = new ArrayList<>();
        for (AttendanceRecord r : loadAllRecords()) {
            if (!employeeNo.equals(r.getEmployeeNumber())) continue;
            if (r.getDate() == null) continue;
            if (!YearMonth.from(r.getDate()).equals(ym)) continue;
            list.add(r);
        }

        list.sort(Comparator.comparing(AttendanceRecord::getDate)
                .thenComparing(AttendanceRecord::getLogIn, Comparator.nullsLast(Comparator.naturalOrder())));

        return list;
    }

    /**
     * Loads simplified entries used by payroll calculation / timecard table.
     */
    public static List<AttendanceEntry> loadEntriesForEmployeeMonth(String employeeNo, YearMonth ym) {
        List<AttendanceEntry> list = new ArrayList<>();
        List<AttendanceRecord> records = loadRecordsForEmployeeMonth(employeeNo, ym);

        for (AttendanceRecord r : records) {
            if (r.getDate() == null || r.getLogIn() == null || r.getLogOut() == null) continue;
            list.add(new AttendanceEntry(r.getEmployeeNumber(), r.getDate(), r.getLogIn(), r.getLogOut()));
        }
        return list;
    }

    // ===================== TIME CARD CRUD =====================
    public static void addRecord(AttendanceRecord record) throws IOException {
        List<AttendanceRecord> all = loadAllRecords();
        all.add(record);
        saveAllRecords(all);
    }

    public static void updateRecord(AttendanceRecord originalKey, AttendanceRecord updated) throws IOException {
        List<AttendanceRecord> all = loadAllRecords();
        int idx = findRecordIndex(all, originalKey);
        if (idx == -1) throw new IOException("Record not found to update.");
        all.set(idx, updated);
        saveAllRecords(all);
    }

    public static void deleteRecord(AttendanceRecord key) throws IOException {
        List<AttendanceRecord> all = loadAllRecords();
        int idx = findRecordIndex(all, key);
        if (idx == -1) throw new IOException("Record not found to delete.");
        all.remove(idx);
        saveAllRecords(all);
    }

    // This identifies a record. We use Employee # + Date + Log In + Log Out.
    private static int findRecordIndex(List<AttendanceRecord> all, AttendanceRecord key) {
        for (int i = 0; i < all.size(); i++) {
            AttendanceRecord r = all.get(i);
            if (!safeEq(r.getEmployeeNumber(), key.getEmployeeNumber())) continue;
            if (!safeEq(r.getDate(), key.getDate())) continue;
            if (!safeEq(r.getLogIn(), key.getLogIn())) continue;
            if (!safeEq(r.getLogOut(), key.getLogOut())) continue;
            return i;
        }
        return -1;
    }

    private static boolean safeEq(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // Loads all attendance records (entire file)
    public static List<AttendanceRecord> loadAllRecords() {
        ensureFileExists();

        List<AttendanceRecord> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_PATH))) {
            String line = br.readLine(); // header
            if (line == null) return list;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                AttendanceRecord r = new AttendanceRecord();
                r.setEmployeeNumber(p[0].trim());
                r.setLastName(p[1].trim());
                r.setFirstName(p[2].trim());
                r.setDate(parseDateFlexible(p[3].trim()));
                r.setLogIn(parseTimeFlexible(p[4].trim()));
                r.setLogOut(parseTimeFlexible(p[5].trim()));

                list.add(r);
            }
        } catch (Exception ignored) {}

        // Keep a stable order in the file
        list.sort(Comparator.comparing(AttendanceRecord::getEmployeeNumber, Comparator.nullsLast(String::compareTo))
                .thenComparing(AttendanceRecord::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(AttendanceRecord::getLogIn, Comparator.nullsLast(Comparator.naturalOrder())));

        return list;
    }

    // Saves all records back to CSV (rewrite file)
    public static void saveAllRecords(List<AttendanceRecord> records) throws IOException {
        ensureFileExists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(ATTENDANCE_CSV_PATH))) {
            pw.println(HEADER);

            for (AttendanceRecord r : records) {
                String date = (r.getDate() == null) ? "" : r.getDate().format(OUT_DATE);
                String in = (r.getLogIn() == null) ? "" : r.getLogIn().format(OUT_TIME);
                String out = (r.getLogOut() == null) ? "" : r.getLogOut().format(OUT_TIME);

                pw.println(String.join(",",
                        safeCsv(r.getEmployeeNumber()),
                        safeCsv(r.getLastName()),
                        safeCsv(r.getFirstName()),
                        safeCsv(date),
                        safeCsv(in),
                        safeCsv(out)
                ));
            }
        }
    }

    private static String safeCsv(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static void ensureFileExists() {
        File f = new File(ATTENDANCE_CSV_PATH);
        if (!f.exists()) {
            try {
                File parent = f.getParentFile();
                if (parent != null) parent.mkdirs();
                try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                    pw.println(HEADER);
                }
            } catch (Exception ignored) {}
        }
    }

    // ===================== RULE HELPERS =====================
    public static long computeLateMinutesWithGrace(LocalTime timeIn, int graceMinutes) {
        LocalTime graceCutoff = START_TIME.plusMinutes(graceMinutes);
        if (timeIn != null && timeIn.isAfter(graceCutoff)) {
            return Duration.between(START_TIME, timeIn).toMinutes();
        }
        return 0;
    }

    public static double computeWorkedHours(LocalTime in, LocalTime out) {
        if (in == null || out == null) return 0;
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes < 0) return 0;
        return minutes / 60.0;
    }

    // ===================== PARSERS =====================
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
