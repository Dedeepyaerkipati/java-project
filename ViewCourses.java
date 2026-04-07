package com.courseregistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewCourses extends JFrame implements ActionListener {

    private final String studentEmail;
    private JButton btnRegister, btnBack;
    private JTable table;

    public ViewCourses(String email) {
        this.studentEmail = email;
        setTitle("Available Courses");
        setSize(660, 520);
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
        root.add(UITheme.headerPanel("Available Courses", "Browse all courses offered by our institution"), BorderLayout.NORTH);

        // Table
        String[] cols = {"#", "Course Name", "Fee (₹)", "Duration"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UITheme.styledTable(cols, new Object[0][]);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        loadCourses(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UITheme.BG_LIGHT);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        root.add(tablePanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnRegister = UITheme.accentButton("➕  Register Course");
        btnBack     = UITheme.primaryButton("← Back");
        btnRegister.addActionListener(this);
        btnBack.addActionListener(this);
        btnPanel.add(btnRegister); btnPanel.add(btnBack);
        root.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadCourses(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT course_name, fee, duration FROM courses ORDER BY course_name");
            int idx = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    idx++,
                    rs.getString("course_name"),
                    String.format("₹ %,.2f", rs.getDouble("fee")),
                    rs.getString("duration")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack)     { backToDashboard(); }
        else if (e.getSource() == btnRegister) { new CourseRegistrationModule(studentEmail); dispose(); }
    }

    private void backToDashboard() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM students WHERE email=?");
            ps.setString(1, studentEmail);
            ResultSet rs = ps.executeQuery();
            String name = rs.next() ? rs.getString("name") : studentEmail;
            new StudentDashboard(studentEmail, name);
            dispose();
        } catch (SQLException ex) {
            new StudentDashboard(studentEmail, studentEmail); dispose();
        }
    }
}
