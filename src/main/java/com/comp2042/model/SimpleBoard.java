/*
 * This class represents the main game logic and state.
 * It manages the grid (matrix), the active falling piece, collision detection,
 * and game rules like line clearing and holding pieces.
 *
 * It implements the Board interface to decouple logic from the controller.
 */
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

    // The generator decides which brick comes next (Random or Custom)
    private final BrickGenerator gen;
    // Helper to handle brick rotation logic
    private final BrickRotator rot;

    // The 2D grid representing the game board (0 means empty, other numbers are colors)
    private int[][] matrix;
    // Current coordinates (x, y) of the falling brick
    private Point p;
    // Tracks the game score and level
    private final Score score;

    // Variables for the active brick and the Hold Piece mechanic
    private Brick currentBrick;
    private Brick holdBrick;
    // This flag prevents the user from swapping pieces infinitely in a single turn
    private boolean canHold = true;

    // Default constructor used by the actual game application.
    // It creates a RandomBrickGenerator so the game is unpredictable.
    public SimpleBoard(int width, int height) {
        this(width, height, new RandomBrickGenerator());
    }

    // Special constructor used for JUNIT TESTING.
    // This facilitates Dependency Injection, allowing a stub generator
    // to be passed in. This ensures tests run with specific, predictable
    // bricks (e.g. only I-Bricks) rather than random ones.
    public SimpleBoard(int width, int height, BrickGenerator generator) {
        matrix = new int[GameConfig.ROWS][GameConfig.COLS];
        this.gen = generator;
        this.rot = new BrickRotator();
        this.score = new Score();
    }

    // Helper to check if a brick is hitting a wall or another block
    // Returns true if the position is VALID (no collision)
    private boolean checkCollision(int[][] m, int[][] shape, int x, int y) {
        return !MatrixOperations.intersect(m, shape, x, y);
    }

    // Tries to move the brick by a specific amount (dx, dy).
    // If it hits something, it returns false and doesn't update the position.
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


    /**
     * Attempts to move the active brick one column to the left.
     * Checks for collisions with the left wall or existing blocks.
     *
     * @return true if the move was successful, false otherwise.
     */
    @Override
    public boolean moveBrickLeft() {
        return attemptMove(-1, 0);
    }


    /**
     * Attempts to move the active brick one column to the right.
     * Checks for collisions with the right wall or existing blocks.
     *
     * @return true if the move was successful, false otherwise.
     */
    @Override
    public boolean moveBrickRight() {
        return attemptMove(1, 0);
    }



    /**
     * Rotates the active brick 90 degrees counter-clockwise.
     * Implements wall-kick logic: if the rotation causes a collision, it attempts to
     * shift the brick left or right to find a valid position.
     *
     * @return true if rotation succeeded (possibly with a kick); false if no valid position was found.
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] board = MatrixOperations.copy(matrix);
        NextShapeInfo info = rot.getNextShape();
        int[][] rShape = info.getShape();
        int x = p.x;
        int y = p.y;

        // Normal rotation check
        if (checkCollision(board, rShape, x, y)) {
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        // Wall Kick: Try moving left 1 space
        if (checkCollision(board, rShape, x - 1, y)) {
            p.translate(-1, 0);
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        // Wall Kick: Try moving right 1 space
        if (checkCollision(board, rShape, x + 1, y)) {
            p.translate(1, 0);
            rot.setCurrentShape(info.getPosition());
            return true;
        }
        return false;
    }


    /**
     * Instantly drops the brick to the lowest valid position in its current column.
     *
     * @return The number of rows the brick was dropped.
     */
    @Override
    public int dropBrickToBottom() {
        int dropped = 0;
        while (attemptMove(0, 1)) {
            dropped++;
        }
        return dropped;
    }


    /**
     * Spawns a new active brick at the top center of the board.
     * Also resets the hold permission for the new turn.
     *
     * @return true if the brick spawned successfully; false if it collided immediately (Game Over).
     */
    @Override
    public boolean createNewBrick() {
        // Get the next brick from the generator (Random or Stub)
        currentBrick = gen.getBrick();
        rot.setBrick(currentBrick);

        // Reset spawn position to top center
        p = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);

        // Reset the hold flag so the player can use Hold again for this new turn
        canHold = true;

        // If the new piece collides immediately, it means Game Over
        return MatrixOperations.intersect(matrix, rot.getCurrentShape(), p.x, p.y);
    }

    // Implements the Hold Piece mechanic logic
    @Override
    public void holdBrick() {
        // If the user already swapped this turn, prevent another swap
        if (!canHold) return;

        if (holdBrick == null) {
            // If hold is empty, store current brick and spawn a new one
            holdBrick = currentBrick;
            createNewBrick();
        } else {
            // Swap current brick with the held brick
            Brick temp = holdBrick;
            holdBrick = currentBrick;
            currentBrick = temp;

            // Set the swapped brick as active and reset position
            rot.setBrick(currentBrick);
            p = new Point(GameConfig.SPAWN_X, GameConfig.SPAWN_Y);
        }

        // Lock hold until the piece lands
        canHold = false;
    }

    // Locks the current brick into the static background matrix
    @Override
    public void mergeBrickToBackground() {
        matrix = MatrixOperations.merge(matrix, rot.getCurrentShape(), p.x, p.y);
    }

    // Checks for full lines, removes them, and returns info about lines cleared
    @Override
    public ClearRow clearRows() {
        ClearRow result = MatrixOperations.checkRemoving(matrix);
        matrix = result.getNewMatrix();
        return result;
    }

    /**
     * Retrieves the current state of the entire game board grid.
     *
     * @return A 2D integer array representing the grid, where 0 is empty and other numbers are color IDs.
     */
    @Override
    public int[][] getBoardMatrix() {
        return matrix;
    }


    /**
     * Compiles all necessary game state data into a Data Transfer Object for the View.
     * This includes the active brick, ghost position, next pieces, and hold piece.
     *
     * @return A immutable ViewData object.
     */
    @Override
    public ViewData getViewData() {
        // Get previews of the next 3 bricks
        List<Brick> upcoming = gen.getNextBricks(3);
        List<int[][]> shapes = new ArrayList<>();

        for (Brick b : upcoming) {
            shapes.add(b.getShapeMatrix().get(0));
        }

        // Calculate Ghost Piece position (shows where the block will land)
        int ghostY = p.y;
        while (checkCollision(matrix, rot.getCurrentShape(), p.x, ghostY + 1)) {
            ghostY++;
        }

        // Get data for the held brick (if any) to show in the UI
        int[][] holdData = (holdBrick != null) ? holdBrick.getShapeMatrix().get(0) : null;

        return new ViewData(rot.getCurrentShape(), p.x, p.y, ghostY, shapes, holdData);
    }


    /**
     * Retrieves the Score model associated with this board.
     *
     * @return The Score object tracking points, lines, and levels.
     */
    @Override
    public Score getScore() {
        return score;
    }


    /**
     * Resets the board state for a completely new game.
     * Clears the matrix, resets the score, clears the hold slot, and spawns the first brick.
     */
    @Override
    public void newGame() {
        matrix = new int[GameConfig.ROWS][GameConfig.COLS];
        score.reset();
        holdBrick = null; // Clear held brick on restart
        createNewBrick();
    }
}