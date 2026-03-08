package motorph.ui;

import javax.swing.*;
import java.awt.*;

/*
 * This panel acts as the main container of the system screens.
 * It uses CardLayout to switch between different panels.
 */
public class MainContentPanel extends JPanel {

    private final CardLayout cardLayout;

    public MainContentPanel(MainFrame mainFrame) {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        /*
         * These panels are added as cards so the system
         * can switch views inside the same window.
         */
        add(new DashboardPanel(), "DASHBOARD");
        add(new EmployeePanel(mainFrame), "EMPLOYEE");
        add(new PayrollPanel(mainFrame), "PAYROLL");
    }

    /*
     * This method displays the selected panel
     * based on its assigned card name.
     */
    public void showPanel(String name) {
        cardLayout.show(this, name);
    }
}