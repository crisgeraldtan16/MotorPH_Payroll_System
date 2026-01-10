package motorph.ui;

import motorph.model.Employee;
import motorph.util.CSVUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    // Stat values
    private JLabel totalEmployeesVal;
    private JLabel regularVal;
    private JLabel probationaryVal;
    private JLabel avgSalaryVal;

    // Other UI
    private JLabel lastUpdatedVal;
    private JLabel employeesCsvStatusVal;
    private JLabel attendanceCsvStatusVal;

    private DefaultTableModel recentTableModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        refreshDashboard();
    }

    // ---------------- Header ----------------
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Left side (title + subtitle)
        JLabel title = new JLabel("MotorPH Employee Payroll System");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Dashboard Overview");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(subtitle);

        // Right side (refresh + last updated)
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshDashboard());

        lastUpdatedVal = new JLabel("Last updated: -");
        lastUpdatedVal.setFont(new Font("Arial", Font.PLAIN, 12));
        lastUpdatedVal.setForeground(Color.DARK_GRAY);

        JPanel rightBox = new JPanel();
        rightBox.setOpaque(false);
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
        refreshBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lastUpdatedVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightBox.add(refreshBtn);
        rightBox.add(Box.createVerticalStrut(6));
        rightBox.add(lastUpdatedVal);

        header.add(titleBox, BorderLayout.WEST);
        header.add(rightBox, BorderLayout.EAST);

        return header;
    }

    // ---------------- Main Content ----------------
    private JComponent buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);

        // Top: stats row
        content.add(buildStatsRow(), BorderLayout.NORTH);

        // Bottom: split (Recent employees + System status / Quick actions)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildRecentEmployeesCard(),
                buildRightSideCards()
        );
        split.setResizeWeight(0.65);
        split.setDividerLocation(650);

        content.add(split, BorderLayout.CENTER);

        return content;
    }

    private JComponent buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 12));
        row.setOpaque(false);

        totalEmployeesVal = createBigValueLabel();
        regularVal = createBigValueLabel();
        probationaryVal = createBigValueLabel();
        avgSalaryVal = createBigValueLabel();

        row.add(createStatCard("Total Employees", totalEmployeesVal, "Count of all employees in employees.csv"));
        row.add(createStatCard("Regular", regularVal, "Employees marked as Regular"));
        row.add(createStatCard("Probationary", probationaryVal, "Employees marked as Probationary"));
        row.add(createStatCard("Avg Basic Salary", avgSalaryVal, "Average of Basic Salary field"));

        return row;
    }

    private JLabel createBigValueLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.BOLD, 28));
        return l;
    }

    private JComponent createStatCard(String title, JLabel value, String hint) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel hintLabel = new JLabel("<html><span style='font-size:10px;'>" + hint + "</span></html>");
        hintLabel.setForeground(Color.DARK_GRAY);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(titleLabel);
        top.add(Box.createVerticalStrut(4));
        top.add(hintLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // ---------------- Recent Employees ----------------
    private JComponent buildRecentEmployeesCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Recent Employees");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.DARK_GRAY);

        JLabel subtitle = new JLabel("Latest 5 employees by Employee #");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(3));
        top.add(subtitle);

        recentTableModel = new DefaultTableModel(new Object[]{"Employee #", "Name", "Position", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(recentTableModel);
        table.setRowHeight(22);

        card.add(top, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        return card;
    }

    // ---------------- Right Side (System + Quick actions) ----------------
    private JComponent buildRightSideCards() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        right.add(buildSystemStatusCard());
        right.add(Box.createVerticalStrut(12));
        right.add(buildQuickActionsCard());
        right.add(Box.createVerticalGlue());

        return right;
    }

    private JComponent buildSystemStatusCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("System Status");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.DARK_GRAY);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        employeesCsvStatusVal = new JLabel("-");
        attendanceCsvStatusVal = new JLabel("-");

        int r = 0;
        r = addStatusRow(grid, gbc, r, "employees.csv:", employeesCsvStatusVal);
        r = addStatusRow(grid, gbc, r, "attendance.csv:", attendanceCsvStatusVal);

        JLabel note = new JLabel("<html><span style='font-size:10px;'>If a file is missing, some features may show 0 results.</span></html>");
        note.setForeground(Color.DARK_GRAY);

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        card.add(note, BorderLayout.SOUTH);

        return card;
    }

    private int addStatusRow(JPanel grid, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.4;
        grid.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        grid.add(value, gbc);

        return row + 1;
    }

    private JComponent buildQuickActionsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(Color.DARK_GRAY);

        JLabel subtitle = new JLabel("Navigate quickly to key modules");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(Color.DARK_GRAY);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(3));
        top.add(subtitle);

        JButton employeesBtn = new JButton("Open Employees");
        employeesBtn.addActionListener(e -> navigateTo("EMPLOYEE"));

        JButton payrollBtn = new JButton("Open Payroll");
        payrollBtn.addActionListener(e -> navigateTo("PAYROLL"));

        JPanel buttons = new JPanel(new GridLayout(2, 1, 8, 8));
        buttons.setOpaque(false);
        buttons.add(employeesBtn);
        buttons.add(payrollBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);

        return card;
    }

    // ---------------- Refresh Logic ----------------
    private void refreshDashboard() {
        List<Employee> employees = CSVUtil.loadEmployees();

        int total = employees.size();
        int regular = 0;
        int probationary = 0;

        double sumSalary = 0;
        int salaryCount = 0;

        for (Employee e : employees) {
            String status = (e.getStatus() == null) ? "" : e.getStatus().trim().toLowerCase();
            if (status.equals("regular")) regular++;
            if (status.equals("probationary") || status.equals("probitionary")) probationary++;

            double basic = e.getBasicSalary();
            if (basic > 0) {
                sumSalary += basic;
                salaryCount++;
            }
        }

        double avgSalary = (salaryCount == 0) ? 0 : (sumSalary / salaryCount);

        totalEmployeesVal.setText(String.valueOf(total));
        regularVal.setText(String.valueOf(regular));
        probationaryVal.setText(String.valueOf(probationary));
        avgSalaryVal.setText(String.format("%,.2f", avgSalary));

        refreshRecentEmployees(employees);
        refreshSystemStatus();

        lastUpdatedVal.setText("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void refreshRecentEmployees(List<Employee> employees) {
        // sort by numeric employee # descending (handles "EMP-10001" too)
        List<Employee> recent = employees.stream()
                .sorted(Comparator.comparingInt((Employee e) -> parseEmpNo(e.getEmployeeNumber())).reversed())
                .limit(5)
                .collect(Collectors.toList());

        recentTableModel.setRowCount(0);
        for (Employee e : recent) {
            recentTableModel.addRow(new Object[]{
                    e.getEmployeeNumber(),
                    e.getFullName(),
                    e.getPosition(),
                    e.getStatus()
            });
        }
    }

    private int parseEmpNo(String empNo) {
        if (empNo == null) return 0;
        String digits = empNo.replaceAll("\\D+", "");
        if (digits.isEmpty()) return 0;
        try { return Integer.parseInt(digits); } catch (Exception ex) { return 0; }
    }

    private void refreshSystemStatus() {
        employeesCsvStatusVal.setText(fileExists("data/employees.csv") ? "FOUND ✅" : "MISSING ❌");
        attendanceCsvStatusVal.setText(fileExists("data/attendance.csv") ? "FOUND ✅" : "MISSING ❌");
    }

    private boolean fileExists(String path) {
        return new File(path).exists();
    }

    // ---------------- Navigation helper ----------------
    private void navigateTo(String screenName) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).showContent(screenName);
        }
    }
}
