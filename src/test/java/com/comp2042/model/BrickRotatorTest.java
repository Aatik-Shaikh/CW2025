package com.comp2042.model;

import com.comp2042.logic.bricks.IBrick;
import com.comp2042.logic.bricks.JBrick;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BrickRotatorTest {

    @Test
    void testInitialState() {
        BrickRotator rotator = new BrickRotator();
        IBrick brick = new IBrick(); // I-Brick has 2 states
        rotator.setBrick(brick);

        // Default current shape should be index 0
        int[][] shape = rotator.getCurrentShape();

        // Compare with the actual first state of IBrick
        assertArrayEquals(brick.getShapeMatrix().get(0), shape, "Rotator should start at index 0");
    }

    @Test
    void testNextShapeCycle() {
        BrickRotator rotator = new BrickRotator();
        IBrick brick = new IBrick(); // I-Brick has 2 states (0 and 1)
        rotator.setBrick(brick);

        // 1. Get next shape (Should be index 1)
        NextShapeInfo nextInfo = rotator.getNextShape();
        assertEquals(1, nextInfo.getPosition(), "First rotation should move to index 1");

        // 2. Update rotator to that state
        rotator.setCurrentShape(nextInfo.getPosition());

        // 3. Get next shape again (Should wrap back to index 0)
        NextShapeInfo wrapInfo = rotator.getNextShape();
        assertEquals(0, wrapInfo.getPosition(), "Second rotation should wrap back to index 0");
    }

    @Test
    void testDifferentBrickType() {
        BrickRotator rotator = new BrickRotator();
        JBrick brick = new JBrick(); // J-Brick has 4 states
        rotator.setBrick(brick);

        // Rotate 4 times
        rotator.setCurrentShape(rotator.getNextShape().getPosition()); // 1
        rotator.setCurrentShape(rotator.getNextShape().getPosition()); // 2
        rotator.setCurrentShape(rotator.getNextShape().getPosition()); // 3

        // Next one should wrap to 0
        assertEquals(0, rotator.getNextShape().getPosition(), "Should wrap to 0 after 4 rotations");
    }
}