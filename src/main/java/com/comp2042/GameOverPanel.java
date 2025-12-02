package com.comp2042;

import controller.GuiController;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;

public class GameOverPanel extends VBox {

    private final Label scoreLabel;
    private final Label hs1, hs2, hs3;
    private final Button btnTryAgain;
    private final Button btnMainMenu;

    public GameOverPanel() {
        // Style the container
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.getStyleClass().add("game-over-box");
        this.setMaxWidth(450);
        this.setMaxHeight(500);

        // 1. Title
        Label title = new Label("GAME OVER");
        title.getStyleClass().add("retro-title");
        title.setStyle("-fx-font-size: 50px; -fx-text-fill: linear-gradient(to bottom, #FF0000, #990000);");

        // 2. Final Score
        scoreLabel = new Label("FINAL SCORE: 0");
        scoreLabel.getStyleClass().add("hud-value");
        scoreLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");

        // 3. High Scores Box
        VBox hsBox = new VBox(10);
        hsBox.getStyleClass().add("highscore-box");
        hsBox.setAlignment(Pos.CENTER);
        hsBox.setMaxWidth(350);

        Label hsTitle = new Label("TOP SCORES");
        hsTitle.getStyleClass().add("highscore-title");
        hsTitle.setStyle("-fx-font-size: 24px;");

        hs1 = new Label("1ST: ---");
        hs1.getStyleClass().add("highscore-entry");
        hs2 = new Label("2ND: ---");
        hs2.getStyleClass().add("highscore-entry");
        hs3 = new Label("3RD: ---");
        hs3.getStyleClass().add("highscore-entry");

        hsBox.getChildren().addAll(hsTitle, hs1, hs2, hs3);

        // 4. Buttons
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        btnTryAgain = new Button("TRY AGAIN");
        btnTryAgain.getStyleClass().add("arcade-button");
        btnTryAgain.setStyle("-fx-font-size: 24px; -fx-min-width: 250px; -fx-min-height: 50px;");

        btnMainMenu = new Button("MAIN MENU");
        btnMainMenu.getStyleClass().add("arcade-button");
        btnMainMenu.setStyle("-fx-font-size: 24px; -fx-min-width: 250px; -fx-min-height: 50px;");

        buttonBox.getChildren().addAll(btnTryAgain, btnMainMenu);

        // Add everything to the panel
        this.getChildren().addAll(title, scoreLabel, hsBox, buttonBox);
    }

    public void show(int score, GuiController controller) {
        // Update Score
        scoreLabel.setText("FINAL SCORE: " + score);

        // Update High Scores
        List<Integer> top = HighScoreManager.getTopScores();
        hs1.setText("1ST: " + (top.size() > 0 ? top.get(0) : "---"));
        hs2.setText("2ND: " + (top.size() > 1 ? top.get(1) : "---"));
        hs3.setText("3RD: " + (top.size() > 2 ? top.get(2) : "---"));

        // Set Button Actions
        btnTryAgain.setOnAction(e -> controller.newGame(null));
        btnMainMenu.setOnAction(e -> controller.returnToMenu(null));

        this.setVisible(true);
    }
}