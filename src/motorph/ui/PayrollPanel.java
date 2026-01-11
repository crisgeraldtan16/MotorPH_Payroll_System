package motorph.ui;

import motorph.model.Employee;
import motorph.model.PayrollRecord;
import motorph.util.AttendanceUtil;
import motorph.util.CSVUtil;
import motorph.util.PayrollCalculator;
import motorph.util.PayrollIOUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayrollPanel extends JPanel {

    private final MainFrame mainFrame;

    private List<Employee> employees;

    // Left side selection
    private JTable employeeTable;
    private DefaultTableModel empTableModel;
    private TableRowSorter<DefaultTableModel> empSorter;
    private JTextField searchField;

    // Month selection
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;

    // Tabs
    private JTabbedPane tabs;

    // Compute tab (summary labels)
    private JLabel warnLabel;

    private JLabel empNoVal, nameVal, positionVal, statusVal, monthVal;
    private JLabel daysPresentVal, lateMinutesVal, lateDeductVal;
    private JLabel basicVal, allowVal, grossVal;
    private JLabel sssVal, philVal, pagibigVal, totalGovVal;
    private JLabel taxableVal, taxVal, netVal;

    // Payslip tab
    private JTextArea payslipArea;

    // Monthly summary tab
    private DefaultTableModel monthTableModel;
    private JLabel monthTotalNetVal, monthTotalTaxVal, monthTotalGovVal, monthCountVal;

    // Last computed record
    private PayrollRecord lastRecord;
    private Employee lastEmployee;

    public PayrollPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        employees = CSVUtil.loadEmployees();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    // ---------------- Header ----------------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Payroll Computation");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> mainFrame.showContent("DASHBOARD"));

        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);

        return header;
    }

    // ---------------- Body ----------------
    private JComponent buildBody() {
        JPanel left = buildLeftSelectionPanel();

        tabs = new JTabbedPane();
        tabs.addTab("Compute", buildComputeTab());
        tabs.addTab("Payslip", buildPayslipTab());
        tabs.addTab("Monthly Summary", buildMonthlySummaryTab());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tabs);
        split.setResizeWeight(0.42);
        split.setDividerLocation(420);

        return split;
    }

    // ---------------- Left Selection Panel ----------------
    private JPanel buildLeftSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Controls (Year/Month + buttons)
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        yearCombo = new JComboBox<>(new Integer[]{2024, 2025, 2026, 2027});
        yearCombo.setSelectedItem(2024);

        monthCombo = new JComboBox<>(new String[]{
                "01 - January", "02 - February", "03 - March", "04 - April",
                "05 - May", "06 - June", "07 - July", "08 - August",
                "09 - September", "10 - October", "11 - November", "12 - December"
        });
        monthCombo.setSelectedIndex(0);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshEmployees());

        JButton computeSelectedBtn = new JButton("Compute Selected");
        computeSelectedBtn.setFont(new Font("Arial", Font.BOLD, 12));
        computeSelectedBtn.addActionListener(e -> computeSelectedEmployee());

        JButton computeAllBtn = new JButton("Compute All (Monthly)");
        computeAllBtn.addActionListener(e -> computeAllEmployeesForMonth());

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearAllOutputs());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        controls.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(yearCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        controls.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(monthCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        controls.add(refreshBtn, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(computeSelectedBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        controls.add(clearBtn, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(computeAllBtn, gbc);

        panel.add(controls, BorderLayout.NORTH);

        // Search bar
        JPanel search = new JPanel(new BorderLayout(6, 6));
        search.add(new JLabel("Search (Name or Employee #):"), BorderLayout.NORTH);
        searchField = new JTextField();
        search.add(searchField, BorderLayout.CENTER);

        panel.add(search, BorderLayout.SOUTH);

        // Employee table
        empTableModel = new DefaultTableModel(new Object[]{"Employee #", "Name", "Position", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        employeeTable = new JTable(empTableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(22);

        empSorter = new TableRowSorter<>(empTableModel);
        employeeTable.setRowSorter(empSorter);

        refreshEmployeeTable();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
        });

        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        return panel;
    }

    private void applySearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            empSorter.setRowFilter(null);
            return;
        }
        empSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private void refreshEmployees() {
        employees = CSVUtil.loadEmployees();
        refreshEmployeeTable();
        JOptionPane.showMessageDialog(this, "Employee list refreshed.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshEmployeeTable() {
        empTableModel.setRowCount(0);
        for (Employee e : employees) {
            empTableModel.addRow(new Object[]{
                    e.getEmployeeNumber(),
                    e.getFullName(),
                    e.getPosition(),
                    e.getStatus()
            });
        }
    }

    private Employee getSelectedEmployee() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow == -1) return null;

        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        String empNo = String.valueOf(empTableModel.getValueAt(modelRow, 0));

        for (Employee e : employees) {
            if (e.getEmployeeNumber().equals(empNo)) return e;
        }
        return null;
    }

    private YearMonth getSelectedMonth() {
        int year = (int) yearCombo.getSelectedItem();
        String monthText = (String) monthCombo.getSelectedItem();
        int month = Integer.parseInt(monthText.substring(0, 2));
        return YearMonth.of(year, month);
    }

    // ---------------- Compute Tab ----------------
    private JPanel buildComputeTab() {
        JPanel wrap = new JPanel(new BorderLayout(10, 10));
        wrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Warning/info line
        warnLabel = new JLabel("Select an employee then click “Compute Selected”.");
        warnLabel.setForeground(new Color(80, 80, 80));
        wrap.add(warnLabel, BorderLayout.NORTH);

        // Summary grid
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBorder(new EmptyBorder(4, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int r = 0;

        r = section(grid, gbc, r, "Employee Information");
        empNoVal = valLabel(); nameVal = valLabel(); positionVal = valLabel(); statusVal = valLabel(); monthVal = valLabel();
        r = row(grid, gbc, r, "Employee #:", empNoVal);
        r = row(grid, gbc, r, "Name:", nameVal);
        r = row(grid, gbc, r, "Position:", positionVal);
        r = row(grid, gbc, r, "Status:", statusVal);
        r = row(grid, gbc, r, "Month:", monthVal);

        r = section(grid, gbc, r, "Attendance Summary (10-min grace)");
        daysPresentVal = valLabel(); lateMinutesVal = valLabel(); lateDeductVal = valLabel();
        r = row(grid, gbc, r, "Days Present:", daysPresentVal);
        r = row(grid, gbc, r, "Late Minutes:", lateMinutesVal);
        r = row(grid, gbc, r, "Late Deduction:", lateDeductVal);

        r = section(grid, gbc, r, "Earnings");
        basicVal = valLabel(); allowVal = valLabel(); grossVal = valLabel();
        r = row(grid, gbc, r, "Monthly Basic Salary:", basicVal);
        r = row(grid, gbc, r, "Allowances (Monthly):", allowVal);
        r = row(grid, gbc, r, "Gross Pay (after late):", grossVal);

        r = section(grid, gbc, r, "Deductions");
        sssVal = valLabel(); philVal = valLabel(); pagibigVal = valLabel(); totalGovVal = valLabel();
        r = row(grid, gbc, r, "SSS:", sssVal);
        r = row(grid, gbc, r, "PhilHealth (employee):", philVal);
        r = row(grid, gbc, r, "Pag-IBIG (employee):", pagibigVal);
        r = row(grid, gbc, r, "Total Gov Deductions:", totalGovVal);

        r = section(grid, gbc, r, "Tax & Net Pay");
        taxableVal = valLabel(); taxVal = valLabel(); netVal = new JLabel("-");
        netVal.setFont(new Font("Arial", Font.BOLD, 16));
        r = row(grid, gbc, r, "Taxable Income:", taxableVal);
        r = row(grid, gbc, r, "Withholding Tax:", taxVal);
        r = row(grid, gbc, r, "NET PAY:", netVal);

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(BorderFactory.createEmptyBorder());
        wrap.add(sp, BorderLayout.CENTER);

        // Bottom actions: Save Record
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton saveRecordBtn = new JButton("Save Record");
        saveRecordBtn.addActionListener(e -> saveLastRecord());
        bottom.add(saveRecordBtn);

        wrap.add(bottom, BorderLayout.SOUTH);

        clearComputeView();
        return wrap;
    }

    private JLabel valLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private int section(JPanel grid, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 60));

        grid.add(label, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }

    private int row(JPanel grid, GridBagConstraints gbc, int row, String left, JComponent right) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        grid.add(new JLabel(left), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        grid.add(right, gbc);

        return row + 1;
    }

    // ---------------- Payslip Tab ----------------
    private JPanel buildPayslipTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setText("Payslip will appear here after computing an employee.\n");

        JScrollPane sp = new JScrollPane(payslipArea);
        panel.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton copyBtn = new JButton("Copy Payslip");
        copyBtn.addActionListener(e -> {
            payslipArea.selectAll();
            payslipArea.copy();
            payslipArea.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, "Payslip copied to clipboard.", "Copied", JOptionPane.INFORMATION_MESSAGE);
        });

        bottom.add(copyBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------- Monthly Summary Tab ----------------
    private JPanel buildMonthlySummaryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Monthly Summary (Compute All Employees)");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        monthTableModel = new DefaultTableModel(
                new Object[]{"Employee #", "Name", "Days Present", "Late (min)", "Gov Deductions", "Tax", "Net Pay"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(monthTableModel);
        table.setRowHeight(22);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel totals = new JPanel(new GridLayout(2, 2, 10, 6));
        totals.setBorder(new EmptyBorder(8, 0, 0, 0));

        monthCountVal = new JLabel("-");
        monthTotalGovVal = new JLabel("-");
        monthTotalTaxVal = new JLabel("-");
        monthTotalNetVal = new JLabel("-");
        monthTotalNetVal.setFont(new Font("Arial", Font.BOLD, 14));

        totals.add(labelPair("Employees Computed:", monthCountVal));
        totals.add(labelPair("Total Gov Deductions:", monthTotalGovVal));
        totals.add(labelPair("Total Withholding Tax:", monthTotalTaxVal));
        totals.add(labelPair("Total Net Pay:", monthTotalNetVal));

        panel.add(totals, BorderLayout.SOUTH);

        clearMonthlySummary();
        return panel;
    }

    private JPanel labelPair(String left, JLabel right) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(left), BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ---------------- Actions ----------------
    private void computeSelectedEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        YearMonth ym = getSelectedMonth();

        AttendanceUtil.AttendanceSummary summary =
                AttendanceUtil.summarizeForEmployeeMonth(emp.getEmployeeNumber(), ym);

        PayrollRecord pr = PayrollCalculator.computeMonthlyPayroll(
                emp, ym, summary.daysPresent, summary.totalLateMinutes
        );

        lastRecord = pr;
        lastEmployee = emp;

        // Warn if no attendance found
        if (summary.daysPresent == 0) {
            warnLabel.setText("Warning: No attendance entries found for this employee in " + ym + ". Payroll computed with 0 attendance.");
            warnLabel.setForeground(new Color(150, 80, 0));
        } else {
            warnLabel.setText("Computed successfully. You can Save Record or view Payslip.");
            warnLabel.setForeground(new Color(20, 120, 60));
        }

        updateComputeView(pr, emp);
        payslipArea.setText(formatPayslip(pr, emp));
        payslipArea.setCaretPosition(0);

        tabs.setSelectedIndex(0);
    }

    private void computeAllEmployeesForMonth() {
        YearMonth ym = getSelectedMonth();

        List<PayrollRecord> records = new ArrayList<>();
        monthTableModel.setRowCount(0);

        double totalGov = 0;
        double totalTax = 0;
        double totalNet = 0;

        for (Employee emp : employees) {
            AttendanceUtil.AttendanceSummary summary =
                    AttendanceUtil.summarizeForEmployeeMonth(emp.getEmployeeNumber(), ym);

            PayrollRecord pr = PayrollCalculator.computeMonthlyPayroll(emp, ym, summary.daysPresent, summary.totalLateMinutes);
            records.add(pr);

            double gov = pr.getTotalDeductionsBeforeTax();
            double tax = pr.getWithholdingTax();
            double net = pr.getNetPay();

            totalGov += gov;
            totalTax += tax;
            totalNet += net;

            monthTableModel.addRow(new Object[]{
                    pr.getEmployeeNumber(),
                    pr.getEmployeeName(),
                    pr.getDaysPresent(),
                    pr.getLateMinutes(),
                    money(gov),
                    money(tax),
                    money(net)
            });
        }

        monthCountVal.setText(String.valueOf(records.size()));
        monthTotalGovVal.setText(money(totalGov));
        monthTotalTaxVal.setText(money(totalTax));
        monthTotalNetVal.setText(money(totalNet));

        tabs.setSelectedIndex(2);
    }

    private void saveLastRecord() {
        if (lastRecord == null) {
            JOptionPane.showMessageDialog(this, "No computed payroll record yet. Compute an employee first.", "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PayrollIOUtil.appendPayrollRecord(lastRecord);
            JOptionPane.showMessageDialog(this,
                    "Payroll record saved to: data/payroll_records.csv",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save payroll record:\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAllOutputs() {
        lastRecord = null;
        lastEmployee = null;

        clearComputeView();
        payslipArea.setText("Payslip will appear here after computing an employee.\n");
        clearMonthlySummary();

        warnLabel.setText("Cleared. Select an employee then click “Compute Selected”.");
        warnLabel.setForeground(new Color(80, 80, 80));
    }

    // ---------------- UI updates ----------------
    private void updateComputeView(PayrollRecord pr, Employee emp) {
        empNoVal.setText(pr.getEmployeeNumber());
        nameVal.setText(pr.getEmployeeName());
        positionVal.setText(emp.getPosition());
        statusVal.setText(emp.getStatus());
        monthVal.setText(String.valueOf(pr.getMonth()));

        daysPresentVal.setText(String.valueOf(pr.getDaysPresent()));
        lateMinutesVal.setText(String.valueOf(pr.getLateMinutes()));
        lateDeductVal.setText(money(pr.getLateDeduction()));

        basicVal.setText(money(pr.getMonthlyBasicSalary()));
        allowVal.setText(money(pr.getTotalAllowancesMonthly()));
        grossVal.setText(money(pr.getGrossPay()));

        sssVal.setText(money(pr.getSss()));
        philVal.setText(money(pr.getPhilHealth()));
        pagibigVal.setText(money(pr.getPagIbig()));
        totalGovVal.setText(money(pr.getTotalDeductionsBeforeTax()));

        taxableVal.setText(money(pr.getTaxableIncome()));
        taxVal.setText(money(pr.getWithholdingTax()));
        netVal.setText(money(pr.getNetPay()));
    }

    private void clearComputeView() {
        empNoVal.setText("-");
        nameVal.setText("-");
        positionVal.setText("-");
        statusVal.setText("-");
        monthVal.setText("-");

        daysPresentVal.setText("-");
        lateMinutesVal.setText("-");
        lateDeductVal.setText("-");

        basicVal.setText("-");
        allowVal.setText("-");
        grossVal.setText("-");

        sssVal.setText("-");
        philVal.setText("-");
        pagibigVal.setText("-");
        totalGovVal.setText("-");

        taxableVal.setText("-");
        taxVal.setText("-");
        netVal.setText("-");
    }

    private void clearMonthlySummary() {
        monthTableModel.setRowCount(0);
        monthCountVal.setText("-");
        monthTotalGovVal.setText("-");
        monthTotalTaxVal.setText("-");
        monthTotalNetVal.setText("-");
    }

    private String money(double v) {
        return String.format("%,.2f", v);
    }

    // ---------------- Payslip formatting ----------------
    private String formatPayslip(PayrollRecord pr, Employee emp) {
        StringBuilder sb = new StringBuilder();

        sb.append("=========== MOTORPH PAYROLL (Monthly) ===========\n");
        sb.append("Employee #: ").append(pr.getEmployeeNumber()).append("\n");
        sb.append("Name     : ").append(pr.getEmployeeName()).append("\n");
        sb.append("Position : ").append(emp.getPosition()).append("\n");
        sb.append("Status   : ").append(emp.getStatus()).append("\n");
        sb.append("Month    : ").append(pr.getMonth()).append("\n");
        sb.append("\n");

        sb.append("Attendance Summary\n");
        sb.append("Days Present : ").append(pr.getDaysPresent()).append("\n");
        sb.append("Late Minutes : ").append(pr.getLateMinutes()).append(" (after 10-min grace)\n");
        sb.append("Late Deduct  : ").append(money(pr.getLateDeduction())).append("\n");
        sb.append("\n");

        sb.append("Earnings\n");
        sb.append("Monthly Basic Salary : ").append(money(pr.getMonthlyBasicSalary())).append("\n");
        sb.append("Allowances (Monthly) : ").append(money(pr.getTotalAllowancesMonthly())).append("\n");
        sb.append("Gross Pay (after late): ").append(money(pr.getGrossPay())).append("\n");
        sb.append("\n");

        sb.append("Government Deductions\n");
        sb.append("SSS       : ").append(money(pr.getSss())).append("\n");
        sb.append("PhilHealth: ").append(money(pr.getPhilHealth())).append(" (employee share)\n");
        sb.append("Pag-IBIG  : ").append(money(pr.getPagIbig())).append("\n");
        sb.append("Total Gov Deductions: ").append(money(pr.getTotalDeductionsBeforeTax())).append("\n");
        sb.append("\n");

        sb.append("Tax\n");
        sb.append("Taxable Income (Basic - Gov Deductions): ").append(money(pr.getTaxableIncome())).append("\n");
        sb.append("Withholding Tax: ").append(money(pr.getWithholdingTax())).append("\n");
        sb.append("\n");

        sb.append("NET PAY: ").append(money(pr.getNetPay())).append("\n");
        sb.append("===============================================\n");

        sb.append("\nNotes:\n");
        sb.append("- Late is deducted only if Time In is after 08:10 (10-minute grace).\n");
        sb.append("- PhilHealth premium is 3% (min 300, max 1800), employee pays 50%.\n");
        sb.append("- Withholding tax is computed AFTER applying government deductions.\n");

        return sb.toString();
    }
}
