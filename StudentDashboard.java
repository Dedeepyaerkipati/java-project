package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentDashboard extends JFrame implements ActionListener {

    private final String studentEmail;
    private final String studentName;
    private JButton btnViewCourses, btnRegisterCourse, btnMyRegistrations;

    public StudentDashboard(String email, String name) {
        this.studentEmail = email;
        this.studentName  = name;
        setTitle("Student Dashboard — " + name);
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

        // ── Navbar ─────────────────────────────────────────────────────────
        JPanel navbar = UITheme.navBar(
            "Student Dashboard",
            "Welcome back, " + studentName + "! 👋",
            studentName, studentEmail,
            new Color(67, 97, 238), new Color(47, 122, 220),
            e -> logout()
        );
        root.add(navbar, BorderLayout.NORTH);

        // ── Two-column layout ──────────────────────────────────────────────
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;

        // LEFT — Overview
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.55;
        gc.insets = new Insets(0, 0, 0, 16);
        mainPanel.add(buildOverviewPanel(), gc);

        // RIGHT — Quick Actions
        gc.gridx = 1; gc.weightx = 0.45;
        gc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(buildActionsPanel(), gc);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_LIGHT);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(240, 244, 255));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 228, 250)),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        JLabel fl = new JLabel("Online Course Registration System  •  v1.0");
        fl.setFont(UITheme.FONT_SMALL);
        fl.setForeground(UITheme.TEXT_MUTED);
        footer.add(fl);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── Left: Overview ──────────────────────────────────────────────────────
    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(sectionLabel("Overview"));
        panel.add(Box.createVerticalStrut(14));

        int[] counts = fetchStudentCounts(); // [available courses, enrolled, remaining]

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.add(bigStatCard("📚", "Available",   String.valueOf(counts[0]), "Courses to explore",  new Color(67, 97, 238)));
        grid.add(bigStatCard("✅", "Enrolled",    String.valueOf(counts[1]), "Your registrations",  new Color(34, 197, 94)));
        grid.add(bigStatCard("🎓", "Remaining",   String.valueOf(counts[2]), "Courses not taken",   new Color(139, 92, 246)));
        grid.add(bigStatCard("⭐", "Status",      "Active",                  "Account in good standing", new Color(245, 158, 11)));

        panel.add(grid);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ── Right: Quick Actions ────────────────────────────────────────────────
    private JPanel buildActionsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(sectionLabel("Quick Actions"));
        panel.add(Box.createVerticalStrut(14));

        btnViewCourses    = UITheme.cardMenuButton("📚", "View Available Courses",
            "Browse all courses offered", new Color(67, 97, 238));
        btnRegisterCourse = UITheme.cardMenuButton("➕", "Register for a Course",
            "Enroll in new courses", new Color(34, 197, 94));
        btnMyRegistrations = UITheme.cardMenuButton("📋", "My Registered Courses",
            "View your current enrollments", new Color(139, 92, 246));

        for (JButton b : new JButton[]{btnViewCourses, btnRegisterCourse, btnMyRegistrations}) {
            b.addActionListener(this);
            panel.add(b);
            panel.add(Box.createVerticalStrut(10));
        }
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(UITheme.TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel bigStatCard(String icon, String label, String count, String sub, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0, accent.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 5, 4, 4);
                g2.setColor(new Color(220, 228, 255));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 120));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JLabel iconLbl = new JLabel(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setOpaque(false);
        iconLbl.setPreferredSize(new Dimension(40, 40));
        iconLbl.setMaximumSize(new Dimension(40, 40));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel countLbl = new JLabel(count);
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        countLbl.setForeground(accent);
        countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(UITheme.TEXT_DARK);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(countLbl);
        card.add(nameLbl);
        card.add(subLbl);
        return card;
    }

    private int[] fetchStudentCounts() {
        int[] c = {0, 0, 0};
        try (Connection con = DBConnection.getConnection()) {
            ResultSet r1 = con.createStatement().executeQuery("SELECT COUNT(*) FROM courses");
            if (r1.next()) c[0] = r1.getInt(1);
            PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM registrations WHERE student_email=?");
            ps.setString(1, studentEmail);
            ResultSet r2 = ps.executeQuery();
            if (r2.next()) c[1] = r2.getInt(1);
            c[2] = Math.max(0, c[0] - c[1]);
        } catch (SQLException ignored) {}
        return c;
    }

    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { new HomePage(); dispose(); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if      (s == btnViewCourses)      { new ViewCourses(studentEmail); dispose(); }
        else if (s == btnRegisterCourse)   { new CourseRegistrationModule(studentEmail); dispose(); }
        else if (s == btnMyRegistrations)  { new RegisteredCourses(studentEmail); dispose(); }
    }
}
