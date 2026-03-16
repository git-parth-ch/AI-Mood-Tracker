package com.moodtracker.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private AnalyticsPanel analyticsPanel;
    private JTabbedPane tabbedPane;
    private JPanel topPanel;

    public MainFrame() {
        setTitle("Daily Mood Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        initUI(false);
    }

    private void initUI(boolean isDarkCurrent) {
        getContentPane().removeAll();
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        JCheckBox darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.setBackground(UIConstants.BACKGROUND_COLOR);
        darkModeToggle.setForeground(UIConstants.TEXT_PRIMARY);
        darkModeToggle.setFont(UIConstants.MAIN_FONT);
        darkModeToggle.setSelected(isDarkCurrent);
        darkModeToggle.addActionListener(e -> toggleDarkMode(darkModeToggle.isSelected()));

        topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        topPanel.add(darkModeToggle);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 14f));
        tabbedPane.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);

        MoodPanel moodPanel = new MoodPanel();
        analyticsPanel = new AnalyticsPanel(); 

        tabbedPane.addTab("  Log Mood  ", moodPanel);
        tabbedPane.addTab(" Analytics  ", analyticsPanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    private void toggleDarkMode(boolean isDark) {
        if (isDark) {
            try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf()); } catch (Exception ex) {}
            UIConstants.setDarkMode(true);
        } else {
            try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf()); } catch (Exception ex) {}
            UIConstants.setDarkMode(false);
        }

        int selectedIndex = tabbedPane.getSelectedIndex();
        
        initUI(isDark);

        tabbedPane.setSelectedIndex(selectedIndex);
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void refreshAnalytics() {
        if (analyticsPanel != null) {
            analyticsPanel.refreshData();
        }
    }
}