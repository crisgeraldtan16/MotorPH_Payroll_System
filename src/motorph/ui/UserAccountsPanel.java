package motorph.ui;

import motorph.model.Employee;
import motorph.model.User;
import motorph.util.CSVUtil;
import motorph.util.Session;
import motorph.util.UserIOUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserAccountsPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(28, 35, 50);
    private static final Color MUTED = new Color(110, 120, 140);

    private JComboBox<String> employeeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JLabel selectedEmployeeInfo;

    private List<Employee> employees;

    public UserAccountsPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(BG);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refreshData();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setOpaque(false);

        JLabel title = new JLabel("User Accounts");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("IT can create login credentials for employees");
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
        refreshBtn.addActionListener(e -> refreshData());

        header.add(left, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        employeeCombo = new JComboBox<>();
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        roleCombo = new JComboBox<>(new String[]{
                "REGULAR", "PROBATIONARY", "HR", "IT", "FINANCE", "ADMIN"
        });

        selectedEmployeeInfo = new JLabel("Select an employee.");
        selectedEmployeeInfo.setForeground(MUTED);

        employeeCombo.addActionListener(e -> updateSelectedEmployeeInfo());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        form.add(label("Employee *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(employeeCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        form.add(label("Username *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(usernameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        form.add(label("Password *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(passwordField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        form.add(label("Role *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.75;
        form.add(roleCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(selectedEmployeeInfo, gbc);
        gbc.gridwidth = 1;

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton createBtn = new JButton("Create Credentials");
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> createCredentials());

        bottom.add(createBtn);

        card.add(form, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        return l;
    }

    public void refreshData() {
        User current = Session.getCurrentUser();
        if (current == null || !current.isIt()) return;

        employees = CSVUtil.loadEmployees();

        employeeCombo.removeAllItems();
        for (Employee e : employees) {
            employeeCombo.addItem(e.getEmployeeNumber() + " - " + e.getFullName());
        }

        if (employeeCombo.getItemCount() > 0) {
            employeeCombo.setSelectedIndex(0);
        }

        updateSelectedEmployeeInfo();
    }

    private void updateSelectedEmployeeInfo() {
        int idx = employeeCombo.getSelectedIndex();
        if (idx < 0 || employees == null || idx >= employees.size()) {
            selectedEmployeeInfo.setText("Select an employee.");
            return;
        }

        Employee e = employees.get(idx);
        selectedEmployeeInfo.setText(
                "Selected: " + e.getFullName() + " | Position: " + e.getPosition() + " | Status: " + e.getStatus()
        );
    }

    private void createCredentials() {
        User current = Session.getCurrentUser();
        if (current == null || !current.isIt()) {
            JOptionPane.showMessageDialog(this, "Only IT users can create login credentials.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idx = employeeCombo.getSelectedIndex();
        if (idx < 0 || employees == null || idx >= employees.size()) {
            JOptionPane.showMessageDialog(this, "Please select an employee.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employee emp = employees.get(idx);

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String roleText = String.valueOf(roleCombo.getSelectedItem()).trim();

        if (username.isEmpty() || password.isEmpty() || roleText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (UserIOUtil.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please use a different username.", "Duplicate Username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid role selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(username, password, role, emp.getEmployeeNumber());
        UserIOUtil.appendUser(newUser);

        JOptionPane.showMessageDialog(this,
                "Login credentials created for " + emp.getFullName() + ".",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        usernameField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedItem("REGULAR");
    }
}