package com.comp2042.model;

import com.comp2042.logic.bricks.IBrick;
import com.comp2042.testhelpers.StubBrickGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;
    private StubBrickGenerator stubGen;

    @BeforeEach
    void setUp() {
        stubGen = new StubBrickGenerator();
        stubGen.addBrick(new IBrick());
        stubGen.addBrick(new IBrick());

        board = new SimpleBoard(10, 20, stubGen);
        board.createNewBrick();
    }

    @Test
    void testBoardInitialization() {
        int[][] matrix = board.getBoardMatrix();
        for (int[] row : matrix) {
            for (int cell : row) {
                assertEquals(0, cell, "Board should be empty on start");
            }
        }
    }

    @Test
    void testBrickMovement() {
        int initialX = board.getViewData().getxPosition();

        boolean moved = board.moveBrickRight();

        assertTrue(moved, "Brick should move right in empty space");
        assertEquals(initialX + 1, board.getViewData().getxPosition(), "X position should increase by 1");
    }

    @Test
    void testBrickDrop() {
        int initialY = board.getViewData().getyPosition();

        boolean moved = board.moveBrickDown();

        assertTrue(moved, "Brick should move down");
        assertEquals(initialY + 1, board.getViewData().getyPosition(), "Y position should increase by 1");
    }

    @Test
    void testRotation() {

        board.rotateLeftBrick();
        assertNotNull(board.getViewData().getBrickData(), "Brick data should not be null after rotation");
    }
}