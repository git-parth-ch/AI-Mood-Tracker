@echo off
echo.
echo ===================================
echo   Mood Tracker Project
echo ===================================
echo.

setlocal

REM Define classpath
set CP=lib\flatlaf-3.4.1.jar;lib\xchart-3.8.7.jar;lib\sqlite-jdbc-3.45.3.0.jar;lib\json-20240303.jar
echo [1] Setting classpath...

REM Clean previous build
if exist "out" (
echo [2] Cleaning previous build...
rmdir /s /q out
)
mkdir out

echo [3] Compiling .java files...
REM Compile all java files from all packages
javac -d out -cp "%CP%" src/com/moodtracker/main/*.java src/com/moodtracker/db/*.java src/com/moodtracker/model/*.java src/com/moodtracker/ui/*.java src/com/moodtracker/util/*.java src/com/moodtracker/service/*.java

IF %ERRORLEVEL% NEQ 0 (
echo.
echo ===================================
echo   COMPILE FAILED! See errors above.
echo ===================================
goto end
)

echo [4] Compilation successful.
echo [5] Running application...
echo.

REM Run the main application
java -cp "out;%CP%" com.moodtracker.main.MainApp

:end
echo.
echo Script finished. Press any key to exit.
pause > nul
endlocal