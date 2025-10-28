package com.moodtracker.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private AnalyticsPanel analyticsPanel;

    public MainFrame() {
        setTitle("Daily Mood Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 14f));
        tabbedPane.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);

        MoodPanel moodPanel = new MoodPanel();
        analyticsPanel = new AnalyticsPanel(); 

        tabbedPane.addTab("  Log Mood  ", moodPanel);
        tabbedPane.addTab(" Analytics  ", analyticsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshAnalytics() {
        if (analyticsPanel != null) {
            analyticsPanel.refreshData();
        }
    }
}