package com.courseregistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewRegisteredStudents extends JFrame implements ActionListener {

    private JButton btnBack;

    public ViewRegisteredStudents() {
        setTitle("Registered Students — Admin");
        setSize(780, 540);
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
        root.add(UITheme.headerPanel("Registered Students", "View all course enrollments"), BorderLayout.NORTH);

        String[] cols = {"#", "Student Name", "Email", "Phone", "Course Name"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = UITheme.styledTable(cols, new Object[0][]);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(185);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        int[] countRef = {0};
        try (Connection con = DBConnection.getConnection()) {
            String sql =
                "SELECT s.name, s.email, s.phone, r.course_name " +
                "FROM registrations r " +
                "JOIN students s ON r.student_email = s.email " +
                "ORDER BY s.name, r.course_name";
            ResultSet rs = con.createStatement().executeQuery(sql);
            int idx = 1;
            while (rs.next()) {
                countRef[0]++;
                model.addRow(new Object[]{
                    idx++,
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone") == null ? "—" : rs.getString("phone"),
                    rs.getString("course_name")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        tablePanel.setBackground(UITheme.BG_LIGHT);
        tablePanel.add(sp);
        root.add(tablePanel, BorderLayout.CENTER);

        // Summary bar
        JPanel sumPanel = new JPanel(new BorderLayout());
        sumPanel.setBackground(new Color(240, 247, 255));
        sumPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 215, 240)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        JLabel sumLbl = new JLabel("Total Registrations: " + countRef[0]);
        sumLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sumLbl.setForeground(UITheme.PRIMARY_DARK);
        sumPanel.add(sumLbl, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnBack = UITheme.primaryButton("← Back to Dashboard");
        btnBack.addActionListener(this);
        btnPanel.add(btnBack);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG_LIGHT);
        south.add(sumPanel, BorderLayout.NORTH);
        south.add(btnPanel, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    @Override public void actionPerformed(ActionEvent e) { new AdminDashboard(); dispose(); }
}
