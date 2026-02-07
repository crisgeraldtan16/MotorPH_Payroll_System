package motorph.ui;

import motorph.model.Employee;
import motorph.model.LeaveRequest;
import motorph.model.PayrollRecord;
import motorph.model.User;
import motorph.util.CSVUtil;
import motorph.util.LeaveIOUtil;
import motorph.util.PayrollIOUtil;
import motorph.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeDashboardPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private JLabel lastUpdatedVal;

    // Profile
    private JLabel empNoVal, nameVal, positionVal, statusVal;

    // Leave summary
    private JLabel pendingVal, approvedVal, deniedVal;

    // Payroll summary
    private JLabel payrollMonthVal, grossVal, taxVal, govVal, netVal;

    public EmployeeDashboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refresh();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("Employee Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Your profile, payslip info, and leave summary");
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

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        // top cards
        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 12));
        cards.setOpaque(false);

        // Profile card values
        empNoVal = value();
        nameVal = value();
        positionVal = value();
        statusVal = value();

        // Leave card values
        pendingVal = bigValue();
        approvedVal = bigValue();
        deniedVal = bigValue();

        // Payroll card values
        payrollMonthVal = value();
        grossVal = value();
        govVal = value();
        taxVal = value();
        netVal = new JLabel("-");
        netVal.setFont(new Font("Arial", Font.BOLD, 18));
        netVal.setForeground(TEXT);

        cards.add(profileCard());
        cards.add(leaveCard());
        cards.add(payrollCard());

        body.add(cards, BorderLayout.NORTH);

        // Quick actions row
        JPanel quick = new JPanel(new GridLayout(1, 2, 12, 12));
        quick.setOpaque(false);

        JButton openPayslipBtn = new JButton("Open My Payslip");
        JButton openLeaveBtn = new JButton("Open Leave Request");
        openPayslipBtn.setFocusPainted(false);
        openLeaveBtn.setFocusPainted(false);

        openPayslipBtn.addActionListener(e -> navigateTo("EMPLOYEE_PAYSLIP"));
        openLeaveBtn.addActionListener(e -> navigateTo("LEAVE_REQUEST"));

        quick.add(actionCard("Payslip", "View your latest payroll record and payslip details.", openPayslipBtn));
        quick.add(actionCard("Leave", "File a leave request and check approval status.", openLeaveBtn));

        body.add(quick, BorderLayout.CENTER);

        return body;
    }

    private JLabel value() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        l.setForeground(TEXT);
        return l;
    }

    private JLabel bigValue() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Arial", Font.BOLD, 26));
        l.setForeground(TEXT);
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

    private JComponent profileCard() {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setOpaque(false);

        grid.add(label("Employee #")); grid.add(empNoVal);
        grid.add(label("Name")); grid.add(nameVal);
        grid.add(label("Position")); grid.add(positionVal);
        grid.add(label("Status")); grid.add(statusVal);

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JComponent leaveCard() {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Leave Summary");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JPanel grid = new JPanel(new GridLayout(3, 2, 8, 10));
        grid.setOpaque(false);

        grid.add(label("Pending")); grid.add(pendingVal);
        grid.add(label("Approved")); grid.add(approvedVal);
        grid.add(label("Denied")); grid.add(deniedVal);

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JComponent payrollCard() {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Latest Payroll");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JPanel grid = new JPanel(new GridLayout(5, 2, 8, 8));
        grid.setOpaque(false);

        grid.add(label("Month")); grid.add(payrollMonthVal);
        grid.add(label("Gross")); grid.add(grossVal);
        grid.add(label("Gov Deductions")); grid.add(govVal);
        grid.add(label("Tax")); grid.add(taxVal);
        grid.add(label("Net Pay")); grid.add(netVal);

        card.add(title, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JComponent actionCard(String titleText, String subtitleText, JButton actionBtn) {
        JPanel card = cardWrap();
        card.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("<html><span style='color:#6E788C;'>" + subtitleText + "</span></html>");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(4));
        top.add(subtitle);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(actionBtn);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t + ":");
        l.setForeground(MUTED);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    private void refresh() {
        User u = Session.getCurrentUser();
        if (u == null || !u.isEmployee()) {
            empNoVal.setText("-");
            nameVal.setText("-");
            positionVal.setText("-");
            statusVal.setText("-");
            pendingVal.setText("-");
            approvedVal.setText("-");
            deniedVal.setText("-");
            payrollMonthVal.setText("-");
            grossVal.setText("-");
            govVal.setText("-");
            taxVal.setText("-");
            netVal.setText("-");
            return;
        }

        String empNo = u.getEmployeeNumber();
        Employee emp = CSVUtil.findEmployeeByNumber(empNo);

        if (emp != null) {
            empNoVal.setText(emp.getEmployeeNumber());
            nameVal.setText(emp.getFullName());
            positionVal.setText(emp.getPosition());
            statusVal.setText(emp.getStatus());
        } else {
            empNoVal.setText(empNo);
            nameVal.setText(u.getUsername());
            positionVal.setText("-");
            statusVal.setText("-");
        }

        // Leave summary
        int pending = 0, approved = 0, denied = 0;
        List<LeaveRequest> leaves = LeaveIOUtil.loadForEmployee(empNo);
        for (LeaveRequest r : leaves) {
            if (r.getStatus() == LeaveRequest.Status.PENDING) pending++;
            if (r.getStatus() == LeaveRequest.Status.APPROVED) approved++;
            if (r.getStatus() == LeaveRequest.Status.DENIED) denied++;
        }
        pendingVal.setText(String.valueOf(pending));
        approvedVal.setText(String.valueOf(approved));
        deniedVal.setText(String.valueOf(denied));

        // Latest payroll
        PayrollRecord latest = PayrollIOUtil.findLatestForEmployee(empNo);
        if (latest == null) {
            payrollMonthVal.setText("No records");
            grossVal.setText("-");
            govVal.setText("-");
            taxVal.setText("-");
            netVal.setText("-");
        } else {
            payrollMonthVal.setText(String.valueOf(latest.getMonth()));
            grossVal.setText(money(latest.getGrossPay()));
            govVal.setText(money(latest.getTotalDeductionsBeforeTax()));
            taxVal.setText(money(latest.getWithholdingTax()));
            netVal.setText(money(latest.getNetPay()));
        }

        lastUpdatedVal.setText("Last updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private String money(double v) {
        return String.format("%,.2f", v);
    }

    private void navigateTo(String screenName) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).showContent(screenName);
        }
    }
}
