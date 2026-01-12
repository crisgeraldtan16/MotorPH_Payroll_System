package motorph.ui;

import motorph.model.AttendanceEntry;
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

    // THEME (light)
    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(35, 45, 65);
    private static final Color MUTED = new Color(110, 120, 145);

    // Status pill colors
    private static final Color PILL_NEUTRAL_BG = new Color(235, 238, 245);
    private static final Color PILL_NEUTRAL_FG = new Color(90, 100, 120);

    private static final Color PILL_OK_BG = new Color(214, 245, 224);
    private static final Color PILL_OK_FG = new Color(20, 120, 60);

    private static final Color PILL_WARN_BG = new Color(255, 236, 200);
    private static final Color PILL_WARN_FG = new Color(160, 120, 30);

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

    // Compute tab
    private JLabel warnLabel;
    private JLabel statusPill;

    private JLabel empNoVal, nameVal, positionVal, statusVal, monthVal;
    private JLabel daysPresentVal, lateMinutesVal, lateDeductVal;
    private JLabel basicVal, allowVal, grossVal;
    private JLabel sssVal, philVal, pagibigVal, totalGovVal;
    private JLabel taxableVal, taxVal, netVal;

    private JButton saveRecordBtn; // disabled until computed

    // Payslip tab
    private JTextArea payslipArea;

    // Monthly summary tab
    private DefaultTableModel monthTableModel;
    private JLabel monthTotalNetVal, monthTotalTaxVal, monthTotalGovVal, monthCountVal;

    // Timecard tab
    private DefaultTableModel timecardTableModel;
    private JLabel timecardDaysVal, timecardHoursVal, timecardLateVal;

    // Last computed record
    private PayrollRecord lastRecord;
    private Employee lastEmployee;

    public PayrollPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        employees = CSVUtil.loadEmployees();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    // ---------------- Header ----------------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("Payroll");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Compute payroll using attendance and government deductions");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> mainFrame.showContent("DASHBOARD"));

        header.add(left, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        return header;
    }

    // ---------------- Body ----------------
    private JComponent buildBody() {
        JPanel left = buildLeftSelectionPanel();

        tabs = new JTabbedPane();
        tabs.setBackground(CARD_BG);

        tabs.addTab("Compute", buildComputeTab());
        tabs.addTab("Payslip", buildPayslipTab());
        tabs.addTab("Monthly Summary", buildMonthlySummaryTab());
        tabs.addTab("Timecard", buildTimecardTab()); // ✅ NEW

        JPanel tabsCard = createCard(new BorderLayout());
        tabsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
        tabsCard.add(tabs, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, tabsCard);
        split.setResizeWeight(0.40);
        split.setDividerLocation(430);
        split.setBorder(null);

        return split;
    }

    // ---------------- Left Selection Panel ----------------
    private JPanel buildLeftSelectionPanel() {
        JPanel card = createCard(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Select Employee");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel hint = new JLabel("Choose month/year, then compute payroll or view timecard");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(MUTED);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(2));
        head.add(hint);

        card.add(head, BorderLayout.NORTH);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
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
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshEmployees());

        JButton computeSelectedBtn = new JButton("Compute Selected");
        computeSelectedBtn.setFocusPainted(false);
        computeSelectedBtn.setFont(new Font("Arial", Font.BOLD, 12));
        computeSelectedBtn.addActionListener(e -> computeSelectedEmployee());

        JButton viewTimecardBtn = new JButton("View Timecard");
        viewTimecardBtn.setFocusPainted(false);
        viewTimecardBtn.addActionListener(e -> viewSelectedTimecard());

        JButton computeAllBtn = new JButton("Compute All (Monthly)");
        computeAllBtn.setFocusPainted(false);
        computeAllBtn.addActionListener(e -> computeAllEmployeesForMonth());

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearAllOutputs());

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        controls.add(labelMuted("Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(yearCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        controls.add(labelMuted("Month:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(monthCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        controls.add(refreshBtn, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(computeSelectedBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        controls.add(viewTimecardBtn, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        controls.add(clearBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        controls.add(computeAllBtn, gbc);
        gbc.gridwidth = 1;

        JPanel search = new JPanel(new BorderLayout(8, 6));
        search.setOpaque(false);
        JLabel sTitle = labelMuted("Search (Name or Employee #):");
        searchField = new JTextField();
        search.add(sTitle, BorderLayout.NORTH);
        search.add(searchField, BorderLayout.CENTER);

        empTableModel = new DefaultTableModel(new Object[]{"Employee #", "Name", "Position", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        employeeTable = new JTable(empTableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(26);

        empSorter = new TableRowSorter<>(empTableModel);
        employeeTable.setRowSorter(empSorter);

        refreshEmployeeTable();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearch(); }
        });

        JScrollPane tableScroll = new JScrollPane(employeeTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(controls, BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);
        center.add(search, BorderLayout.SOUTH);

        card.add(center, BorderLayout.CENTER);

        return card;
    }

    private JLabel labelMuted(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(MUTED);
        return l;
    }

    private JPanel createCard(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD_BG);
        return p;
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
        JPanel wrap = new JPanel(new BorderLayout(12, 12));
        wrap.setBackground(CARD_BG);
        wrap.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topRow = new JPanel(new BorderLayout(10, 0));
        topRow.setOpaque(false);

        warnLabel = new JLabel("Select an employee then click “Compute Selected”.");
        warnLabel.setForeground(MUTED);

        statusPill = createStatusPill("WAITING", PILL_NEUTRAL_BG, PILL_NEUTRAL_FG);

        topRow.add(warnLabel, BorderLayout.WEST);
        topRow.add(statusPill, BorderLayout.EAST);

        wrap.add(topRow, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
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
        netVal.setFont(new Font("Arial", Font.BOLD, 18));
        netVal.setForeground(TEXT);
        r = row(grid, gbc, r, "Taxable Income:", taxableVal);
        r = row(grid, gbc, r, "Withholding Tax:", taxVal);
        r = row(grid, gbc, r, "NET PAY:", netVal);

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        wrap.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        saveRecordBtn = new JButton("Save Record");
        saveRecordBtn.setFocusPainted(false);
        saveRecordBtn.setEnabled(false); // ✅ disabled until computed
        saveRecordBtn.addActionListener(e -> saveLastRecord());

        bottom.add(saveRecordBtn);
        wrap.add(bottom, BorderLayout.SOUTH);

        clearComputeView();
        setStatus("WAITING", PILL_NEUTRAL_BG, PILL_NEUTRAL_FG);
        return wrap;
    }

    private JLabel createStatusPill(String text, Color bg, Color fg) {
        JLabel pill = new JLabel(text);
        pill.setOpaque(true);
        pill.setBackground(bg);
        pill.setForeground(fg);
        pill.setFont(new Font("Arial", Font.BOLD, 12));
        pill.setBorder(new EmptyBorder(6, 10, 6, 10));
        return pill;
    }

    private void setStatus(String text, Color bg, Color fg) {
        if (statusPill == null) return;
        statusPill.setText(text);
        statusPill.setBackground(bg);
        statusPill.setForeground(fg);
    }

    private JLabel valLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setForeground(TEXT);
        return l;
    }

    private int section(JPanel grid, GridBagConstraints gbc, int row, String title) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(MUTED);

        grid.add(label, gbc);
        gbc.gridwidth = 1;
        return row + 1;
    }

    private int row(JPanel grid, GridBagConstraints gbc, int row, String left, JComponent right) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;

        JLabel l = new JLabel(left);
        l.setForeground(MUTED);
        grid.add(l, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        grid.add(right, gbc);

        return row + 1;
    }

    // ---------------- Payslip Tab ----------------
    private JPanel buildPayslipTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Payslip Preview");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel hint = new JLabel("Compute an employee to generate the payslip.");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(hint);

        panel.add(top, BorderLayout.NORTH);

        payslipArea = new JTextArea();
        payslipArea.setEditable(false);
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setText("Payslip will appear here after computing an employee.\n");
        payslipArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(payslipArea);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton copyBtn = new JButton("Copy Payslip");
        copyBtn.setFocusPainted(false);
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
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Monthly Summary");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel hint = new JLabel("Click “Compute All (Monthly)” on the left to populate this table.");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(hint);

        panel.add(top, BorderLayout.NORTH);

        monthTableModel = new DefaultTableModel(
                new Object[]{"Employee #", "Name", "Days Present", "Late (min)", "Gov Deductions", "Tax", "Net Pay"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(monthTableModel);
        table.setRowHeight(26);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.add(sp, BorderLayout.CENTER);

        JPanel totalsCard = new JPanel(new GridLayout(2, 2, 12, 10));
        totalsCard.setBackground(new Color(250, 251, 253));
        totalsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        monthCountVal = new JLabel("-");
        monthTotalGovVal = new JLabel("-");
        monthTotalTaxVal = new JLabel("-");
        monthTotalNetVal = new JLabel("-");
        monthTotalNetVal.setFont(new Font("Arial", Font.BOLD, 16));
        monthTotalNetVal.setForeground(TEXT);

        totalsCard.add(labelPair("Employees Computed:", monthCountVal));
        totalsCard.add(labelPair("Total Gov Deductions:", monthTotalGovVal));
        totalsCard.add(labelPair("Total Withholding Tax:", monthTotalTaxVal));
        totalsCard.add(labelPair("Total Net Pay:", monthTotalNetVal));

        panel.add(totalsCard, BorderLayout.SOUTH);

        clearMonthlySummary();
        return panel;
    }

    private JPanel labelPair(String left, JLabel right) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel l = new JLabel(left);
        l.setForeground(MUTED);

        right.setForeground(TEXT);

        p.add(l, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ----------------  Timecard Tab ----------------
    private JPanel buildTimecardTab() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Timecard (Daily Logs)");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel hint = new JLabel("Select employee + month, then click “View Timecard”.");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(hint);

        panel.add(top, BorderLayout.NORTH);

        timecardTableModel = new DefaultTableModel(
                new Object[]{"Date", "Time In", "Time Out", "Late (min)", "Worked Hours"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(timecardTableModel);
        table.setRowHeight(26);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        panel.add(sp, BorderLayout.CENTER);

        JPanel totalsCard = new JPanel(new GridLayout(1, 3, 12, 10));
        totalsCard.setBackground(new Color(250, 251, 253));
        totalsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));

        timecardDaysVal = new JLabel("-");
        timecardLateVal = new JLabel("-");
        timecardHoursVal = new JLabel("-");
        timecardHoursVal.setFont(new Font("Arial", Font.BOLD, 14));
        timecardHoursVal.setForeground(TEXT);

        totalsCard.add(labelPair("Days with Logs:", timecardDaysVal));
        totalsCard.add(labelPair("Total Late Minutes:", timecardLateVal));
        totalsCard.add(labelPair("Total Worked Hours:", timecardHoursVal));

        panel.add(totalsCard, BorderLayout.SOUTH);

        clearTimecard();
        return panel;
    }

    private void viewSelectedTimecard() {
        Employee emp = getSelectedEmployee();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        YearMonth ym = getSelectedMonth();
        refreshTimecard(emp.getEmployeeNumber(), ym);
        tabs.setSelectedIndex(3); // Timecard tab index
    }

    private void refreshTimecard(String employeeNo, YearMonth ym) {
        List<AttendanceEntry> entries = AttendanceUtil.loadEntriesForEmployeeMonth(employeeNo, ym);

        timecardTableModel.setRowCount(0);

        double totalHours = 0.0;
        long totalLate = 0;

        for (AttendanceEntry e : entries) {
            long late = AttendanceUtil.computeLateMinutesWithGrace(e.getTimeIn(), 10);
            double hours = AttendanceUtil.computeWorkedHours(e.getTimeIn(), e.getTimeOut());

            totalLate += late;
            totalHours += hours;

            timecardTableModel.addRow(new Object[]{
                    e.getDate(),
                    e.getTimeIn(),
                    e.getTimeOut(),
                    late,
                    String.format("%.2f", hours)
            });
        }

        timecardDaysVal.setText(String.valueOf(entries.size()));
        timecardLateVal.setText(String.valueOf(totalLate));
        timecardHoursVal.setText(String.format("%.2f", totalHours));
    }

    private void clearTimecard() {
        if (timecardTableModel != null) timecardTableModel.setRowCount(0);
        if (timecardDaysVal != null) timecardDaysVal.setText("-");
        if (timecardLateVal != null) timecardLateVal.setText("-");
        if (timecardHoursVal != null) timecardHoursVal.setText("-");
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

        saveRecordBtn.setEnabled(true);

        if (summary.daysPresent == 0) {
            warnLabel.setText("Warning: No attendance entries found for this employee in " + ym + ". Payroll computed with 0 attendance.");
            warnLabel.setForeground(PILL_WARN_FG);
            setStatus("NO ATTENDANCE", PILL_WARN_BG, PILL_WARN_FG);
        } else {
            warnLabel.setText("Computed successfully. You can Save Record or view Payslip.");
            warnLabel.setForeground(PILL_OK_FG);
            setStatus("COMPUTED", PILL_OK_BG, PILL_OK_FG);
        }

        updateComputeView(pr, emp);
        payslipArea.setText(formatPayslip(pr, emp));
        payslipArea.setCaretPosition(0);

        //  also refresh timecard automatically after compute
        refreshTimecard(emp.getEmployeeNumber(), ym);

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
        clearTimecard();
        payslipArea.setText("Payslip will appear here after computing an employee.\n");
        clearMonthlySummary();

        warnLabel.setText("Cleared. Select an employee then click “Compute Selected”.");
        warnLabel.setForeground(MUTED);

        if (saveRecordBtn != null) saveRecordBtn.setEnabled(false);

        setStatus("WAITING", PILL_NEUTRAL_BG, PILL_NEUTRAL_FG);
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

        return sb.toString();
    }
}
