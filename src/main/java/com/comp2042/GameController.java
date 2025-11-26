package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(GameConfig.ROWS, GameConfig.COLS);
    private final GuiController viewGuiController;



    public GameController(GuiController c) {
        viewGuiController = c;

        // Initialize first brick
        board.createNewBrick();

        // Connect GUI -> Controller
        viewGuiController.setEventListener(this);

        // Create visual board
        viewGuiController.initGameView(
                board.getBoardMatrix(),
                board.getViewData()
        );

        // Bind all the score UI elements
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindExtraStats(board.getScore());

        // Bind dynamic speed based on level
        setupSpeedAdjustment();
    }

    /**
     * Makes the falling speed increase automatically whenever the level increases.
     */
    private void setupSpeedAdjustment() {
        board.getScore().levelProperty().addListener((obs, oldVal, newVal) -> {

            // Level increases → improve drop speed
            double multiplier = 1.0 + (newVal.intValue() - 1) * GameConfig.LEVEL_SPEED_MULTIPLIER;

            // Increase timeline speed
            viewGuiController.getTimeline().setRate(multiplier);
        });
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {

        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {

            // Merge falling piece into background
            board.mergeBrickToBackground();

            // Clear any filled rows
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {

                // Add score bonus
                board.getScore().add(clearRow.getScoreBonus());

                // Add lines cleared + update level automatically
                board.getScore().addLines(clearRow.getLinesRemoved());
            }

            // Spawn next brick — if overlapping → game over
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

            // Redraw the background matrix
            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            // Reward manual DOWN key press
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }

        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();

        // Reset display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());

        // Reset speed to level 1
        viewGuiController.getTimeline().setRate(1.0);
    }
}
