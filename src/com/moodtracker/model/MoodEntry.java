package com.moodtracker.model;

/**
 * Model class representing a single mood entry.
 * This is a Plain Old Java Object (POJO) or data class.
 */
public class MoodEntry {

    private int id;
    private String entryDate; // Changed from LocalDate to String
    private int rating;
    private String feelings;
    private String notes;

    /**
     * Constructor for creating a MoodEntry.
     *
     * @param id        The unique ID from the database.
     * @param entryDate The date of the entry (as a String).
     * @param rating    The mood rating (1-5).
     * @param feelings  A comma-separated string of feelings.
     * @param notes     The user's notes.
     */
    public MoodEntry(int id, String entryDate, int rating, String feelings, String notes) {
        this.id = id;
        this.entryDate = entryDate;
        this.rating = rating;
        this.feelings = feelings;
        this.notes = notes;
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public int getRating() {
        return rating;
    }

    public String getFeelings() {
        return feelings;
    }

    public String getNotes() {
        return notes;
    }

    /**
     * Override toString() to provide a nice string representation for the JList.
     *
     * @return A formatted string for display.
     */
    @Override
    public String toString() {
        // Format: "YYYY-MM-DD : Rating 4/5"
        return String.format("%s : Rating %d/5", entryDate, rating);
    }

    // We don't need setters for this application as entries are read-only once created.
}

