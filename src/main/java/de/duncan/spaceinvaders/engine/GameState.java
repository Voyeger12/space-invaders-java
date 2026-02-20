package de.duncan.spaceinvaders.engine;

/**
 * Definiert die möglichen Zustände des Spiels.
 * 
 * WARUM EIN ENUM?
 * ───────────────
 * Ein Enum (Aufzählungstyp) ist perfekt für eine feste Menge von Zuständen.
 * Vorteile gegenüber Strings oder int-Konstanten:
 *   - Typsicher: Der Compiler fängt Tippfehler ab
 *   - Selbstdokumentierend: Die Namen sagen genau, was sie bedeuten
 *   - Switch-freundlich: Java warnt, wenn ein Zustand vergessen wird
 * 
 * ZUSTANDSÜBERGÄNGE:
 * ──────────────────
 *   MENU ──[Enter]──► PLAYING ──[alle Leben weg]──► GAME_OVER
 *     ▲                                                  │
 *     └──────────────────[Enter]─────────────────────────┘
 */
public enum GameState {

    /** Startbildschirm — wartet auf Enter zum Starten */
    MENU,

    /** Spiel läuft — Spieler steuert, Aliens bewegen sich */
    PLAYING,

    /** Spiel vorbei — zeigt Score, wartet auf Enter für Neustart */
    GAME_OVER
}
