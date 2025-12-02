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

    // [NEW] Data for the held piece (can be null/empty)
    private final int[][] holdBrickData;

    public ViewData(int[][] brickData, int x, int y, int ghostY, List<int[][]> nextBricksData, int[][] holdBrickData) {
        this.brickData = MatrixOperations.copy(brickData);
        this.xPosition = x;
        this.yPosition = y;
        this.ghostYPosition = ghostY;

        // [NEW] Copy hold data if it exists
        if (holdBrickData != null) {
            this.holdBrickData = MatrixOperations.copy(holdBrickData);
        } else {
            this.holdBrickData = null;
        }

        this.nextBricksData = new ArrayList<>();
        for (int[][] matrix : nextBricksData) {
            this.nextBricksData.add(MatrixOperations.copy(matrix));
        }
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    public List<int[][]> getNextBrickData() {
        List<int[][]> copy = new ArrayList<>();
        for (int[][] matrix : nextBricksData) {
            copy.add(MatrixOperations.copy(matrix));
        }
        return copy;
    }

    // [NEW]
    public int[][] getHoldBrickData() {
        return holdBrickData != null ? MatrixOperations.copy(holdBrickData) : null;
    }
}