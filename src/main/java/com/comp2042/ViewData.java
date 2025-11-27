package com.comp2042;

import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition; // [NEW] Holds the predicted drop Y coordinate
    private final List<int[][]> nextBricksData;

    public ViewData(int[][] brickData, int x, int y, int ghostY, List<int[][]> nextBricksData) {
        this.brickData = MatrixOperations.copy(brickData);
        this.xPosition = x;
        this.yPosition = y;
        this.ghostYPosition = ghostY; // [NEW]

        // Deep copy the list of matrices
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
}