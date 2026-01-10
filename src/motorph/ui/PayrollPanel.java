package motorph.ui;

import javax.swing.*;
import java.awt.*;

public class PayrollPanel extends JPanel {

    public PayrollPanel() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Payroll Computation", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        add(label, BorderLayout.CENTER);
    }
}
