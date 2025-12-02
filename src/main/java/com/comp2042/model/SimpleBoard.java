package com.comp2042.model;

import com.comp2042.GameConfig;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.MatrixOperations;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleBoard implements Board {

    private final BrickGenerator gen;
    private final BrickRotator rot;
    private int[][] matrix;
    private Point p;
    private final Score score;

    private Brick currentBrick;
    private Brick holdBrick;
    private boolean canHold = true;

    public SimpleBoard(int width, int height) {
        matrix = new int[GameConfig.ROWS][GameConfig.COLS];
        gen = new RandomBrickGenerator();
        rot = new BrickRotator();
        score = new Score();
    }

    private boolean checkCollision(int[][] m, int[][] shape, int x, int y) {
        return !MatrixOperations.intersect(m, shape, x, y);
    }

    private boolean attemptMove(int dx, int dy) {
        int[][] current = MatrixOperations.copy(matrix);
        Point nextP = new Point(p);
        nextP.translate(dx, dy);

        if (!checkCollision(current, rot.getCurrentShape(), nextP.x, nextP.y)) {
            return false;
        }
        p = nextP;
        return true;
    }

    @Override
    public boolean moveBrickDown() {
        return attemptMove(0, 1);
    }

    @Override
    public boolean moveBrickLeft() {
        return attemptMove(-1, 0);
    }

    @Override
    public boolean moveBrickRight() {
        return attemptMove(1, 0);
    }

    @Override
    public boolean rotateLeftBrick() {
        int[][] board = MatrixOperations.copy(matrix);
        NextShapeInfo info = rot.getNextShape();
        int[][] rShape = info.getShape();
        int x = p.x;
        int y = p.y;

        if (checkCollision(board, rShape, x, y)) {
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        if (checkCollision(board, rShape, x - 1, y)) {
            p.translate(-1, 0);
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        if (checkCollision(board, rShape, x + 1, y)) {
            p.translate(1, 0);
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        return false;
    }

    @Override
    public int dropBrickToBottom() {
        int dropped = 0;
        while (attemptMove(0, 1)) {
            dropped++;
        }
        return dropped;
    }

    @Override
    public boolean createNewBrick() {
        currentBrick = gen.getBrick();
        rot.setBrick(currentBrick);
        p = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);

        canHold = true;

        return MatrixOperations.intersect(matrix, rot.getCurrentShape(), p.x, p.y);
    }

    @Override
    public void holdBrick() {
        if (!canHold) return;

        if (holdBrick == null) {
            holdBrick = currentBrick;
            createNewBrick();
        } else {
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            currentBrick = temp;

            rot.setBrick(currentBrick);
            p = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);
        }
        canHold = false;
    }

    @Override
    public void mergeBrickToBackground() {
        matrix = MatrixOperations.merge(matrix, rot.getCurrentShape(), p.x, p.y);
    }

    @Override
    public ClearRow clearRows() {
        ClearRow result = MatrixOperations.checkRemoving(matrix);
        matrix = result.getNewMatrix();
        return result;
    }

    @Override
    public int[][] getBoardMatrix() {
        return matrix;
    }

    @Override
    public ViewData getViewData() {
        List<Brick> upcoming = gen.getNextBricks(3);
        List<int[][]> shapes = new ArrayList<>();

        for (Brick b : upcoming) {
            shapes.add(b.getShapeMatrix().get(0));
        }

        int ghostY = p.y;
        while (checkCollision(matrix, rot.getCurrentShape(), p.x, ghostY + 1)) {
            ghostY++;
        }

        int[][] holdData = (holdBrick != null) ? holdBrick.getShapeMatrix().get(0) : null;

        return new ViewData(rot.getCurrentShape(), p.x, p.y, ghostY, shapes, holdData);
    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        matrix = new int[GameConfig.ROWS][GameConfig.COLS];
        score.reset();
        holdBrick = null;
        createNewBrick();
    }
}