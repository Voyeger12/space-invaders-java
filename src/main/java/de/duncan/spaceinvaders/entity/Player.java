package de.duncan.spaceinvaders.entity;

/**
 * Das Spieler-Raumschiff, gesteuert über die Tastatur.
 * 
 * BEWEGUNGSKONZEPT:
 * ─────────────────
 * Der Spieler bewegt sich NUR horizontal (links/rechts).
 * Die Geschwindigkeit (SPEED) ist in Pixeln pro Frame.
 * Bei 60 FPS bewegt sich der Spieler also 60 * 5 = 300 Pixel pro Sekunde.
 * 
 * SCHUSS-COOLDOWN:
 * ────────────────
 * Damit der Spieler nicht im Dauerfeuer schießen kann, gibt es einen Cooldown.
 * Nach jedem Schuss muss eine bestimmte Anzahl Frames gewartet werden.
 * Das wird über shootCooldown gesteuert: Bei jedem Frame wird er runtergezählt,
 * und nur wenn er bei 0 ist, darf geschossen werden.
 */
public class Player extends Entity {

    /** Bewegungsgeschwindigkeit in Pixeln pro Frame */
    private static final double SPEED = 5.0;

    /** Frames zwischen zwei Schüssen (15 Frames = 0.25 Sekunden bei 60 FPS) */
    private static final int SHOOT_COOLDOWN_FRAMES = 15;

    /** Spielfeldbreite — damit der Spieler nicht aus dem Bild fliegt */
    private final int gameWidth;

    // Bewegungsrichtung: wird vom InputHandler gesetzt
    private boolean movingLeft;
    private boolean movingRight;

    // Cooldown-Zähler für Schüsse
    private int shootCooldown;

    // Anzahl verbleibender Leben
    private int lives;

    /**
     * Erstellt einen neuen Spieler mittig am unteren Bildschirmrand.
     *
     * @param gameWidth  Breite des Spielfelds in Pixeln
     * @param gameHeight Höhe des Spielfelds in Pixeln
     */
    public Player(int gameWidth, int gameHeight) {
        // super() ruft den Konstruktor von Entity auf
        // Position: mittig unten, 50x30 Pixel groß
        super(
            gameWidth / 2.0 - 25,   // x: Mitte minus halbe Breite
            gameHeight - 60,         // y: 60 Pixel vom unteren Rand
            50,                      // Breite
            30                       // Höhe
        );
        this.gameWidth = gameWidth;
        this.shootCooldown = 0;
        this.lives = 3;  // 3 Leben zu Beginn
    }

    /**
     * Wird jeden Frame aufgerufen (60x pro Sekunde).
     * Bewegt den Spieler basierend auf den aktuellen Tasteneingaben
     * und zählt den Schuss-Cooldown runter.
     */
    @Override
    public void update() {
        // Bewegung nach links (aber nicht über den linken Rand hinaus)
        if (movingLeft && x > 0) {
            x -= SPEED;
        }

        // Bewegung nach rechts (aber nicht über den rechten Rand hinaus)
        if (movingRight && x + width < gameWidth) {
            x += SPEED;
        }

        // Sicherheitscheck: Position auf gültige Werte begrenzen
        // Math.max(0, ...) stellt sicher, dass x nie negativ wird
        // Math.min(gameWidth - width, ...) stellt sicher, dass wir nicht rechts rausfliegen
        x = Math.max(0, Math.min(x, gameWidth - width));

        // Cooldown runterzählen (wird nie negativ dank Math.max)
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    /**
     * Versucht zu schießen. Gibt eine neue Bullet zurück, wenn der
     * Cooldown abgelaufen ist, sonst null.
     *
     * @return Neue Bullet oder null wenn noch im Cooldown
     */
    public Bullet shoot() {
        if (shootCooldown <= 0) {
            shootCooldown = SHOOT_COOLDOWN_FRAMES;  // Cooldown zurücksetzen

            // Kugel wird mittig über dem Spieler erzeugt
            // getCenterX() - 3: Kugel (6px breit) mittig zentrieren
            // y - 15: Kugel startet knapp über dem Spieler
            // direction = -1: Kugel fliegt nach OBEN (negatives Y)
            return new Bullet(getCenterX() - 3, y - 15, -1);
        }
        return null;  // Noch im Cooldown, kein Schuss
    }

    /**
     * Spieler verliert ein Leben.
     * Wenn alle Leben aufgebraucht sind, stirbt der Spieler.
     */
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }

    /**
     * Setzt den Spieler auf Startwerte zurück (für neues Spiel).
     *
     * @param gameHeight Höhe des Spielfelds
     */
    public void reset(int gameHeight) {
        x = gameWidth / 2.0 - 25;
        y = gameHeight - 60;
        lives = 3;
        alive = true;
        shootCooldown = 0;
        movingLeft = false;
        movingRight = false;
    }

    // ─── Getter & Setter ─────────────────────────────────────────

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public int getLives() {
        return lives;
    }

    public boolean canShoot() {
        return shootCooldown <= 0;
    }
}
