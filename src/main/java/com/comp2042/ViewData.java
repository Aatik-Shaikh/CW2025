package com.comp2042;

import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBricksData; // [MODIFIED] Now holds a list of matrices

    public ViewData(int[][] brickData, int x, int y, List<int[][]> nextBricksData) {
        this.brickData = MatrixOperations.copy(brickData);
        this.xPosition = x;
        this.yPosition = y;

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

    public List<int[][]> getNextBrickData() {
        // Return a safe copy
        List<int[][]> copy = new ArrayList<>();
        for (int[][] matrix : nextBricksData) {
            copy.add(MatrixOperations.copy(matrix));
        }
        return copy;
    }
}