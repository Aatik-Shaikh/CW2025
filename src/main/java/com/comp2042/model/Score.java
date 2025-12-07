/*
 * Score.java
 *
 * This class represents the player's progress in the game.
 * It tracks the current score, the current level, and the total lines cleared.
 *
 * Key Feature:
 * It uses JavaFX 'Properties' (IntegerProperty) instead of regular int variables.
 * This allows the UI (GuiController) to "bind" to these values. When the score
 * changes here, the label on the screen updates automatically without needing
 * a manual refresh call.
 */
package com.comp2042.model;

import com.comp2042.GameConfig;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    // Properties that the UI can observe
    private final IntegerProperty score = new SimpleIntegerProperty(0);

    // Initialize level based on the Start Menu selection using the getter method
    private final IntegerProperty level = new SimpleIntegerProperty(GameConfig.getStartLevel());

    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);

    // Scaling factor: Level up every 10 lines
    private static final int LINES_PER_LEVEL = 10;

    // Getters for the properties themselves (for UI binding)
    public IntegerProperty scoreProperty() { return score; }
    public IntegerProperty levelProperty() { return level; }
    public IntegerProperty linesClearedProperty() { return linesCleared; }

    // Adds points to the total score
    public void add(int points) {
        score.set(score.get() + points);
    }

    // Records cleared lines and checks if the player should level up
    public void addLines(int lines) {
        linesCleared.set(linesCleared.get() + lines);

        // Logic: Calculate if the new line count crosses the threshold for the next level
        if (linesCleared.get() / LINES_PER_LEVEL + 1 > level.get()) {
            level.set(level.get() + 1);
        }
    }

    // Resets all stats to their starting values for a new game
    public void reset() {
        score.set(0);
        // Ensure the level resets to the user's selected start level, not just 1
        level.set(GameConfig.getStartLevel());
        linesCleared.set(0);
    }
}