/*
 * GameConfig.java
 *
 * This class serves as the central configuration repository for the application.
 * It defines global constants and settings used throughout the game, such as
 * board dimensions, animation speeds, and visual styling parameters.
 *
 * Key Refactoring Feature: Encapsulation
 * The 'START_LEVEL' variable is encapsulated (private static) with public
 * getter and setter methods. This ensures that the game state can only be
 * modified through controlled access points, protecting the integrity of the data.
 */
package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public final class GameConfig {

    // --- Grid Dimensions ---
    // The standard Tetris board is 10 columns wide and typically 20-22 rows high visible.
    // Extra rows are added at the top for spawning new pieces.
    public static final int ROWS = 25;
    public static final int COLS = 10;
    public static final int HIDDEN_ROWS = 2; // Rows used for spawning that are not rendered
    public static final int BRICK_SIZE = 24; // Pixel size of each block
    public static final int BOARD_GAP = 0;   // Spacing between blocks

    // --- Spawn & Offset Settings ---
    public static final int BRICK_PANEL_OFFSET_Y = -42;
    public static final int SPAWN_X = COLS / 2 - 2; // Center the spawn horizontally
    public static final int SPAWN_Y = 0;

    // --- Game Logic Settings ---
    public static final int ACTIVE_BRICK_OFFSET_X = 0;
    public static final int ACTIVE_BRICK_OFFSET_Y = -42;
    public static final int DROP_SPEED_MS = 400; // Base speed: blocks fall every 400ms
    public static final int VISIBLE_ROW_OFFSET = HIDDEN_ROWS;
    public static final int BASE_DROP_SPEED_MS = 400;
    public static final int LINES_PER_LEVEL = 10; // Difficulty increases every 10 lines
    public static final double LEVEL_SPEED_MULTIPLIER = 0.20; // Speed increases by 20% per level

    // --- Encapsulated Game State ---
    // Stores the user's selected starting level from the main menu.
    private static int START_LEVEL = 1;

    // Private constructor prevents instantiation of this utility class
    private GameConfig() {}

    /**
     * Retrieves the currently selected starting level.
     * @return The level integer (typically 1-3).
     */
    public static int getStartLevel() {
        return START_LEVEL;
    }

    /**
     * Updates the starting level based on user selection in the Settings menu.
     * @param level The new level to set.
     */
    public static void setStartLevel(int level) {
        START_LEVEL = level;
    }

    // --- Visual Assets ---
    // Defines the palette of colors used for the different Tetromino shapes.
    // Index 0 is TRANSPARENT (empty space), indices 1-7 match specific brick IDs.
    public static final Paint[] COLORS = {
            Color.TRANSPARENT,
            makeRetroBevel(Color.AQUA),         // I-Piece
            makeRetroBevel(Color.BLUEVIOLET),   // J-Piece
            makeRetroBevel(Color.DARKGREEN),    // L-Piece
            makeRetroBevel(Color.YELLOW),       // O-Piece
            makeRetroBevel(Color.RED),          // S-Piece
            makeRetroBevel(Color.BEIGE),        // T-Piece
            makeRetroBevel(Color.BURLYWOOD)     // Z-Piece
    };

    /**
     * Creates a pseudo-3D "bevel" effect for the bricks using a LinearGradient.
     * This gives the flat 2D rectangles a retro arcade look with highlights and shadows.
     *
     * @param baseColor The primary color of the brick.
     * @return A LinearGradient paint object applied to the block.
     */
    private static LinearGradient makeRetroBevel(Color baseColor) {
        return new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, baseColor.deriveColor(0, 1, 1.5, 1)), // Highlight (top-left)
                new Stop(0.5, baseColor),                           // Mid-tone
                new Stop(1.0, baseColor.deriveColor(0, 1, 0.6, 1))  // Shadow (bottom-right)
        );
    }
}