package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddCourse extends JFrame implements ActionListener {

    private JTextField tfName, tfFee, tfDuration;
    private JButton btnAdd, btnClear, btnBack;

    public AddCourse() {
        setTitle("Add New Course");
        setSize(500, 450);
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
        root.add(UITheme.headerPanel("Add New Course", "Enter the details of the new course"), BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new HomePage.ShadowBorder(),
            BorderFactory.createEmptyBorder(35, 40, 35, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.anchor = GridBagConstraints.WEST;

        tfName     = UITheme.styledField();
        tfFee      = UITheme.styledField();
        tfDuration = UITheme.styledField();

        String[] labels = {"Course Name", "Fee (₹)", "Duration"};
        JTextField[] fields = {tfName, tfFee, tfDuration};
        String[] hints = {"e.g. Java Programming", "e.g. 4999", "e.g. 3 Months"};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE;
            card.add(UITheme.fieldLabel(labels[i] + " :"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            fields[i].setToolTipText(hints[i]);
            card.add(fields[i], gbc);
            gbc.weightx = 0;
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnAdd   = UITheme.successButton("➕  Add Course");
        btnClear = UITheme.accentButton("↺  Clear");
        btnBack  = UITheme.primaryButton("← Back");
        for (JButton b : new JButton[]{btnAdd, btnClear, btnBack}) { b.addActionListener(this); btnPanel.add(b); }

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(24, 5, 5, 5);
        card.add(btnPanel, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_LIGHT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        wrapper.add(card);
        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack)  { new AdminDashboard(); dispose(); return; }
        if (e.getSource() == btnClear) { tfName.setText(""); tfFee.setText(""); tfDuration.setText(""); return; }
        addCourse();
    }

    private void addCourse() {
        String name = tfName.getText().trim();
        String feeStr = tfFee.getText().trim();
        String dur  = tfDuration.getText().trim();

        if (name.isEmpty() || feeStr.isEmpty() || dur.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double fee;
        try { fee = Double.parseDouble(feeStr); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric fee.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fee <= 0) {
            JOptionPane.showMessageDialog(this, "Fee must be a positive value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement chk = con.prepareStatement("SELECT course_id FROM courses WHERE course_name=?");
            chk.setString(1, name);
            if (chk.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "A course with this name already exists.", "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO courses(course_name, fee, duration) VALUES(?,?,?)");
            ps.setString(1, name); ps.setDouble(2, fee); ps.setString(3, dur);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "✅ Course \"" + name + "\" added successfully!",
                "Course Added", JOptionPane.INFORMATION_MESSAGE);
            tfName.setText(""); tfFee.setText(""); tfDuration.setText("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
