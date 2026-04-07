package com.courseregistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminViewCourses extends JFrame implements ActionListener {

    private JButton btnBack;

    public AdminViewCourses() {
        setTitle("All Courses — Admin");
        setSize(660, 500);
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
        root.add(UITheme.headerPanel("All Courses", "Complete list of courses in the system"), BorderLayout.NORTH);

        String[] cols = {"#", "Course Name", "Fee (₹)", "Duration"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = UITheme.styledTable(cols, new Object[0][]);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);

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
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        tablePanel.setBackground(UITheme.BG_LIGHT);
        tablePanel.add(sp);
        root.add(tablePanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnBack = UITheme.primaryButton("← Back to Dashboard");
        btnBack.addActionListener(this);
        btnPanel.add(btnBack);
        root.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    @Override public void actionPerformed(ActionEvent e) { new AdminDashboard(); dispose(); }
}
