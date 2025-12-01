package com.comp2042;

public class GameController implements InputEventListener {

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

        // [FIX] Force speed update on start
        updateSpeed(board.getScore().levelProperty().get());
    }

    public void startGame() {
        viewGuiController.startCountdown(() -> {
            viewGuiController.getTimeline().play();
            // Start clock
            viewGuiController.startClock();
        });
    }

    private void setupSpeedAdjustment() {
        board.getScore().levelProperty().addListener((obs, oldVal, newVal) -> {
            updateSpeed(newVal.intValue());
        });
    }

    private void updateSpeed(int level) {
        double multiplier = 1.0 + (level - 1) * GameConfig.LEVEL_SPEED_MULTIPLIER;
        System.out.println("Applying Speed Multiplier: " + multiplier);
        viewGuiController.getTimeline().setRate(multiplier);
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

        board.getScore().add(linesDropped * 2);

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();

        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }

        if (board.createNewBrick()) {
            HighScoreManager.addScore(board.getScore().scoreProperty().get());
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
        updateSpeed(board.getScore().levelProperty().get());
    }
}