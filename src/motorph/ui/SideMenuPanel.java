package motorph.ui;

import javax.swing.*;
import java.awt.*;

public class SideMenuPanel extends JPanel {

    public SideMenuPanel(MainFrame mainFrame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        // ---- Title wrapper (centered like buttons) ----
        JPanel titleWrapper = new JPanel();
        titleWrapper.setLayout(new BoxLayout(titleWrapper, BoxLayout.X_AXIS));
        titleWrapper.setMaximumSize(new Dimension(170, 50));
        titleWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleWrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("<html><center>MotorPH<br/>Payroll System</center></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titleWrapper.add(Box.createHorizontalGlue());
        titleWrapper.add(titleLabel);
        titleWrapper.add(Box.createHorizontalGlue());

        add(titleWrapper);
        add(Box.createVerticalStrut(20));

        // ---- Buttons ----
        JButton dashboardBtn = new JButton("Dashboard");
        JButton employeeBtn = new JButton("Employees");
        JButton payrollBtn = new JButton("Payroll");
        JButton logoutBtn = new JButton("Logout");

        Dimension buttonSize = new Dimension(170, 40);
        dashboardBtn.setMaximumSize(buttonSize);
        employeeBtn.setMaximumSize(buttonSize);
        payrollBtn.setMaximumSize(buttonSize);
        logoutBtn.setMaximumSize(buttonSize);

        dashboardBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        employeeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        payrollBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        dashboardBtn.addActionListener(e -> mainFrame.showContent("DASHBOARD"));
        employeeBtn.addActionListener(e -> mainFrame.showContent("EMPLOYEE"));
        payrollBtn.addActionListener(e -> mainFrame.showContent("PAYROLL"));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.logout();
            }
        });

        add(dashboardBtn);
        add(Box.createVerticalStrut(10));
        add(employeeBtn);
        add(Box.createVerticalStrut(10));
        add(payrollBtn);

        add(Box.createVerticalGlue()); // pushes logout button to the bottom
        add(logoutBtn);
    }
}
