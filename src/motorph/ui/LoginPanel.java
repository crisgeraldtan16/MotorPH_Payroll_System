package motorph.ui;

import motorph.model.User;
import motorph.util.CSVUtil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("MotorPH Payroll System - Login");
        title.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> authenticate());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = 0; add(title, gbc);

        gbc.gridy = 1; add(userLabel, gbc);
        gbc.gridy = 2; add(usernameField, gbc);

        gbc.gridy = 3; add(passLabel, gbc);
        gbc.gridy = 4; add(passwordField, gbc);

        gbc.gridy = 5; add(loginButton, gbc);
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<User> users = CSVUtil.loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                mainFrame.showMainApp();
                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Invalid username or password.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
