package coaching.ui;

import coaching.dao.UserDAO;
import coaching.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

public class SignupFrame extends JFrame {
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backToLoginButton;
    private UserDAO userDAO;

    public SignupFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Coaching Management System - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 150, 243));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        mainPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(confirmPasswordField, gbc);

        // Signup Button
        signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(76, 175, 80));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.addActionListener(this::signupAction);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(signupButton, gbc);

        // Back to Login
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(Color.WHITE);
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backPanel.add(haveAccountLabel);

        backToLoginButton = new JButton("Login");
        backToLoginButton.setForeground(new Color(33, 150, 243));
        backToLoginButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        backPanel.add(backToLoginButton);

        gbc.gridy = 7;
        gbc.insets = new Insets(5, 10, 10, 10);
        mainPanel.add(backPanel, gbc);

        add(mainPanel);
    }

    private void signupAction(ActionEvent e) {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Username must be at least 4 characters long.",
                    "Invalid Username",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long.",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("Creating account...");

        User newUser = new User(fullName, email, username, password); // password will be hashed in DAO

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userDAO.registerUser(newUser);
            }

            @Override
            protected void done() {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(SignupFrame.this,
                                "Account created successfully! You can now login.",
                                "Registration Successful",
                                JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame().setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(SignupFrame.this,
                                "Registration failed. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    if (msg.contains("Duplicate entry")) {
                        if (msg.contains("username")) {
                            JOptionPane.showMessageDialog(SignupFrame.this,
                                    "Username already taken. Please choose another.",
                                    "Duplicate Username",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (msg.contains("email")) {
                            JOptionPane.showMessageDialog(SignupFrame.this,
                                    "Email already registered. Please use a different email.",
                                    "Duplicate Email",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(SignupFrame.this,
                                "Registration error: " + msg,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        worker.execute();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }
}