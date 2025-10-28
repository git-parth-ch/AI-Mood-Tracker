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

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:moodtracking.db";

    public DatabaseManager() {
        createTable();
    }

    private void createTable() {

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

    public boolean insertEntry(String entryDate, int rating, String feelings, String notes) {
        String sql = "INSERT INTO mood_entries(entry_date, rating, feelings, notes) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entryDate);
            pstmt.setInt(2, rating);
            pstmt.setString(3, feelings);
            pstmt.setString(4, notes);
            pstmt.executeUpdate();
            return true; 
        } catch (SQLException e) {
            System.err.println("Error inserting entry: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
    }

    public List<MoodEntry> getAllEntries() {
        List<MoodEntry> entries = new ArrayList<>();

        String sql = "SELECT * FROM mood_entries ORDER BY entry_date ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                entries.add(new MoodEntry(
                        rs.getInt("id"),
                        rs.getString("entry_date"), 
                        rs.getInt("rating"),
                        rs.getString("feelings"),
                        rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all entries: " + e.getMessage());
            e.printStackTrace();
            return null; 
        }
        return entries;
    }
}