package motorph.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome to MotorPH Payroll System", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        add(label, BorderLayout.CENTER);
    }
}
