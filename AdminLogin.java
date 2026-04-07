package com.courseregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin extends JFrame implements ActionListener {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin, btnBack;

    public AdminLogin() {
        setTitle("Admin Login");
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 20, 70),
                    getWidth(), getHeight(), new Color(80, 20, 110));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circles
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-40, -40, 200, 200);
                g2.fillOval(getWidth()-120, getHeight()-80, 200, 200);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 140));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        // Person icon drawn as a circle+body
        JPanel personIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth()/2, cy = getHeight()/2;
                // head
                g2.setColor(new Color(200,180,255,180));
                g2.fillOval(cx-14, cy-22, 28, 28);
                // body arc
                g2.fillArc(cx-22, cy+4, 44, 32, 0, 180);
                g2.dispose();
            }
        };
        personIcon.setOpaque(false);
        personIcon.setPreferredSize(new Dimension(56, 56));
        personIcon.setMaximumSize(new Dimension(56, 56));
        personIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("Admin Control Panel");
        titleLbl.setFont(UITheme.FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLbl = new JLabel("Authorized personnel only");
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(new Color(200, 180, 230));
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

        tfUser = UITheme.styledField();
        pfPass = UITheme.styledPassword();

        // Username row
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        card.add(UITheme.fieldLabel("Username :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        card.add(tfUser, gbc);
        gbc.weightx = 0;

        // Password row with eye toggle
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        card.add(UITheme.fieldLabel("Password :"), gbc);

        JPanel passRow = buildPasswordRow(pfPass);
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

        pfPass.addActionListener(this);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG_LIGHT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        wrapper.add(card);
        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Builds a password field + eye toggle button in a panel */
    static JPanel buildPasswordRow(JPasswordField pf) {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(260, 40));

        pf.setPreferredSize(new Dimension(220, 40));

        JButton eye = new JButton("👁") {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(hov ? new Color(67,97,238,30) : Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        eye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        eye.setFocusPainted(false);
        eye.setBorderPainted(false);
        eye.setContentAreaFilled(false);
        eye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eye.setPreferredSize(new Dimension(38, 40));
        eye.setToolTipText("Show/hide password");

        // Wrap field+eye in a bordered panel
        JPanel fieldWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(new Color(200,210,230));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.dispose();
            }
        };
        fieldWrap.setOpaque(false);
        // Remove border from password field since wrapper handles it
        pf.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        fieldWrap.add(pf, BorderLayout.CENTER);
        fieldWrap.add(eye, BorderLayout.EAST);

        row.add(fieldWrap, BorderLayout.CENTER);

        final boolean[] visible = {false};
        eye.addActionListener(e -> {
            visible[0] = !visible[0];
            pf.setEchoChar(visible[0] ? (char)0 : '•');
            eye.setText(visible[0] ? "🙈" : "👁");
        });
        return row;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) { new HomePage(); dispose(); return; }
        String user = tfUser.getText().trim();
        String pass = new String(pfPass.getPassword()).trim();
        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            new AdminDashboard(); dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
