package com.moodtracker.model;

public class MoodEntry {

    private int id;
    private String entryDate; 
    private int rating;
    private String feelings;
    private String notes;

    public MoodEntry(int id, String entryDate, int rating, String feelings, String notes) {
        this.id = id;
        this.entryDate = entryDate;
        this.rating = rating;
        this.feelings = feelings;
        this.notes = notes;
    }

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

    @Override
    public String toString() {

        return String.format("%s : Rating %d/5", entryDate, rating);
    }

}