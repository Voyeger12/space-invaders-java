package de.duncan.spaceinvaders.ui;

import de.duncan.spaceinvaders.engine.GameEngine;
import de.duncan.spaceinvaders.engine.GameState;
import de.duncan.spaceinvaders.entity.Alien;
import de.duncan.spaceinvaders.entity.Bullet;
import de.duncan.spaceinvaders.entity.Player;

import java.awt.*;

/**
 * Zeichnet alle Spielobjekte und UI-Elemente auf den Bildschirm.
 * 
 * RENDERING-KONZEPT:
 * ──────────────────
 * In Java Swing wird alles über ein Graphics2D-Objekt gezeichnet.
 * Das ist wie ein virtueller "Pinsel" — wir setzen Farbe und Form,
 * und Graphics2D malt es auf den Bildschirm.
 * 
 * ANTI-ALIASING:
 * ──────────────
 * Wir aktivieren Anti-Aliasing für Text, damit Buchstaben glatt
 * aussehen statt pixelig. Für die Spielobjekte nutzen wir es nicht,
 * weil der Pixel-Look zum Retro-Stil passt.
 * 
 * FARBEN:
 * ───────
 * Alien Typ 0: Magenta (30 Punkte)
 * Alien Typ 1: Cyan    (20 Punkte)
 * Alien Typ 2: Gelb    (10 Punkte)
 * Spieler:     Grün
 * Spieler-Kugeln: Grün
 * Alien-Kugeln: Rot
 */
public class Renderer {

    // ─── Farben ──────────────────────────────────────────────────
    private static final Color COLOR_BACKGROUND = new Color(10, 10, 30);
    private static final Color COLOR_PLAYER = new Color(50, 255, 50);
    private static final Color COLOR_PLAYER_BULLET = new Color(50, 255, 50);
    private static final Color COLOR_ALIEN_BULLET = new Color(255, 80, 80);
    private static final Color COLOR_HUD_TEXT = new Color(220, 220, 220);
    private static final Color COLOR_TITLE = new Color(50, 255, 50);
    private static final Color COLOR_SUBTITLE = new Color(180, 180, 180);

    // Alien-Farben nach Typ
    private static final Color[] ALIEN_COLORS = {
            new Color(255, 50, 255),  // Typ 0: Magenta
            new Color(50, 220, 255),  // Typ 1: Cyan
            new Color(255, 255, 50)   // Typ 2: Gelb
    };

    // ─── Fonts ───────────────────────────────────────────────────
    private static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 48);
    private static final Font FONT_SUBTITLE = new Font("Monospaced", Font.PLAIN, 18);
    private static final Font FONT_HUD = new Font("Monospaced", Font.BOLD, 16);
    private static final Font FONT_GAME_OVER = new Font("Monospaced", Font.BOLD, 40);
    private static final Font FONT_SCORE = new Font("Monospaced", Font.BOLD, 24);

    private final int gameWidth;
    private final int gameHeight;

    public Renderer(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    /**
     * Hauptmethode: Zeichnet den gesamten Bildschirm basierend auf dem Spielzustand.
     *
     * @param g2d    Das Graphics2D-Objekt zum Zeichnen
     * @param engine Die GameEngine mit allen Spieldaten
     */
    public void render(Graphics2D g2d, GameEngine engine) {
        // Anti-Aliasing für glatten Text
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        // Hintergrund füllen (dunkelblau/fast schwarz)
        g2d.setColor(COLOR_BACKGROUND);
        g2d.fillRect(0, 0, gameWidth, gameHeight);

        // Je nach Spielzustand unterschiedliche Screens zeichnen
        switch (engine.getState()) {
            case MENU      -> renderMenu(g2d, engine);
            case PLAYING   -> renderPlaying(g2d, engine);
            case GAME_OVER -> renderGameOver(g2d, engine);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  MENÜ-SCREEN
    // ═══════════════════════════════════════════════════════════════

    private void renderMenu(Graphics2D g2d, GameEngine engine) {
        // Titel
        g2d.setColor(COLOR_TITLE);
        g2d.setFont(FONT_TITLE);
        drawCenteredString(g2d, "SPACE INVADERS", gameHeight / 3);

        // Untertitel
        g2d.setColor(COLOR_SUBTITLE);
        g2d.setFont(FONT_SUBTITLE);
        drawCenteredString(g2d, "Druecke ENTER zum Starten", gameHeight / 2);

        // Steuerungshinweise
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
        int y = gameHeight / 2 + 60;
        drawCenteredString(g2d, "PFEILTASTEN / A,D  =  Bewegen", y);
        drawCenteredString(g2d, "LEERTASTE          =  Schiessen", y + 25);
        drawCenteredString(g2d, "ESC                =  Beenden", y + 50);

        // Highscore anzeigen (wenn vorhanden)
        if (engine.getHighScore() > 0) {
            g2d.setColor(COLOR_TITLE);
            g2d.setFont(FONT_HUD);
            drawCenteredString(g2d, "Highscore: " + engine.getHighScore(), gameHeight - 80);
        }

        // Deko-Aliens im Menü
        renderMenuAliens(g2d);
    }

    /**
     * Zeichnet dekorative Aliens im Menü mit ihren Punktwerten.
     */
    private void renderMenuAliens(Graphics2D g2d) {
        int startY = gameHeight / 2 + 150;
        String[] labels = {"= 30 PTS", "= 20 PTS", "= 10 PTS"};

        for (int i = 0; i < 3; i++) {
            int centerX = gameWidth / 2;
            int y = startY + i * 35;

            // Alien-Symbol zeichnen
            g2d.setColor(ALIEN_COLORS[i]);
            drawAlienShape(g2d, centerX - 60, y, i);

            // Punktwert daneben
            g2d.setColor(COLOR_SUBTITLE);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
            g2d.drawString(labels[i], centerX - 25, y + 18);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  SPIEL-SCREEN
    // ═══════════════════════════════════════════════════════════════

    private void renderPlaying(Graphics2D g2d, GameEngine engine) {
        // HUD (Head-Up-Display) oben
        renderHUD(g2d, engine);

        // Spieler zeichnen
        renderPlayer(g2d, engine.getPlayer());

        // Aliens zeichnen
        for (Alien alien : engine.getAliens()) {
            if (alien.isAlive()) {
                renderAlien(g2d, alien);
            }
        }

        // Kugeln zeichnen
        for (Bullet bullet : engine.getBullets()) {
            if (bullet.isAlive()) {
                renderBullet(g2d, bullet);
            }
        }

        // Trennlinie über dem Spieler
        g2d.setColor(new Color(50, 50, 80));
        g2d.drawLine(0, gameHeight - 70, gameWidth, gameHeight - 70);
    }

    /**
     * Zeichnet das Head-Up-Display: Score, Welle, Leben.
     */
    private void renderHUD(Graphics2D g2d, GameEngine engine) {
        g2d.setColor(COLOR_HUD_TEXT);
        g2d.setFont(FONT_HUD);

        // Score links
        g2d.drawString("SCORE: " + engine.getScore(), 15, 25);

        // Welle mittig
        String waveText = "WELLE " + engine.getWave();
        drawCenteredString(g2d, waveText, 25);

        // Leben rechts (als Herz-Symbole)
        if (engine.getPlayer() != null) {
            String livesText = "LEBEN: " + "\u2665 ".repeat(engine.getPlayer().getLives());
            int textWidth = g2d.getFontMetrics().stringWidth(livesText);
            g2d.setColor(new Color(255, 80, 80));
            g2d.drawString(livesText, gameWidth - textWidth - 15, 25);
        }

        // Trennlinie unter dem HUD
        g2d.setColor(new Color(50, 50, 80));
        g2d.drawLine(0, 35, gameWidth, 35);
    }

    /**
     * Zeichnet das Spieler-Raumschiff als Dreieck/Pfeilform.
     */
    private void renderPlayer(Graphics2D g2d, Player player) {
        if (player == null || !player.isAlive()) return;

        int px = (int) player.getX();
        int py = (int) player.getY();
        int pw = player.getWidth();
        int ph = player.getHeight();

        g2d.setColor(COLOR_PLAYER);

        // Rumpf (Hauptkörper)
        g2d.fillRect(px + 5, py + 8, pw - 10, ph - 8);

        // Spitze (Dreieck oben)
        int[] triX = {px + pw / 2 - 6, px + pw / 2, px + pw / 2 + 6};
        int[] triY = {py + 8, py, py + 8};
        g2d.fillPolygon(triX, triY, 3);

        // Flügel links und rechts
        g2d.fillRect(px, py + 15, 8, ph - 15);
        g2d.fillRect(px + pw - 8, py + 15, 8, ph - 15);

        // Highlight-Effekt (hellerer Streifen in der Mitte)
        g2d.setColor(new Color(150, 255, 150));
        g2d.fillRect(px + pw / 2 - 2, py + 10, 4, ph - 14);
    }

    /**
     * Zeichnet einen einzelnen Alien basierend auf seinem Typ.
     * Jeder Typ hat eine leicht andere Form.
     */
    private void renderAlien(Graphics2D g2d, Alien alien) {
        g2d.setColor(ALIEN_COLORS[alien.getType()]);
        drawAlienShape(g2d, (int) alien.getX(), (int) alien.getY(), alien.getType());
    }

    /**
     * Zeichnet die Alien-Form basierend auf dem Typ.
     * 
     * Typ 0: Oktagon-ähnlich (der "Boss")
     * Typ 1: Breiter Körper mit "Armen"
     * Typ 2: Einfaches Quadrat mit "Augen" (der Basis-Alien)
     */
    private void drawAlienShape(Graphics2D g2d, int x, int y, int type) {
        Color currentColor = g2d.getColor();

        switch (type) {
            case 0 -> {
                // Typ 0: Runder Kopf mit Tentakeln
                g2d.fillRoundRect(x + 4, y + 2, 28, 18, 8, 8);
                g2d.fillRect(x + 2, y + 12, 6, 12);
                g2d.fillRect(x + 28, y + 12, 6, 12);
                g2d.fillRect(x + 10, y + 18, 4, 8);
                g2d.fillRect(x + 22, y + 18, 4, 8);
            }
            case 1 -> {
                // Typ 1: Breiter Alien mit Flügeln
                g2d.fillRect(x + 8, y + 2, 20, 16);
                g2d.fillRect(x, y + 6, 36, 10);
                g2d.fillRect(x + 4, y + 18, 8, 6);
                g2d.fillRect(x + 24, y + 18, 8, 6);
            }
            case 2 -> {
                // Typ 2: Kompakter Alien
                g2d.fillRect(x + 4, y + 4, 28, 18);
                g2d.fillRect(x, y + 8, 36, 10);
                g2d.fillRect(x + 8, y + 20, 6, 6);
                g2d.fillRect(x + 22, y + 20, 6, 6);
            }
        }

        // "Augen" zeichnen (dunkle Pixel auf dem Alien)
        g2d.setColor(COLOR_BACKGROUND);
        g2d.fillRect(x + 11, y + 8, 4, 4);
        g2d.fillRect(x + 21, y + 8, 4, 4);

        g2d.setColor(currentColor); // Farbe zurücksetzen
    }

    /**
     * Zeichnet ein Projektil (Kugel).
     * Spieler-Kugeln sind grün, Alien-Kugeln rot.
     */
    private void renderBullet(Graphics2D g2d, Bullet bullet) {
        Color bulletColor = bullet.isPlayerBullet() ? COLOR_PLAYER_BULLET : COLOR_ALIEN_BULLET;
        g2d.setColor(bulletColor);
        g2d.fillRect((int) bullet.getX(), (int) bullet.getY(),
                bullet.getWidth(), bullet.getHeight());

        // Leuchteffekt: Hellerer Kern
        g2d.setColor(bulletColor.brighter());
        g2d.fillRect((int) bullet.getX() + 1, (int) bullet.getY() + 2,
                bullet.getWidth() - 2, bullet.getHeight() - 4);
    }

    // ═══════════════════════════════════════════════════════════════
    //  GAME OVER-SCREEN
    // ═══════════════════════════════════════════════════════════════

    private void renderGameOver(Graphics2D g2d, GameEngine engine) {
        // Semi-transparentes Overlay
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, gameWidth, gameHeight);

        // "GAME OVER" Text
        g2d.setColor(new Color(255, 60, 60));
        g2d.setFont(FONT_GAME_OVER);
        drawCenteredString(g2d, "GAME OVER", gameHeight / 3);

        // Endgültiger Score
        g2d.setColor(COLOR_HUD_TEXT);
        g2d.setFont(FONT_SCORE);
        drawCenteredString(g2d, "Score: " + engine.getScore(), gameHeight / 3 + 50);

        // Welle
        g2d.setFont(FONT_SUBTITLE);
        drawCenteredString(g2d, "Welle erreicht: " + engine.getWave(), gameHeight / 3 + 85);

        // Highscore
        if (engine.getScore() >= engine.getHighScore() && engine.getHighScore() > 0) {
            g2d.setColor(COLOR_TITLE);
            g2d.setFont(FONT_HUD);
            drawCenteredString(g2d, "*** NEUER HIGHSCORE! ***", gameHeight / 2 + 30);
        } else {
            g2d.setColor(COLOR_SUBTITLE);
            g2d.setFont(FONT_HUD);
            drawCenteredString(g2d, "Highscore: " + engine.getHighScore(), gameHeight / 2 + 30);
        }

        // Neustart-Hinweis
        g2d.setColor(COLOR_SUBTITLE);
        g2d.setFont(FONT_SUBTITLE);
        drawCenteredString(g2d, "Druecke ENTER fuer Neustart", gameHeight - 100);
    }

    // ═══════════════════════════════════════════════════════════════
    //  HILFSMETHODEN
    // ═══════════════════════════════════════════════════════════════

    /**
     * Zeichnet einen String horizontal zentriert auf dem Bildschirm.
     * 
     * FontMetrics gibt uns die tatsächliche Pixel-Breite des Texts,
     * damit die Zentrierung bei jeder Schriftgröße funktioniert.
     *
     * @param g2d  Graphics-Objekt
     * @param text Der zu zeichnende Text
     * @param y    Vertikale Position
     */
    private void drawCenteredString(Graphics2D g2d, String text, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (gameWidth - fm.stringWidth(text)) / 2;
        g2d.drawString(text, x, y);
    }
}
