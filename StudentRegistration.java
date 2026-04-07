package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentRegistration extends JFrame implements ActionListener {

    private JTextField tfName, tfEmail, tfPhone;
    private JPasswordField pfPassword, pfConfirm;
    private JButton btnRegister, btnClear, btnBack;

    public StudentRegistration() {
        setTitle("Student Registration");
        setSize(520, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.centerWindow(this);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        root.add(UITheme.headerPanel("Student Registration", "Create your account to get started"), BorderLayout.NORTH);

        // ── Form card ──────────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new HomePage.ShadowBorder(),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        tfName     = UITheme.styledField();
        tfEmail    = UITheme.styledField();
        tfPhone    = UITheme.styledField();
        pfPassword = UITheme.styledPassword();
        pfConfirm  = UITheme.styledPassword();

        String[] labels = {"Full Name", "Email Address", "Phone Number", "Password", "Confirm Password"};
        JComponent[] fields = {tfName, tfEmail, tfPhone, pfPassword, pfConfirm};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE;
            card.add(UITheme.fieldLabel(labels[i] + " :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            card.add(fields[i], gbc);
            gbc.weightx = 0;
        }

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnRegister = UITheme.successButton("✔  Register");
        btnClear    = UITheme.accentButton("↺  Clear");
        btnBack     = UITheme.primaryButton("← Back");
        for (JButton b : new JButton[]{btnRegister, btnClear, btnBack}) { b.addActionListener(this); btnPanel.add(b); }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(20, 5, 5, 5);
        card.add(btnPanel, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_LIGHT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        wrapper.add(card);

        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack)  { new HomePage(); dispose(); return; }
        if (e.getSource() == btnClear) { clearFields(); return; }
        if (e.getSource() == btnRegister) registerStudent();
    }

    private void clearFields() {
        tfName.setText(""); tfEmail.setText(""); tfPhone.setText("");
        pfPassword.setText(""); pfConfirm.setText("");
    }

    private void registerStudent() {
        String name  = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();
        String pass  = new String(pfPassword.getPassword()).trim();
        String conf  = new String(pfConfirm.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Email, and Password are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!phone.isEmpty() && !phone.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits (numbers only).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            tfPhone.requestFocus();
            return;
        }
        if (!pass.equals(conf)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            // Check duplicate
            PreparedStatement chk = con.prepareStatement("SELECT id FROM students WHERE email=?");
            chk.setString(1, email);
            if (chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "An account with this email already exists.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO students(name,email,phone,password) VALUES(?,?,?,?)");
            ps.setString(1, name); ps.setString(2, email);
            ps.setString(3, phone); ps.setString(4, pass);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "🎉 Registration successful!\nYou can now login with your credentials.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            new StudentLogin(); dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
