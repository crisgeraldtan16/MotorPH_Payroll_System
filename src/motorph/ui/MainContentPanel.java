package motorph.ui;

import javax.swing.*;
import java.awt.*;

public class MainContentPanel extends JPanel {

    private final CardLayout cardLayout;

    public MainContentPanel(MainFrame mainFrame) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        add(new DashboardPanel(), "DASHBOARD");
        add(new EmployeePanel(mainFrame), "EMPLOYEE");
        add(new PayrollPanel(mainFrame), "PAYROLL");
    }

    public void showPanel(String name) {
        cardLayout.show(this, name);
    }
}
