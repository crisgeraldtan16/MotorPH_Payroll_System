package motorph.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout rootLayout;
    private JPanel rootPanel;

    private MainContentPanel contentPanel;

    public MainFrame() {
        setTitle("MotorPH Employee Payroll System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootLayout = new CardLayout();
        rootPanel = new JPanel(rootLayout);

        // Login screen
        rootPanel.add(new LoginPanel(this), "LOGIN");

        // Main application screen
        rootPanel.add(createMainAppPanel(), "MAIN");

        setContentPane(rootPanel);
        showLogin();
    }

    private JPanel createMainAppPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        contentPanel = new MainContentPanel(this);
        SideMenuPanel menuPanel = new SideMenuPanel(this);

        panel.add(menuPanel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    // Navigation methods
    public void showLogin() {
        rootLayout.show(rootPanel, "LOGIN");
    }

    public void showMainApp() {
        rootLayout.show(rootPanel, "MAIN");
        showContent("DASHBOARD");
    }

    public void showContent(String name) {
        contentPanel.showPanel(name);
    }

    public void logout() {
        showLogin();
    }
}
