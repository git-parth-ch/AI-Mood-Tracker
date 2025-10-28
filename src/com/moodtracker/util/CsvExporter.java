package com.moodtracker.util;

import com.moodtracker.db.DatabaseManager;
import com.moodtracker.model.MoodEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {

    public static String exportToCsv(String filePath) {

        DatabaseManager dbManager = new DatabaseManager();
        List<MoodEntry> entries;
        try {
            entries = dbManager.getAllEntries();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to read entries from database: " + e.getMessage();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("id,entry_date,rating,feelings,notes");

            for (MoodEntry entry : entries) {
                writer.printf("%d,%s,%d,%s,%s\n",
                        entry.getId(),
                        escapeCsv(entry.getEntryDate()),
                        entry.getRating(),
                        escapeCsv(entry.getFeelings()),
                        escapeCsv(entry.getNotes())
                );
            }

            return null; 

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to write file: " + e.getMessage();
        }
    }

    private static String escapeCsv(String data) {
        if (data == null) {
            return "";
        }

        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {

            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }
}