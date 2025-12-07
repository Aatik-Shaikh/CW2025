/*
 * This class serves as an immutable Data Transfer Object (DTO) for the application.
 * It encapsulates the state of the active game components (falling brick, ghost piece,
 * next pieces, and held piece) at a specific moment in time.
 *
 * Purpose:
 * It allows the Game Logic (Model) to pass data to the User Interface (View/Controller)
 * without exposing internal mutable state. This ensures thread safety and prevents
 * the UI from accidentally modifying the game logic's data structures.
 */
package com.comp2042.model;

import com.comp2042.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition;
    private final List<int[][]> nextBricksData;

    // Data for the held piece (can be null if nothing is held)
    private final int[][] holdBrickData;

    public ViewData(int[][] brickData, int x, int y, int ghostY, List<int[][]> nextBricksData, int[][] holdBrickData) {
        // Defensive Copying: Create a deep copy of the array to ensure immutability
        this.brickData = MatrixOperations.copy(brickData);
        this.xPosition = x;
        this.yPosition = y;
        this.ghostYPosition = ghostY;

        // Copy hold data if it exists, otherwise set to null
        if (holdBrickData != null) {
            this.holdBrickData = MatrixOperations.copy(holdBrickData);
        } else {
            this.holdBrickData = null;
        }

        // Deep copy the list of upcoming bricks for the preview panel
        this.nextBricksData = new ArrayList<>();
        for (int[][] matrix : nextBricksData) {
            this.nextBricksData.add(MatrixOperations.copy(matrix));
        }
    }

    public int[][] getBrickData() {
        // Return a copy so the receiver cannot mutate the original internal array
        return MatrixOperations.copy(brickData);
    }


    /**
     * Retrieves the current x-coordinate of the active brick.
     *
     * @return The column index of the brick's top-left corner.
     */
    public int getxPosition() {
        return xPosition;
    }


    /**
     * Retrieves the current y-coordinate of the active brick.
     *
     * @return The row index of the brick's top-left corner.
     */
    public int getyPosition() {
        return yPosition;
    }


    /**
     * Retrieves the calculated y-coordinate for the "Ghost" piece.
     * This indicates where the brick would land if dropped instantly.
     *
     * @return The row index for the ghost piece.
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }


    /**
     * Retrieves the list of upcoming bricks for the preview panel.
     *
     * @return A list of 2D arrays representing the shapes of the next bricks.
     */
    public List<int[][]> getNextBrickData() {
        // Return a deep copy of the list to protect internal state
        List<int[][]> copy = new ArrayList<>();
        for (int[][] matrix : nextBricksData) {
            copy.add(MatrixOperations.copy(matrix));
        }
        return copy;
    }


    /**
     * Retrieves the shape data of the currently held brick.
     *
     * @return A 2D integer array representing the held brick, or null if no brick is held.
     */
    public int[][] getHoldBrickData() {
        return holdBrickData != null ? MatrixOperations.copy(holdBrickData) : null;
    }
}