package com.comp2042.logic.bricks;

public class BrickFactory {

    public static Brick createBrick(int id) {
        switch (id) {
            case 1: return new IBrick();
            case 2: return new JBrick();
            case 3: return new LBrick();
            case 4: return new OBrick();
            case 5: return new SBrick();
            case 6: return new TBrick();
            case 7: return new ZBrick();
            default: return null;
        }
    }
}