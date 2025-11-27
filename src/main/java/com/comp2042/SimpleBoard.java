package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    private boolean canPlace(int[][] matrix, int[][] shape, int x, int y) {
        return !MatrixOperations.intersect(matrix, shape, x, y);
    }

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

    @Override
    public boolean rotateLeftBrick() {
        int[][] board = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[][] rotated = nextShape.getShape();
        int x = currentOffset.x;
        int y = currentOffset.y;

        if (canPlace(board, rotated, x, y)) {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
        if (canPlace(board, rotated, x - 1, y)) {
            currentOffset.translate(-1, 0);
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
        if (canPlace(board, rotated, x + 1, y)) {
            currentOffset.translate(1, 0);
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
        return false;
    }

    @Override
    public int dropBrickToBottom() {
        int lines = 0;
        // Keep moving down until we can't anymore
        while (tryMove(0, 1)) {
            lines++;
        }
        return lines;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = nextBrick;
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);
        nextBrick = brickGenerator.getBrick();

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
        List<Brick> nextBricks = brickGenerator.getNextBricks(3);
        List<int[][]> nextShapes = new ArrayList<>();

        for (Brick b : nextBricks) {
            nextShapes.add(b.getShapeMatrix().get(0));
        }

        // Ghost calculation
        int ghostY = currentOffset.y;
        while (canPlace(currentGameMatrix, brickRotator.getCurrentShape(), currentOffset.x, ghostY + 1)) {
            ghostY++;
        }

        return new ViewData(
                brickRotator.getCurrentShape(),
                currentOffset.x,
                currentOffset.y,
                ghostY,
                nextShapes
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