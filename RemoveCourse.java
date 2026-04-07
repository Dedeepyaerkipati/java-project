package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RemoveCourse extends JFrame {

    // ── Colours (matching AdminDashboard palette) ──────────────────────────
    private static final Color HEADER_BG    = new Color(72, 0, 120);
    private static final Color HEADER_FG    = Color.WHITE;
    private static final Color BG_MAIN      = new Color(245, 245, 250);
    private static final Color BTN_DEL      = new Color(192, 57, 43);
    private static final Color BTN_CANCEL   = new Color(120, 120, 140);
    private static final Color BORDER_COL   = new Color(200, 190, 220);
    private static final Color STATUS_BG    = new Color(235, 230, 245);
    private static final Color DROPDOWN_BG  = new Color(255, 235, 235);
    private static final Color DROPDOWN_FG  = new Color(150, 20, 20);
    private static final Color DROPDOWN_SEL = new Color(192, 57, 43);
    private static final Color INFO_BG      = new Color(255, 245, 245);
    private static final Color INFO_BORDER  = new Color(192, 57, 43);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_SUB    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_VALUE  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_BTN    = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_STATUS = new Font("Segoe UI", Font.ITALIC, 12);

    // ── Fields ─────────────────────────────────────────────────────────────
    private JComboBox<CourseItem> courseDropdown;
    private JLabel  lblCourseName, lblFee, lblDuration;
    private JButton deleteBtn;
    private JLabel  statusLabel;

    // ── Inner model — matches actual DB columns: course_name, fee, duration ─
    static class CourseItem {
        final String courseName, fee, duration;

        CourseItem(String courseName, String fee, String duration) {
            this.courseName = courseName;
            this.fee        = fee;
            this.duration   = duration;
        }

        @Override
        public String toString() {
            return courseName;   // shown in the dropdown list
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    public RemoveCourse() {
        setTitle("Remove Course — Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.centerWindow(this);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
        loadCourses();   // populate AFTER setVisible so labels exist
    }

    // ── Header ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(HEADER_BG);
        bar.setPreferredSize(new Dimension(0, 70));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JButton back = headerBtn("← Back");
        back.addActionListener(e -> { new AdminDashboard(); dispose(); });

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.add(Box.createVerticalGlue());
        mid.add(centreLabel("🗑  Remove a Course", F_TITLE, HEADER_FG));
        mid.add(centreLabel("Select a course from the dropdown and click Delete",
                F_SUB, new Color(210, 190, 240)));
        mid.add(Box.createVerticalGlue());

        JButton refresh = headerBtn("⟳  Refresh");
        refresh.addActionListener(e -> loadCourses());

        bar.add(back,    BorderLayout.WEST);
        bar.add(mid,     BorderLayout.CENTER);
        bar.add(refresh, BorderLayout.EAST);
        return bar;
    }

    // ── Centre card ────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_MAIN);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL, 1),
                BorderFactory.createEmptyBorder(36, 48, 36, 48)));
        card.setMaximumSize(new Dimension(600, 9999));

        // ── Dropdown label ────────────────────────────────────────────────
        JLabel dropLabel = new JLabel("Select Course to Remove");
        dropLabel.setFont(F_LABEL);
        dropLabel.setForeground(new Color(150, 20, 20));
        dropLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Dropdown ──────────────────────────────────────────────────────
        courseDropdown = new JComboBox<>();
        courseDropdown.setFont(new Font("Segoe UI", Font.BOLD, 14));
        courseDropdown.setBackground(DROPDOWN_BG);
        courseDropdown.setForeground(DROPDOWN_FG);
        courseDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        courseDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseDropdown.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        courseDropdown.setBorder(BorderFactory.createLineBorder(new Color(192, 57, 43), 2));

        // Red-themed renderer for the open list
        courseDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", (index == -1) ? Font.BOLD : Font.PLAIN, 14));
                if (isSelected) {
                    setBackground(DROPDOWN_SEL);
                    setForeground(Color.WHITE);
                } else {
                    setBackground((index % 2 == 0) ? DROPDOWN_BG : new Color(255, 220, 220));
                    setForeground(DROPDOWN_FG);
                }
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                return this;
            }
        });

        // ── Info panel — shows Fee & Duration when a course is selected ───
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 14));
        infoPanel.setBackground(INFO_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INFO_BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        lblCourseName = infoValue("—");
        lblFee        = infoValue("—");
        lblDuration   = infoValue("—");

        infoPanel.add(infoKey("📚  Course Name :"));  infoPanel.add(lblCourseName);
        infoPanel.add(infoKey("💰  Fee         :"));  infoPanel.add(lblFee);
        infoPanel.add(infoKey("⏱   Duration   :"));  infoPanel.add(lblDuration);

        // Attach listener AFTER all labels are created
        courseDropdown.addActionListener(e -> updateInfoPanel());

        // ── Buttons ───────────────────────────────────────────────────────
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        deleteBtn = actionBtn("🗑  Delete Course", BTN_DEL);
        deleteBtn.setPreferredSize(new Dimension(220, 48));
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> confirmAndRemove());

        JButton cancelBtn = actionBtn("✖  Cancel", BTN_CANCEL);
        cancelBtn.setPreferredSize(new Dimension(140, 48));
        cancelBtn.addActionListener(e -> { new AdminDashboard(); dispose(); });

        btnRow.add(deleteBtn);
        btnRow.add(cancelBtn);

        // ── Assemble ──────────────────────────────────────────────────────
        card.add(dropLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(courseDropdown);
        card.add(Box.createVerticalStrut(24));
        card.add(infoPanel);
        card.add(Box.createVerticalStrut(28));
        card.add(btnRow);

        outer.add(card, new GridBagConstraints());
        return outer;
    }

    // ── Footer / status bar ────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel sb = new JPanel(new BorderLayout());
        sb.setBackground(STATUS_BG);
        sb.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        statusLabel = new JLabel("Loading courses…");
        statusLabel.setFont(F_STATUS);

        JLabel hint = new JLabel("💡 Select a course to view details, then click Delete to remove it");
        hint.setFont(F_STATUS);
        hint.setForeground(new Color(100, 80, 140));

        sb.add(statusLabel, BorderLayout.WEST);
        sb.add(hint,        BorderLayout.EAST);
        return sb;
    }

    // ── Load all courses from DB into dropdown ─────────────────────────────
    private void loadCourses() {
        // Detach listener while populating to prevent stale-label events
        ActionListener[] als = courseDropdown.getActionListeners();
        for (ActionListener al : als) courseDropdown.removeActionListener(al);

        courseDropdown.removeAllItems();
        clearInfoPanel();
        deleteBtn.setEnabled(false);
        statusLabel.setText("Loading courses…");

        try (Connection con = DBConnection.getConnection();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(
                     "SELECT course_name, fee, duration " +
                     "FROM courses ORDER BY course_name")) {

            int count = 0;
            while (rs.next()) {
                String feeStr = String.format("₹ %,.2f", rs.getDouble("fee"));
                courseDropdown.addItem(new CourseItem(
                        rs.getString("course_name"),
                        feeStr,
                        rs.getString("duration")));
                count++;
            }

            statusLabel.setText(count == 0
                    ? "No courses found in the database."
                    : count + " course(s) loaded. Select one to view details.");

        } catch (Exception ex) {
            statusLabel.setText("❌ DB Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Could not load courses:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Re-attach listener, then show first item details
        courseDropdown.addActionListener(e -> updateInfoPanel());
        updateInfoPanel();
    }

    // ── Update info panel when selection changes ───────────────────────────
    private void updateInfoPanel() {
        CourseItem item = (CourseItem) courseDropdown.getSelectedItem();
        if (item == null) {
            clearInfoPanel();
            deleteBtn.setEnabled(false);
            return;
        }
        lblCourseName.setText(item.courseName);
        lblFee       .setText(item.fee      != null ? item.fee      : "N/A");
        lblDuration  .setText(item.duration != null ? item.duration : "N/A");
        deleteBtn.setEnabled(true);
    }

    private void clearInfoPanel() {
        lblCourseName.setText("—");
        lblFee       .setText("—");
        lblDuration  .setText("—");
    }

    // ── "Are you sure?" popup → delete ────────────────────────────────────
    private void confirmAndRemove() {
        CourseItem item = (CourseItem) courseDropdown.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course from the dropdown first.",
                    "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation popup
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this course?\n\n"
                + "   📚  Course : " + item.courseName + "\n"
                + "   💰  Fee    : " + item.fee + "\n"
                + "   ⏱   Duration: " + item.duration
                + "\n\nThis action cannot be undone.",
                "Confirm Delete Course",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) return;

        // Perform deletion inside a transaction
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            // 1. Remove linked student registrations first
            int regsDeleted = 0;
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM registrations WHERE course_name = ?")) {
                ps.setString(1, item.courseName);
                regsDeleted = ps.executeUpdate();
            } catch (SQLException ignored) { /* skip if table absent */ }

            // 2. Delete the course row
            int courseDeleted;
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM courses WHERE course_name = ?")) {
                ps.setString(1, item.courseName);
                courseDeleted = ps.executeUpdate();
            }

            if (courseDeleted > 0) {
                con.commit();
                String ok = "✔  \"" + item.courseName + "\" removed successfully.";
                if (regsDeleted > 0)
                    ok += "\n   " + regsDeleted + " student registration(s) also removed.";
                JOptionPane.showMessageDialog(this, ok,
                        "Course Removed", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Removed: " + item.courseName);
                loadCourses();   // refresh dropdown after deletion
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this,
                        "Course not found — it may have already been removed.",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                loadCourses();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error removing course:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("❌ Error: " + ex.getMessage());
        }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
    private JLabel infoKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_LABEL);
        l.setForeground(new Color(150, 20, 20));
        return l;
    }

    private JLabel infoValue(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_VALUE);
        l.setForeground(new Color(40, 40, 40));
        return l;
    }

    private JButton headerBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(HEADER_BG);
        btn.setForeground(HEADER_FG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(100, 20, 160)); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(HEADER_BG); }
        });
        return btn;
    }

    private JButton actionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JLabel centreLabel(String text, Font font, Color fg) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(font);
        l.setForeground(fg);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}