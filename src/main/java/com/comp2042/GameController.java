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

    private void setupSpeedAdjustment() {
        board.getScore().levelProperty().addListener((obs, oldVal, newVal) -> {
            double multiplier = 1.0 + (newVal.intValue() - 1) * GameConfig.LEVEL_SPEED_MULTIPLIER;
            viewGuiController.getTimeline().setRate(multiplier);
        });
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                board.getScore().addLines(clearRow.getLinesRemoved());
            }

            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            }

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
    public DownData onHardDropEvent(MoveEvent event) {
        // [MODIFIED] 1. Capture state BEFORE dropping to draw the trail
        ViewData currentView = board.getViewData();
        int startX = currentView.getxPosition();
        int startY = currentView.getyPosition();
        int[][] shape = currentView.getBrickData();

        // 2. Drop instantly
        int linesDropped = board.dropBrickToBottom();

        // 3. Trigger Trail Effect
        if (linesDropped > 0) {
            viewGuiController.showHardDropTrail(startX, startY, linesDropped, shape);
        }

        // 4. Score bonus
        board.getScore().add(linesDropped * 2);

        // 5. Perform Lock Logic
        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }

        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());

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
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.getTimeline().setRate(1.0);
    }
}