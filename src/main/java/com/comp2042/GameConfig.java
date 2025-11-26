package com.comp2042;

import javafx.scene.paint.Color;


public final class GameConfig {

    // === Board Size ===
    public static final int ROWS = 25;
    public static final int COLS = 10;

    // Hidden rows where pieces spawn (original game used 2)
    public static final int HIDDEN_ROWS = 2;

    // Brick size in pixels
    public static final int BRICK_SIZE = 24;

    // Gaps between grid cells
    public static final int BOARD_GAP = 1;

    public static final int BRICK_PANEL_OFFSET_Y = -42;

    // Center spawn
    public static final int SPAWN_X = COLS / 2 - 2;
    public static final int SPAWN_Y = 0;

    private GameConfig() {}

    // Offset for positioning the falling brick panel relative to the game board
    public static final int ACTIVE_BRICK_OFFSET_X = 0;
    public static final int ACTIVE_BRICK_OFFSET_Y = -42;

    // How fast the brick drops (ms)
    public static final int DROP_SPEED_MS = 400;

    // Dynamic visible row offset (same as HIDDEN_ROWS but clearer name)
    public static final int VISIBLE_ROW_OFFSET = HIDDEN_ROWS;

    public static final Color[] COLORS = {
            Color.TRANSPARENT,  // 0
            Color.AQUA,         // 1
            Color.BLUEVIOLET,   // 2
            Color.DARKGREEN,    // 3
            Color.YELLOW,       // 4
            Color.RED,          // 5
            Color.BEIGE,        // 6
            Color.BURLYWOOD     // 7
    };


}
