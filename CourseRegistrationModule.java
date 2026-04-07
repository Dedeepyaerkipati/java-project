package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class CourseRegistrationModule extends JFrame implements ActionListener {

    private final String studentEmail;
    private JButton btnRegister, btnCancel, btnBack;
    private JLabel lblTotal;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private List<Double> fees = new ArrayList<>();
    private List<String> courseNames = new ArrayList<>();

    public CourseRegistrationModule(String email) {
        this.studentEmail = email;
        setTitle("Register for Courses");
        setSize(620, 580);
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
        root.add(UITheme.headerPanel("Course Registration", "Select courses you want to enroll in"), BorderLayout.NORTH);

        // Instruction banner
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT));
        info.setBackground(new Color(230, 242, 255));
        info.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        JLabel infoLbl = new JLabel("ℹ️  Courses already registered will be shown as disabled.");
        infoLbl.setFont(UITheme.FONT_SMALL);
        infoLbl.setForeground(UITheme.PRIMARY_DARK);
        info.add(infoLbl);

        // Courses scroll area
        JPanel coursePanel = new JPanel();
        coursePanel.setBackground(Color.WHITE);
        coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
        coursePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        loadCourses(coursePanel);

        JScrollPane scrollPane = new JScrollPane(coursePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Total fee panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(240, 247, 255));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 215, 240)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        lblTotal = new JLabel("Total Fee: ₹ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(UITheme.PRIMARY_DARK);
        totalPanel.add(lblTotal, BorderLayout.WEST);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(UITheme.BG_LIGHT);
        btnRegister = UITheme.successButton("✔  Enroll Now");
        btnCancel   = UITheme.dangerButton("✖  Cancel");
        btnBack     = UITheme.primaryButton("← Back");
        for (JButton b : new JButton[]{btnRegister, btnCancel, btnBack}) { b.addActionListener(this); btnPanel.add(b); }

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(UITheme.BG_LIGHT);
        south.add(totalPanel, BorderLayout.NORTH);
        south.add(btnPanel, BorderLayout.SOUTH);

        root.add(info, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);

        // Reorder: header at top, info below
        JPanel northCombo = new JPanel(new BorderLayout());
        northCombo.add(UITheme.headerPanel("Course Registration", "Select courses you want to enroll in"), BorderLayout.NORTH);
        northCombo.add(info, BorderLayout.SOUTH);
        root.add(northCombo, BorderLayout.NORTH);

        setContentPane(root);
    }

    private void loadCourses(JPanel panel) {
        Set<String> registered = getRegisteredCourses();
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT course_name, fee, duration FROM courses ORDER BY course_name");
            while (rs.next()) {
                String name   = rs.getString("course_name");
                double fee    = rs.getDouble("fee");
                String dur    = rs.getString("duration");
                boolean taken = registered.contains(name);

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(taken ? new Color(245, 245, 245) : Color.WHITE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 240, 250)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)));

                JCheckBox cb = new JCheckBox();
                cb.setBackground(row.getBackground());
                cb.setEnabled(!taken);
                cb.addActionListener(ev -> updateTotal());
                checkBoxes.add(cb);
                fees.add(fee);
                courseNames.add(name);

                JLabel nameLbl = new JLabel((taken ? "✓  " : "     ") + name);
                nameLbl.setFont(UITheme.FONT_LABEL);
                nameLbl.setForeground(taken ? UITheme.TEXT_MUTED : UITheme.TEXT_DARK);

                JPanel right = new JPanel(new GridLayout(2, 1));
                right.setBackground(row.getBackground());
                JLabel feeLbl = new JLabel("₹ " + String.format("%,.2f", fee), SwingConstants.RIGHT);
                feeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                feeLbl.setForeground(UITheme.PRIMARY);
                JLabel durLbl = new JLabel(dur, SwingConstants.RIGHT);
                durLbl.setFont(UITheme.FONT_SMALL);
                durLbl.setForeground(UITheme.TEXT_MUTED);
                right.add(feeLbl); right.add(durLbl);

                if (taken) {
                    JLabel takenLbl = new JLabel("  Already Registered");
                    takenLbl.setFont(UITheme.FONT_SMALL);
                    takenLbl.setForeground(UITheme.SUCCESS);
                    row.add(takenLbl, BorderLayout.WEST);
                } else {
                    row.add(cb, BorderLayout.WEST);
                }
                row.add(nameLbl, BorderLayout.CENTER);
                row.add(right, BorderLayout.EAST);
                panel.add(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < checkBoxes.size(); i++)
            if (checkBoxes.get(i).isSelected()) total += fees.get(i);
        lblTotal.setText(String.format("Total Fee: ₹ %,.2f", total));
    }

    private Set<String> getRegisteredCourses() {
        Set<String> set = new HashSet<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT course_name FROM registrations WHERE student_email=?");
            ps.setString(1, studentEmail);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) set.add(rs.getString("course_name"));
        } catch (SQLException ignored) {}
        return set;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack || e.getSource() == btnCancel) { backToDashboard(); return; }
        if (e.getSource() == btnRegister) registerCourses();
    }

    private void registerCourses() {
        List<String> selected = new ArrayList<>();
        double total = 0;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selected.add(courseNames.get(i));
                total += fees.get(i);
            }
        }
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one course.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("You are about to register for:\n\n");
        for (String c : selected) sb.append("  • ").append(c).append("\n");
        sb.append(String.format("\nTotal Fee: ₹ %,.2f\n\nProceed?", total));

        int confirm = JOptionPane.showConfirmDialog(this, sb.toString(),
            "Confirm Registration", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Payment Mode Selection
        JPanel payPanel = new JPanel(new GridBagLayout());
        payPanel.setBackground(Color.WHITE);
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(6, 8, 6, 8);
        pgbc.anchor = GridBagConstraints.WEST;

        JLabel payTitle = new JLabel("Select Payment Mode");
        payTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payTitle.setForeground(UITheme.PRIMARY_DARK);
        pgbc.gridx = 0; pgbc.gridy = 0; pgbc.gridwidth = 2;
        payPanel.add(payTitle, pgbc);

        JLabel amtLbl = new JLabel(String.format("Amount to Pay: ₹ %,.2f", total));
        amtLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        amtLbl.setForeground(UITheme.SUCCESS);
        pgbc.gridy = 1;
        payPanel.add(amtLbl, pgbc);

        String[] payModes = {"UPI (Google Pay / PhonePe / BHIM)", "Paytm Wallet", "Net Banking", "Credit / Debit Card"};
        ButtonGroup bg = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[payModes.length];
        pgbc.gridwidth = 1;
        for (int i = 0; i < payModes.length; i++) {
            radios[i] = new JRadioButton(payModes[i]);
            radios[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
            radios[i].setBackground(Color.WHITE);
            bg.add(radios[i]);
            pgbc.gridx = 0; pgbc.gridy = i + 2;
            payPanel.add(radios[i], pgbc);
        }
        radios[0].setSelected(true); // Default: UPI

        JTextField tfUpiId = new JTextField(20);
        tfUpiId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfUpiId.setToolTipText("Enter UPI ID (e.g. name@upi)");
        JLabel upiLabel = new JLabel("UPI ID :");
        upiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pgbc.gridx = 0; pgbc.gridy = payModes.length + 2;
        payPanel.add(upiLabel, pgbc);
        pgbc.gridx = 1;
        payPanel.add(tfUpiId, pgbc);

        // Show/hide UPI ID field based on selection
        for (JRadioButton rb : radios) {
            rb.addActionListener(ev -> {
                boolean isUpi = radios[0].isSelected() || radios[1].isSelected();
                upiLabel.setVisible(isUpi);
                tfUpiId.setVisible(isUpi);
            });
        }

        int payResult = JOptionPane.showConfirmDialog(this, payPanel,
            "💳  Payment Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (payResult != JOptionPane.OK_OPTION) return;

        // Validate UPI ID if UPI/Paytm selected
        String selectedMode = "";
        for (int i = 0; i < radios.length; i++) {
            if (radios[i].isSelected()) { selectedMode = payModes[i]; break; }
        }
        if ((radios[0].isSelected() || radios[1].isSelected()) && tfUpiId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your UPI ID to proceed.", "Payment Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO registrations(student_email, course_name) VALUES(?,?)");
            for (String c : selected) {
                ps.setString(1, studentEmail); ps.setString(2, c); ps.addBatch();
            }
            ps.executeBatch();
            JOptionPane.showMessageDialog(this,
                "🎉 Successfully enrolled in " + selected.size() + " course(s)!\nTotal Fee: ₹ " + String.format("%,.2f", total) + "\nPayment Mode: " + selectedMode,
                "Enrollment Successful", JOptionPane.INFORMATION_MESSAGE);
            backToDashboard();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error registering courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backToDashboard() {
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
