package motorph.ui;

import motorph.model.User;
import motorph.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SideMenuPanel extends JPanel {

    // 🌤 LIGHTER MODERN THEME
    private static final Color BG = new Color(235, 238, 245);        // light gray-blue
    private static final Color BTN_BG = new Color(255, 255, 255);   // white
    private static final Color BTN_HOVER = new Color(225, 230, 240);
    private static final Color BTN_ACTIVE = new Color(65, 105, 225); // blue accent

    private static final Color TEXT = new Color(35, 45, 65);
    private static final Color TEXT_MUTED = new Color(110, 120, 145);

    // Admin/HR buttons
    private JButton dashboardBtn;
    private JButton employeeBtn;
    private JButton payrollBtn;
    private JButton leaveApprovalsBtn;

    // Employee buttons
    private JButton empDashboardBtn;
    private JButton myPayslipBtn;
    private JButton requestLeaveBtn;

    private JButton logoutBtn;

    public SideMenuPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(240, 0));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 16, 18, 16));

        add(buildBrandHeader(), BorderLayout.NORTH);
        add(buildMenu(mainFrame), BorderLayout.CENTER);
        add(buildLogout(mainFrame), BorderLayout.SOUTH);

        // Default highlight depends on role
        User u = Session.getCurrentUser();
        if (u != null && u.isEmployee()) {
            setActive(empDashboardBtn);
            mainFrame.showContent("EMPLOYEE_DASHBOARD");
        } else {
            setActive(dashboardBtn);
            mainFrame.showContent("DASHBOARD");
        }
    }

    // ---------- Header ----------
    private JComponent buildBrandHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(6, 6, 20, 6));

        JPanel logo = new JPanel();
        logo.setPreferredSize(new Dimension(42, 42));
        logo.setBackground(BTN_ACTIVE);

        JLabel logoText = new JLabel("M");
        logoText.setFont(new Font("Arial", Font.BOLD, 18));
        logoText.setForeground(Color.WHITE);
        logo.add(logoText);

        JLabel title = new JLabel("MotorPH");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Payroll System");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        // show role
        User u = Session.getCurrentUser();
        String roleTxt = (u == null) ? "-" : u.getRole().name();
        JLabel roleLabel = new JLabel("Role: " + roleTxt);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        roleLabel.setForeground(TEXT_MUTED);

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
        textBox.add(title);
        textBox.add(subtitle);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(roleLabel);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(logo);
        left.add(Box.createHorizontalStrut(10));
        left.add(textBox);

        header.add(left, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 205, 215));
        header.add(sep, BorderLayout.SOUTH);

        return header;
    }

    // ---------- Menu ----------
    private JComponent buildMenu(MainFrame mainFrame) {
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        User u = Session.getCurrentUser();
        boolean isEmployee = (u != null && u.isEmployee());
        boolean isAdminOrHr = (u != null && (u.isAdmin() || u.isHr()));

        if (isEmployee) {
            // ✅ EMPLOYEE menu (includes Employee Dashboard)
            empDashboardBtn = createMenuButton("Employee Dashboard");
            myPayslipBtn = createMenuButton("My Payslip");
            requestLeaveBtn = createMenuButton("Request Leave");

            empDashboardBtn.addActionListener(e -> {
                setActive(empDashboardBtn);
                mainFrame.showContent("EMPLOYEE_DASHBOARD");
            });

            myPayslipBtn.addActionListener(e -> {
                setActive(myPayslipBtn);
                mainFrame.showContent("MY_PAYSLIP");
            });

            requestLeaveBtn.addActionListener(e -> {
                setActive(requestLeaveBtn);
                mainFrame.showContent("LEAVE_REQUEST");
            });

            menu.add(empDashboardBtn);
            menu.add(Box.createVerticalStrut(10));
            menu.add(myPayslipBtn);
            menu.add(Box.createVerticalStrut(10));
            menu.add(requestLeaveBtn);

        } else {
            // ADMIN/HR menu
            dashboardBtn = createMenuButton("Dashboard");
            employeeBtn = createMenuButton("Employees");
            payrollBtn = createMenuButton("Payroll");

            dashboardBtn.addActionListener(e -> {
                setActive(dashboardBtn);
                mainFrame.showContent("DASHBOARD");
            });

            employeeBtn.addActionListener(e -> {
                setActive(employeeBtn);
                mainFrame.showContent("EMPLOYEE");
            });

            payrollBtn.addActionListener(e -> {
                setActive(payrollBtn);
                mainFrame.showContent("PAYROLL");
            });

            menu.add(dashboardBtn);
            menu.add(Box.createVerticalStrut(10));
            menu.add(employeeBtn);
            menu.add(Box.createVerticalStrut(10));
            menu.add(payrollBtn);

            // HR/Admin can approve leave
            if (isAdminOrHr) {
                menu.add(Box.createVerticalStrut(10));
                leaveApprovalsBtn = createMenuButton("Leave Approvals");

                leaveApprovalsBtn.addActionListener(e -> {
                    setActive(leaveApprovalsBtn);
                    mainFrame.showContent("LEAVE_APPROVAL");
                });

                menu.add(leaveApprovalsBtn);
            }
        }

        return menu;
    }

    // ---------- Logout ----------
    private JComponent buildLogout(MainFrame mainFrame) {
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        logoutBtn = createLogoutButton("Logout");

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Session.clear();
                mainFrame.logout();
            }
        });

        bottom.add(Box.createVerticalGlue());
        bottom.add(logoutBtn);
        return bottom;
    }

    // ---------- Buttons ----------
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.setBackground(BTN_BG);
        btn.setForeground(TEXT);
        btn.setFont(new Font("Arial", Font.BOLD, 13));

        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        addHoverEffect(btn, BTN_BG, BTN_HOVER);
        return btn;
    }

    private JButton createLogoutButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        Color bg = new Color(245, 225, 225);
        Color hover = new Color(235, 205, 205);

        btn.setBackground(bg);
        btn.setForeground(new Color(120, 40, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        addHoverEffect(btn, bg, hover);
        return btn;
    }

    private void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive(btn)) btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive(btn)) btn.setBackground(normal);
            }
        });
    }

    // ---------- Active State ----------
    private void setActive(JButton activeBtn) {
        JButton[] all = {
                // Admin/HR
                dashboardBtn, employeeBtn, payrollBtn, leaveApprovalsBtn,
                // Employee
                empDashboardBtn, myPayslipBtn, requestLeaveBtn
        };

        for (JButton b : all) {
            if (b == null) continue;
            b.putClientProperty("active", false);
            b.setBackground(BTN_BG);
            b.setForeground(TEXT);
        }

        if (activeBtn != null) {
            activeBtn.putClientProperty("active", true);
            activeBtn.setBackground(BTN_ACTIVE);
            activeBtn.setForeground(Color.WHITE);
        }
    }

    private boolean isActive(JButton btn) {
        if (btn == null) return false;
        Object v = btn.getClientProperty("active");
        return v instanceof Boolean && (Boolean) v;
    }
}
