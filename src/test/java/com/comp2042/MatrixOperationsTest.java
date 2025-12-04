package com.comp2042;

import com.comp2042.model.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void testIntersect_CollisionWithBlock() {
        int[][] board = new int[20][10];
        board[5][5] = 1;

        int[][] brick = { {1} };

        boolean result = MatrixOperations.intersect(board, brick, 5, 5);

        assertTrue(result, "Should detect collision when brick overlaps a filled cell");
    }

    @Test
    void testIntersect_NoCollision() {
        int[][] board = new int[20][10];
        int[][] brick = { {1} };

        boolean result = MatrixOperations.intersect(board, brick, 5, 5);

        assertFalse(result, "Should not detect collision in empty space");
    }

    @Test
    void testIntersect_OutOfBounds() {
        int[][] board = new int[20][10];
        int[][] brick = { {1} };

        boolean leftWall = MatrixOperations.intersect(board, brick, -1, 5);

        assertTrue(leftWall, "Should detect collision when X is out of bounds (Left)");
    }

    @Test
    void testCheckRemoving_FullLine() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 1, 1, 1}
        };

        ClearRow result = MatrixOperations.checkRemoving(board);

        assertEquals(1, result.getLinesRemoved(), "Should detect exactly 1 full line");
        assertEquals(50, result.getScoreBonus(), "Score bonus should be 50 for 1 line");

        int[][] newBoard = result.getNewMatrix();
        assertEquals(0, newBoard[3][0], "Bottom row should be empty (0) after clear");
    }

    @Test
    void testCheckRemoving_NoLine() {
        int[][] board = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 0, 1, 1}
        };

        ClearRow result = MatrixOperations.checkRemoving(board);

        assertEquals(0, result.getLinesRemoved(), "Should NOT clear line if it has a gap");
        assertEquals(0, result.getScoreBonus(), "Score bonus should be 0");
    }
}