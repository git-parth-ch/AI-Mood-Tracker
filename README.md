# 🌤️ Java Swing Mood Tracker with AI Analysis

## 🧠 Description

This is a desktop application built using **Java Swing** for tracking your daily mood.  
It allows users to log their **mood rating (1–5)**, select associated **feelings**, and add **textual notes**.  
The application visualizes mood trends over time using **charts** and integrates with a **local AI model (via Jan.ai)** to provide insightful analysis and encouragement based on daily entries.

The app features a **modern look and feel** using the **FlatLaf** library and provides **data export** functionality to CSV.

---

## ✨ Features

### 📝 Daily Mood Logging
- Record mood rating (**1–5**)
- Select multiple feelings (e.g., _Happy_, _Sad_, _Anxious_, _Productive_)
- Add detailed notes

### 📅 Manual Date Entry
- Log entries for past dates

### 📊 Data Visualization
- **Stacked bar chart:** Shows frequency and combination of feelings each day  
- **Line chart:** Displays overall mood trend over time  
- **Scrollable chart view:** Handles many entries without clutter

### 🔍 Entry Details
- View detailed information for any selected past entry

### 🤖 AI Analysis (Offline)
Connects to a locally running **Jan.ai** server to provide:
- A **summary** of the day based on your notes  
- **Validation and acknowledgment** of recorded feelings  
- **Empathetic encouragement and motivation**, especially for low-rated or negative days  
- Supportive **emojis** in AI feedback

### 💻 Modern UI
- Uses **FlatLaf** for a clean, light-themed interface

### 📂 Data Export
- Export all mood data to a **CSV** file

### 💾 Local Database
- Uses **SQLite** (`moodtracking.db`) to store data locally

---

## 🧱 Project Structure

```plaintext
MoodTrackerProject/
├── lib/                     # External .jar libraries
│   ├── flatlaf-3.4.1.jar
│   ├── xchart-3.8.7.jar
│   ├── sqlite-jdbc-3.45.3.0.jar
│   └── json-20240303.jar
├── src/                     # Source code
│   └── com/
│       └── moodtracker/
│           ├── db/          # Database management
│           │   └── DatabaseManager.java
│           ├── main/        # Main application entry point
│           │   └── MainApp.java
│           ├── model/       # Data models and business logic
│           │   ├── MoodEntry.java
│           │   └── MoodService.java
│           ├── service/     # AI service interaction
│           │   └── AIAnalyzerService.java
│           ├── ui/          # User Interface components
│           │   ├── AnalyticsPanel.java
│           │   ├── MainFrame.java
│           │   ├── MoodPanel.java
│           │   └── UIConstants.java
│           └── util/        # Utility classes (CSV export)
│               └── CsvExporter.java
├── moodtracking.db          # SQLite database file (created automatically)
└── LaunchMoodTracker.bat    # Script to compile and run the app
```

## ⚙️ Setup and Running

### ✅ Prerequisites

- **Java Development Kit (JDK)** (version 11 or higher recommended)  
- **Jan.ai** application installed  

---

### 🪜 Steps

#### 1. Clone or Download

- Get the project files onto your computer.

#### 2. Setup Jan.ai

1. Run `jan_setup.exe` (or equivalent installer for your OS).  
2. Open the **Jan** application.  
3. Go to the **"Explore"** or **"Models"** tab and download: the ```Jan-v1-4B-Q4_K_M``` model.
4. Go to the main Chat screen in Jan and select the ```Jan-v1-4B-Q4_K_M``` model from the dropdown to ensure it's loaded into memory. Wait for any loading indicators to finish.
5. Navigate to **Settings > Local API Server**.
6. Set a personal **API Key** (copy this key, you'll need it in the Mood Tracker app).
7. Click **Start Server**. Ensure it says "Server is running".

#### 3. Run the Mood Tracker:

1. Navigate to the root ```MoodTrackerProject``` folder in your terminal or file explorer.

2. Double-click the ```LaunchMoodTracker.bat``` file. This script will automatically compile the Java source code and then run the application.

3. Note: The first time you run it, your firewall might ask for permission for "Java(TM) Platform SE Binary". You must Allow access for the AI feature to connect to the Jan server.

##
### Basic Usage

#### 1. Log Mood:

- Go to the "Log Mood" tab.
- Enter the date (YYYY-MM-DD format).
- Use the slider to set your mood rating (1-5).
- Select one or more feelings that apply.
- Optionally, add notes about your day in the text area.
- Click "Save Mood".

#### 2. View Analytics:

- Go to the "Analytics" tab.
- The charts will display your mood history. You can scroll horizontally if you have many entries.
- The list on the right shows all your entries (newest first). Click an entry to see its details below the list.

#### 3. Use AI Analyzer:

- Go to the "Analytics" tab.
- In the "AI Configuration" section on the right:
  - Paste your Jan API Key into the corresponding field.
  - Ensure the API Endpoint Path is set correctly (default: ```/v1/chat/completions```).
  - Ensure the Model Name is set correctly (default: ```Jan-v1-4B-Q4_K_M```).
- Select an entry from the list on the right.
- Click the "Analyze Selected Day" button.
- The AI's analysis and encouragement will appear in the "AI Day Analysis" panel below the charts.

#### 4. Export Data:

- Go to the "Log Mood" tab.
- Click the "Export All to CSV" button.
- Choose a location and filename to save your data.
