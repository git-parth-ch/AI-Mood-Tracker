package com.moodtracker.model;

import com.moodtracker.db.DatabaseManager;

import java.util.Collections;
import java.util.List;

public class MoodService {

    private final DatabaseManager dbManager;

    public MoodService() {

        this.dbManager = new DatabaseManager();
    }

    public boolean addMoodEntry(String entryDate, int rating, String feelings, String notes) {

        return dbManager.insertEntry(entryDate, rating, feelings, notes);
    }

    public List<MoodEntry> getAllEntries() {

        List<MoodEntry> entries = dbManager.getAllEntries();
        if (entries == null) {

            return Collections.emptyList();
        }

        return entries;
    }
}