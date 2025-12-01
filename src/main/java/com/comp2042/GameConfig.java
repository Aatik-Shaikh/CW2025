package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public final class GameConfig {

    public static final int ROWS = 25;
    public static final int COLS = 10;
    public static final int HIDDEN_ROWS = 2;
    public static final int BRICK_SIZE = 24;
    public static final int BOARD_GAP = 0;
    public static final int BRICK_PANEL_OFFSET_Y = -42;
    public static final int SPAWN_X = COLS / 2 - 2;
    public static final int SPAWN_Y = 0;

    public static int START_LEVEL = 1;

    private GameConfig() {}

    public static final int ACTIVE_BRICK_OFFSET_X = 0;
    public static final int ACTIVE_BRICK_OFFSET_Y = -42;
    public static final int DROP_SPEED_MS = 400;
    public static final int VISIBLE_ROW_OFFSET = HIDDEN_ROWS;
    public static final int BASE_DROP_SPEED_MS = 400;
    public static final int LINES_PER_LEVEL = 10;
    public static final double LEVEL_SPEED_MULTIPLIER = 0.20;

    // [MODIFIED] Use Paint[] for 3D Gradients
    public static final Paint[] COLORS = {
            Color.TRANSPARENT,                  // 0
            makeRetroBevel(Color.AQUA),         // 1
            makeRetroBevel(Color.BLUEVIOLET),   // 2
            makeRetroBevel(Color.DARKGREEN),    // 3
            makeRetroBevel(Color.YELLOW),       // 4
            makeRetroBevel(Color.RED),          // 5
            makeRetroBevel(Color.BEIGE),        // 6
            makeRetroBevel(Color.BURLYWOOD)     // 7
    };

    private static LinearGradient makeRetroBevel(Color baseColor) {
        return new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, baseColor.deriveColor(0, 1, 1.5, 1)),
                new Stop(0.5, baseColor),
                new Stop(1.0, baseColor.deriveColor(0, 1, 0.6, 1))
        );
    }
}