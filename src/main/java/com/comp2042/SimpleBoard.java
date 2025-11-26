package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

public class SimpleBoard implements Board {

    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    private Brick nextBrick;

    public SimpleBoard(int width, int height) {
        currentGameMatrix = new int[GameConfig.ROWS][GameConfig.COLS];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();

        // Initialize next piece queue
        nextBrick = brickGenerator.getBrick();
    }

    /**
     * Helper method: checks if a shape can be placed at (x, y)
     */
    private boolean canPlace(int[][] matrix, int[][] shape, int x, int y) {
        return !MatrixOperations.intersect(matrix, shape, x, y);
    }

    /**
     * Clean movement helper (used for left, right, down)
     */
    private boolean tryMove(int dx, int dy) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);

        Point p = new Point(currentOffset);
        p.translate(dx, dy);

        if (!canPlace(currentMatrix, brickRotator.getCurrentShape(), p.x, p.y)) {
            return false;
        }

        currentOffset = p;
        return true;
    }

    @Override
    public boolean moveBrickDown() {
        return tryMove(0, 1);
    }

    @Override
    public boolean moveBrickLeft() {
        return tryMove(-1, 0);
    }

    @Override
    public boolean moveBrickRight() {
        return tryMove(1, 0);
    }

    /**
     * Improved rotation with soft wall-kicks.
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] board = MatrixOperations.copy(currentGameMatrix);

        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] rotated = nextShape.getShape();

        int x = currentOffset.x;
        int y = currentOffset.y;

        // 1. Try rotate in place
        if (canPlace(board, rotated, x, y)) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // 2. Try wall-kick LEFT
        if (canPlace(board, rotated, x - 1, y)) {
            currentOffset.translate(-1, 0);
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // 3. Try wall-kick RIGHT
        if (canPlace(board, rotated, x + 1, y)) {
            currentOffset.translate(1, 0);
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }

        // No rotation possible
        return false;
    }

    @Override
    public boolean createNewBrick() {

        // Use queued next brick
        Brick currentBrick = nextBrick;
        brickRotator.setBrick(currentBrick);

        // Spawn at configured position
        currentOffset = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);

        // Queue the next brick
        nextBrick = brickGenerator.getBrick();

        // Collision at spawn => game over
        return MatrixOperations.intersect(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y
        );
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(
                currentGameMatrix,
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y
        );
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y,
                nextBrick.getShapeMatrix().get(0) // next piece preview
        );
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[GameConfig.ROWS][GameConfig.COLS];
        score.reset();
        createNewBrick();
    }
}
