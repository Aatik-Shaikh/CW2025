/*
 * This class implements the Factory Method Design Pattern.
 *
 * Purpose:
 * It encapsulates the logic for instantiating concrete Brick objects (IBrick, JBrick, etc.).
 * Instead of using the 'new' keyword scattered throughout the code (specifically in the generator),
 * the application delegates creation to this factory.
 *
 * Benefits:
 * 1. Decoupling: The client code (RandomBrickGenerator) does not need to know the specific
 * class names of the bricks, only the interface 'Brick'.
 * 2. Scalability: Adding a new brick type in the future only requires updating this single file,
 * adhering to the Open/Closed Principle.
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