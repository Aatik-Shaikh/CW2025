package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    // [NEW] Method to peek at the next 'count' bricks without removing them
    List<Brick> getNextBricks(int count);
}