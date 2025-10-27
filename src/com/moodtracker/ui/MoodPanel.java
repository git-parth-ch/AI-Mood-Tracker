package com.moodtracker.ui;

import com.moodtracker.model.MoodService;
import com.moodtracker.util.CsvExporter; // Import CsvExporter
import com.moodtracker.ui.UIConstants; // *** FIX: Add missing import ***

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JPanel for logging a new mood entry.
 * Includes new color palette.
 */
public class MoodPanel extends JPanel {

    private final MoodService moodService;
    private JSlider moodSlider;
    private JTextArea notesArea;
    private JTextField dateField;
    private JLabel sliderValueLabel;
    private JCheckBox[] feelingCheckboxes;
    private JPanel feelingsPanel;

    public MoodPanel() {
        this.moodService = new MoodService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 40, 40));
        setBackground(UIConstants.BACKGROUND_LIGHT); // Set light background

        // --- Main Content Panel (GridBagLayout) ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.PANEL_BACKGROUND); // Slightly darker panel
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UIConstants.BORDER_COLOR, 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Title ---
        JLabel titleLabel = new JLabel("How are you feeling today?");
        titleLabel.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 26f));
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(titleLabel, gbc);

        // --- Date Field ---
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(createLabel("Date (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        dateField = new JTextField();
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setFont(UIConstants.MAIN_FONT.deriveFont(14f));
        dateField.setToolTipText("Enter the date in YYYY-MM-DD format");
        mainPanel.add(dateField, gbc);

        // --- Mood Slider ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        mainPanel.add(createLabel("Overall Rating:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        moodSlider = new JSlider(1, 5, 3);
        moodSlider.setFont(UIConstants.MAIN_FONT.deriveFont(12f));
        moodSlider.setPaintTicks(true);
        moodSlider.setPaintLabels(true);
        moodSlider.setMajorTickSpacing(1);
        moodSlider.setBackground(UIConstants.PANEL_BACKGROUND);
        mainPanel.add(moodSlider, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        sliderValueLabel = new JLabel("3 / 5");
        sliderValueLabel.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        sliderValueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        mainPanel.add(sliderValueLabel, gbc);

        moodSlider.addChangeListener(e -> sliderValueLabel.setText(moodSlider.getValue() + " / 5"));

        // --- Feelings Checkboxes ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(createLabel("I felt..."), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        createFeelingsPanel(); // This populates the feelingsPanel
        mainPanel.add(feelingsPanel, gbc);

        // --- Notes Area ---
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(createLabel("Notes:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.ipady = 80; // Make notes area taller
        notesArea = new JTextArea("Add some details...");
        notesArea.setFont(UIConstants.MAIN_FONT.deriveFont(14f));
        notesArea.setForeground(Color.GRAY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(new LineBorder(UIConstants.BORDER_COLOR, 1, true));
        notesArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (notesArea.getText().equals("Add some details...")) {
                    notesArea.setText("");
                    notesArea.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (notesArea.getText().isEmpty()) {
                    notesArea.setText("Add some details...");
                    notesArea.setForeground(Color.GRAY);
                }
            }
        });
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(notesScrollPane, gbc);
        gbc.ipady = 0; // Reset

        // --- Save Button ---
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveButton = new JButton("Save Today's Mood");
        saveButton.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        saveButton.setBackground(UIConstants.BUTTON_PRIMARY);
        saveButton.setForeground(Color.WHITE);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(220, 45));
        saveButton.addActionListener(e -> saveMood());
        mainPanel.add(saveButton, gbc);

        // --- Center Panel Alignment ---
        // This wrapper panel ensures the mainPanel stays centered and doesn't stretch
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UIConstants.BACKGROUND_LIGHT);
        centerWrapper.add(mainPanel, new GridBagConstraints());
        
        add(centerWrapper, BorderLayout.CENTER);

        // --- Footer Panel for Export Button ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UIConstants.BACKGROUND_LIGHT);
        JButton exportButton = new JButton("Export All to CSV");
        exportButton.setFont(UIConstants.MAIN_FONT.deriveFont(12f));
        exportButton.setToolTipText("Save all entries to a .csv file");
        exportButton.addActionListener(e -> exportData());
        footerPanel.add(exportButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 14f));
        label.setForeground(UIConstants.TEXT_SECONDARY);
        return label;
    }

    private void createFeelingsPanel() {
        feelingsPanel = new JPanel(new GridLayout(0, 2, 8, 8)); // 0 rows, 2 columns
        feelingsPanel.setBackground(UIConstants.PANEL_BACKGROUND);
        
        feelingCheckboxes = new JCheckBox[UIConstants.FEELINGS.length];
        for (int i = 0; i < UIConstants.FEELINGS.length; i++) {
            String feeling = UIConstants.FEELINGS[i];
            Color color = UIConstants.FEELING_COLORS.get(feeling);
            
            feelingCheckboxes[i] = new JCheckBox(feeling);
            feelingCheckboxes[i].setFont(UIConstants.MAIN_FONT.deriveFont(14f));
            feelingCheckboxes[i].setForeground(color.darker()); // Use the feeling color for the text
            feelingCheckboxes[i].setBackground(UIConstants.PANEL_BACKGROUND);
            feelingsPanel.add(feelingCheckboxes[i]);
        }
    }

    private List<String> getSelectedFeelings() {
        List<String> selected = new ArrayList<>();
        for (JCheckBox checkBox : feelingCheckboxes) {
            if (checkBox.isSelected()) {
                selected.add(checkBox.getText());
            }
        }
        return selected;
    }

    private void saveMood() {
        String entryDate = dateField.getText();
        int rating = moodSlider.getValue();
        List<String> feelingsList = getSelectedFeelings();
        String feelings = String.join(",", feelingsList); // Convert list to comma-separated string
        String notes = notesArea.getText();

        if (notes.equals("Add some details...")) {
            notes = ""; // Don't save placeholder text
        }

        // Validate date format (simple regex)
        if (!entryDate.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = moodService.addMoodEntry(entryDate, rating, feelings, notes);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Mood entry saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh analytics tab
            ((MainFrame) SwingUtilities.getWindowAncestor(this)).refreshAnalytics();
            
            // Reset fields
            notesArea.setText("Add some details...");
            notesArea.setForeground(Color.GRAY);
            moodSlider.setValue(3);
            for (JCheckBox checkBox : feelingCheckboxes) {
                checkBox.setSelected(false);
            }
            // Optionally, update date to current date again
            // dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save mood entry. Check console for errors.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Export");
        fileChooser.setSelectedFile(new File("mood_export_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // *** FIX: CsvExporter.exportToCsv now returns a String (error message) or null (success) ***
            String errorMessage = CsvExporter.exportToCsv(fileToSave.getAbsolutePath());
            
            if (errorMessage == null) {
                JOptionPane.showMessageDialog(this,
                        "Data exported successfully to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Export failed: " + errorMessage,
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

