package com.moodtracker.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

/**
 * Shared constants for the UI, including fonts, colors, and static data.
 */
public class UIConstants {

    // --- NEW COLOR PALETTE ---
    // Switched to a light, airy theme with a mint-green/blue accent
    public static final Color BACKGROUND_LIGHT = new Color(0xF4F7F5); // Very light green-gray
    public static final Color PANEL_BACKGROUND = new Color(0xFFFFFF); // White
    public static final Color BORDER_COLOR = new Color(0xDEE2E6);     // Light gray
    public static final Color TEXT_PRIMARY = new Color(0x212529);     // Dark gray/black
    public static final Color TEXT_SECONDARY = new Color(0x495057);   // Medium gray
    public static final Color BUTTON_PRIMARY = new Color(0x007BFF);   // Blue
    public static final Color ACCENT_COLOR = new Color(0x28A745);     // Green

    // --- ALIASES TO FIX COMPILATION ERRORS ---
    // These point to the new variables so older code still works.
    public static final Color BACKGROUND_COLOR = BACKGROUND_LIGHT;
    public static final Color PANEL_BACKGROUND_COLOR = PANEL_BACKGROUND;
    public static final Color TEXT_COLOR = TEXT_PRIMARY;


    // --- FONT ---
    // Use a standard, clean font
    public static final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // --- FEELINGS ---
    public static final String[] FEELINGS = {
        "Happy", "Sad", "Anxious", "Calm", "Angry", "Tired", "Productive", "Stressed"
    };

    // --- FEELING COLORS ---
    // Assign specific colors to each feeling for the chart
    public static final Map<String, Color> FEELING_COLORS = Map.of(
        "Happy", new Color(0xFFD700), // Gold
        "Sad", new Color(0x1E90FF),     // Dodger Blue
        "Anxious", new Color(0xFF8C00), // Dark Orange
        "Calm", new Color(0x3CB371),    // Medium Sea Green
        "Angry", new Color(0xDC143C),   // Crimson
        "Tired", new Color(0x696969),   // Dim Gray
        "Productive", new Color(0x007BFF), // Blue (same as button)
        "Stressed", new Color(0xFF4500)  // Orange-Red
    );
}

