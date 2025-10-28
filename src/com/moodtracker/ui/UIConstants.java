package com.moodtracker.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

public class UIConstants {

    public static final Color BACKGROUND_LIGHT = new Color(0xF4F7F5); 
    public static final Color PANEL_BACKGROUND = new Color(0xFFFFFF); 
    public static final Color BORDER_COLOR = new Color(0xDEE2E6);     
    public static final Color TEXT_PRIMARY = new Color(0x212529);     
    public static final Color TEXT_SECONDARY = new Color(0x495057);   
    public static final Color BUTTON_PRIMARY = new Color(0x007BFF);   
    public static final Color ACCENT_COLOR = new Color(0x28A745);     

    public static final Color BACKGROUND_COLOR = BACKGROUND_LIGHT;
    public static final Color PANEL_BACKGROUND_COLOR = PANEL_BACKGROUND;
    public static final Color TEXT_COLOR = TEXT_PRIMARY;

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
        "Tired", new Color(0x696969),   
        "Productive", new Color(0x007BFF), 
        "Stressed", new Color(0xFF4500)  
    );
}