package de.duncan.spaceinvaders;

import de.duncan.spaceinvaders.ui.GameWindow;

import javax.swing.*;

/**
 * Entry Point — hier startet das Spiel.
 * 
 * SWING & DER EVENT DISPATCH THREAD (EDT):
 * ─────────────────────────────────────────
 * Swing ist NICHT thread-safe. Das bedeutet: Alle Swing-Operationen
 * (Fenster erstellen, Buttons anzeigen, etc.) MÜSSEN auf dem sogenannten
 * "Event Dispatch Thread" (EDT) ausgeführt werden.
 * 
 * SwingUtilities.invokeLater() stellt sicher, dass unser Code auf dem EDT läuft.
 * Ohne das könnten schwer zu findende Bugs auftreten (Fenster friert ein,
 * Grafik-Artefakte, etc.).
 * 
 * Das ist eine BEST PRACTICE, die oft vergessen wird — hier machen wir es richtig!
 * 
 * ABLAUF:
 * ───────
 * 1. main() wird aufgerufen (auf dem Main-Thread)
 * 2. invokeLater() schiebt die Arbeit auf den EDT
 * 3. Auf dem EDT: GameWindow erstellen und anzeigen
 * 4. GameWindow startet die Game Loop (über javax.swing.Timer)
 * 5. Timer läuft auch auf dem EDT → alles thread-safe
 */
public class Main {

    /**
     * Programm-Einstiegspunkt.
     * Erstellt das Spielfenster auf dem Event Dispatch Thread.
     *
     * @param args Kommandozeilenargumente (werden nicht verwendet)
     */
    public static void main(String[] args) {
        // Swing-Fenster MÜSSEN auf dem EDT erstellt werden!
        SwingUtilities.invokeLater(() -> {
            // Look & Feel auf System-Standard setzen
            // (sieht auf Windows/macOS/Linux jeweils nativ aus)
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Kein kritischer Fehler — Fallback auf Standard-Look
                System.err.println("Konnte System Look & Feel nicht setzen: " + e.getMessage());
            }

            // Fenster erstellen und Spiel starten
            GameWindow window = new GameWindow();
            window.showAndStart();

            System.out.println("=================================");
            System.out.println("  Space Invaders gestartet!");
            System.out.println("  Druecke ENTER zum Spielen.");
            System.out.println("=================================");
        });
    }
}
