package com.courseregistration;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UITheme.applyLookAndFeel();
        SwingUtilities.invokeLater(HomePage::new);
    }
}
