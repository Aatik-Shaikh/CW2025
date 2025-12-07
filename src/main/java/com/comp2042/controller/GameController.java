/*
 * This class acts as the central logic coordinator for the game.
 * It implements the InputEventListener interface to react to user actions
 * passed from the GUI and updates the game model accordingly.
 *
 * Key Responsibilities:
 * 1. Initializing the game board and connecting it to the view.
 * 2. Managing game speed and difficulty progression based on the score/level.
 * 3. Processing gameplay events (move down, left, right, rotate) and updating the model.
 * 4. Handling special mechanics like Hard Drop and Hold Piece.
 * 5. Detecting Game Over conditions and high score updates.
 */
package com.comp2042.controller;

import com.comp2042.*;
import com.comp2042.events.EventSource;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.model.*;

public class GameController implements InputEventListener {

    // The game board model which holds the state of the grid and pieces
    private Board board = new SimpleBoard(GameConfig.ROWS, GameConfig.COLS);
    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;

        board.createNewBrick();
        viewGuiController.setEventListener(this);

        viewGuiController.initGameView(
                board.getBoardMatrix(),
                board.getViewData()
        );

        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindExtraStats(board.getScore());

        setupSpeedAdjustment();
        updateSpeed(board.getScore().levelProperty().get());
    }
    // Starts the game loop after the countdown animation finishes
    public void startGame() {
        viewGuiController.startCountdown(() -> {
            viewGuiController.getTimeline().play();
            viewGuiController.startClock();
        });
    }

    // Sets up a listener to monitor level changes and adjust game speed dynamically
    private void setupSpeedAdjustment() {
        board.getScore().levelProperty().addListener((obs, oldVal, newVal) -> {
            updateSpeed(newVal.intValue());
        });
    }

    // Calculates the speed multiplier based on the current level
    private void updateSpeed(int level) {
        double multiplier = 1.0 + (level - 1) * GameConfig.LEVEL_SPEED_MULTIPLIER;
        viewGuiController.getTimeline().setRate(multiplier);
    }


    //Handles the Hold Piece event triggered by the user
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        board.holdBrick();
        return board.getViewData();
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            // Delegates score calculation to the Score model to handle combo multipliers
            board.getScore().processLineClear(clearRow.getLinesRemoved(), clearRow.getScoreBonus());

            if (board.createNewBrick()) {
                HighScoreManager.addScore(board.getScore().scoreProperty().get());
                viewGuiController.gameOver();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    // Process the Hard Drop event
    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        ViewData currentView = board.getViewData();
        int startX = currentView.getxPosition();
        int startY = currentView.getyPosition();
        int[][] shape = currentView.getBrickData();

        int linesDropped = board.dropBrickToBottom();

        if (linesDropped > 0) {
            viewGuiController.showHardDropTrail(startX, startY, linesDropped, shape);
        }

        // Award points for hard dropping (2 points per line)
        board.getScore().add(linesDropped * 2);

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        // Delegates score calculation to the Score model to handle combo multipliers
        board.getScore().processLineClear(clearRow.getLinesRemoved(), clearRow.getScoreBonus());

        if (board.createNewBrick()) {
            HighScoreManager.addScore(board.getScore().scoreProperty().get());
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        return new DownData(clearRow, board.getViewData());
    }

    // Handles movement to the left
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    // Handles movement to the right
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    // Handles rotation
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    // Resets the game state to start a new session
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        updateSpeed(board.getScore().levelProperty().get());
    }
}