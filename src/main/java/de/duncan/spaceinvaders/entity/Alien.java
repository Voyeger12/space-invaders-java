package de.duncan.spaceinvaders.entity;

/**
 * Ein einzelner Alien-Gegner.
 * 
 * BEWEGUNGSMUSTER:
 * ────────────────
 * Aliens bewegen sich als Gruppe seitlich. Wenn EIN Alien den Rand erreicht,
 * bewegen sich ALLE eine Reihe nach unten und ändern die Richtung.
 * Das wird nicht hier, sondern in der GameEngine gesteuert — jeder Alien
 * kennt nur seine eigene Position und Geschwindigkeit.
 * 
 * ALIEN-TYPEN:
 * ────────────
 * Es gibt 3 Typen mit unterschiedlichen Punktwerten und Farben:
 * - Typ 0 (oben):    30 Punkte — die wertvollsten, weil am weitesten weg
 * - Typ 1 (mitte):   20 Punkte
 * - Typ 2 (unten):   10 Punkte — am einfachsten zu treffen
 */
public class Alien extends Entity {

    /** Punkte, die der Spieler beim Abschuss bekommt */
    private final int points;

    /** Alien-Typ (0-2) bestimmt Farbe und Punkte */
    private final int type;

    /** Aktuelle horizontale Geschwindigkeit (wird von GameEngine gesetzt) */
    private double speedX;

    /**
     * Erstellt einen neuen Alien.
     *
     * @param x    Startposition horizontal
     * @param y    Startposition vertikal
     * @param type Alien-Typ (0 = 30 Punkte, 1 = 20 Punkte, 2 = 10 Punkte)
     */
    public Alien(double x, double y, int type) {
        super(x, y, 36, 28);  // Jeder Alien ist 36x28 Pixel groß
        this.type = type;

        // Punkte basierend auf Typ zuweisen
        // switch-Expression (Java 14+): kompakter als if/else
        this.points = switch (type) {
            case 0 -> 30;
            case 1 -> 20;
            default -> 10;
        };

        this.speedX = 1.0;  // Startgeschwindigkeit
    }

    /**
     * Bewegt den Alien horizontal.
     * Die vertikale Bewegung (nach unten rücken) wird von der GameEngine gesteuert.
     */
    @Override
    public void update() {
        x += speedX;
    }

    /**
     * Lässt den Alien eine Stufe nach unten rücken und kehrt die Richtung um.
     * Wird von der GameEngine aufgerufen, wenn ein Alien den Rand erreicht.
     *
     * @param dropDistance Wie viele Pixel nach unten gerückt wird
     */
    public void dropAndReverse(double dropDistance) {
        y += dropDistance;
        speedX = -speedX;  // Richtung umkehren
    }

    /**
     * Erstellt ein Alien-Projektil, das nach unten fliegt.
     *
     * @return Neue Bullet die nach unten fliegt (direction = +1)
     */
    public Bullet shoot() {
        // Kugel startet mittig unter dem Alien
        return new Bullet(getCenterX() - 3, y + height + 5, 1);
    }

    // ─── Getter ──────────────────────────────────────────────────

    public int getPoints() {
        return points;
    }

    public int getType() {
        return type;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }
}
