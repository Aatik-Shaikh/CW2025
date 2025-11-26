package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);

    private static final int LINES_PER_LEVEL = 10;

    // ====================
    // SCORE ACCESSORS
    // ====================
    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty linesClearedProperty() {
        return linesCleared;
    }

    // ====================
    // SCORE MANIPULATION
    // ====================

    public void add(int points) {
        score.set(score.get() + points);
    }

    public void addLines(int lines) {
        // Increase total cleared lines
        linesCleared.set(linesCleared.get() + lines);

        // Level up check
        if (linesCleared.get() / LINES_PER_LEVEL + 1 > level.get()) {
            level.set(level.get() + 1);
        }
    }

    // Reset everything when starting new game
    public void reset() {
        score.set(0);
        level.set(1);
        linesCleared.set(0);
    }
}
