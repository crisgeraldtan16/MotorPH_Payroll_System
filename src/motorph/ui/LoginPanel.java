package motorph.ui;

import motorph.model.User;
import motorph.util.CSVUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;

    // Light theme colors (matches your updated UI direction)
    private static final Color BG = new Color(245, 247, 252);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(225, 230, 240);
    private static final Color TEXT = new Color(35, 45, 65);
    private static final Color MUTED = new Color(110, 120, 145);

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(BG);

        // Main card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(22, 26, 22, 26)
        ));
        card.setPreferredSize(new Dimension(420, 360));

        // Header
        JLabel brand = new JLabel("MotorPH");
        brand.setFont(new Font("Arial", Font.BOLD, 22));
        brand.setForeground(TEXT);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Employee Payroll System");
        title.setFont(new Font("Arial", Font.PLAIN, 13));
        title.setForeground(MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginTitle = new JLabel("Sign in");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 18));
        loginTitle.setForeground(TEXT);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginHint = new JLabel("Enter your credentials to continue.");
        loginHint.setFont(new Font("Arial", Font.PLAIN, 12));
        loginHint.setForeground(MUTED);
        loginHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(brand);
        card.add(Box.createVerticalStrut(3));
        card.add(title);
        card.add(Box.createVerticalStrut(18));
        card.add(loginTitle);
        card.add(Box.createVerticalStrut(3));
        card.add(loginHint);
        card.add(Box.createVerticalStrut(18));

        // Form
        card.add(makeLabel("Username"));
        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(12));

        card.add(makeLabel("Password"));
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));

        // Show password
        showPasswordCheck = new JCheckBox("Show password");
        showPasswordCheck.setOpaque(false);
        showPasswordCheck.setForeground(MUTED);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        char defaultEcho = passwordField.getEchoChar();
        showPasswordCheck.addActionListener(e -> {
            if (showPasswordCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEcho);
            }
        });

        card.add(showPasswordCheck);
        card.add(Box.createVerticalStrut(16));

        // Buttons row
        JPanel btnRow = new JPanel(new GridLayout(1, 1, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(0, 38));
        loginButton.addActionListener(e -> authenticate());

        btnRow.add(loginButton);

        card.add(btnRow);
        card.add(Box.createVerticalStrut(12));

        // Footer note
        JLabel footer = new JLabel("Tip: Press Enter to login.");
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(footer);

        // Enter key triggers login
        KeyAdapter enterToLogin = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticate();
                }
            }
        };
        usernameField.addKeyListener(enterToLogin);
        passwordField.addKeyListener(enterToLogin);

        // Add the card centered
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(card, gbc);
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(TEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
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

                // Optional: remove the success popup for smoother UX
                // JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields after success (nice UX)
                usernameField.setText("");
                passwordField.setText("");

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
