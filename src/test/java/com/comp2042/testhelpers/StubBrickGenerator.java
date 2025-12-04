package com.comp2042.testhelpers;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.IBrick;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class StubBrickGenerator implements BrickGenerator {

    private final Queue<Brick> brickQueue = new LinkedList<>();

    public void addBrick(Brick brick) {
        brickQueue.add(brick);
    }

    @Override
    public Brick getBrick() {
        if (brickQueue.isEmpty()) {
            return new IBrick(); // Default if empty
        }
        return brickQueue.poll();
    }

    @Override
    public Brick getNextBrick() {
        return brickQueue.peek();
    }

    @Override
    public List<Brick> getNextBricks(int count) {
        // Return dummy bricks for preview during testing
        List<Brick> next = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            next.add(new IBrick());
        }
        return next;
    }
}