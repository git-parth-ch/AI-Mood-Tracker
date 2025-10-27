package com.moodtracker.util;

import com.moodtracker.db.DatabaseManager;
import com.moodtracker.model.MoodEntry;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Utility class to export mood entries to a CSV file.
 */
public class CsvExporter {

    /**
     * Exports all mood entries to a CSV file.
     * @param filePath The absolute path of the file to save.
     * @return null on success, or an error message string on failure.
     */
    public static String exportToCsv(String filePath) {
        // Use DatabaseManager to get all entries
        DatabaseManager dbManager = new DatabaseManager();
        List<MoodEntry> entries;
        try {
            entries = dbManager.getAllEntries();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to read entries from database: " + e.getMessage();
        }

        // Write to the CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            
            // Write CSV Header
            writer.println("id,entry_date,rating,feelings,notes");

            // Write each entry as a row
            for (MoodEntry entry : entries) {
                writer.printf("%d,%s,%d,%s,%s\n",
                        entry.getId(),
                        escapeCsv(entry.getEntryDate()),
                        entry.getRating(),
                        escapeCsv(entry.getFeelings()),
                        escapeCsv(entry.getNotes())
                );
            }
            
            return null; // Success

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to write file: " + e.getMessage();
        }
    }

    /**
     * Escapes special characters for CSV format (quotes and commas).
     */
    private static String escapeCsv(String data) {
        if (data == null) {
            return "";
        }
        // If data contains a comma, newline, or quote, wrap it in double quotes.
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            // Escape existing double quotes by doubling them up
            data = data.replace("\"", "\"\"");
            return "\"" + data + "\"";
        }
        return data;
    }
}

