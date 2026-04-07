package com.courseregistration;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class HomePage extends JFrame implements ActionListener {

    private JButton btnStudentLogin, btnStudentReg, btnAdminLogin, btnExit;

    public HomePage() {
        setTitle("Online Course Registration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 640);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        UITheme.centerWindow(this);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_LIGHT);

        // ── Gradient header ─────────────────────────────────────────────
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY,
                    getWidth(), getHeight(), UITheme.PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circle
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fillOval(-60, -60, 260, 260);
                g2.fillOval(getWidth() - 100, getHeight() - 80, 200, 200);
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setPreferredSize(new Dimension(0, 160));
        header.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel appIcon = new JLabel("🎓", SwingConstants.CENTER);
        appIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        appIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Online Course Registration", SwingConstants.CENTER);
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Your gateway to professional learning", SwingConstants.CENTER);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(new Color(190, 210, 255));
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(appIcon);
        header.add(Box.createVerticalStrut(8));
        header.add(titleLbl);
        header.add(Box.createVerticalStrut(4));
        header.add(subLbl);
        root.add(header, BorderLayout.NORTH);

        // ── Center card ─────────────────────────────────────────────────
        JPanel center = new JPanel();
        center.setBackground(UITheme.BG_LIGHT);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel chooseLabel = new JLabel("Choose how to continue");
        chooseLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        chooseLabel.setForeground(UITheme.TEXT_DARK);
        chooseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagLine = new JLabel("Learn  ·  Grow  ·  Succeed");
        tagLine.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tagLine.setForeground(UITheme.TEXT_MUTED);
        tagLine.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(chooseLabel);
        center.add(Box.createVerticalStrut(4));
        center.add(tagLine);
        center.add(Box.createVerticalStrut(24));

        // Menu card
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(220, 228, 255));
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                // shadow
                g2.setColor(new Color(67, 97, 238, 12));
                for (int i = 1; i <= 6; i++)
                    g2.drawRoundRect(-i, -i, getWidth() + i * 2, getHeight() + i * 2, 18 + i, 18 + i);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(440, 999));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnStudentLogin = homeMenuBtn("🎓", "Student Login", "Access your student portal", UITheme.PRIMARY);
        btnStudentReg   = homeMenuBtn("📝", "Student Registration", "Create a new student account", UITheme.SUCCESS);
        btnAdminLogin   = homeMenuBtn("🛠", "Admin Login", "Access the administration panel", new Color(100, 50, 180));
        btnExit         = homeMenuBtn("⏏", "Exit Application", "Close the program", UITheme.DANGER);

        for (JButton b : new JButton[]{btnStudentLogin, btnStudentReg, btnAdminLogin, btnExit}) {
            b.addActionListener(this);
            card.add(b);
            card.add(Box.createVerticalStrut(10));
        }

        center.add(card);

        JLabel ver = new JLabel("v1.0  ·  Online Course Registration System", SwingConstants.CENTER);
        ver.setFont(UITheme.FONT_SMALL);
        ver.setForeground(UITheme.TEXT_MUTED);
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(Box.createVerticalStrut(18));
        center.add(ver);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UITheme.BG_LIGHT);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JButton homeMenuBtn(String icon, String label, String desc, Color accent) {
        return UITheme.cardMenuButton(icon, label, desc, accent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnStudentLogin) { new StudentLogin(); dispose(); }
        else if (src == btnStudentReg)   { new StudentRegistration(); dispose(); }
        else if (src == btnAdminLogin)   { new AdminLogin(); dispose(); }
        else if (src == btnExit) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) System.exit(0);
        }
    }

    // Drop-shadow border — kept for compatibility with other files
    public static class ShadowBorder implements Border {
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(67, 97, 238, 15));
            for (int i = 0; i < 5; i++)
                g2.drawRoundRect(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, 14, 14);
            g2.setColor(new Color(220, 228, 255));
            g2.drawRoundRect(x, y, w - 1, h - 1, 14, 14);
            g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(5, 5, 5, 5); }
        public boolean isBorderOpaque() { return false; }
    }
}
