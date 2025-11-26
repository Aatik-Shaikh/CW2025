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
        Font.loadFont(
                getClass().getClassLoader().getResource("digital.ttf").toExternalForm(),
                38
        );

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

        for (int i = HIDDEN_ROWS; i < boardMatrix.length; i++) {
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

        double x = gamePanel.getLayoutX()
                + ACTIVE_BRICK_OFFSET_X
                + brick.getxPosition() * (BRICK_SIZE + BOARD_GAP);

        double y = gamePanel.getLayoutY()
                + ACTIVE_BRICK_OFFSET_Y
                + brick.getyPosition() * (BRICK_SIZE + BOARD_GAP);

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

        for (int i = HIDDEN_ROWS; i < board.length; i++) {
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

    // Allows GameController to register itself (input event handler)
    public void setEventListener(InputEventListener listener) {
        this.eventListener = listener;
    }

    // Allows GameController to change falling speed based on level
    public Timeline getTimeline() {
        return timeLine;
    }


    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}

