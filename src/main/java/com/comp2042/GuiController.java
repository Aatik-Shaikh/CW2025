package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import static com.comp2042.GameConfig.*;

public class GuiController implements Initializable {

    // ======================
    // FXML UI COMPONENTS
    // ======================
    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private Pane gameZone;
    @FXML private GameOverPanel gameOverPanel;

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;

    @FXML private GridPane nextPiecePanel;

    // ======================
    // INTERNAL STATE
    // ======================
    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;

    private InputEventListener eventListener;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();


    // ======================
    // INITIALIZATION
    // ======================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load font safely
        if (getClass().getClassLoader().getResource("digital.ttf") != null) {
            Font.loadFont(
                    getClass().getClassLoader().getResource("digital.ttf").toExternalForm(),
                    38
            );
        }

        // Apply spacing
        gamePanel.setHgap(BOARD_GAP);
        gamePanel.setVgap(BOARD_GAP);

        brickPanel.setHgap(BOARD_GAP);
        brickPanel.setVgap(BOARD_GAP);

        if (nextPiecePanel != null) {
            nextPiecePanel.setHgap(BOARD_GAP);
            nextPiecePanel.setVgap(BOARD_GAP);
        }

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        setupKeyControls();

        gameOverPanel.setVisible(false);
    }


    private void setupKeyControls() {

        gamePanel.setOnKeyPressed(event -> {

            if (!isPause.get() && !isGameOver.get()) {

                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A)
                    refreshBrick(eventListener.onLeftEvent(
                            new MoveEvent(EventType.LEFT, EventSource.USER)));

                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D)
                    refreshBrick(eventListener.onRightEvent(
                            new MoveEvent(EventType.RIGHT, EventSource.USER)));

                if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W)
                    refreshBrick(eventListener.onRotateEvent(
                            new MoveEvent(EventType.ROTATE, EventSource.USER)));

                if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S)
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            }

            if (event.getCode() == KeyCode.N)
                newGame(null);
        });
    }


    // ======================
    // GAME VIEW CREATION
    // ======================
    public void initGameView(int[][] boardMatrix, ViewData brick) {

        // Allocate display grid for board
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];

        for (int i = VISIBLE_ROW_OFFSET; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {

                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);

                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - VISIBLE_ROW_OFFSET);
            }
        }

        // Falling brick grid
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];

        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {

                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));

                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }

        updateBrickPanelPosition(brick);
        renderNextPiece(brick.getNextBrickData());

        // --- SIZE & CLIPPING LOGIC ---

        // 1. Calculate the EXACT size of the visible board
        double boardWidth = COLS * BRICK_SIZE + (COLS - 1) * BOARD_GAP;
        double boardHeight = (ROWS - VISIBLE_ROW_OFFSET) * BRICK_SIZE + (ROWS - VISIBLE_ROW_OFFSET - 1) * BOARD_GAP;

        // 2. FORCE the gameZone to this size so the BorderPane doesn't wiggle
        gameZone.setPrefSize(boardWidth, boardHeight);
        gameZone.setMinSize(boardWidth, boardHeight);
        gameZone.setMaxSize(boardWidth, boardHeight);

        // 3. Clip content that falls outside (like bricks moving out of bounds)
        Rectangle clip = new Rectangle(0, 0, boardWidth, boardHeight);
        gameZone.setClip(clip);

        // -----------------------------

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(DROP_SPEED_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
    }


    // ======================
    // POSITIONING
    // ======================
    private void updateBrickPanelPosition(ViewData brick) {
        // Simple relative positioning

        double x = brick.getxPosition() * (BRICK_SIZE + BOARD_GAP);

        // Subtract VISIBLE_ROW_OFFSET (2) so rows 0-1 are "above" the board (negative Y)
        double y = (brick.getyPosition() - VISIBLE_ROW_OFFSET) * (BRICK_SIZE + BOARD_GAP);

        brickPanel.setLayoutX(x);
        brickPanel.setLayoutY(y);
    }

    // ======================
    // NEXT PIECE DISPLAY
    // ======================
    private void renderNextPiece(int[][] nextData) {

        nextPiecePanel.getChildren().clear();

        for (int r = 0; r < nextData.length; r++) {
            for (int c = 0; c < nextData[r].length; c++) {

                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);

                rect.setFill(nextData[r][c] != 0
                        ? getFillColor(nextData[r][c])
                        : Color.TRANSPARENT);

                nextPiecePanel.add(rect, c, r);
            }
        }
    }


    // ======================
    // COLOR MAPPING
    // ======================
    private Paint getFillColor(int id) {
        if (id < 0 || id >= COLORS.length)
            return Color.WHITE;

        return COLORS[id];
    }


    // ======================
    // BRICK RENDERING
    // ======================
    private void refreshBrick(ViewData brick) {

        if (!isPause.get()) {

            updateBrickPanelPosition(brick);

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }

            renderNextPiece(brick.getNextBrickData());
        }
    }


    public void refreshGameBackground(int[][] board) {

        for (int i = VISIBLE_ROW_OFFSET; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }


    private void setRectangleData(int color, Rectangle r) {
        r.setFill(getFillColor(color));
        r.setArcHeight(9);
        r.setArcWidth(9);
    }


    // ======================
    // MOVEMENT
    // ======================
    private void moveDown(MoveEvent event) {

        if (!isPause.get()) {

            DownData data = eventListener.onDownEvent(event);

            if (data.getClearRow() != null && data.getClearRow().getLinesRemoved() > 0) {

                NotificationPanel panel =
                        new NotificationPanel("+" + data.getClearRow().getScoreBonus());

                groupNotification.getChildren().add(panel);
                panel.showScore(groupNotification.getChildren());
            }

            refreshBrick(data.getViewData());
        }

        gamePanel.requestFocus();
    }


    // ======================
    // SCORE + LEVEL UI
    // ======================
    public void bindScore(IntegerProperty score) {
        scoreLabel.textProperty().bind(score.asString("Score: %d"));
    }

    public void bindExtraStats(Score scoreObj) {
        levelLabel.textProperty().bind(scoreObj.levelProperty().asString("Level: %d"));
        linesLabel.textProperty().bind(scoreObj.linesClearedProperty().asString("Lines: %d"));
    }


    // ======================
    // GAME MANAGEMENT
    // ======================
    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.set(true);
    }


    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);

        eventListener.createNewGame();
        gamePanel.requestFocus();

        timeLine.play();
        isPause.set(false);
        isGameOver.set(false);
    }


    // ======================
    // REQUIRED BY GameController
    // ======================
    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    public Timeline getTimeline() {
        return timeLine;
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}