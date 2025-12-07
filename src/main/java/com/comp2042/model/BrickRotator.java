/*
 * BrickRotator.java
 *
 * This class manages the rotation state of the active brick.
 * Since each brick can have multiple orientations (matrices), this class
 * tracks the current index and calculates the next one.
 *
 * It ensures that rotation cycles correctly (e.g., from state 3 back to state 0)
 * without crashing the game.
 */
package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    // Tracks the current rotation index (0, 1, 2, 3)
    private int currentShape = 0;

    // Calculates what the Next rotation would look like without actually applying it.
    // This allows the board to check for collisions before committing to the move.
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;

        // Cycle to the next shape index using modulo arithmetic.
        // If we are at the last shape, this wraps around to 0.
        nextShape = (++nextShape) % brick.getShapeMatrix().size();

        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    // Returns the 2D matrix for the current orientation
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    // Updates the state to a new rotation index.
    // This is called only after the board confirms the move is valid.
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    // Assigns a new brick to control and resets rotation to default (0)
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
}