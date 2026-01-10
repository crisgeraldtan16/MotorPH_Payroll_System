package motorph.ui;

import motorph.model.Employee;
import motorph.model.PayrollRecord;
import motorph.util.AttendanceUtil;
import motorph.util.CSVUtil;
import motorph.util.PayrollCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.YearMonth;
import java.util.List;

public class PayrollPanel extends JPanel {

    private final MainFrame mainFrame;

    private List<Employee> employees;

    // Employee table + search
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    // Month selection
    private JComboBox<Integer> yearCombo;
    private JComboBox<String> monthCombo;

    // Tabs
    private JTabbedPane tabs;
    private JTextArea payslipArea;

    // Summary labels (big readable)
    private JLabel empNoVal, nameVal, positionVal, statusVal, monthVal;
    private JLabel daysPresentVal, lateMinutesVal, lateDeductVal;
    private JLabel basicVal, allowVal, grossVal;
    private JLabel sssVal, philVal, pagibigVal, totalGovVal;
    private JLabel taxableVal, taxVal, netVal;

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

    // ---------------- Body Layout ----------------
    private JComponent buildBody() {
        // Left: employee selection
        JPanel left = buildEmployeeSelectionPanel();

        // Right: tabs (Summary + Payslip)
        tabs = new JTabbedPane();
        tabs.addTab("Payroll Summary", buildSummaryTab());
        tabs.addTab("Payslip (Text)", buildPayslipTab());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tabs);
        split.setResizeWeight(0.45); // left 45%, right 55%
        split.setDividerLocation(420);

        return split;
    }

    // ---------------- Left Panel (Employee selection + controls) ----------------
    private JPanel buildEmployeeSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Row 1: Month selection + compute button
        JPanel controlsTop = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int currentYear = YearMonth.now().getYear();
        Integer[] years = new Integer[]{2024, 2025, 2026, 2027};
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(2024); // default to 2024 for your attendance

        monthCombo = new JComboBox<>(new String[]{
                "01 - January", "02 - February", "03 - March", "04 - April",
                "05 - May", "06 - June", "07 - July", "08 - August",
                "09 - September", "10 - October", "11 - November", "12 - December"
        });
        monthCombo.setSelectedIndex(0);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshEmployees());

        JButton computeBtn = new JButton("Compute Selected");
        computeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        computeBtn.addActionListener(e -> computeSelectedEmployee());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        controlsTop.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controlsTop.add(yearCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        controlsTop.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controlsTop.add(monthCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        controlsTop.add(refreshBtn, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controlsTop.add(computeBtn, gbc);

        panel.add(controlsTop, BorderLayout.NORTH);

        // Row 2: Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(6, 6));
        searchPanel.add(new JLabel("Search (Name or Employee #):"), BorderLayout.NORTH);
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);

        panel.add(searchPanel, BorderLayout.SOUTH);

        // Table in the center
        tableModel = new DefaultTableModel(new Object[]{"Employee #", "Name", "Position", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(22);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        refreshTable();

        // Search filter (live)
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
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
    }

    private void refreshEmployees() {
        employees = CSVUtil.loadEmployees();
        refreshTable();
        clearResults();
        JOptionPane.showMessageDialog(this, "Employee list refreshed.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{
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
        String empNo = String.valueOf(tableModel.getValueAt(modelRow, 0));

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

    // ---------------- Right Tabs ----------------

    private JPanel buildSummaryTab() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel hint = new JLabel("Select an employee and click “Compute Selected”.");
        hint.setForeground(Color.DARK_GRAY);
        wrap.add(hint, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBorder(new EmptyBorder(10, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int r = 0;

        // Employee info
        r = addSection(grid, gbc, r, "Employee Information");
        empNoVal = new JLabel("-");
        nameVal = new JLabel("-");
        positionVal = new JLabel("-");
        statusVal = new JLabel("-");
        monthVal = new JLabel("-");
        r = addRow(grid, gbc, r, "Employee #:", empNoVal);
        r = addRow(grid, gbc, r, "Name:", nameVal);
        r = addRow(grid, gbc, r, "Position:", positionVal);
        r = addRow(grid, gbc, r, "Status:", statusVal);
        r = addRow(grid, gbc, r, "Month:", monthVal);

        // Attendance
        r = addSection(grid, gbc, r, "Attendance Summary (10-min grace)");
        daysPresentVal = new JLabel("-");
        lateMinutesVal = new JLabel("-");
        lateDeductVal = new JLabel("-");
        r = addRow(grid, gbc, r, "Days Present:", daysPresentVal);
        r = addRow(grid, gbc, r, "Late Minutes:", lateMinutesVal);
        r = addRow(grid, gbc, r, "Late Deduction:", lateDeductVal);

        // Earnings
        r = addSection(grid, gbc, r, "Earnings");
        basicVal = new JLabel("-");
        allowVal = new JLabel("-");
        grossVal = new JLabel("-");
        r = addRow(grid, gbc, r, "Monthly Basic Salary:", basicVal);
        r = addRow(grid, gbc, r, "Allowances (Monthly):", allowVal);
        r = addRow(grid, gbc, r, "Gross Pay (after late):", grossVal);

        // Deductions
        r = addSection(grid, gbc, r, "Government Deductions");
        sssVal = new JLabel("-");
        philVal = new JLabel("-");
        pagibigVal = new JLabel("-");
        totalGovVal = new JLabel("-");
        r = addRow(grid, gbc, r, "SSS:", sssVal);
        r = addRow(grid, gbc, r, "PhilHealth (employee):", philVal);
        r = addRow(grid, gbc, r, "Pag-IBIG (employee):", pagibigVal);
        r = addRow(grid, gbc, r, "Total Gov Deductions:", totalGovVal);

        // Tax + Net
        r = addSection(grid, gbc, r, "Tax & Net Pay");
        taxableVal = new JLabel("-");
        taxVal = new JLabel("-");
        netVal = new JLabel("-");
        netVal.setFont(new Font("Arial", Font.BOLD, 14));

        r = addRow(grid, gbc, r, "Taxable Income:", taxableVal);
        r = addRow(grid, gbc, r, "Withholding Tax:", taxVal);
        r = addRow(grid, gbc, r, "NET PAY:", netVal);

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        wrap.add(scroll, BorderLayout.CENTER);

        return wrap;
    }

    private JPanel buildPayslipTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setLineWrap(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setText("Payslip will appear here after computation.\n");

        JScrollPane sp = new JScrollPane(payslipArea);
        sp.setPreferredSize(new Dimension(600, 420));

        panel.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

    private int addSection(JPanel grid, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 13));

        grid.add(label, gbc);

        gbc.gridwidth = 1;
        return row + 1;
    }

    private int addRow(JPanel grid, GridBagConstraints gbc, int row, String left, JComponent right) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        grid.add(new JLabel(left), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        grid.add(right, gbc);

        return row + 1;
    }

    // ---------------- Compute + Display ----------------

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

        // Update Summary tab (big readable)
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

        // Update Payslip tab (bigger font, bigger area)
        payslipArea.setText(formatPayslip(pr, emp));
        payslipArea.setCaretPosition(0);

        // Jump to Summary tab automatically
        tabs.setSelectedIndex(0);
    }

    private void clearResults() {
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
        payslipArea.setText("Payslip will appear here after computation.\n");
    }

    private String money(double v) {
        return String.format("%,.2f", v);
    }

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
