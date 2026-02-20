package de.duncan.spaceinvaders.engine;

import de.duncan.spaceinvaders.entity.Entity;

/**
 * Prüft Kollisionen zwischen Spielobjekten.
 * 
 * METHODE: Axis-Aligned Bounding Box (AABB)
 * ──────────────────────────────────────────
 * Die einfachste Kollisionserkennung: Jedes Objekt hat ein unsichtbares
 * Rechteck (Bounding Box) um sich herum. Wenn sich zwei Rechtecke
 * überlappen, liegt eine Kollision vor.
 * 
 * "Axis-Aligned" bedeutet: Die Rechtecke sind NICHT rotiert, sondern
 * immer parallel zu den Achsen ausgerichtet. Das macht die Berechnung
 * extrem schnell — perfekt für ein Spiel mit vielen Objekten.
 * 
 * WARUM EINE EIGENE KLASSE?
 * ─────────────────────────
 * Separation of Concerns: Die Kollisionslogik ist von der Spiellogik
 * getrennt. Wenn wir später z.B. Pixel-genaue Kollisionen wollen,
 * müssen wir nur diese Klasse ändern.
 */
public final class CollisionDetector {

    // Private Constructor → kann nicht instanziiert werden
    // Alle Methoden sind static, wir brauchen kein Objekt davon
    private CollisionDetector() {
        // Utility-Klasse — nicht instanziieren
    }

    /**
     * Prüft ob zwei Entities kollidieren.
     * 
     * Beide müssen "alive" sein, damit eine Kollision zählt.
     * Rectangle.intersects() prüft die Überlappung der Bounding Boxes.
     *
     * @param a Erstes Entity
     * @param b Zweites Entity
     * @return true wenn die Bounding Boxes sich überlappen
     */
    public static boolean checkCollision(Entity a, Entity b) {
        // Nur lebende Objekte können kollidieren
        if (!a.isAlive() || !b.isAlive()) {
            return false;
        }

        // Rectangle.intersects() ist die eingebaute Java-Methode für AABB-Check
        return a.getBounds().intersects(b.getBounds());
    }
}
