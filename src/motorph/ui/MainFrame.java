package motorph.ui;

import motorph.model.User;
import motorph.util.Session;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel root = new JPanel(rootLayout);

    private JPanel appShell;
    private JPanel contentCards;
    private CardLayout contentLayout;

    public MainFrame() {
        setTitle("MotorPH Employee Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // start maximized
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        root.add(new LoginPanel(this), "LOGIN");
        root.add(buildAppShell(), "APP");

        setContentPane(root);
        rootLayout.show(root, "LOGIN");
    }

    private JPanel buildAppShell() {
        appShell = new JPanel(new BorderLayout());
        appShell.setBackground(new Color(245, 247, 252));

        contentLayout = new CardLayout();
        contentCards = new JPanel(contentLayout);
        contentCards.setBackground(new Color(245, 247, 252));

        // Admin/HR screens
        contentCards.add(new DashboardPanel(), "DASHBOARD");
        contentCards.add(new EmployeePanel(this), "EMPLOYEE");
        contentCards.add(new PayrollPanel(this), "PAYROLL");
        contentCards.add(new LeaveApprovalPanel(), "LEAVE_APPROVAL");

        // Employee portal screens
        contentCards.add(new EmployeeDashboardPanel(), "EMPLOYEE_DASHBOARD");
        contentCards.add(new MyPayslipPanel(), "MY_PAYSLIP");
        contentCards.add(new LeaveRequestPanel(), "LEAVE_REQUEST");

        appShell.add(contentCards, BorderLayout.CENTER);
        return appShell;
    }

    /**
     * Rebuild sidebar + show APP shell after login.
     * Employee will land on EMPLOYEE_DASHBOARD, Admin/HR will land on DASHBOARD.
     */
    public void showMainApp() {
        buildMainAppUI();
        rootLayout.show(root, "APP");

        User u = Session.getCurrentUser();
        if (u != null && u.isEmployee()) {
            showContent("EMPLOYEE_DASHBOARD");
        } else {
            showContent("DASHBOARD");
        }
    }

    /**
     * Rebuilds the left sidebar depending on role (Employee vs Admin/HR).
     * This ensures the menu is always correct after login/logout.
     */
    private void buildMainAppUI() {
        // remove old sidebar if any
        Component oldWest = ((BorderLayout) appShell.getLayout()).getLayoutComponent(BorderLayout.WEST);
        if (oldWest != null) {
            appShell.remove(oldWest);
        }

        // add new role-based sidebar
        appShell.add(new SideMenuPanel(this), BorderLayout.WEST);

        // refresh UI
        appShell.revalidate();
        appShell.repaint();
    }

    /**
     * Switches the right-side screen (content area).
     * ✅ Polymorphism-based access control:
     * Role rules are inside AccessPolicy classes (u.getAccessPolicy()).
     */
    public void showContent(String screen) {
        User u = Session.getCurrentUser();

        if (u == null) {
            rootLayout.show(root, "LOGIN");
            return;
        }

        // ✅ Polymorphism: role rules are inside policy classes
        if (!u.getAccessPolicy().canOpenScreen(screen)) {
            // safe fallback per role
            screen = u.isEmployee() ? "EMPLOYEE_DASHBOARD" : "DASHBOARD";
        }

        contentLayout.show(contentCards, screen);
    }

    /**
     * Logout resets session and returns to login screen.
     */
    public void logout() {
        Session.clear();
        rootLayout.show(root, "LOGIN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame f = new MainFrame();
            f.setVisible(true);
        });
    }
}