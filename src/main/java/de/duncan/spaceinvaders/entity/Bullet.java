package de.duncan.spaceinvaders.entity;

/**
 * Ein Projektil (Kugel), das entweder nach oben (Spieler) oder
 * nach unten (Alien) fliegt.
 * 
 * DESIGN-ENTSCHEIDUNG:
 * ────────────────────
 * Statt zwei getrennte Klassen (PlayerBullet, AlienBullet) zu machen,
 * nutzen wir EINE Klasse mit einem direction-Parameter:
 *   direction = -1 → fliegt nach oben (Spieler-Kugel)
 *   direction = +1 → fliegt nach unten (Alien-Kugel)
 * 
 * Das ist einfacher und vermeidet Code-Duplikation, weil sich Kugeln
 * identisch verhalten — nur die Richtung und Farbe unterscheiden sich.
 * 
 * KOORDINATENSYSTEM:
 * ──────────────────
 * In Java Swing ist y=0 OBEN im Fenster.
 * y wird größer nach UNTEN → "nach oben" bedeutet y wird KLEINER.
 * Deshalb ist direction=-1 für Spieler-Kugeln (fliegen nach oben).
 */
public class Bullet extends Entity {

    /** Fluggeschwindigkeit in Pixeln pro Frame */
    private static final double SPEED = 7.0;

    /** Flugrichtung: -1 = hoch (Spieler), +1 = runter (Alien) */
    private final int direction;

    /**
     * Erstellt ein neues Projektil.
     *
     * @param x         Startposition horizontal (Mitte der Kugel)
     * @param y         Startposition vertikal
     * @param direction Flugrichtung (-1 = hoch, +1 = runter)
     */
    public Bullet(double x, double y, int direction) {
        super(x, y, 6, 14);  // Kugel ist 6x14 Pixel (schmal und lang)
        this.direction = direction;
    }

    /**
     * Bewegt die Kugel in Flugrichtung.
     * Wenn sie das Spielfeld verlässt, wird sie als "tot" markiert
     * und im nächsten Frame von der GameEngine entfernt.
     */
    @Override
    public void update() {
        y += SPEED * direction;

        // Kugel ist aus dem sichtbaren Bereich geflogen → entfernen
        // Wir prüfen mit etwas Puffer (-20 und +800), damit Kugeln
        // nicht genau am Rand "hängen bleiben"
        if (y < -20 || y > 800) {
            alive = false;
        }
    }

    /**
     * Gibt die Flugrichtung zurück.
     * Nützlich um in der Kollisionserkennung zu unterscheiden,
     * ob eine Kugel vom Spieler oder von einem Alien stammt.
     *
     * @return -1 für Spieler-Kugel, +1 für Alien-Kugel
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Prüft ob dies eine Spieler-Kugel ist (fliegt nach oben).
     */
    public boolean isPlayerBullet() {
        return direction == -1;
    }

    /**
     * Prüft ob dies eine Alien-Kugel ist (fliegt nach unten).
     */
    public boolean isAlienBullet() {
        return direction == 1;
    }
}
