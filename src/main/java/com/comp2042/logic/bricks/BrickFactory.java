/**
 * A Factory class for creating Brick objects.
 * Decouples brick instantiation from the game logic using integer IDs.
 */
package com.comp2042.logic.bricks;

public class BrickFactory {

    /**
     * Creates and returns a specific Brick instance based on the provided ID.
     *
     * @param id The integer identifier for the brick type (1-7).
     * @return A new instance of the corresponding Brick subclass, or null if the ID is invalid.
     */
    public static Brick createBrick(int id) {
        return switch (id) {
            case 1 -> new IBrick();
            case 2 -> new JBrick();
            case 3 -> new LBrick();
            case 4 -> new OBrick();
            case 5 -> new SBrick();
            case 6 -> new TBrick();
            case 7 -> new ZBrick();
            default -> null; // Handle invalid IDs gracefully
        };
    }
}