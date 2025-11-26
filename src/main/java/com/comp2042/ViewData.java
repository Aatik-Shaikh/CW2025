package com.comp2042;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;

    public ViewData(int[][] brickData, int x, int y, int[][] nextBrickData) {
        this.brickData = MatrixOperations.copy(brickData);
        this.xPosition = x;
        this.yPosition = y;
        this.nextBrickData = MatrixOperations.copy(nextBrickData);
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

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
