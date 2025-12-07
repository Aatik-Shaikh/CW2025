package com.comp2042.events;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;


/**
 * Interface for handling game-related input events.
 * Implementations of this interface (like GameController) define how the game
 * responds to user actions such as moving, rotating, or dropping a brick.
 */
public interface InputEventListener {


    /**
     * Handles the 'Down' movement event.
     * This is triggered when the brick moves down automatically (gravity) or by user input.
     *
     * @param event The move event details containing the source and type.
     * @return DownData containing the result of the move (e.g., cleared rows, updated board state).
     */
    DownData onDownEvent(MoveEvent event);


    /**
     * Handles the 'Left' movement event.
     * Triggered when the user presses the Left Arrow or 'A' key.
     *
     * @param event The move event details.
     * @return ViewData containing the updated position of the brick for rendering.
     */
    ViewData onLeftEvent(MoveEvent event);


    /**
     * Handles the 'Right' movement event.
     * Triggered when the user presses the Right Arrow or 'D' key.
     *
     * @param event The move event details.
     * @return ViewData containing the updated position of the brick for rendering.
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the 'Rotate' movement event.
     * Triggered when the user presses the Up Arrow or 'W' key.
     *
     * @param event The move event details.
     * @return ViewData containing the new orientation of the brick for rendering.
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the 'Hard Drop' event.
     * Triggered when the user presses the Spacebar to instantly drop the piece.
     *
     * @param event The move event details.
     * @return DownData containing the final state of the board after the drop and any cleared lines.
     */
    DownData onHardDropEvent(MoveEvent event);


    /**
     * Handles the 'Hold Piece' event.
     * Triggered when the user presses the 'C' key to swap the current piece.
     *
     * @param event The move event details.
     * @return ViewData containing the updated board state, including the newly held or swapped piece.
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Resets the game logic to start a new session.
     * Clears the board, resets the score, and spawns a new brick.
     */
    void createNewGame();
}