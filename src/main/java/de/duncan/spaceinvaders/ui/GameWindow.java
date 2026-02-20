package de.duncan.spaceinvaders.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Das Hauptfenster des Spiels — ein JFrame, das das GamePanel enthält.
 * 
 * SWING-HIERARCHIE:
 * ─────────────────
 *   JFrame (Fenster mit Titelleiste, Schließen-Button)
 *     └── GamePanel (JPanel — hier wird das Spiel gezeichnet)
 * 
 * JFrame ist der "Container" — er stellt das Betriebssystem-Fenster bereit.
 * JPanel ist die "Leinwand" — hier passiert das eigentliche Zeichnen.
 * 
 * WARUM setResizable(false)?
 * ──────────────────────────
 * Unser Spiel hat feste Pixelwerte für Positionen und Größen.
 * Wenn das Fenster vergrößert würde, wäre alles verschoben.
 * In einem professionellen Spiel würde man relative Koordinaten nutzen,
 * aber für ein Portfolio-Projekt ist eine feste Größe pragmatischer.
 */
public class GameWindow extends JFrame {

    private final GamePanel gamePanel;

    /**
     * Erstellt und konfiguriert das Spielfenster.
     */
    public GameWindow() {
        // Fenstertitel
        setTitle("Space Invaders — Duncan's Portfolio");

        // Fenster kann nicht in der Größe verändert werden
        setResizable(false);

        // Programm beenden wenn Fenster geschlossen wird
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // GamePanel erstellen und zum Fenster hinzufügen
        gamePanel = new GamePanel();
        add(gamePanel);

        // pack() passt die Fenstergröße an die preferredSize des Panels an
        // Das ist besser als setSize(), weil es die Titelleiste berücksichtigt
        pack();

        // Fenster mittig auf dem Bildschirm positionieren
        setLocationRelativeTo(null);

        // WindowListener für sauberes Aufräumen beim Schließen
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.stopGameLoop();
            }
        });
    }

    /**
     * Macht das Fenster sichtbar und startet die Game Loop.
     * 
     * WICHTIG: requestFocusInWindow() muss NACH setVisible(true) aufgerufen
     * werden, sonst bekommt das Panel keinen Fokus und KeyEvents kommen
     * nicht an.
     */
    public void showAndStart() {
        setVisible(true);
        gamePanel.startGameLoop();
    }
}
