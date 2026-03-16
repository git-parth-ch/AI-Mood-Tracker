package com.moodtracker.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

public class UIConstants {

    public static Color BACKGROUND_LIGHT = new Color(0xF4F7F5);
    public static Color PANEL_BACKGROUND = new Color(0xFFFFFF);
    public static Color BORDER_COLOR = new Color(0xDEE2E6);
    public static Color TEXT_PRIMARY = new Color(0x212529);
    public static Color TEXT_SECONDARY = new Color(0x495057);
    public static Color BUTTON_PRIMARY = new Color(0x007BFF);
    public static Color ACCENT_COLOR = new Color(0x28A745);

    public static Color BACKGROUND_COLOR = BACKGROUND_LIGHT;
    public static Color PANEL_BACKGROUND_COLOR = PANEL_BACKGROUND;
    public static Color TEXT_COLOR = TEXT_PRIMARY;

    public static void setDarkMode(boolean dark) {
        if (dark) {
            BACKGROUND_LIGHT = new Color(0x2B2D30);
            PANEL_BACKGROUND = new Color(0x3C3F41);
            BORDER_COLOR = new Color(0x555555);
            TEXT_PRIMARY = new Color(0xBBBBBB);
            TEXT_SECONDARY = new Color(0x888888);
            BUTTON_PRIMARY = new Color(0x1A6B3D);
            ACCENT_COLOR = new Color(0x1F8A43);
        } else {
            BACKGROUND_LIGHT = new Color(0xF4F7F5);
            PANEL_BACKGROUND = new Color(0xFFFFFF);
            BORDER_COLOR = new Color(0xDEE2E6);
            TEXT_PRIMARY = new Color(0x212529);
            TEXT_SECONDARY = new Color(0x495057);
            BUTTON_PRIMARY = new Color(0x007BFF);
            ACCENT_COLOR = new Color(0x28A745);
        }
        BACKGROUND_COLOR = BACKGROUND_LIGHT;
        PANEL_BACKGROUND_COLOR = PANEL_BACKGROUND;
        TEXT_COLOR = TEXT_PRIMARY;
    }

    public static final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public static final String[] FEELINGS = {
            "Happy", "Sad", "Anxious", "Calm", "Angry", "Tired", "Productive", "Stressed"
    };

    public static final Map<String, Color> FEELING_COLORS = Map.of(
            "Happy", new Color(0xFFD700),
            "Sad", new Color(0x1E90FF),
            "Anxious", new Color(0xFF8C00),
            "Calm", new Color(0x3CB371),
            "Angry", new Color(0xDC143C),
            "Tired", new Color(0x9370DB),
            "Productive", new Color(0x007BFF),
            "Stressed", new Color(0xFF4500));
}