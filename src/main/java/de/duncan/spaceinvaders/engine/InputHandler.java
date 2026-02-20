package de.duncan.spaceinvaders.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Verarbeitet Tastatureingaben und speichert den aktuellen Zustand jeder Taste.
 * 
 * WARUM EIN EIGENER HANDLER?
 * ──────────────────────────
 * KeyListener-Events kommen asynchron (zu beliebigen Zeitpunkten).
 * Die Game Loop läuft aber synchron (genau 60x pro Sekunde).
 * 
 * Lösung: Der InputHandler merkt sich, WELCHE Tasten gerade gedrückt sind.
 * Die GameEngine fragt dann bei jedem Frame den aktuellen Zustand ab.
 * So gibt es keine Race Conditions oder verpasste Eingaben.
 * 
 * KONZEPT: KeyPressed vs. KeyReleased
 * ────────────────────────────────────
 * - keyPressed:  Taste wird gedrückt → Variable auf true
 * - keyReleased: Taste wird losgelassen → Variable auf false
 * 
 * Solange der Spieler z.B. die rechte Pfeiltaste HÄLT,
 * ist rightPressed = true und das Schiff bewegt sich jeden Frame.
 */
public class InputHandler implements KeyListener {

    // Aktueller Zustand der Steuerungstasten
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private boolean enterPressed;
    private boolean escapePressed;

    // Flag um zu erkennen, ob Enter EINMAL gedrückt wurde
    // (nicht gehalten — wichtig für Menü-Steuerung)
    private boolean enterJustPressed;

    /**
     * Wird aufgerufen wenn eine Taste GEDRÜCKT wird.
     * 
     * @param e Das KeyEvent mit Informationen über die gedrückte Taste
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A   -> leftPressed = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D  -> rightPressed = true;
            case KeyEvent.VK_SPACE                  -> spacePressed = true;
            case KeyEvent.VK_ENTER -> {
                if (!enterPressed) {
                    // Enter war vorher NICHT gedrückt → "just pressed"
                    enterJustPressed = true;
                }
                enterPressed = true;
            }
            case KeyEvent.VK_ESCAPE -> escapePressed = true;
            default -> { /* Andere Tasten ignorieren */ }
        }
    }

    /**
     * Wird aufgerufen wenn eine Taste LOSGELASSEN wird.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A   -> leftPressed = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D  -> rightPressed = false;
            case KeyEvent.VK_SPACE                  -> spacePressed = false;
            case KeyEvent.VK_ENTER                  -> enterPressed = false;
            case KeyEvent.VK_ESCAPE                 -> escapePressed = false;
            default -> { /* Andere Tasten ignorieren */ }
        }
    }

    /**
     * Wird aufgerufen wenn ein Zeichen getippt wird — brauchen wir nicht.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Nicht verwendet — wir arbeiten nur mit keyPressed/keyReleased
    }

    /**
     * Prüft UND verbraucht das "Enter just pressed" Flag.
     * Nach dem Aufruf ist das Flag false, bis Enter erneut gedrückt wird.
     * 
     * Das verhindert, dass ein einzelner Enter-Druck mehrfach gezählt wird
     * (z.B. über mehrere Frames hinweg).
     *
     * @return true wenn Enter seit dem letzten Aufruf gedrückt wurde
     */
    public boolean consumeEnter() {
        if (enterJustPressed) {
            enterJustPressed = false;
            return true;
        }
        return false;
    }

    // ─── Getter ──────────────────────────────────────────────────

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isSpacePressed() {
        return spacePressed;
    }

    public boolean isEscapePressed() {
        return escapePressed;
    }
}
