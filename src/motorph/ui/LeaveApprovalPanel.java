package motorph.ui;

import motorph.model.LeaveRequest;
import motorph.model.User;
import motorph.util.LeaveIOUtil;
import motorph.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LeaveApprovalPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private DefaultTableModel model;
    private JTable table;

    public LeaveApprovalPanel() {
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

        JLabel title = new JLabel("Leave Approvals");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Approve or deny employee leave requests");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(subtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> refresh());

        JButton approveBtn = new JButton("Approve");
        approveBtn.setFocusPainted(false);
        approveBtn.addActionListener(e -> updateSelected(LeaveRequest.Status.APPROVED));

        JButton denyBtn = new JButton("Deny");
        denyBtn.setFocusPainted(false);
        denyBtn.addActionListener(e -> updateSelected(LeaveRequest.Status.DENIED));

        right.add(refreshBtn);
        right.add(approveBtn);
        right.add(denyBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent body() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)
        ));

        model = new DefaultTableModel(
                new Object[]{"Request ID", "Employee #", "Name", "From", "To", "Reason", "Status", "Submitted", "Reviewed By"},
                0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));

        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void refresh() {
        model.setRowCount(0);

        User u = Session.getCurrentUser();
        if (u == null || !(u.isAdmin() || u.isHr())) return;

        List<LeaveRequest> all = LeaveIOUtil.loadAll();
        for (LeaveRequest r : all) {
            model.addRow(new Object[]{
                    r.getRequestId(),
                    r.getEmployeeNumber(),
                    r.getEmployeeName(),
                    r.getFromDate(),
                    r.getToDate(),
                    shorten(r.getReason(), 30),
                    r.getStatus(),
                    r.getSubmittedAt(),
                    r.getReviewedBy()
            });
        }
    }

    private void updateSelected(LeaveRequest.Status newStatus) {
        User u = Session.getCurrentUser();
        if (u == null || !(u.isAdmin() || u.isHr())) {
            JOptionPane.showMessageDialog(this, "Only ADMIN/HR can approve/deny leaves.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a leave request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String requestId = String.valueOf(model.getValueAt(row, 0));

        List<LeaveRequest> all = LeaveIOUtil.loadAll();
        LeaveRequest target = null;
        for (LeaveRequest r : all) {
            if (requestId.equals(r.getRequestId())) {
                target = r;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "Request not found. Refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (target.getStatus() != LeaveRequest.Status.PENDING) {
            JOptionPane.showMessageDialog(this, "This request is already " + target.getStatus() + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Set status to " + newStatus + " for " + target.getEmployeeName() + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        target.setStatus(newStatus);
        target.setReviewedBy(u.getUsername());
        target.setReviewedAt(LeaveIOUtil.now());

        LeaveIOUtil.overwriteAll(all);
        refresh();
    }

    private String shorten(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...";
    }
}
