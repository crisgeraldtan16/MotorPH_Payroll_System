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

    // Theme (matches your modern dark sidebar)
    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private JLabel lastUpdatedVal;

    // Stat values
    private JLabel totalVal, regularVal, probationaryVal, avgSalaryVal;

    // Status values
    private JLabel employeesCsvVal, attendanceCsvVal;

    // Recent table
    private DefaultTableModel recentModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refreshDashboard();
    }

    // ---------------- Header ----------------
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Overview of employees, payroll readiness, and file status");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refreshDashboard());

        lastUpdatedVal = new JLabel("Last updated: -");
        lastUpdatedVal.setFont(new Font("Arial", Font.PLAIN, 12));
        lastUpdatedVal.setForeground(MUTED);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        refreshBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lastUpdatedVal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        right.add(refreshBtn);
        right.add(Box.createVerticalStrut(6));
        right.add(lastUpdatedVal);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ---------------- Body ----------------
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        // Top: cards row
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);

        totalVal = bigValueLabel();
        regularVal = bigValueLabel();
        probationaryVal = bigValueLabel();
        avgSalaryVal = bigValueLabel();

        cards.add(statCard("Total Employees", totalVal, "All records in employees.csv"));
        cards.add(statCard("Regular", regularVal, "Status = Regular"));
        cards.add(statCard("Probationary", probationaryVal, "Status = Probationary"));
        cards.add(statCard("Avg Basic Salary", avgSalaryVal, "Average monthly basic pay"));

        body.add(cards, BorderLayout.NORTH);

        // Bottom: split layout (Recent + Right column)
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                recentEmployeesCard(),
                rightColumn()
        );
        split.setResizeWeight(0.65);
        split.setDividerLocation(700);
        split.setBorder(null);

        body.add(split, BorderLayout.CENTER);

        return body;
    }

    // ---------------- Components ----------------
    private JLabel bigValueLabel() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.BOLD, 28));
        l.setForeground(TEXT);
        return l;
    }

    private JComponent statCard(String title, JLabel value, String hint) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 13));
        t.setForeground(MUTED);

        JLabel h = new JLabel(hint);
        h.setFont(new Font("Arial", Font.PLAIN, 11));
        h.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(t);
        top.add(Box.createVerticalStrut(4));
        top.add(h);

        card.add(top, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private JComponent recentEmployeesCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Recent Employees");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Last 5 employees by Employee #");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(subtitle);

        recentModel = new DefaultTableModel(new Object[]{"Employee #", "Name", "Position", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(recentModel);
        table.setRowHeight(24);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        card.add(top, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JComponent rightColumn() {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        col.add(systemStatusCard());
        col.add(Box.createVerticalStrut(12));
        col.add(quickActionsCard());
        col.add(Box.createVerticalGlue());

        return col;
    }

    private JComponent systemStatusCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("System Status");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        employeesCsvVal = new JLabel("-");
        attendanceCsvVal = new JLabel("-");
        employeesCsvVal.setForeground(TEXT);
        attendanceCsvVal.setForeground(TEXT);

        addStatusRow(grid, gbc, 0, "employees.csv", employeesCsvVal);
        addStatusRow(grid, gbc, 1, "attendance.csv", attendanceCsvVal);

        JLabel note = new JLabel("<html><span style='font-size:10px; color:#6E788C;'>Missing files will result in 0 computed values.</span></html>");

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        card.add(note, BorderLayout.SOUTH);

        return card;
    }

    private void addStatusRow(JPanel grid, GridBagConstraints gbc, int row, String label, JLabel value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        JLabel l = new JLabel(label + ":");
        l.setForeground(MUTED);
        grid.add(l, gbc);

        gbc.gridx = 1; gbc.weightx = 0.5;
        grid.add(value, gbc);
    }

    private JComponent quickActionsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Open key modules quickly");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(subtitle);

        JButton empBtn = new JButton("Open Employees");
        JButton payBtn = new JButton("Open Payroll");

        empBtn.setFocusPainted(false);
        payBtn.setFocusPainted(false);

        empBtn.addActionListener(e -> navigateTo("EMPLOYEE"));
        payBtn.addActionListener(e -> navigateTo("PAYROLL"));

        JPanel buttons = new JPanel(new GridLayout(2, 1, 8, 8));
        buttons.setOpaque(false);
        buttons.add(empBtn);
        buttons.add(payBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(buttons, BorderLayout.CENTER);

        return card;
    }

    // ---------------- Refresh ----------------
    private void refreshDashboard() {
        List<Employee> employees = CSVUtil.loadEmployees();

        int total = employees.size();
        int regular = 0;
        int probationary = 0;

        double sumBasic = 0;
        int countBasic = 0;

        for (Employee e : employees) {
            String status = (e.getStatus() == null) ? "" : e.getStatus().trim().toLowerCase();
            if (status.equals("regular")) regular++;
            if (status.equals("probationary") || status.equals("probitionary")) probationary++;

            double basic = e.getBasicSalary();
            if (basic > 0) {
                sumBasic += basic;
                countBasic++;
            }
        }

        double avgBasic = (countBasic == 0) ? 0 : (sumBasic / countBasic);

        totalVal.setText(String.valueOf(total));
        regularVal.setText(String.valueOf(regular));
        probationaryVal.setText(String.valueOf(probationary));
        avgSalaryVal.setText(String.format("%,.2f", avgBasic));

        refreshRecentEmployees(employees);
        refreshSystemStatus();

        lastUpdatedVal.setText("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private void refreshRecentEmployees(List<Employee> employees) {
        List<Employee> recent = employees.stream()
                .sorted(Comparator.comparingInt((Employee e) -> parseEmpNo(e.getEmployeeNumber())).reversed())
                .limit(5)
                .collect(Collectors.toList());

        recentModel.setRowCount(0);
        for (Employee e : recent) {
            recentModel.addRow(new Object[]{
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
        employeesCsvVal.setText(fileExists("data/employees.csv") ? "FOUND ✅" : "MISSING ❌");
        attendanceCsvVal.setText(fileExists("data/attendance.csv") ? "FOUND ✅" : "MISSING ❌");
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
