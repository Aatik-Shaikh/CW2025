package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    // [FIX] Initialize with selected start level
    private final IntegerProperty level = new SimpleIntegerProperty(GameConfig.START_LEVEL);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);

    private static final int LINES_PER_LEVEL = 10;

    public IntegerProperty scoreProperty() { return score; }
    public IntegerProperty levelProperty() { return level; }
    public IntegerProperty linesClearedProperty() { return linesCleared; }

    public void add(int points) {
        score.set(score.get() + points);
    }

    public void addLines(int lines) {
        linesCleared.set(linesCleared.get() + lines);
        if (linesCleared.get() / LINES_PER_LEVEL + 1 > level.get()) {
            level.set(level.get() + 1);
        }
    }

    public void reset() {
        score.set(0);
        // [FIX] Use the user's selection when resetting
        level.set(GameConfig.START_LEVEL);
        linesCleared.set(0);
    }
}