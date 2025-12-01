package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private Label hs1;
    @FXML private Label hs2;
    @FXML private Label hs3;
    @FXML private VBox settingsPanel;
    @FXML private Label selectedLevelLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (getClass().getClassLoader().getResource("PressStart2P-Regular.ttf") != null) {
            Font.loadFont(getClass().getClassLoader().getResource("PressStart2P-Regular.ttf").toExternalForm(), 38);
        }

        List<Integer> topScores = HighScoreManager.getTopScores();

        // [FIX] Removed "1. ", "2. ", "3. " prefixes
        hs1.setText(topScores.size() > 0 ? topScores.get(0).toString() : "---");
        hs2.setText(topScores.size() > 1 ? topScores.get(1).toString() : "---");
        hs3.setText(topScores.size() > 2 ? topScores.get(2).toString() : "---");

        updateLevelDisplay();
    }

    @FXML
    public void openSettings(ActionEvent event) {
        settingsPanel.setVisible(true);
    }

    @FXML
    public void closeSettings(ActionEvent event) {
        settingsPanel.setVisible(false);
    }

    @FXML
    public void setLevel(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String levelStr = (String) clickedButton.getUserData();
        int newLevel = Integer.parseInt(levelStr);

        GameConfig.START_LEVEL = newLevel;
        updateLevelDisplay();

        if (clickedButton.getParent() instanceof GridPane) {
            GridPane grid = (GridPane) clickedButton.getParent();
            for (Node node : grid.getChildren()) {
                node.getStyleClass().remove("level-button-selected");
                node.getStyleClass().add("level-button");
            }
        }
        clickedButton.getStyleClass().add("level-button-selected");
    }

    private void updateLevelDisplay() {
        selectedLevelLabel.setText("Selected: Speed " + GameConfig.START_LEVEL);
    }

    @FXML
    public void onPlay(ActionEvent event) {
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();

            GuiController c = fxmlLoader.getController();
            GameController game = new GameController(c);
            game.startGame();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onQuit(ActionEvent event) {
        System.exit(0);
    }
}