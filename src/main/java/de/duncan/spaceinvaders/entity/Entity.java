package de.duncan.spaceinvaders.entity;

import java.awt.Rectangle;

/**
 * Abstrakte Basisklasse für alle Spielobjekte (Player, Alien, Bullet).
 * 
 * WARUM ABSTRAKT?
 * ───────────────
 * Jedes Objekt im Spiel hat eine Position (x, y) und eine Größe (width, height).
 * Anstatt diesen Code in jeder Klasse zu wiederholen, definieren wir ihn EINMAL hier.
 * Die konkreten Klassen (Player, Alien, Bullet) erben alles und fügen nur
 * ihr eigenes Verhalten hinzu.
 * 
 * KONZEPT: "Axis-Aligned Bounding Box" (AABB)
 * ─────────────────────────────────────────────
 * getBounds() gibt ein Rechteck zurück, das das Objekt umschließt.
 * Zwei Objekte kollidieren, wenn sich ihre Rechtecke überlappen.
 * Das ist die einfachste Form der Kollisionserkennung und für ein
 * 2D-Spiel wie Space Invaders völlig ausreichend.
 */
public abstract class Entity {

    // Position auf dem Spielfeld (in Pixeln)
    protected double x;
    protected double y;

    // Größe des Objekts (in Pixeln)
    protected int width;
    protected int height;

    // Ist das Objekt noch "am Leben"? Wenn false, wird es entfernt.
    protected boolean alive;

    /**
     * Erstellt ein neues Entity an der gegebenen Position mit gegebener Größe.
     *
     * @param x      Startposition horizontal (0 = links)
     * @param y      Startposition vertikal (0 = oben)
     * @param width  Breite in Pixeln
     * @param height Höhe in Pixeln
     */
    protected Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.alive = true;  // Standardmäßig lebt jedes neue Objekt
    }

    /**
     * Aktualisiert das Objekt (Position, Zustand, etc.).
     * Jede Unterklasse implementiert das anders:
     * - Player: reagiert auf Tastatureingaben
     * - Alien: bewegt sich im Muster
     * - Bullet: fliegt geradeaus
     */
    public abstract void update();

    /**
     * Gibt das Bounding-Box-Rechteck zurück für Kollisionserkennung.
     * Rectangle ist eine Java-AWT-Klasse mit eingebauter intersects()-Methode.
     *
     * @return Rechteck, das dieses Objekt umschließt
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // ─── Getter & Setter ─────────────────────────────────────────

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Gibt die horizontale Mitte des Objekts zurück.
     * Nützlich z.B. um eine Kugel mittig über dem Spieler zu spawnen.
     */
    public double getCenterX() {
        return x + width / 2.0;
    }

    /**
     * Gibt die vertikale Mitte des Objekts zurück.
     */
    public double getCenterY() {
        return y + height / 2.0;
    }
}
