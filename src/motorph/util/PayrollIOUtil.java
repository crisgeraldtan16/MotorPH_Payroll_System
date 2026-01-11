package motorph.util;

import motorph.model.PayrollRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class PayrollIOUtil {

    private static final String PAYROLL_LOG_FILE = "data/payroll_records.csv";
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    // Header for saved payroll records (simple log)
    private static final String HEADER =
            "Month,Employee #,Employee Name,Days Present,Late Minutes,Late Deduction," +
                    "Monthly Basic Salary,Allowances,Gross Pay,SSS,PhilHealth,Pag-IBIG,Total Gov Deductions," +
                    "Taxable Income,Withholding Tax,Net Pay";

    public static void appendPayrollRecord(PayrollRecord pr) {
        try {
            ensureParentFolderExists(PAYROLL_LOG_FILE);

            File file = new File(PAYROLL_LOG_FILE);
            boolean needsHeader = !file.exists() || file.length() == 0;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (needsHeader) {
                    bw.write(HEADER);
                    bw.newLine();
                }

                bw.write(toCsvLine(pr));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save payroll record: " + e.getMessage(), e);
        }
    }

    private static void ensureParentFolderExists(String filepath) throws IOException {
        Path p = Path.of(filepath).toAbsolutePath();
        Path parent = p.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private static String toCsvLine(PayrollRecord pr) {
        // Escape commas by wrapping in quotes if needed (Employee Name)
        String month = pr.getMonth() == null ? "" : pr.getMonth().format(YM_FMT);

        String empNo = safe(pr.getEmployeeNumber());
        String empName = csvSafe(pr.getEmployeeName());

        return String.join(",",
                month,
                empNo,
                empName,
                String.valueOf(pr.getDaysPresent()),
                String.valueOf(pr.getLateMinutes()),
                money(pr.getLateDeduction()),
                money(pr.getMonthlyBasicSalary()),
                money(pr.getTotalAllowancesMonthly()),
                money(pr.getGrossPay()),
                money(pr.getSss()),
                money(pr.getPhilHealth()),
                money(pr.getPagIbig()),
                money(pr.getTotalDeductionsBeforeTax()),
                money(pr.getTaxableIncome()),
                money(pr.getWithholdingTax()),
                money(pr.getNetPay())
        );
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String csvSafe(String s) {
        String v = safe(s);
        if (v.contains(",") || v.contains("\"")) {
            v = v.replace("\"", "\"\"");
            return "\"" + v + "\"";
        }
        return v;
    }

    private static String money(double v) {
        return String.format("%.2f", v);
    }
}
