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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LeaveRequestPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JTextField fromField;
    private JTextField toField;
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
        split.setDividerLocation(260);
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

        fromField = new JTextField(16);
        toField = new JTextField(16);
        reasonArea = new JTextArea(4, 24);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.25;
        form.add(label("From (yyyy-MM-dd) *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(fromField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        form.add(label("To (yyyy-MM-dd) *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(toField, gbc);

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

        String from = fromField.getText().trim();
        String to = toField.getText().trim();
        String reason = reasonArea.getText().trim();

        if (from.isEmpty() || to.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidDate(from) || !isValidDate(to)) {
            JOptionPane.showMessageDialog(this, "Date format must be yyyy-MM-dd (example: 2024-06-01).", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            fromField.setText("");
            toField.setText("");
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

    private boolean isValidDate(String s) {
        try { LocalDate.parse(s, DF); return true; }
        catch (Exception e) { return false; }
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
