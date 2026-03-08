package motorph.ui;

import motorph.model.User;
import motorph.util.Session;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    /*
     * This CardLayout controls the top-level screens of the application,
     * such as the login screen and the main app screen.
     */
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel root = new JPanel(rootLayout);

    private JPanel appShell;
    private JPanel contentCards;
    private CardLayout contentLayout;

    private DashboardPanel dashboardPanel;
    private EmployeePanel employeePanel;
    private PayrollPanel payrollPanel;
    private LeaveApprovalPanel leaveApprovalPanel;
    private UserAccountsPanel userAccountsPanel;

    private EmployeeDashboardPanel employeeDashboardPanel;
    private MyPayslipPanel myPayslipPanel;
    private LeaveRequestPanel leaveRequestPanel;

    public MainFrame() {
        setTitle("MotorPH Employee Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        root.add(new LoginPanel(this), "LOGIN");
        root.add(buildAppShell(), "APP");

        setContentPane(root);
        rootLayout.show(root, "LOGIN");
    }

    /*
     * This builds the main application shell and registers
     * all screens that can be shown inside the system.
     */
    private JPanel buildAppShell() {
        appShell = new JPanel(new BorderLayout());
        appShell.setBackground(new Color(245, 247, 252));

        contentLayout = new CardLayout();
        contentCards = new JPanel(contentLayout);
        contentCards.setBackground(new Color(245, 247, 252));

        dashboardPanel = new DashboardPanel();
        employeePanel = new EmployeePanel(this);
        payrollPanel = new PayrollPanel(this);
        leaveApprovalPanel = new LeaveApprovalPanel();
        userAccountsPanel = new UserAccountsPanel();

        employeeDashboardPanel = new EmployeeDashboardPanel();
        myPayslipPanel = new MyPayslipPanel();
        leaveRequestPanel = new LeaveRequestPanel();

        contentCards.add(dashboardPanel, "DASHBOARD");
        contentCards.add(employeePanel, "EMPLOYEE");
        contentCards.add(payrollPanel, "PAYROLL");
        contentCards.add(leaveApprovalPanel, "LEAVE_APPROVAL");
        contentCards.add(userAccountsPanel, "USER_ACCOUNTS");

        contentCards.add(employeeDashboardPanel, "EMPLOYEE_DASHBOARD");
        contentCards.add(myPayslipPanel, "MY_PAYSLIP");
        contentCards.add(leaveRequestPanel, "LEAVE_REQUEST");

        appShell.add(contentCards, BorderLayout.CENTER);
        return appShell;
    }

    /*
     * This method opens the main app after successful login.
     * It also decides the first screen based on the user's role.
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

    private void buildMainAppUI() {
        Component oldWest = ((BorderLayout) appShell.getLayout())
                .getLayoutComponent(BorderLayout.WEST);

        if (oldWest != null) {
            appShell.remove(oldWest);
        }

        appShell.add(new SideMenuPanel(this), BorderLayout.WEST);
        appShell.revalidate();
        appShell.repaint();
    }

    /*
     * This handles screen navigation and also checks
     * if the current user is allowed to open the selected screen.
     */
    public void showContent(String screen) {
        User u = Session.getCurrentUser();

        if (u == null) {
            rootLayout.show(root, "LOGIN");
            return;
        }

        if (!u.getAccessPolicy().canOpenScreen(screen)) {
            screen = u.isEmployee() ? "EMPLOYEE_DASHBOARD" : "DASHBOARD";
        }

        /*
         * These refresh calls make sure each screen
         * shows updated data before it is displayed.
         */
        if ("DASHBOARD".equals(screen) && dashboardPanel != null) {
            dashboardPanel.refreshData();
        }

        if ("EMPLOYEE_DASHBOARD".equals(screen) && employeeDashboardPanel != null) {
            employeeDashboardPanel.refreshData();
        }

        if ("MY_PAYSLIP".equals(screen) && myPayslipPanel != null) {
            myPayslipPanel.refreshData();
        }

        if ("LEAVE_REQUEST".equals(screen) && leaveRequestPanel != null) {
            leaveRequestPanel.refreshData();
        }

        if ("LEAVE_APPROVAL".equals(screen) && leaveApprovalPanel != null) {
            leaveApprovalPanel.refreshData();
        }

        if ("USER_ACCOUNTS".equals(screen) && userAccountsPanel != null) {
            userAccountsPanel.refreshData();
        }

        contentLayout.show(contentCards, screen);
    }

    /*
     * This clears the current session and returns the user
     * back to the login screen.
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