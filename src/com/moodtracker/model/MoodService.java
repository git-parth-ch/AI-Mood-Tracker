package com.moodtracker.model;

import com.moodtracker.db.DatabaseManager;

import java.util.Collections;
import java.util.List;

/**
 * Service layer that acts as an intermediary between the UI (View)
 * and the DatabaseManager (Data).
 * This decouples the UI from direct database logic.
 */
public class MoodService {

    private final DatabaseManager dbManager;

    public MoodService() {
        // Initialize the DatabaseManager.
        // This also triggers the database and table creation if they don't exist.
        this.dbManager = new DatabaseManager();
    }

    /**
     * Adds a new mood entry to the database.
     *
     * @param entryDate The date of the entry (as "YYYY-MM-DD" string).
     * @param rating    The mood rating (1-5).
     * @param feelings  A comma-separated string of feelings.
     * @param notes     The user's notes.
     * @return true if insertion was successful, false otherwise.
     */
    public boolean addMoodEntry(String entryDate, int rating, String feelings, String notes) {
        // In a larger app, we might do data validation or business logic here.
        // For this app, we pass it directly to the database manager.
        return dbManager.insertEntry(entryDate, rating, feelings, notes);
    }

    /**
     * Retrieves all mood entries from the database.
     *
     * @return A List of all MoodEntry objects.
     */
    public List<MoodEntry> getAllEntries() {
        // In a larger app, we might handle errors or caching here.
        List<MoodEntry> entries = dbManager.getAllEntries();
        if (entries == null) {
            // Return an empty list to prevent NullPointerExceptions in the UI
            return Collections.emptyList();
        }
        // The database query already orders them by date.
        return entries;
    }
}

