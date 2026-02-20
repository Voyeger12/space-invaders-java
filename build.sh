#!/bin/bash
# ──────────────────────────────────────────
# Build & Run Script für Space Invaders
# Voraussetzung: Java 17+ installiert
# ──────────────────────────────────────────

set -e  # Script bei Fehlern abbrechen

echo "╔══════════════════════════════════╗"
echo "║  Space Invaders — Build & Run    ║"
echo "╚══════════════════════════════════╝"

# Ausgabeverzeichnis erstellen/leeren
echo "[1/3] Erstelle Build-Verzeichnis..."
rm -rf out
mkdir -p out

# Alle Java-Dateien kompilieren
echo "[2/3] Kompiliere Java-Dateien..."
find src -name "*.java" > sources.txt
javac -d out @sources.txt
rm sources.txt
echo "      ✓ Kompilierung erfolgreich!"

# Starten
echo "[3/3] Starte Spiel..."
echo ""
java -cp out de.duncan.spaceinvaders.Main
