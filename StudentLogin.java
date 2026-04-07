package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentLogin extends JFrame implements ActionListener {

    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnLogin, btnBack;

    public StudentLogin() {
        setTitle("Student Login");
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

        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.PRIMARY,
                    getWidth(), getHeight(), UITheme.PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-40, -40, 200, 200);
                g2.fillOval(getWidth()-120, getHeight()-80, 200, 200);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 140));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Person icon
        JPanel personIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()/2, cy = getHeight()/2;
                g2.setColor(new Color(180, 210, 255, 200));
                g2.fillOval(cx-14, cy-22, 28, 28);
                g2.fillArc(cx-22, cy+4, 44, 32, 0, 180);
                g2.dispose();
            }
        };
        personIcon.setOpaque(false);
        personIcon.setPreferredSize(new Dimension(56, 56));
        personIcon.setMaximumSize(new Dimension(56, 56));
        personIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Student Login");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Welcome back! Please sign in");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(new Color(180, 200, 240));
        subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(personIcon);
        header.add(Box.createVerticalStrut(6));
        header.add(titleLbl);
        header.add(Box.createVerticalStrut(3));
        header.add(subLbl);
        root.add(header, BorderLayout.NORTH);

        // ── Form card ──────────────────────────────────────────────────────
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new HomePage.ShadowBorder(),
            BorderFactory.createEmptyBorder(35, 40, 35, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;

        tfEmail    = UITheme.styledField();
        pfPassword = UITheme.styledPassword();

        // Email row
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        card.add(UITheme.fieldLabel("Email Address :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        card.add(tfEmail, gbc);
        gbc.weightx = 0;

        // Password row with eye toggle
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        card.add(UITheme.fieldLabel("Password :"), gbc);
        JPanel passRow = AdminLogin.buildPasswordRow(pfPassword);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        card.add(passRow, gbc);
        gbc.weightx = 0;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(UITheme.BG_CARD);
        btnLogin = UITheme.primaryButton("  Login");
        btnBack  = UITheme.accentButton("← Back");
        btnLogin.addActionListener(this);
        btnBack.addActionListener(this);
        btnPanel.add(btnLogin);
        btnPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(24, 5, 0, 5);
        card.add(btnPanel, gbc);

        // Register link
        JLabel regLink = new JLabel("Don't have an account? Register here", SwingConstants.CENTER);
        regLink.setFont(UITheme.FONT_SMALL);
        regLink.setForeground(UITheme.PRIMARY);
        regLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        regLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new StudentRegistration(); dispose(); }
        });
        gbc.gridy = 3; gbc.insets = new Insets(14, 5, 0, 5);
        card.add(regLink, gbc);

        pfPassword.addActionListener(this);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_LIGHT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        wrapper.add(card);
        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) { new HomePage(); dispose(); return; }
        login();
    }

    private void login() {
        String email = tfEmail.getText().trim();
        String pass  = new String(pfPassword.getPassword()).trim();
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT name FROM students WHERE email=? AND password=?");
            ps.setString(1, email); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                new StudentDashboard(email, rs.getString("name")); dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
