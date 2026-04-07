package com.courseregistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisteredCourses extends JFrame implements ActionListener {

    private final String studentEmail;
    private JButton btnBack;
    private JLabel lblTotal;

    public RegisteredCourses(String email) {
        this.studentEmail = email;
        setTitle("My Registered Courses");
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
        root.add(UITheme.headerPanel("My Registered Courses", "Courses you are currently enrolled in"), BorderLayout.NORTH);

        String[] cols = {"#", "Course Name", "Fee (₹)", "Duration"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = UITheme.styledTable(cols, new Object[0][]);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        double[] totalRef = {0};
        loadRegistrations(model, totalRef);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        tablePanel.setBackground(UITheme.BG_LIGHT);
        tablePanel.add(scrollPane);
        root.add(tablePanel, BorderLayout.CENTER);

        // Total / buttons south
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(240, 247, 255));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 215, 240)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        lblTotal = new JLabel(String.format("Total Enrolled Fee: ₹ %,.2f", totalRef[0]));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(UITheme.PRIMARY_DARK);
        totalPanel.add(lblTotal, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnBack = UITheme.primaryButton("← Back to Dashboard");
        btnBack.addActionListener(this);
        btnPanel.add(btnBack);

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG_LIGHT);
        south.add(totalPanel, BorderLayout.NORTH);
        south.add(btnPanel, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadRegistrations(DefaultTableModel model, double[] totalRef) {
        model.setRowCount(0);
        totalRef[0] = 0;
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT r.course_name, c.fee, c.duration " +
                "FROM registrations r JOIN courses c ON r.course_name = c.course_name " +
                "WHERE r.student_email=? ORDER BY r.course_name");
            ps.setString(1, studentEmail);
            ResultSet rs = ps.executeQuery();
            int idx = 1;
            while (rs.next()) {
                double fee = rs.getDouble("fee");
                totalRef[0] += fee;
                model.addRow(new Object[]{
                    idx++,
                    rs.getString("course_name"),
                    String.format("₹ %,.2f", fee),
                    rs.getString("duration")
                });
            }
            if (model.getRowCount() == 0) {
                JLabel emptyLbl = new JLabel("You have not registered for any courses yet.");
                // shown via table being empty — handled by empty rows
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT name FROM students WHERE email=?");
                ps.setString(1, studentEmail);
                ResultSet rs = ps.executeQuery();
                String name = rs.next() ? rs.getString("name") : studentEmail;
                new StudentDashboard(studentEmail, name); dispose();
            } catch (SQLException ex) {
                new StudentDashboard(studentEmail, studentEmail); dispose();
            }
        }
    }
}
