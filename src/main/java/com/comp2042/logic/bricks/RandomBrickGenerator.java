/*
 *
 * This class is responsible for generating the sequence of bricks for the game.
 * It ensures that the game always has a steady supply of random pieces.
  */
package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();

        // [Design Pattern] Use the Factory to populate the list of available bricks.
        // IDs 1 to 7 correspond to the standard Tetris shapes (I, J, L, O, S, T, Z).
        // This approach avoids repetitive instantiation logic.
        for (int i = 1; i <= 7; i++) {
            brickList.add(BrickFactory.createBrick(i));
        }

        // Pre-fill the queue with 5 random bricks so the game can start immediately
        while (nextBricks.size() < 5) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
    }

    @Override
    public Brick getBrick() {
        // If the queue is running low (e.g., after the player places a piece),
        // automatically refill it to maintain the buffer for previews.
        if (nextBricks.size() <= 4) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        // Return (and remove) the next brick from the front of the queue
        return nextBricks.poll();
    }


    /**
     * Peeks at the next brick in the queue without removing it.
     *
     * @return The next Brick object that will be spawned.
     */
    @Override
    public Brick getNextBrick() {
        // Peek at the next brick without removing it (useful for logic checks)
        return nextBricks.peek();
    }


    /**
     * Retrieves a list of upcoming bricks from the queue for preview purposes.
     * Ensures the queue has enough bricks to satisfy the request before returning.
     *
     * @param count The number of future bricks to retrieve.
     * @return A list of the next 'count' Brick objects.
     */
    @Override
    public List<Brick> getNextBricks(int count) {
        // Ensure enough bricks are in the queue to satisfy the request
        while (nextBricks.size() < count) {
            nextBricks.add(brickList.get(ThreadLocalRandom.current().nextInt(brickList.size())));
        }
        // Return a list of the next 'count' bricks for the "Next Piece" UI panel.
        // The method uses stream().limit() to peek at the first N items without altering the queue.
        return nextBricks.stream().limit(count).collect(Collectors.toList());
    }
}