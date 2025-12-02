package com.comp2042.controller;

import com.comp2042.*;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.model.DownData;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.comp2042.GameConfig.*;

public class GuiController implements Initializable {

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private GridPane ghostPanel;
    @FXML private Pane gameZone;

    @FXML private GameOverPanel gameOverPanel;

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;
    @FXML private Label timeLabel;

    @FXML private Label countdownLabel;
    @FXML private VBox pauseMenu;
    @FXML private VBox nextPiecePanel;

    // [NEW] Hold Piece UI Panel
    @FXML private VBox holdPiecePanel;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;

    private Group trailGroup;

    private InputEventListener eventListener;
    private Timeline timeLine;

    private Timeline clock;
    private int secondsPlayed = 0;

    private boolean isCountdownRunning = false;

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    private IntegerProperty currentScoreProperty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (getClass().getClassLoader().getResource("digital.ttf") != null) {
            Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        }

        gamePanel.setHgap(BOARD_GAP);
        gamePanel.setVgap(BOARD_GAP);
        brickPanel.setHgap(BOARD_GAP);
        brickPanel.setVgap(BOARD_GAP);
        ghostPanel.setHgap(BOARD_GAP);
        ghostPanel.setVgap(BOARD_GAP);

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();

        setupKeyControls();
        setupClock();

        gameOverPanel.setVisible(false);
    }

    private void setupClock() {
        clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsPlayed++;
            int m = secondsPlayed / 60;
            int s = secondsPlayed % 60;
            timeLabel.setText(String.format("%02d:%02d", m, s));
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
    }

    public void startClock() { clock.play(); }
    public void stopClock() { clock.stop(); }
    public void resetClock() {
        secondsPlayed = 0;
        timeLabel.setText("00:00");
    }

    private void setupKeyControls() {
        gamePanel.setOnKeyPressed(event -> {
            if (isCountdownRunning) return;

            if (event.getCode() == KeyCode.P || event.getCode() == KeyCode.ESCAPE) {
                togglePause();
                return;
            }

            if (!isPause.get() && !isGameOver.get()) {
                if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.A)
                    refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.D)
                    refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W)
                    refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));

                if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S)
                    moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));

                if (event.getCode() == KeyCode.SPACE)
                    moveDown(new MoveEvent(EventType.HARD_DROP, EventSource.USER));

                // [NEW] Hold Piece Binding
                if (event.getCode() == KeyCode.C)
                    refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
            }
            if (event.getCode() == KeyCode.N) newGame(null);
        });
    }

    @FXML
    public void onPauseClicked(ActionEvent event) {
        if (isCountdownRunning) return;

        if (!isGameOver.get() && !isPause.get()) {
            togglePause();
        }
    }

    private void togglePause() {
        if (isGameOver.get()) return;
        if (isPause.get()) {
            resumeGame(null);
        } else {
            isPause.set(true);
            timeLine.stop();
            clock.stop();
            pauseMenu.setVisible(true);
        }
    }

    @FXML
    public void resumeGame(ActionEvent event) {
        pauseMenu.setVisible(false);
        isPause.set(false);
        timeLine.play();
        clock.play();
        gamePanel.requestFocus();
    }

    @FXML
    public void returnToMenu(ActionEvent event) {
        timeLine.stop();
        clock.stop();
        try {
            URL location = getClass().getClassLoader().getResource("startMenu.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();

            Stage stage;
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) gamePanel.getScene().getWindow();
            }

            Scene scene = new Scene(root, 600, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitGame(ActionEvent event) {
        System.exit(0);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = VISIBLE_ROW_OFFSET; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(Color.TRANSPARENT);
                r.setStroke(Color.rgb(255, 255, 255, 0.1));
                r.setStrokeWidth(1);
                r.setStrokeType(StrokeType.INSIDE);
                displayMatrix[i][j] = r;
                gamePanel.add(r, j, i - VISIBLE_ROW_OFFSET);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setFill(getFillColor(brick.getBrickData()[i][j]));
                if (brick.getBrickData()[i][j] == 0) {
                    r.setStroke(Color.TRANSPARENT);
                } else {
                    r.setStroke(Color.BLACK);
                }
                r.setStrokeWidth(1);
                r.setStrokeType(StrokeType.INSIDE);
                rectangles[i][j] = r;
                brickPanel.add(r, j, i);
            }
        }

        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                r.setStrokeWidth(1);
                r.setStrokeType(StrokeType.INSIDE);
                setGhostRectangleData(brick.getBrickData()[i][j], r);
                ghostRectangles[i][j] = r;
                ghostPanel.add(r, j, i);
            }
        }

        trailGroup = new Group();
        gameZone.getChildren().add(2, trailGroup);

        updateBrickPanelPosition(brick);
        renderNextPiece(brick.getNextBrickData());

        // [NEW] Render initial hold state (likely empty)
        renderHoldPiece(brick.getHoldBrickData());

        double boardWidth = COLS * BRICK_SIZE + (COLS - 1) * BOARD_GAP;
        double boardHeight = (ROWS - VISIBLE_ROW_OFFSET) * BRICK_SIZE + (ROWS - VISIBLE_ROW_OFFSET - 1) * BOARD_GAP;
        gameZone.setPrefSize(boardWidth, boardHeight);
        gameZone.setMinSize(boardWidth, boardHeight);
        gameZone.setMaxSize(boardWidth, boardHeight);
        Rectangle clip = new Rectangle(0, 0, boardWidth, boardHeight);
        gameZone.setClip(clip);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(DROP_SPEED_MS),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
    }

    // [NEW] Render Hold Piece
    private void renderHoldPiece(int[][] holdData) {
        holdPiecePanel.getChildren().clear();
        if (holdData == null) return;

        GridPane pieceGrid = new GridPane();
        pieceGrid.setAlignment(Pos.CENTER);
        pieceGrid.setHgap(BOARD_GAP);
        pieceGrid.setVgap(BOARD_GAP);

        for (int r = 0; r < holdData.length; r++) {
            for (int c = 0; c < holdData[r].length; c++) {
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                if (holdData[r][c] != 0) {
                    rect.setFill(getFillColor(holdData[r][c]));
                    rect.setStroke(Color.BLACK);
                    rect.setStrokeWidth(1);
                    rect.setStrokeType(StrokeType.INSIDE);
                } else {
                    rect.setFill(Color.TRANSPARENT);
                }
                pieceGrid.add(rect, c, r);
            }
        }
        holdPiecePanel.getChildren().add(pieceGrid);
    }

    public void startCountdown(Runnable onFinished) {
        isCountdownRunning = true;
        countdownLabel.setVisible(true);
        countdownLabel.setText("3");

        PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
        pause1.setOnFinished(e -> {
            countdownLabel.setText("2");
            countdownLabel.setStyle("-fx-text-fill: #FF00FF; -fx-effect: dropshadow(three-pass-box, #FF00FF, 50, 0, 0, 0); -fx-font-size: 150px; -fx-font-family: 'Let\\'s go Digital';");
        });

        PauseTransition pause2 = new PauseTransition(Duration.seconds(1));
        pause2.setOnFinished(e -> {
            countdownLabel.setText("1");
            countdownLabel.setStyle("-fx-text-fill: #00FFFF; -fx-effect: dropshadow(three-pass-box, #00FFFF, 50, 0, 0, 0); -fx-font-size: 150px; -fx-font-family: 'Let\\'s go Digital';");
        });

        PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
        pause3.setOnFinished(e -> {
            countdownLabel.setText("GO!");
            countdownLabel.setStyle("-fx-text-fill: #00FF00; -fx-effect: dropshadow(three-pass-box, #00FF00, 50, 0, 0, 0); -fx-font-size: 150px; -fx-font-family: 'Let\\'s go Digital';");
        });

        PauseTransition pauseGo = new PauseTransition(Duration.seconds(0.5));
        pauseGo.setOnFinished(e -> {
            countdownLabel.setVisible(false);
            isCountdownRunning = false;
            onFinished.run();
        });

        pause1.play();
        pause1.setOnFinished(e -> {
            countdownLabel.setText("2");
            pause2.play();
        });
        pause2.setOnFinished(e -> {
            countdownLabel.setText("1");
            pause3.play();
        });
        pause3.setOnFinished(e -> {
            countdownLabel.setText("GO!");
            pauseGo.play();
        });
    }

    private void updateBrickPanelPosition(ViewData brick) {
        double x = brick.getxPosition() * (BRICK_SIZE + BOARD_GAP);
        double y = (brick.getyPosition() - VISIBLE_ROW_OFFSET) * (BRICK_SIZE + BOARD_GAP);
        brickPanel.setLayoutX(x);
        brickPanel.setLayoutY(y);

        double ghostY = (brick.getGhostYPosition() - VISIBLE_ROW_OFFSET) * (BRICK_SIZE + BOARD_GAP);
        ghostPanel.setLayoutX(x);
        ghostPanel.setLayoutY(ghostY);
    }

    public void showHardDropTrail(int startX, int startY, int distance, int[][] brickData) {
        for (int d = 0; d < distance; d++) {
            int yOffset = startY + d;

            for (int row = 0; row < brickData.length; row++) {
                for (int col = 0; col < brickData[row].length; col++) {
                    int colorId = brickData[row][col];
                    if (colorId != 0) {
                        Rectangle r = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                        Paint baseColor = getFillColor(colorId);
                        r.setFill(baseColor);
                        r.setOpacity(0.4);
                        r.setArcHeight(0);
                        r.setArcWidth(0);

                        double xPos = (startX + col) * (BRICK_SIZE + BOARD_GAP);
                        double yPos = (yOffset + row - VISIBLE_ROW_OFFSET) * (BRICK_SIZE + BOARD_GAP);

                        r.setLayoutX(xPos);
                        r.setLayoutY(yPos);

                        trailGroup.getChildren().add(r);

                        FadeTransition ft = new FadeTransition(Duration.millis(300), r);
                        ft.setFromValue(0.4);
                        ft.setToValue(0.0);
                        ft.setOnFinished(e -> trailGroup.getChildren().remove(r));
                        ft.play();
                    }
                }
            }
        }
    }

    private void renderNextPiece(List<int[][]> nextPieces) {
        nextPiecePanel.getChildren().clear();
        for (int[][] nextData : nextPieces) {
            GridPane pieceGrid = new GridPane();
            pieceGrid.setAlignment(Pos.CENTER);
            pieceGrid.setHgap(BOARD_GAP);
            pieceGrid.setVgap(BOARD_GAP);
            for (int r = 0; r < nextData.length; r++) {
                for (int c = 0; c < nextData[r].length; c++) {
                    Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    if (nextData[r][c] != 0) {
                        rect.setFill(getFillColor(nextData[r][c]));
                        rect.setStroke(Color.BLACK);
                        rect.setStrokeWidth(1);
                        rect.setStrokeType(StrokeType.INSIDE);
                    } else {
                        rect.setFill(Color.TRANSPARENT);
                    }
                    pieceGrid.add(rect, c, r);
                }
            }
            nextPiecePanel.getChildren().add(pieceGrid);
        }
    }

    private Paint getFillColor(int id) {
        if (id < 0 || id >= COLORS.length) return Color.WHITE;
        return COLORS[id];
    }

    private void refreshBrick(ViewData brick) {
        if (!isPause.get()) {
            updateBrickPanelPosition(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    int colorId = brick.getBrickData()[i][j];
                    setRectangleData(colorId, rectangles[i][j]);
                    setGhostRectangleData(colorId, ghostRectangles[i][j]);
                }
            }
            renderNextPiece(brick.getNextBrickData());
            // [NEW] Update Hold Piece view
            renderHoldPiece(brick.getHoldBrickData());
        }
    }

    public void refreshGameBackground(int[][] board) {
        for (int i = VISIBLE_ROW_OFFSET; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int color = board[i][j];
                setRectangleData(color, displayMatrix[i][j]);
                if (color == 0) {
                    displayMatrix[i][j].setStroke(Color.rgb(255, 255, 255, 0.1));
                } else {
                    displayMatrix[i][j].setStroke(Color.BLACK);
                }
            }
        }
    }

    private void setRectangleData(int color, Rectangle r) {
        r.setFill(getFillColor(color));
        r.setArcHeight(0);
        r.setArcWidth(0);
        if (color == 0) {
            r.setStroke(Color.TRANSPARENT);
        } else {
            r.setStroke(Color.BLACK);
        }
    }

    private void setGhostRectangleData(int colorId, Rectangle r) {
        if (colorId != 0) {
            r.setVisible(true);
            Paint fill = getFillColor(colorId);
            r.setFill(fill);
            r.setOpacity(0.3);
            r.setStroke(Color.rgb(255, 255, 255, 0.3));
            r.setStrokeWidth(1);
            r.setStrokeType(StrokeType.INSIDE);
            r.setArcHeight(0);
            r.setArcWidth(0);
        } else {
            r.setVisible(false);
        }
    }

    private void moveDown(MoveEvent event) {
        if (!isPause.get()) {
            DownData data;
            if (event.getEventType() == EventType.HARD_DROP) {
                data = eventListener.onHardDropEvent(event);
            } else {
                data = eventListener.onDownEvent(event);
            }

            if (data.getClearRow() != null && data.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel panel = new NotificationPanel("+" + data.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(panel);
                panel.showScore(groupNotification.getChildren());
            }
            refreshBrick(data.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void bindScore(IntegerProperty score) {
        scoreLabel.textProperty().bind(score.asString("%d"));
        this.currentScoreProperty = score;
    }

    public void bindExtraStats(Score scoreObj) {
        levelLabel.textProperty().bind(scoreObj.levelProperty().asString("%d"));
        linesLabel.textProperty().bind(scoreObj.linesClearedProperty().asString("%d"));
    }

    public void gameOver() {
        timeLine.stop();
        clock.stop();
        gameOverPanel.show(currentScoreProperty.get(), this);
        isGameOver.set(true);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        clock.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();

        resetClock();

        gamePanel.requestFocus();
        timeLine.play();
        clock.play();
        isPause.set(false);
        isGameOver.set(false);
    }

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