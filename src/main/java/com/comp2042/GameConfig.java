package com.comp2042;

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

    // Tweak this if your brickPanel appears too high/low
    public static final int BRICK_PANEL_OFFSET_Y = -42;

    // Center spawn
    public static final int SPAWN_X = COLS / 2 - 2;
    public static final int SPAWN_Y = 0;

    private GameConfig() {}
}
