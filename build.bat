@echo off
REM ──────────────────────────────────────────
REM Build & Run Script für Space Invaders
REM Voraussetzung: Java 17+ installiert
REM ──────────────────────────────────────────

echo ========================================
echo   Space Invaders — Build ^& Run
echo ========================================

echo [1/3] Erstelle Build-Verzeichnis...
if exist out rmdir /s /q out
mkdir out

echo [2/3] Kompiliere Java-Dateien...
dir /s /B src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
echo       Kompilierung erfolgreich!

echo [3/3] Starte Spiel...
echo.
java -cp out de.duncan.spaceinvaders.Main
