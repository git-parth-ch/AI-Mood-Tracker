package com.moodtracker.ui;

import com.moodtracker.model.MoodEntry;
import com.moodtracker.model.MoodService;
import com.moodtracker.service.AIAnalyzerService; 
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import com.moodtracker.ui.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class AnalyticsPanel extends JPanel {

    private final MoodService moodService;
    private final AIAnalyzerService aiService; 
    private JPanel chartWrapperPanel; 
    private JScrollPane mainChartScroller; 
    private JTextPane detailsTextPane;
    private JList<MoodEntry> entryList;
    private DefaultListModel<MoodEntry> listModel;
    private JLabel loadingLabel;

    private JTextPane aiAnalysisArea; 
    private JButton analyzeButton; 
    private JLabel aiStatusLabel; 
    private JPasswordField apiKeyField; 
    private JTextField endpointPathField; 
    private JTextField modelNameField; 

    private static final int MIN_BAR_WIDTH = 60; 
    private static final int BAR_CHART_HEIGHT = 280; 
    private static final int LINE_CHART_HEIGHT = 180; 
    private static final int CHART_VERTICAL_GAP = 10; 

    public AnalyticsPanel() {
        this.moodService = new MoodService();
        this.aiService = new AIAnalyzerService(); 
        initComponents();
        refreshData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10)); 
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        chartWrapperPanel = new JPanel();
        chartWrapperPanel.setLayout(new BoxLayout(chartWrapperPanel, BoxLayout.Y_AXIS));
        chartWrapperPanel.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);

        loadingLabel = new JLabel("Loading chart data...", SwingConstants.CENTER);
        loadingLabel.setFont(UIConstants.MAIN_FONT.deriveFont(16f));

        JPanel initialLoadingPanel = new JPanel(new BorderLayout());
        initialLoadingPanel.add(loadingLabel, BorderLayout.CENTER);
        initialLoadingPanel.setOpaque(false); 
        chartWrapperPanel.add(initialLoadingPanel); 

        mainChartScroller = new JScrollPane(chartWrapperPanel);
        mainChartScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainChartScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        mainChartScroller.setBorder(BorderFactory.createEmptyBorder());
        mainChartScroller.setMinimumSize(new Dimension(300, BAR_CHART_HEIGHT + LINE_CHART_HEIGHT + CHART_VERTICAL_GAP));
        mainChartScroller.getViewport().setBackground(UIConstants.PANEL_BACKGROUND_COLOR); 

        JPanel chartScrollerWrapper = new JPanel(new BorderLayout());
        chartScrollerWrapper.setBackground(UIConstants.BACKGROUND_COLOR);
        chartScrollerWrapper.add(mainChartScroller, BorderLayout.CENTER); 
        chartScrollerWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Mood Charts ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f),
                UIConstants.TEXT_COLOR));

        JPanel aiOutputPanel = createAIOutputPanel(); 

        centerPanel.add(chartScrollerWrapper, BorderLayout.NORTH);
        centerPanel.add(aiOutputPanel, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new GridBagLayout()); 
        detailsPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        detailsPanel.setPreferredSize(new Dimension(320, 0)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(0, 0, 10, 0); 
        gbc.gridx = 0;
        gbc.weightx = 1.0; 

        JPanel listPanel = createListPanel();
        JPanel entryDetailsPanel = createEntryDetailsPanel();
        JPanel aiSettingsPanel = createAISettingsPanel(); 

        gbc.gridy = 0;
        gbc.weighty = 0.4; 
        detailsPanel.add(listPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.3; 
        detailsPanel.add(entryDetailsPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.3; 
        gbc.insets = new Insets(0, 0, 0, 0); 
        detailsPanel.add(aiSettingsPanel, gbc);

        add(centerPanel, BorderLayout.CENTER); 
        add(detailsPanel, BorderLayout.EAST);
    }

    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Select Entry ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f),
                UIConstants.TEXT_COLOR));

        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        entryList.setFont(UIConstants.MAIN_FONT.deriveFont(14f));
        entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entryList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof MoodEntry) {
                    MoodEntry entry = (MoodEntry) value;
                    setText(String.format("%s (Rating: %d/5)", entry.getEntryDate(), entry.getRating()));
                    setBackground(isSelected ? UIConstants.ACCENT_COLOR : UIConstants.PANEL_BACKGROUND_COLOR);
                    setForeground(isSelected ? Color.WHITE : UIConstants.TEXT_COLOR);
                }
                return c;
            }
        });

        JScrollPane listScrollPane = new JScrollPane(entryList);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        listPanel.add(listScrollPane, BorderLayout.CENTER);

        entryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MoodEntry selectedEntry = entryList.getSelectedValue();

                if (analyzeButton != null) {
                    if (selectedEntry != null) {
                        updateDetailsPane(selectedEntry);
                        analyzeButton.setEnabled(true); 

                        if (aiAnalysisArea != null) {
                             aiAnalysisArea.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + "; font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 10pt;'>Click 'Analyze' to get AI insights for the selected day.</p></body></html>");
                        }
                    } else {
                        analyzeButton.setEnabled(false);
                        updateDetailsPane(null); 
                    }
                }
            }
        });

        return listPanel;
    }

    private JPanel createEntryDetailsPanel() {
        JPanel detailsWrapper = new JPanel(new BorderLayout());
        detailsWrapper.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        detailsWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Entry Details ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f),
                UIConstants.TEXT_COLOR));

        detailsTextPane = new JTextPane();
        detailsTextPane.setContentType("text/html");
        detailsTextPane.setEditable(false);
        detailsTextPane.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        detailsTextPane.setForeground(UIConstants.TEXT_COLOR);
        detailsTextPane.setFont(UIConstants.MAIN_FONT.deriveFont(14f));
        detailsTextPane.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + "; font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 10pt;'>Select an entry from the list above to see details.</p></body></html>");

        JScrollPane detailsScrollPane = new JScrollPane(detailsTextPane);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        detailsWrapper.add(detailsScrollPane, BorderLayout.CENTER);
        return detailsWrapper;
    }

    private JPanel createAISettingsPanel() {
        JPanel aiSettingsPanel = new JPanel(new BorderLayout(5, 5)); 
        aiSettingsPanel.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        aiSettingsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " AI Configuration ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f),
                UIConstants.TEXT_COLOR));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST; 

        JLabel keyLabel = new JLabel("Jan API Key:");
        keyLabel.setFont(UIConstants.MAIN_FONT.deriveFont(12f));
        keyLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0; 
        gbc.gridwidth = 1;
        fieldsPanel.add(keyLabel, gbc);

        apiKeyField = new JPasswordField(10);
        apiKeyField.setToolTipText("Get this from your Jan app's local server screen");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; 
        fieldsPanel.add(apiKeyField, gbc);

        JLabel pathLabel = new JLabel("Endpoint Path:");
        pathLabel.setFont(UIConstants.MAIN_FONT.deriveFont(12f));
        pathLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        fieldsPanel.add(pathLabel, gbc);

        endpointPathField = new JTextField("/v1/chat/completions"); 
        endpointPathField.setToolTipText("e.g., /v1/chat/completions or /v1/completions");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        fieldsPanel.add(endpointPathField, gbc);

        JLabel modelLabel = new JLabel("Model Name:");
        modelLabel.setFont(UIConstants.MAIN_FONT.deriveFont(12f));
        modelLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        fieldsPanel.add(modelLabel, gbc);

        modelNameField = new JTextField("Jan-v1-4B-Q4_K_M"); 
        modelNameField.setToolTipText("The exact Model ID from your Jan app");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        fieldsPanel.add(modelNameField, gbc);

        aiSettingsPanel.add(fieldsPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(UIConstants.PANEL_BACKGROUND_COLOR);
        analyzeButton = new JButton("Analyze Selected Day");
        analyzeButton.setFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 14f));
        analyzeButton.setBackground(UIConstants.ACCENT_COLOR); 
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setOpaque(true);
        analyzeButton.setBorderPainted(false);
        analyzeButton.setEnabled(false); 
        analyzeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanel.add(analyzeButton);
        aiSettingsPanel.add(buttonPanel, BorderLayout.CENTER);

        aiStatusLabel = new JLabel(" ");
        aiStatusLabel.setFont(UIConstants.MAIN_FONT.deriveFont(Font.ITALIC, 11f));
        aiStatusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        aiStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aiSettingsPanel.add(aiStatusLabel, BorderLayout.SOUTH);

        analyzeButton.addActionListener(e -> performAIAnalysis());

        return aiSettingsPanel;
    }

    private JPanel createAIOutputPanel() {
        JPanel aiOutputWrapper = new JPanel(new BorderLayout());
        aiOutputWrapper.setBackground(UIConstants.BACKGROUND_COLOR); 
        aiOutputWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " AI Day Analysis ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f),
                UIConstants.TEXT_COLOR));
        aiOutputWrapper.setPreferredSize(new Dimension(0, 180)); 

        aiAnalysisArea = new JTextPane();
        aiAnalysisArea.setContentType("text/html");
        aiAnalysisArea.setEditable(false);
        aiAnalysisArea.setBackground(UIConstants.BACKGROUND_COLOR); 
        aiAnalysisArea.setForeground(UIConstants.TEXT_COLOR);
        aiAnalysisArea.setFont(UIConstants.MAIN_FONT.deriveFont(13f));
        aiAnalysisArea.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + "; font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 10pt;'>Select an entry and click 'Analyze' in the right panel.</p></body></html>");

        JScrollPane aiScrollPane = new JScrollPane(aiAnalysisArea);
        aiScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        aiOutputWrapper.add(aiScrollPane, BorderLayout.CENTER);

        return aiOutputWrapper;
    }

    private void performAIAnalysis() {
        MoodEntry selectedEntry = entryList.getSelectedValue();

        String apiKey = new String(apiKeyField.getPassword());
        String endpointPath = endpointPathField.getText();
        String modelName = modelNameField.getText();

        if (apiKey.trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "API Key is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
             aiStatusLabel.setText("Error: API Key missing.");
            return;
        }
        if (endpointPath.trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "API Endpoint Path is required (e.g., /v1/chat/completions).", "Input Error", JOptionPane.WARNING_MESSAGE);
             aiStatusLabel.setText("Error: Endpoint Path missing.");
            return;
        }
        if (modelName.trim().isEmpty()) {
             JOptionPane.showMessageDialog(this, "Model Name is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
             aiStatusLabel.setText("Error: Model Name missing.");
            return;
        }
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(this, "Please select an entry from the list first.", "Input Error", JOptionPane.WARNING_MESSAGE);
            aiStatusLabel.setText("Error: No entry selected.");
            return;
        }

        aiStatusLabel.setText("Contacting AI service...");
        aiAnalysisArea.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + ";'><i>Loading AI analysis...</i></p></body></html>");
        analyzeButton.setEnabled(false); 

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {

                aiService.setApiKey(apiKey);
                aiService.setEndpointPath(endpointPath);
                aiService.setModelName(modelName);
                return aiService.analyzeMoodEntry(selectedEntry); 
            }

            @Override
            protected void done() {
                try {
                    String aiHtmlResponse = get();
                    aiAnalysisArea.setText(aiHtmlResponse); 
                    aiAnalysisArea.setCaretPosition(0); 
                    aiStatusLabel.setText("Analysis complete.");
                } catch (Exception e) {
                    e.printStackTrace();

                    aiAnalysisArea.setText("<html><body><p style='color: red;'><b>Error:</b> " + e.getMessage() + "</p></body></html>");
                    aiStatusLabel.setText("Error during analysis.");
                }
                analyzeButton.setEnabled(true); 
            }
        };
        worker.execute();
    }

    public void refreshData() {

        chartWrapperPanel.removeAll();
        chartWrapperPanel.setLayout(new BorderLayout()); 
        chartWrapperPanel.add(loadingLabel, BorderLayout.CENTER);
        chartWrapperPanel.revalidate();
        chartWrapperPanel.repaint();

        SwingWorker<List<MoodEntry>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MoodEntry> doInBackground() {
                return moodService.getAllEntries();
            }

            @Override
            protected void done() {
                try {
                    List<MoodEntry> entries = get();

                    chartWrapperPanel.removeAll(); 

                    chartWrapperPanel.setLayout(new BoxLayout(chartWrapperPanel, BoxLayout.Y_AXIS));

                    if (entries != null && !entries.isEmpty()) {
                        updateChart(entries); 
                        listModel.clear();
                        for (int i = entries.size() - 1; i >= 0; i--) { 
                            listModel.addElement(entries.get(i));
                        }
                        if (listModel.getSize() > 0) {
                            entryList.setSelectedIndex(0); 
                        }
                    } else {
                        showNoDataMessage(); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showNoDataMessage(); 
                } finally {

                    chartWrapperPanel.revalidate();
                    chartWrapperPanel.repaint();

                    if (mainChartScroller != null) {
                        mainChartScroller.revalidate();
                        mainChartScroller.repaint();
                    }
                }
            }
        };
        worker.execute();
    }

    private void updateDetailsPane(MoodEntry entry) {
         if (entry == null) {
              detailsTextPane.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + "; font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 10pt;'>No entry selected.</p></body></html>");
              detailsTextPane.setCaretPosition(0);
              return;
         }

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: ").append(UIConstants.MAIN_FONT.getFamily()).append("; font-size: 11pt; padding: 5px; color: ").append(String.format("#%02x%02x%02x", UIConstants.TEXT_COLOR.getRed(), UIConstants.TEXT_COLOR.getGreen(), UIConstants.TEXT_COLOR.getBlue())).append(";'>");
        sb.append("<p><b>Date:</b> ").append(entry.getEntryDate()).append("</p>");
        sb.append("<p><b>Rating:</b> ").append(entry.getRating()).append(" / 5</p>");
        sb.append("<p><b>Feelings:</b></p><ul>");

        String feelingsString = entry.getFeelings();
        if (feelingsString != null && !feelingsString.isEmpty()) {
            List<String> feelingsList = Arrays.asList(feelingsString.split(","));
            for (String feeling : feelingsList) {
                Color color = UIConstants.FEELING_COLORS.getOrDefault(feeling.trim(), Color.BLACK);
                String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                sb.append("<li><span style='color: ").append(hexColor).append(";'><b>").append(feeling.trim()).append("</b></span></li>");
            }
        } else {
            sb.append("<li>None specified</li>");
        }
        sb.append("</ul>");
        sb.append("<p><b>Notes:</b></p><p style='width: 200px;'>").append(entry.getNotes().isEmpty() ? "<i>No notes.</i>" : entry.getNotes().replaceAll("\n", "<br>")).append("</p>");
        sb.append("</body></html>");
        detailsTextPane.setText(sb.toString());
        detailsTextPane.setCaretPosition(0);
    }

    private void showNoDataMessage() {
        chartWrapperPanel.removeAll(); 
        chartWrapperPanel.setLayout(new BorderLayout()); 
        JLabel noDataLabel = new JLabel("No mood entries found. Log a mood to see analytics.", SwingConstants.CENTER);
        noDataLabel.setFont(UIConstants.MAIN_FONT.deriveFont(16f));
        noDataLabel.setForeground(Color.GRAY);
        chartWrapperPanel.add(noDataLabel, BorderLayout.CENTER);

        listModel.clear();
         updateDetailsPane(null); 
        if (aiAnalysisArea != null) {
              aiAnalysisArea.setText("<html><body><p style='padding: 5px; color: " + String.format("#%02x%02x%02x", UIConstants.TEXT_SECONDARY.getRed(), UIConstants.TEXT_SECONDARY.getGreen(), UIConstants.TEXT_SECONDARY.getBlue()) + "; font-family: " + UIConstants.MAIN_FONT.getFamily() + "; font-size: 10pt;'>No data available.</p></body></html>");
        }
        if (analyzeButton != null) analyzeButton.setEnabled(false);
    }

    private void updateChart(List<MoodEntry> entries) {

        List<String> dateLabels = new ArrayList<>();
        List<Double> moodRatings = new ArrayList<>();
        Map<String, List<Double>> feelingsData = new LinkedHashMap<>();

        for (String feeling : UIConstants.FEELINGS) {
            feelingsData.put(feeling, new ArrayList<>());
        }

        for (MoodEntry entry : entries) {
            dateLabels.add(entry.getEntryDate());
            moodRatings.add((double) entry.getRating());

            String feelingsString = entry.getFeelings();
            List<String> entryFeelings = (feelingsString != null && !feelingsString.isEmpty())
                    ? Arrays.asList(feelingsString.split(","))
                    : new ArrayList<>();

            for (String feelingKey : UIConstants.FEELINGS) {
                boolean hasFeeling = false;
                for (String entryFeeling : entryFeelings) {
                    if (entryFeeling.trim().equalsIgnoreCase(feelingKey)) {
                        hasFeeling = true;
                        break;
                    }
                }
                feelingsData.get(feelingKey).add(hasFeeling ? 1.0 : 0.0);
            }
        }

        CategoryChart barChart = new CategoryChartBuilder()
                .width(800).height(BAR_CHART_HEIGHT) 
                .title("Daily Feelings")
                .yAxisTitle("Feelings Count")
                .build();

        barChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        barChart.getStyler().setLegendLayout(Styler.LegendLayout.Horizontal); 
        barChart.getStyler().setStacked(true);
        barChart.getStyler().setAxisTickLabelsFont(UIConstants.MAIN_FONT.deriveFont(10f));
        barChart.getStyler().setAxisTitleFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 12f));
        barChart.getStyler().setChartTitleFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        barChart.getStyler().setLegendFont(UIConstants.MAIN_FONT.deriveFont(10f)); 
        barChart.getStyler().setChartBackgroundColor(UIConstants.PANEL_BACKGROUND_COLOR);
        barChart.getStyler().setPlotBackgroundColor(UIConstants.BACKGROUND_COLOR);
        barChart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230));
        barChart.getStyler().setXAxisLabelRotation(90);
        barChart.getStyler().setAvailableSpaceFill(0.95);
        barChart.getStyler().setXAxisTicksVisible(false); 

        for (String feeling : UIConstants.FEELINGS) {
            CategorySeries series = barChart.addSeries(feeling,
                    dateLabels, 
                    new ArrayList<>(feelingsData.get(feeling).stream().map(Number.class::cast).collect(Collectors.toList()))); 

            series.setMarker(SeriesMarkers.NONE);
            series.setFillColor(UIConstants.FEELING_COLORS.get(feeling));
        }

        CategoryChart lineChart = new CategoryChartBuilder()
                .width(800).height(LINE_CHART_HEIGHT) 
                .title("Overall Mood Rating")
                .xAxisTitle("Date")
                .yAxisTitle("Rating (1-5)")
                .build();

        lineChart.getStyler().setLegendVisible(false);
        lineChart.getStyler().setYAxisMin(0.0);
        lineChart.getStyler().setYAxisMax(5.5);
        lineChart.getStyler().setAxisTickLabelsFont(UIConstants.MAIN_FONT.deriveFont(10f));
        lineChart.getStyler().setAxisTitleFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 12f));
        lineChart.getStyler().setChartTitleFont(UIConstants.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        lineChart.getStyler().setChartBackgroundColor(UIConstants.PANEL_BACKGROUND_COLOR);
        lineChart.getStyler().setPlotBackgroundColor(UIConstants.BACKGROUND_COLOR);
        lineChart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230));
        lineChart.getStyler().setXAxisLabelRotation(90);

        CategorySeries lineSeries = lineChart.addSeries("Mood Rating",
                dateLabels, 
                new ArrayList<>(moodRatings.stream().map(Number.class::cast).collect(Collectors.toList()))); 

        lineSeries.setChartCategorySeriesRenderStyle(CategorySeries.CategorySeriesRenderStyle.Line);
        lineSeries.setMarker(SeriesMarkers.CIRCLE);
        lineSeries.setMarkerColor(Color.BLACK);
        lineSeries.setLineColor(Color.BLACK);

        int preferredWidth = Math.max(entries.size() * MIN_BAR_WIDTH, 300); 

        JPanel barChartWrapper = new JPanel(new BorderLayout());
        barChartWrapper.setOpaque(false);
        XChartPanel<CategoryChart> barChartPanel = new XChartPanel<>(barChart);

        barChartPanel.setMinimumSize(new Dimension(300, BAR_CHART_HEIGHT)); 
        barChartPanel.setBorder(BorderFactory.createEmptyBorder());
        barChartWrapper.add(barChartPanel, BorderLayout.CENTER);

        barChartWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, BAR_CHART_HEIGHT + 30)); 

        JPanel lineChartWrapper = new JPanel(new BorderLayout());
        lineChartWrapper.setOpaque(false);
        XChartPanel<CategoryChart> lineChartPanel = new XChartPanel<>(lineChart);

        lineChartPanel.setMinimumSize(new Dimension(300, LINE_CHART_HEIGHT)); 
        lineChartPanel.setBorder(BorderFactory.createEmptyBorder());
        lineChartWrapper.add(lineChartPanel, BorderLayout.CENTER);

        lineChartWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, LINE_CHART_HEIGHT));

        chartWrapperPanel.add(barChartWrapper);

        chartWrapperPanel.add(Box.createRigidArea(new Dimension(0, CHART_VERTICAL_GAP)));
        chartWrapperPanel.add(lineChartWrapper);

        chartWrapperPanel.setPreferredSize(new Dimension(preferredWidth, BAR_CHART_HEIGHT + LINE_CHART_HEIGHT + CHART_VERTICAL_GAP + 30)); 

        SwingUtilities.invokeLater(() -> {
             if (mainChartScroller != null) { 

                 mainChartScroller.revalidate();
                 mainChartScroller.repaint();

                 JScrollBar horizontalScrollBar = mainChartScroller.getHorizontalScrollBar();
                 horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
             }
        });

    }
}