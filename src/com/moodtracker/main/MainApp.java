package com.moodtracker.main;

import com.formdev.flatlaf.FlatLightLaf; // Import the Light theme
import com.moodtracker.db.DatabaseManager;
import com.moodtracker.ui.MainFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main application entry point.
 * Sets up the Look and Feel, initializes the database, and launches the main UI.
 */
public class MainApp {

    // Database connection string
    private static final String DB_URL = "jdbc:sqlite:moodtracking.db";

    public static void main(String[] args) {
        // --- 1. Set the Modern Look and Feel (FlatLaf Light) ---
        // This MUST be the very first thing done in main()
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
            e.printStackTrace();
        }

        // --- 2. Check and Create Database Table ---
        // We do this here at the start to ensure the file and table exist
        // before any other part of the app tries to access it.
        createTableIfNotExists();

        // --- 3. Launch the Main Application Window ---
        // SwingUtilities.invokeLater ensures all UI operations are done
        // on the Event Dispatch Thread (EDT), which is a Swing requirement.
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    /**
     * Creates the 'mood_entries' table if it does not already exist.
     * This is the same logic from the DatabaseManager's constructor,
     * moved here to ensure it runs on startup before the UI.
     */
    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS mood_entries ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " entry_date TEXT NOT NULL,"
                + " rating INTEGER NOT NULL,"
                + " feelings TEXT,"
                + " notes TEXT"
                + ");";

        // Using try-with-resources to ensure connection is closed
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database tables checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
}

