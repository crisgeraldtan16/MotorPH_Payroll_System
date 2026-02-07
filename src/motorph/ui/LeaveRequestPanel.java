package motorph.ui;

import motorph.model.Employee;
import motorph.model.LeaveRequest;
import motorph.model.User;
import motorph.util.CSVUtil;
import motorph.util.LeaveIOUtil;
import motorph.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LeaveRequestPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ✅ Date dropdowns (From)
    private JComboBox<Integer> fromYear;
    private JComboBox<Integer> fromMonth;
    private JComboBox<Integer> fromDay;

    // ✅ Date dropdowns (To)
    private JComboBox<Integer> toYear;
    private JComboBox<Integer> toMonth;
    private JComboBox<Integer> toDay;

    private JTextArea reasonArea;
    private DefaultTableModel model;

    public LeaveRequestPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(header(), BorderLayout.NORTH);
        add(body(), BorderLayout.CENTER);

        refresh();
    }

    private JComponent header() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("Leave Request");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("File a leave request and track its approval status");
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
        refreshBtn.addActionListener(e -> refresh());

        header.add(left, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        return header;
    }

    private JComponent body() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formCard(), tableCard());
        split.setResizeWeight(0.40);
        split.setDividerLocation(280);
        split.setBorder(null);
        return split;
    }

    private JComponent formCard() {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("File New Leave");
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ✅ Build date dropdowns
        buildDatePickers();

        reasonArea = new JTextArea(4, 24);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        // FROM row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        form.add(label("From Date *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(dateRow(fromYear, fromMonth, fromDay), gbc);

        // TO row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        form.add(label("To Date *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(dateRow(toYear, toMonth, toDay), gbc);

        // Reason row
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        form.add(label("Reason *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        JScrollPane sp = new JScrollPane(reasonArea);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        form.add(sp, gbc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton submitBtn = new JButton("Submit Request");
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> submit());

        bottom.add(submitBtn);

        card.add(t, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void buildDatePickers() {
        // Range: 2020..2030 (you can adjust)
        Integer[] years = new Integer[11];
        for (int i = 0; i < years.length; i++) years[i] = 2020 + i;

        Integer[] months = new Integer[12];
        for (int i = 0; i < months.length; i++) months[i] = i + 1;

        fromYear = new JComboBox<>(years);
        fromMonth = new JComboBox<>(months);
        fromDay = new JComboBox<>();

        toYear = new JComboBox<>(years);
        toMonth = new JComboBox<>(months);
        toDay = new JComboBox<>();

        // Default: today
        LocalDate today = LocalDate.now();
        setPickerDate(fromYear, fromMonth, fromDay, today);
        setPickerDate(toYear, toMonth, toDay, today);

        // Update day list whenever year/month changes
        fromYear.addActionListener(e -> refreshDays(fromYear, fromMonth, fromDay));
        fromMonth.addActionListener(e -> refreshDays(fromYear, fromMonth, fromDay));
        toYear.addActionListener(e -> refreshDays(toYear, toMonth, toDay));
        toMonth.addActionListener(e -> refreshDays(toYear, toMonth, toDay));

        refreshDays(fromYear, fromMonth, fromDay);
        refreshDays(toYear, toMonth, toDay);
    }

    private JPanel dateRow(JComboBox<Integer> y, JComboBox<Integer> m, JComboBox<Integer> d) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);

        p.add(new JLabel("Year"));
        p.add(y);
        p.add(new JLabel("Month"));
        p.add(m);
        p.add(new JLabel("Day"));
        p.add(d);

        return p;
    }

    private void setPickerDate(JComboBox<Integer> y, JComboBox<Integer> m, JComboBox<Integer> d, LocalDate date) {
        y.setSelectedItem(date.getYear());
        m.setSelectedItem(date.getMonthValue());
        refreshDays(y, m, d);
        d.setSelectedItem(date.getDayOfMonth());
    }

    private void refreshDays(JComboBox<Integer> y, JComboBox<Integer> m, JComboBox<Integer> d) {
        int year = (int) y.getSelectedItem();
        int month = (int) m.getSelectedItem();

        int currentDay = (d.getSelectedItem() == null) ? 1 : (int) d.getSelectedItem();

        int maxDay = YearMonth.of(year, month).lengthOfMonth();
        d.removeAllItems();
        for (int day = 1; day <= maxDay; day++) d.addItem(day);

        if (currentDay <= maxDay) d.setSelectedItem(currentDay);
        else d.setSelectedItem(maxDay);
    }

    private String getPickerDate(JComboBox<Integer> y, JComboBox<Integer> m, JComboBox<Integer> d) {
        int year = (int) y.getSelectedItem();
        int month = (int) m.getSelectedItem();
        int day = (int) d.getSelectedItem();
        return LocalDate.of(year, month, day).format(DF);
    }

    private JComponent tableCard() {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("My Leave Requests");
        t.setFont(new Font("Arial", Font.BOLD, 14));
        t.setForeground(TEXT);

        model = new DefaultTableModel(
                new Object[]{"Request ID", "From", "To", "Reason", "Status", "Submitted"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        card.add(t, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void submit() {
        User u = Session.getCurrentUser();
        if (u == null || !u.isEmployee()) {
            JOptionPane.showMessageDialog(this, "Only EMPLOYEE users can file leave.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String empNo = u.getEmployeeNumber();
        if (empNo == null || empNo.isBlank()) {
            JOptionPane.showMessageDialog(this, "Your user account has no Employee # linked.", "Setup Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String from = getPickerDate(fromYear, fromMonth, fromDay);
        String to = getPickerDate(toYear, toMonth, toDay);
        String reason = reasonArea.getText().trim();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reason.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate f = LocalDate.parse(from, DF);
        LocalDate t = LocalDate.parse(to, DF);
        if (t.isBefore(f)) {
            JOptionPane.showMessageDialog(this, "To date must be the same or after From date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = findEmployee(empNo);
        String empName = (emp != null) ? emp.getFullName() : ("Employee " + empNo);

        LeaveRequest r = new LeaveRequest();
        r.setRequestId(LeaveIOUtil.newRequestId());
        r.setEmployeeNumber(empNo);
        r.setEmployeeName(empName);
        r.setFromDate(from);
        r.setToDate(to);
        r.setReason(reason);
        r.setStatus(LeaveRequest.Status.PENDING);
        r.setSubmittedAt(LeaveIOUtil.now());
        r.setReviewedBy("");
        r.setReviewedAt("");

        try {
            LeaveIOUtil.append(r);
            JOptionPane.showMessageDialog(this, "Leave request submitted.", "Submitted", JOptionPane.INFORMATION_MESSAGE);

            // Reset: keep dates today, clear reason
            reasonArea.setText("");
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit leave:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        model.setRowCount(0);

        User u = Session.getCurrentUser();
        if (u == null || !u.isEmployee()) return;

        List<LeaveRequest> list = LeaveIOUtil.loadForEmployee(u.getEmployeeNumber());
        for (LeaveRequest r : list) {
            model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getFromDate(),
                    r.getToDate(),
                    shorten(r.getReason(), 30),
                    r.getStatus(),
                    r.getSubmittedAt()
            });
        }
    }

    private Employee findEmployee(String empNo) {
        List<Employee> employees = CSVUtil.loadEmployees();
        for (Employee e : employees) {
            if (empNo.equals(e.getEmployeeNumber())) return e;
        }
        return null;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(MUTED);
        return l;
    }

    private JPanel cardWrap() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return card;
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
