package com.moodtracker.db;

import com.moodtracker.model.MoodEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database interactions (CRUD operations) for the mood tracker.
 * Uses SQLite as the database engine.
 */
public class DatabaseManager {

    // Database connection string.
    // "moodtracking.db" will be created in the root folder where the app is run.
    private static final String DB_URL = "jdbc:sqlite:moodtracking.db";

    /**
     * Initializes the database connection and creates the table if it doesn't exist.
     * This constructor is called once by the MoodService.
     */
    public DatabaseManager() {
        createTable();
    }

    /**
     * Creates the 'mood_entries' table if it does not already exist.
     */
    private void createTable() {
        // SQL statement to create the table
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

    /**
     * Inserts a new mood entry into the database.
     *
     * @param entryDate The date string (YYYY-MM-DD).
     * @param rating    The mood rating (1-5).
     * @param feelings  A comma-separated string of feelings.
     * @param notes     The user-provided notes.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean insertEntry(String entryDate, int rating, String feelings, String notes) {
        String sql = "INSERT INTO mood_entries(entry_date, rating, feelings, notes) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entryDate);
            pstmt.setInt(2, rating);
            pstmt.setString(3, feelings);
            pstmt.setString(4, notes);
            pstmt.executeUpdate();
            return true; // Return true on success
        } catch (SQLException e) {
            System.err.println("Error inserting entry: " + e.getMessage());
            e.printStackTrace();
            return false; // Return false on failure
        }
    }

    /**
     * Retrieves all entries from the database, ordered by date.
     *
     * @return A List of MoodEntry objects.
     */
    public List<MoodEntry> getAllEntries() {
        List<MoodEntry> entries = new ArrayList<>();
        // Order by date to make the chart logical
        String sql = "SELECT * FROM mood_entries ORDER BY entry_date ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through the result set and create MoodEntry objects
            while (rs.next()) {
                entries.add(new MoodEntry(
                        rs.getInt("id"),
                        rs.getString("entry_date"), // Read as String
                        rs.getInt("rating"),
                        rs.getString("feelings"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all entries: " + e.getMessage());
            e.printStackTrace();
            return null; // Return null to indicate an error
        }
        return entries;
    }
}

