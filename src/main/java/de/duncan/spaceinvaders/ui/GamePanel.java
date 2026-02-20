package de.duncan.spaceinvaders.ui;

import de.duncan.spaceinvaders.engine.GameEngine;
import de.duncan.spaceinvaders.engine.InputHandler;

import javax.swing.*;
import java.awt.*;

/**
 * Das zentrale JPanel, das die Game Loop enthält und alles zusammenbringt.
 * 
 * GAME LOOP KONZEPT:
 * ──────────────────
 * Ein Spiel besteht aus einer Endlosschleife, die jeden Frame:
 * 1. Input einliest (was drückt der Spieler?)
 * 2. Spiellogik aktualisiert (Positionen, Kollisionen, Score)
 * 3. Alles neu zeichnet (Rendering)
 * 
 * FRAMERATE:
 * ──────────
 * Wir nutzen javax.swing.Timer für die Game Loop.
 * 1000ms / 60 FPS ≈ 16ms Intervall zwischen Frames.
 * 
 * Timer ist einfacher als ein eigener Thread und integriert sich
 * sauber mit Swing's Event Dispatch Thread (EDT). Das vermeidet
 * Threading-Probleme, die bei Swing schnell auftreten können.
 * 
 * DOUBLE BUFFERING:
 * ─────────────────
 * JPanel hat eingebautes Double Buffering (wenn setDoubleBuffered(true)).
 * Das bedeutet: Wir zeichnen erst auf einen unsichtbaren Buffer,
 * und erst wenn alles fertig ist, wird der Buffer auf den Bildschirm
 * kopiert. Ohne Double Buffering würde das Bild flackern.
 */
public class GamePanel extends JPanel {

    /** Spielfeldgröße in Pixeln */
    public static final int GAME_WIDTH = 700;
    public static final int GAME_HEIGHT = 650;

    /** Ziel-Framerate: 60 Bilder pro Sekunde */
    private static final int TARGET_FPS = 60;

    /** Millisekunden zwischen zwei Frames (1000 / 60 ≈ 16) */
    private static final int FRAME_DELAY = 1000 / TARGET_FPS;

    // Die drei Hauptkomponenten des Spiels
    private final GameEngine engine;
    private final InputHandler input;
    private final Renderer renderer;

    // Der Timer, der die Game Loop antreibt
    private final Timer gameLoopTimer;

    /**
     * Erstellt das GamePanel und initialisiert alle Komponenten.
     */
    public GamePanel() {
        // Feste Größe für das Spielfeld setzen
        // Alle drei Methoden setzen, damit der LayoutManager die Größe respektiert
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setMinimumSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setMaximumSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        // Double Buffering aktivieren (verhindert Flackern)
        setDoubleBuffered(true);

        // Hintergrundfarbe setzen
        setBackground(Color.BLACK);

        // Fokus auf dieses Panel setzen, damit KeyEvents ankommen
        setFocusable(true);

        // Komponenten initialisieren
        engine = new GameEngine(GAME_WIDTH, GAME_HEIGHT);
        input = new InputHandler();
        renderer = new Renderer(GAME_WIDTH, GAME_HEIGHT);

        // KeyListener registrieren
        addKeyListener(input);

        // ─── Game Loop Timer ─────────────────────────────────────
        // Der Timer ruft alle 16ms (≈60 FPS) die Lambda-Funktion auf.
        // actionPerformed wird auf dem EDT (Event Dispatch Thread) ausgeführt,
        // was Swing-Threading-Probleme vermeidet.
        gameLoopTimer = new Timer(FRAME_DELAY, e -> {
            // 1. Spiellogik aktualisieren
            engine.update(input);

            // 2. Neuzeichnen anfordern (ruft paintComponent auf)
            repaint();

            // 3. ESC → Programm beenden
            if (input.isEscapePressed() &&
                    engine.getState() != de.duncan.spaceinvaders.engine.GameState.PLAYING) {
                System.exit(0);
            }
        });
    }

    /**
     * Startet die Game Loop.
     * Wird von GameWindow aufgerufen, nachdem das Fenster sichtbar ist.
     */
    public void startGameLoop() {
        // Fokus anfordern, damit Tastatureingaben funktionieren
        requestFocusInWindow();
        gameLoopTimer.start();
    }

    /**
     * Stoppt die Game Loop.
     * Wichtig für sauberes Aufräumen beim Schließen.
     */
    public void stopGameLoop() {
        gameLoopTimer.stop();
    }

    /**
     * Wird von Swing aufgerufen, wenn das Panel neu gezeichnet werden muss.
     * 
     * WICHTIG: Wir überschreiben paintComponent (NICHT paint!).
     * paintComponent ist die richtige Methode für Custom Rendering in JPanel.
     * paint() würde auch Ränder und Kinder-Komponenten zeichnen.
     *
     * @param g Das Graphics-Objekt zum Zeichnen
     */
    @Override
    protected void paintComponent(Graphics g) {
        // super.paintComponent() räumt den Hintergrund auf
        super.paintComponent(g);

        // Graphics → Graphics2D casten für erweiterte Funktionalität
        // Graphics2D bietet Anti-Aliasing, Transformationen, bessere Formen
        Graphics2D g2d = (Graphics2D) g;

        // An den Renderer delegieren
        renderer.render(g2d, engine);
    }
}
