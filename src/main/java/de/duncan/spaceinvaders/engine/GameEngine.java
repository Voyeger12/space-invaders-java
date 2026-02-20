package de.duncan.spaceinvaders.engine;

import de.duncan.spaceinvaders.entity.Alien;
import de.duncan.spaceinvaders.entity.Bullet;
import de.duncan.spaceinvaders.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Die zentrale Spiellogik — das "Gehirn" des Spiels.
 * 
 * VERANTWORTLICHKEITEN:
 * ─────────────────────
 * 1. Spielzustand verwalten (MENU → PLAYING → GAME_OVER)
 * 2. Aliens spawnen und in Wellen anordnen
 * 3. Alle Objekte updaten (Player, Aliens, Bullets)
 * 4. Kollisionen prüfen und darauf reagieren
 * 5. Score und Wellen-System verwalten
 * 6. Alien-Schüsse steuern
 * 
 * WELLEN-SYSTEM:
 * ──────────────
 * Jede Welle hat mehr/schnellere Aliens:
 * - Basis: 5 Spalten × 4 Reihen = 20 Aliens
 * - Pro Welle: +1 Spalte, +0.15 Geschwindigkeit
 * - Aliens rücken auch schneller nach unten vor
 * 
 * ALIEN-SCHÜSSE:
 * ──────────────
 * Aliens schießen zufällig. Pro Frame hat jeder lebende Alien
 * eine kleine Chance (0.2%) zu schießen. Das ergibt bei 20 Aliens
 * im Schnitt ~2.4 Schüsse pro Sekunde — das fühlt sich fair an.
 */
public class GameEngine {

    // ─── Spielfeldgröße ──────────────────────────────────────────
    private final int gameWidth;
    private final int gameHeight;

    // ─── Spielobjekte ────────────────────────────────────────────
    private Player player;
    private final List<Alien> aliens;
    private final List<Bullet> bullets;

    // ─── Spielzustand ────────────────────────────────────────────
    private GameState state;
    private int score;
    private int highScore;
    private int wave;

    // ─── Alien-Bewegungssteuerung ────────────────────────────────
    /** Basis-Geschwindigkeit der Aliens (wird pro Welle erhöht) */
    private double alienBaseSpeed;

    /** Wie weit Aliens nach unten rücken wenn sie den Rand erreichen */
    private static final double ALIEN_DROP_DISTANCE = 20;

    // ─── Zufallsgenerator für Alien-Schüsse ──────────────────────
    private final Random random;

    /** Wahrscheinlichkeit pro Frame pro Alien zu schießen (0.2%) */
    private static final double ALIEN_SHOOT_CHANCE = 0.002;

    /**
     * Erstellt eine neue GameEngine.
     *
     * @param gameWidth  Spielfeldbreite in Pixeln
     * @param gameHeight Spielfeldhöhe in Pixeln
     */
    public GameEngine(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.aliens = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.random = new Random();
        this.state = GameState.MENU;
        this.score = 0;
        this.highScore = 0;
        this.wave = 0;
    }

    /**
     * Hauptmethode: Wird jeden Frame (60x/Sek) von GamePanel aufgerufen.
     * Verteilt die Arbeit basierend auf dem aktuellen Spielzustand.
     *
     * @param input Der aktuelle InputHandler mit Tastenzuständen
     */
    public void update(InputHandler input) {
        switch (state) {
            case MENU      -> updateMenu(input);
            case PLAYING   -> updatePlaying(input);
            case GAME_OVER -> updateGameOver(input);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  MENÜ-ZUSTAND
    // ═══════════════════════════════════════════════════════════════

    /**
     * Im Menü: Warte auf Enter um das Spiel zu starten.
     */
    private void updateMenu(InputHandler input) {
        if (input.consumeEnter()) {
            startNewGame();
        }
    }

    /**
     * Initialisiert ein komplett neues Spiel.
     */
    private void startNewGame() {
        player = new Player(gameWidth, gameHeight);
        aliens.clear();
        bullets.clear();
        score = 0;
        wave = 0;
        alienBaseSpeed = 1.0;
        state = GameState.PLAYING;
        spawnWave();  // Erste Welle spawnen
    }

    // ═══════════════════════════════════════════════════════════════
    //  SPIELEN-ZUSTAND (Hauptlogik)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Der Kern des Spiels — wird bei PLAYING jeden Frame aufgerufen.
     * Reihenfolge ist wichtig:
     * 1. Input verarbeiten → Spieler bewegen, schießen
     * 2. Alle Objekte updaten → Positionen ändern
     * 3. Kollisionen prüfen → Treffer registrieren
     * 4. Tote Objekte aufräumen → aus Listen entfernen
     * 5. Spielende prüfen → Alle Aliens tot? Spieler tot?
     */
    private void updatePlaying(InputHandler input) {
        // ESC → Zurück zum Menü
        if (input.isEscapePressed()) {
            state = GameState.MENU;
            return;
        }

        // 1. Spieler-Input verarbeiten
        handlePlayerInput(input);

        // 2. Alle Objekte updaten
        player.update();
        aliens.forEach(Alien::update);   // Kurzform für: alien -> alien.update()
        bullets.forEach(Bullet::update); // Method Reference — kompakter als Lambda

        // 3. Alien-Randerkennung und Richtungswechsel
        handleAlienBoundary();

        // 4. Aliens schießen lassen
        handleAlienShooting();

        // 5. Kollisionen prüfen
        checkCollisions();

        // 6. Tote Objekte entfernen
        // removeIf() mit Lambda: Entferne alle Elemente wo isAlive() == false
        bullets.removeIf(b -> !b.isAlive());
        aliens.removeIf(a -> !a.isAlive());

        // 7. Spielende prüfen
        checkWaveComplete();
        checkGameOver();
    }

    /**
     * Verarbeitet Spieler-Input: Bewegung und Schießen.
     */
    private void handlePlayerInput(InputHandler input) {
        player.setMovingLeft(input.isLeftPressed());
        player.setMovingRight(input.isRightPressed());

        // Leertaste gedrückt → Schuss versuchen
        if (input.isSpacePressed()) {
            Bullet bullet = player.shoot();
            if (bullet != null) {
                bullets.add(bullet);
            }
        }
    }

    /**
     * Prüft ob ein Alien den Spielfeldrand erreicht hat.
     * Wenn ja: ALLE Aliens rücken nach unten und kehren um.
     * 
     * Das erzeugt das klassische Zickzack-Muster der Aliens.
     */
    private void handleAlienBoundary() {
        boolean shouldReverse = false;

        for (Alien alien : aliens) {
            if (alien.isAlive()) {
                // Prüfe ob der Alien den linken oder rechten Rand berührt
                if (alien.getX() <= 0 || alien.getX() + alien.getWidth() >= gameWidth) {
                    shouldReverse = true;
                    break;  // Einer reicht — alle drehen um
                }
            }
        }

        if (shouldReverse) {
            for (Alien alien : aliens) {
                alien.dropAndReverse(ALIEN_DROP_DISTANCE);
            }
        }
    }

    /**
     * Lässt Aliens zufällig schießen.
     * Nur Aliens in der UNTERSTEN Reihe ihrer Spalte dürfen schießen,
     * damit die Kugeln nicht durch andere Aliens fliegen.
     */
    private void handleAlienShooting() {
        // Nur lebende Aliens können schießen
        List<Alien> livingAliens = aliens.stream()
                .filter(Alien::isAlive)
                .toList();

        for (Alien alien : livingAliens) {
            // Prüfe ob ein anderer lebender Alien UNTER diesem steht
            boolean hasAlienBelow = livingAliens.stream()
                    .anyMatch(other -> other != alien
                            && Math.abs(other.getX() - alien.getX()) < 10  // Gleiche Spalte
                            && other.getY() > alien.getY());               // Weiter unten

            // Nur die untersten Aliens schießen (keine Aliens unter ihnen)
            if (!hasAlienBelow && random.nextDouble() < ALIEN_SHOOT_CHANCE) {
                bullets.add(alien.shoot());
            }
        }
    }

    /**
     * Prüft alle Kollisionen:
     * - Spieler-Kugel trifft Alien → Alien stirbt, Score erhöhen
     * - Alien-Kugel trifft Spieler → Spieler verliert Leben
     */
    private void checkCollisions() {
        // Iterator erlaubt sicheres Entfernen während der Iteration
        // (Eine einfache for-each-Schleife würde ConcurrentModificationException werfen!)
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            if (bullet.isPlayerBullet()) {
                // Spieler-Kugel: Prüfe Treffer auf Aliens
                for (Alien alien : aliens) {
                    if (alien.isAlive() && CollisionDetector.checkCollision(bullet, alien)) {
                        alien.setAlive(false);
                        bullet.setAlive(false);
                        score += alien.getPoints();
                        break;  // Eine Kugel kann nur EINEN Alien treffen
                    }
                }
            } else if (bullet.isAlienBullet()) {
                // Alien-Kugel: Prüfe Treffer auf Spieler
                if (CollisionDetector.checkCollision(bullet, player)) {
                    bullet.setAlive(false);
                    player.loseLife();
                }
            }
        }

        // Zusätzlich: Prüfe ob ein Alien den Spieler direkt berührt
        for (Alien alien : aliens) {
            if (alien.isAlive() && CollisionDetector.checkCollision(alien, player)) {
                player.loseLife();
                alien.setAlive(false);
            }
        }
    }

    /**
     * Prüft ob alle Aliens der aktuellen Welle besiegt wurden.
     * Wenn ja → nächste Welle spawnen.
     */
    private void checkWaveComplete() {
        boolean allDead = aliens.stream().noneMatch(Alien::isAlive);
        if (allDead) {
            // Schwierigkeit erhöhen
            alienBaseSpeed += 0.15;
            spawnWave();
        }
    }

    /**
     * Prüft ob das Spiel vorbei ist:
     * - Spieler hat keine Leben mehr
     * - Ein Alien hat den unteren Rand erreicht
     */
    private void checkGameOver() {
        // Spieler tot?
        if (!player.isAlive()) {
            endGame();
            return;
        }

        // Alien hat den unteren Rand erreicht?
        for (Alien alien : aliens) {
            if (alien.isAlive() && alien.getY() + alien.getHeight() >= gameHeight - 80) {
                endGame();
                return;
            }
        }
    }

    /**
     * Beendet das aktuelle Spiel und wechselt zu GAME_OVER.
     */
    private void endGame() {
        if (score > highScore) {
            highScore = score;
        }
        state = GameState.GAME_OVER;
    }

    // ═══════════════════════════════════════════════════════════════
    //  GAME OVER-ZUSTAND
    // ═══════════════════════════════════════════════════════════════

    /**
     * Game Over Bildschirm: Enter → Neues Spiel.
     */
    private void updateGameOver(InputHandler input) {
        if (input.consumeEnter()) {
            startNewGame();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  WELLEN-SYSTEM
    // ═══════════════════════════════════════════════════════════════

    /**
     * Spawnt eine neue Welle von Aliens.
     * 
     * Layout: 
     *   Spalten × Reihen Alien-Gitter, zentriert auf dem Spielfeld.
     *   Die oberen Reihen haben höherwertige Aliens (Typ 0 = 30 Punkte).
     * 
     * Schwierigkeitssteigerung:
     *   - Mehr Spalten pro Welle (bis max 10)
     *   - Höhere Geschwindigkeit
     */
    private void spawnWave() {
        wave++;
        aliens.clear();
        bullets.clear();  // Alte Kugeln entfernen für faire Welle

        // Spaltenanzahl: Startet bei 6, steigt pro Welle, max 10
        int cols = Math.min(6 + wave - 1, 10);
        int rows = 4;  // Immer 4 Reihen

        // Abstände zwischen Aliens
        int spacingX = 50;  // Horizontal
        int spacingY = 42;  // Vertikal

        // Gitter zentrieren auf dem Spielfeld
        int totalWidth = cols * spacingX;
        int startX = (gameWidth - totalWidth) / 2 + 10;
        int startY = 60;  // Abstand vom oberen Rand

        for (int row = 0; row < rows; row++) {
            // Alien-Typ basierend auf Reihe (oben = wertvoller)
            int type;
            if (row == 0) {
                type = 0;       // Oberste Reihe: 30 Punkte
            } else if (row <= 2) {
                type = 1;       // Mittlere Reihen: 20 Punkte
            } else {
                type = 2;       // Unterste Reihe: 10 Punkte
            }

            for (int col = 0; col < cols; col++) {
                double x = startX + col * spacingX;
                double y = startY + row * spacingY;
                Alien alien = new Alien(x, y, type);
                alien.setSpeedX(alienBaseSpeed);
                aliens.add(alien);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  GETTER (für Renderer und GamePanel)
    // ═══════════════════════════════════════════════════════════════

    public Player getPlayer() {
        return player;
    }

    public List<Alien> getAliens() {
        return aliens;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getWave() {
        return wave;
    }
}
