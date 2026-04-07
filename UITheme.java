package com.courseregistration;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UITheme {
    // ── Color Palette ──────────────────────────────────────────────────────
    public static final Color PRIMARY       = new Color(67, 97, 238);
    public static final Color PRIMARY_DARK  = new Color(47, 72, 200);
    public static final Color PRIMARY_LIGHT = new Color(114, 137, 245);
    public static final Color ACCENT        = new Color(255, 140, 0);
    public static final Color ACCENT_HOVER  = new Color(220, 110, 0);
    public static final Color SUCCESS       = new Color(34, 197, 94);
    public static final Color SUCCESS_DARK  = new Color(22, 163, 74);
    public static final Color DANGER        = new Color(239, 68, 68);
    public static final Color DANGER_DARK   = new Color(185, 28, 28);
    public static final Color PURPLE        = new Color(139, 92, 246);
    public static final Color PURPLE_DARK   = new Color(109, 62, 220);
    public static final Color BG_LIGHT      = new Color(248, 250, 255);
    public static final Color BG_CARD       = Color.WHITE;
    public static final Color TEXT_DARK     = new Color(17, 24, 39);
    public static final Color TEXT_MUTED    = new Color(107, 114, 128);
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);
    public static final Color TABLE_HEADER  = new Color(67, 97, 238);
    public static final Color TABLE_ALT     = new Color(240, 244, 255);

    // ── Fonts ──────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_H = new Font("Segoe UI", Font.BOLD, 13);

    // ── Navbar (top bar used in all dashboards) ────────────────────────────
    /**
     * Creates a modern top navigation bar.
     * @param title       page title shown on left
     * @param subtitle    subtitle / breadcrumb shown on left
     * @param profileName name displayed on profile chip (null = hide chip)
     * @param profileEmail email shown in popup (null = hide)
     * @param gradStart   left gradient color
     * @param gradEnd     right gradient color
     * @param logoutAction ActionListener for logout button
     */
    public static JPanel navBar(String title, String subtitle,
                                String profileName, String profileEmail,
                                Color gradStart, Color gradEnd,
                                ActionListener logoutAction) {

        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, gradStart, getWidth(), getHeight(), gradEnd);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom shadow line
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 72));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 20));

        // LEFT: title + subtitle
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        left.add(titleLbl);

        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(FONT_SMALL);
            subLbl.setForeground(new Color(200, 215, 255));
            left.add(Box.createVerticalStrut(2));
            left.add(subLbl);
        }
        bar.add(left, BorderLayout.WEST);

        // RIGHT: profile chip + compact logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Profile chip (only if name provided)
        if (profileName != null) {
            JButton profileBtn = createProfileChip(profileName, profileEmail);
            right.add(profileBtn);
        }

        // Compact logout button
        JButton logoutBtn = compactLogoutButton();
        logoutBtn.addActionListener(logoutAction);
        right.add(logoutBtn);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /** Circular avatar chip that opens a popup with student/admin info */
    private static JButton createProfileChip(String name, String email) {
        // Get initials
        String initials = getInitials(name);

        JButton chip = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Circle background
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                // White ring
                g2.setColor(new Color(255, 255, 255, 180));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(2, 2, getWidth() - 5, getHeight() - 5);
                // Initials text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(initials)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }
        };
        chip.setPreferredSize(new Dimension(40, 40));
        chip.setFocusPainted(false);
        chip.setBorderPainted(false);
        chip.setContentAreaFilled(false);
        chip.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chip.setToolTipText("View Profile");

        chip.addActionListener(e -> showProfilePopup(chip, name, email));

        // Hover effect
        chip.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { chip.setOpaque(false); chip.repaint(); }
            public void mouseExited(MouseEvent e)  { chip.repaint(); }
        });

        return chip;
    }

    /** Shows a sleek popup with profile details */
    private static void showProfilePopup(Component anchor, String name, String email) {
        JPopupMenu popup = new JPopupMenu() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        popup.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 228, 255), 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        popup.setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(12, 18, 14, 18));

        // Avatar circle (large)
        JPanel avatarCircle = new JPanel() {
            String initials = getInitials(name);
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(initials)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(60, 60));
        avatarCircle.setMaximumSize(new Dimension(60, 60));
        avatarCircle.setOpaque(false);
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLbl.setForeground(TEXT_DARK);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLbl = new JLabel(email != null ? email : "");
        emailLbl.setFont(FONT_SMALL);
        emailLbl.setForeground(TEXT_MUTED);
        emailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setForeground(BORDER_COLOR);

        JLabel roleLbl = new JLabel(email != null && email.contains("admin") ? "👑  Administrator" : "🎓  Student Account");
        roleLbl.setFont(FONT_SMALL);
        roleLbl.setForeground(PRIMARY);
        roleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(avatarCircle);
        content.add(Box.createVerticalStrut(10));
        content.add(nameLbl);
        content.add(Box.createVerticalStrut(3));
        content.add(emailLbl);
        content.add(Box.createVerticalStrut(10));
        content.add(sep);
        content.add(Box.createVerticalStrut(8));
        content.add(roleLbl);

        popup.add(content);
        popup.show(anchor, anchor.getWidth() - 220, anchor.getHeight() + 4);
    }

    /** Small, elegant logout button */
    public static JButton compactLogoutButton() {
        JButton btn = new JButton("⏏  Logout") {
            private boolean hovered = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? new Color(255, 80, 80) : new Color(255, 255, 255, 40);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(hovered ? Color.WHITE : new Color(255, 220, 220));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(new Color(255, 220, 220));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(105, 36));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e)  { btn.setForeground(new Color(255, 220, 220)); }
        });
        return btn;
    }

    /** Gets initials from a full name (up to 2 chars) */
    private static String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    // ── Legacy headerPanel (kept for non-dashboard screens) ───────────────
    public static JPanel headerPanel(String title, String subtitle) {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLbl);
        if (subtitle != null && !subtitle.isEmpty()) {
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(FONT_SMALL);
            subLbl.setForeground(new Color(180, 200, 240));
            subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(Box.createVerticalStrut(4));
            panel.add(subLbl);
        }
        return panel;
    }

    // ── Modern card menu button ────────────────────────────────────────────
    /**
     * Creates a large card-style menu button with icon, label and arrow.
     */
    public static JButton cardMenuButton(String icon, String label, String desc, Color accent) {
        JButton btn = new JButton() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                if (hovered) {
                    GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), getHeight(),
                        accent.darker());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    // Left accent bar
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                    // Border
                    g2.setColor(new Color(220, 228, 255));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                }
                // Icon bubble
                int bubbleSize = 44;
                int bubbleX = 18, bubbleY = (getHeight() - bubbleSize) / 2;
                g2.setColor(hovered ? new Color(255, 255, 255, 50) : new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
                g2.fillRoundRect(bubbleX, bubbleY, bubbleSize, bubbleSize, 12, 12);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(hovered ? Color.WHITE : accent);
                g2.drawString(icon, bubbleX + (bubbleSize - fm.stringWidth(icon)) / 2,
                    bubbleY + (bubbleSize + fm.getAscent() - fm.getDescent()) / 2);
                // Label
                int textX = bubbleX + bubbleSize + 14;
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(hovered ? Color.WHITE : TEXT_DARK);
                g2.drawString(label, textX, getHeight() / 2 - 2);
                // Description
                if (desc != null && !desc.isEmpty()) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.setColor(hovered ? new Color(220, 230, 255) : TEXT_MUTED);
                    g2.drawString(desc, textX, getHeight() / 2 + 14);
                }
                // Arrow
                g2.setColor(hovered ? new Color(255, 255, 255, 180) : new Color(180, 190, 220));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String arrow = "›";
                FontMetrics afm = g2.getFontMetrics();
                g2.drawString(arrow, getWidth() - afm.stringWidth(arrow) - 18,
                    (getHeight() + afm.getAscent() - afm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 68));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Standard buttons ──────────────────────────────────────────────────
    public static JButton primaryButton(String text) { return makeBtn(text, PRIMARY, PRIMARY_DARK); }
    public static JButton accentButton(String text)  { return makeBtn(text, ACCENT, ACCENT_HOVER); }
    public static JButton dangerButton(String text)  { return makeBtn(text, DANGER, DANGER_DARK); }
    public static JButton successButton(String text) { return makeBtn(text, SUCCESS, SUCCESS_DARK); }

    private static JButton makeBtn(String text, Color base, Color hover) {
        JButton btn = new JButton(text) {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true; repaint(); }
                public void mouseExited(MouseEvent e)  { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? hover : base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        return btn;
    }

    // ── Form fields ───────────────────────────────────────────────────────
    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FONT_INPUT);
        f.setPreferredSize(new Dimension(260, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        f.setForeground(TEXT_DARK);
        return f;
    }

    public static JPasswordField styledPassword() {
        JPasswordField f = new JPasswordField();
        f.setFont(FONT_INPUT);
        f.setPreferredSize(new Dimension(260, 40));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        f.setForeground(TEXT_DARK);
        return f;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(new Color(55, 65, 81));
        return l;
    }

    // ── Table ─────────────────────────────────────────────────────────────
    public static JTable styledTable(String[] cols, Object[][] data) {
        JTable table = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT);
                c.setForeground(TEXT_DARK);
                return c;
            }
        };
        table.setFont(FONT_TABLE);
        table.setRowHeight(36);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(new Color(67, 97, 238, 180));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(FONT_TABLE_H);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        return table;
    }

    // ── Utility ───────────────────────────────────────────────────────────
    public static void centerWindow(JFrame frame) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screen.width - frame.getWidth()) / 2,
                          (screen.height - frame.getHeight()) / 2);
    }

    public static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
}
