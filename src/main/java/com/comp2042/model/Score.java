/*
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

    // Tracks the number of lines cleared in consecutive moves for combo logic
    private int consecutiveLines = 0;

    // Scaling factor: Level up every 10 lines
    private static final int LINES_PER_LEVEL = 10;


    /**
     * Retrieves the observable property for the current score.
     * Used for binding the score label in the UI.
     *
     * @return The IntegerProperty representing the score.
     */
    public IntegerProperty scoreProperty() { return score; }

    /**
     * Retrieves the observable property for the current level.
     * Used for binding the level label in the UI.
     *
     * @return The IntegerProperty representing the level.
     */
    public IntegerProperty levelProperty() { return level; }

    /**
     * Retrieves the observable property for the total lines cleared.
     * Used for binding the lines label in the UI.
     *
     * @return The IntegerProperty representing lines cleared.
     */
    public IntegerProperty linesClearedProperty() { return linesCleared; }


    /**
     * Adds a specific amount of points to the total score.
     *
     * @param points The number of points to add.
     */
    public void add(int points) {
        score.set(score.get() + points);
    }


    /**
     * Increments the count of cleared lines and checks for level-up conditions.
     * If the threshold is met, the level property is incremented.
     *
     * @param lines The number of lines to add.
     */
    public void addLines(int lines) {
        linesCleared.set(linesCleared.get() + lines);

        // Logic: Calculate if the new line count crosses the threshold for the next level
        if (linesCleared.get() / LINES_PER_LEVEL + 1 > level.get()) {
            level.set(level.get() + 1);
        }
    }

    // Handles the logic for line clears, including the combo multiplier
    public void processLineClear(int lines, int scoreBonus) {
        if (lines > 0) {
            consecutiveLines += lines;
            // Applies a 1.5x multiplier if the user has cleared 5 or more lines consecutively
            if (consecutiveLines >= 5) {
                scoreBonus = (int) (scoreBonus * 1.5);
            }
            add(scoreBonus);
            addLines(lines);
        } else {
            // Resets the streak if a piece lands without clearing any lines
            consecutiveLines = 0;
        }
    }

    // Resets all stats to their starting values for a new game
    public void reset() {
        score.set(0);
        // Ensure the level resets to the user's selected start level, not just 1
        level.set(GameConfig.getStartLevel());
        linesCleared.set(0);
        consecutiveLines = 0;
    }
}