package motorph.util;

import motorph.model.PayrollRecord;

import java.io.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PayrollIOUtil {

    private static final String PATH = "data/payroll_records.csv";

    public static void appendPayrollRecord(PayrollRecord pr) {
        ensureFile();

        try (PrintWriter pw = new PrintWriter(new FileWriter(PATH, true))) {
            pw.println(toCsv(pr));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PayrollRecord> loadPayrollRecordsForEmployeeMonth(String empNo, YearMonth ym) {
        ensureFile();

        List<PayrollRecord> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                String eNo = get(p, 0);
                String monthText = get(p, 2); // yyyy-MM

                if (!eNo.equals(empNo)) continue;
                if (!monthText.equals(ym.toString())) continue;

                out.add(fromCsv(p));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // ✅ ADDED (ONLY WHAT WAS MISSING)
    public static PayrollRecord findLatestForEmployee(String empNo) {
        ensureFile();

        PayrollRecord latest = null;

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                String eNo = get(p, 0);
                if (!eNo.equals(empNo)) continue;

                PayrollRecord pr = fromCsv(p);

                if (latest == null) {
                    latest = pr;
                } else {
                    // compare YearMonth (latest month wins)
                    if (pr.getMonth() != null && latest.getMonth() != null
                            && pr.getMonth().isAfter(latest.getMonth())) {
                        latest = pr;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return latest;
    }

    public static String formatPayslipText(PayrollRecord pr) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== MOTORPH PAYSLIP (Monthly) ===========\n");
        sb.append("Employee #: ").append(pr.getEmployeeNumber()).append("\n");
        sb.append("Name     : ").append(pr.getEmployeeName()).append("\n");
        sb.append("Month    : ").append(pr.getMonth()).append("\n");
        sb.append("\n");

        sb.append("Attendance Summary\n");
        sb.append("Days Present : ").append(pr.getDaysPresent()).append("\n");
        sb.append("Late Minutes : ").append(pr.getLateMinutes()).append("\n");
        sb.append("Late Deduct  : ").append(money(pr.getLateDeduction())).append("\n");
        sb.append("\n");

        sb.append("Earnings\n");
        sb.append("Basic (Earned)  : ").append(money(pr.getMonthlyBasicSalary())).append("\n");
        sb.append("Allowances      : ").append(money(pr.getTotalAllowancesMonthly())).append("\n");
        sb.append("Gross Pay       : ").append(money(pr.getGrossPay())).append("\n");
        sb.append("\n");

        sb.append("Deductions\n");
        sb.append("SSS        : ").append(money(pr.getSss())).append("\n");
        sb.append("PhilHealth : ").append(money(pr.getPhilHealth())).append("\n");
        sb.append("Pag-IBIG   : ").append(money(pr.getPagIbig())).append("\n");
        sb.append("Total Gov  : ").append(money(pr.getTotalDeductionsBeforeTax())).append("\n");
        sb.append("Taxable    : ").append(money(pr.getTaxableIncome())).append("\n");
        sb.append("W/Tax      : ").append(money(pr.getWithholdingTax())).append("\n");
        sb.append("\n");

        sb.append("NET PAY: ").append(money(pr.getNetPay())).append("\n");
        sb.append("===============================================\n");
        return sb.toString();
    }

    // ---------------- CSV ----------------
    private static String toCsv(PayrollRecord pr) {
        return csv(
                pr.getEmployeeNumber(),
                pr.getEmployeeName(),
                pr.getMonth().toString(),
                String.valueOf(pr.getDaysPresent()),
                String.valueOf(pr.getLateMinutes()),
                String.valueOf(pr.getLateDeduction()),
                String.valueOf(pr.getMonthlyBasicSalary()),
                String.valueOf(pr.getTotalAllowancesMonthly()),
                String.valueOf(pr.getGrossPay()),
                String.valueOf(pr.getSss()),
                String.valueOf(pr.getPhilHealth()),
                String.valueOf(pr.getPagIbig()),
                String.valueOf(pr.getTotalDeductionsBeforeTax()),
                String.valueOf(pr.getTaxableIncome()),
                String.valueOf(pr.getWithholdingTax()),
                String.valueOf(pr.getNetPay())
        );
    }

    private static PayrollRecord fromCsv(String[] p) {
        PayrollRecord pr = new PayrollRecord();
        pr.setEmployeeNumber(get(p, 0));
        pr.setEmployeeName(get(p, 1));
        pr.setMonth(YearMonth.parse(get(p, 2)));

        pr.setDaysPresent(parseInt(get(p, 3)));
        pr.setLateMinutes(parseInt(get(p, 4)));

        pr.setLateDeduction(parseD(get(p, 5)));
        pr.setMonthlyBasicSalary(parseD(get(p, 6)));
        pr.setTotalAllowancesMonthly(parseD(get(p, 7)));
        pr.setGrossPay(parseD(get(p, 8)));

        pr.setSss(parseD(get(p, 9)));
        pr.setPhilHealth(parseD(get(p, 10)));
        pr.setPagIbig(parseD(get(p, 11)));
        pr.setTotalDeductionsBeforeTax(parseD(get(p, 12)));
        pr.setTaxableIncome(parseD(get(p, 13)));
        pr.setWithholdingTax(parseD(get(p, 14)));
        pr.setNetPay(parseD(get(p, 15)));

        return pr;
    }

    private static void ensureFile() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        File f = new File(PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("Employee #,Employee Name,Month,Days Present,Late Minutes,Late Deduction,Basic Earned,Allowances Earned,Gross Pay,SSS,PhilHealth,Pag-IBIG,Total Gov,Taxable Income,Withholding Tax,Net Pay");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String get(String[] p, int idx) {
        if (idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static double parseD(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }

    private static String csv(String... vals) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            String v = vals[i] == null ? "" : vals[i];
            v = v.replace(",", " ").replace("\n", " ").replace("\r", " ");
            if (i > 0) sb.append(",");
            sb.append(v);
        }
        return sb.toString();
    }

    private static String money(double v) {
        return String.format("%,.2f", v);
    }
}
