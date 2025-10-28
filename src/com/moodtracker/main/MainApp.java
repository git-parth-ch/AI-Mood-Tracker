package com.moodtracker.main;

import com.formdev.flatlaf.FlatLightLaf; 
import com.moodtracker.db.DatabaseManager;
import com.moodtracker.ui.MainFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp {

    private static final String DB_URL = "jdbc:sqlite:moodtracking.db";

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
            e.printStackTrace();
        }

        createTableIfNotExists();

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS mood_entries ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " entry_date TEXT NOT NULL,"
                + " rating INTEGER NOT NULL,"
                + " feelings TEXT,"
                + " notes TEXT"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database tables checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
}