package motorph.util;

import motorph.model.LeaveRequest;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LeaveIOUtil {

    private static final String PATH = "data/leaves.csv";
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<LeaveRequest> loadAll() {
        ensureFile();

        List<LeaveRequest> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                LeaveRequest r = new LeaveRequest();
                r.setRequestId(get(p, 0));
                r.setEmployeeNumber(get(p, 1));
                r.setEmployeeName(get(p, 2));
                r.setFromDate(get(p, 3));
                r.setToDate(get(p, 4));
                r.setReason(get(p, 5));

                String st = get(p, 6).toUpperCase();
                try { r.setStatus(LeaveRequest.Status.valueOf(st)); }
                catch (Exception e) { r.setStatus(LeaveRequest.Status.PENDING); }

                r.setSubmittedAt(get(p, 7));
                r.setReviewedBy(get(p, 8));
                r.setReviewedAt(get(p, 9));

                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<LeaveRequest> loadForEmployee(String empNo) {
        List<LeaveRequest> all = loadAll();
        List<LeaveRequest> out = new ArrayList<>();
        for (LeaveRequest r : all) {
            if (r.getEmployeeNumber() != null && r.getEmployeeNumber().equals(empNo)) {
                out.add(r);
            }
        }
        return out;
    }

    public static void append(LeaveRequest r) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH, true))) {
            pw.println(csv(
                    r.getRequestId(),
                    r.getEmployeeNumber(),
                    r.getEmployeeName(),
                    r.getFromDate(),
                    r.getToDate(),
                    r.getReason(),
                    r.getStatus().name(),
                    r.getSubmittedAt(),
                    r.getReviewedBy(),
                    r.getReviewedAt()
            ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void overwriteAll(List<LeaveRequest> list) {
        ensureFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH, false))) {
            pw.println("Request ID,Employee #,Employee Name,From,To,Reason,Status,Submitted At,Reviewed By,Reviewed At");
            for (LeaveRequest r : list) {
                pw.println(csv(
                        r.getRequestId(),
                        r.getEmployeeNumber(),
                        r.getEmployeeName(),
                        r.getFromDate(),
                        r.getToDate(),
                        r.getReason(),
                        r.getStatus().name(),
                        r.getSubmittedAt(),
                        r.getReviewedBy(),
                        r.getReviewedAt()
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String newRequestId() {
        return "LR-" + System.currentTimeMillis();
    }

    public static String now() {
        return LocalDateTime.now().format(TS);
    }

    private static void ensureFile() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File f = new File(PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Request ID,Employee #,Employee Name,From,To,Reason,Status,Submitted At,Reviewed By,Reviewed At");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String get(String[] p, int idx) {
        if (idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }

    private static String csv(String... vals) {
        // simple CSV safe (replace commas/newlines)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            String v = (vals[i] == null) ? "" : vals[i];
            v = v.replace(",", " ").replace("\n", " ").replace("\r", " ");
            if (i > 0) sb.append(",");
            sb.append(v);
        }
        return sb.toString();
    }
}
