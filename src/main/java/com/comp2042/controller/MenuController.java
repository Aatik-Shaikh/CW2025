/*
 * This class controls the Start Menu screen logic.
 * It manages the user's interaction with the "Play", "Quit", and "Settings" buttons.
 *
 * Responsibilities:
 * 1. Initializing the menu scene, including loading high scores and custom fonts.
 * 2. Managing the Settings overlay where the user can select the starting game speed/level.
 * 3. Transitioning the application from the Menu scene to the Game scene when "Play" is clicked.
 */
package com.comp2042.controller;

import com.comp2042.GameConfig;
import com.comp2042.HighScoreManager;
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

    // UI elements linked to the FXML file
    @FXML private Label hs1;
    @FXML private Label hs2;
    @FXML private Label hs3;
    @FXML private VBox settingsPanel;
    @FXML private Label selectedLevelLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Loads retro fonts (fallback if Main fails to load them globally)
        if (getClass().getClassLoader().getResource("digital.ttf") != null) {
            Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        }
        if (getClass().getClassLoader().getResource("PressStart2P-Regular.ttf") != null) {
            Font.loadFont(getClass().getClassLoader().getResource("PressStart2P-Regular.ttf").toExternalForm(), 38);
        }

        // Retrieves the top 3 scores from the HighScoreManager to display on the menu
        List<Integer> topScores = HighScoreManager.getTopScores();

        // Updates the labels with the retrieved scores, defaulting to "---" if empty
        hs1.setText(topScores.size() > 0 ? topScores.get(0).toString() : "---");
        hs2.setText(topScores.size() > 1 ? topScores.get(1).toString() : "---");
        hs3.setText(topScores.size() > 2 ? topScores.get(2).toString() : "---");

        // Ensures the UI displays the currently configured start level
        updateLevelDisplay();
    }


    /**
     * Opens the settings overlay panel.
     * Makes the settings VBox visible to the user.
     *
     * @param event The action event triggered by the settings button.
     */
    @FXML
    public void openSettings(ActionEvent event) {
        settingsPanel.setVisible(true);
    }


    /**
     * Closes the settings overlay panel.
     * Hides the settings VBox.
     *
     * @param event The action event triggered by the close button.
     */
    @FXML
    public void closeSettings(ActionEvent event) {
        settingsPanel.setVisible(false);
    }


    /**
     * Updates the game difficulty level based on the user's selection.
     * Highlights the selected button and updates the global GameConfig.
     *
     * @param event The action event triggered by a level button click.
     */
    @FXML
    public void setLevel(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String levelStr = (String) clickedButton.getUserData();
        int newLevel = Integer.parseInt(levelStr);

        // Uses the setter method to update the encapsulated configuration
        GameConfig.setStartLevel(newLevel);

        updateLevelDisplay();

        // Updates visual styling: removes 'selected' style from all buttons, adds to clicked one
        if (clickedButton.getParent() instanceof GridPane) {
            GridPane grid = (GridPane) clickedButton.getParent();
            for (Node node : grid.getChildren()) {
                node.getStyleClass().remove("level-button-selected");
                node.getStyleClass().add("level-button");
            }
        }
        clickedButton.getStyleClass().add("level-button-selected");
    }

    // Updates the text label to reflect the currently selected speed/level
    private void updateLevelDisplay() {
        //Uses the getter method to retrieve the encapsulated configuration
        selectedLevelLabel.setText("Selected: Speed " + GameConfig.getStartLevel());
    }


    /**
     * Handles the 'Play' button click.
     * Loads the main game layout FXML, initializes the GameController, and switches the scene.
     *
     * @param event The action event triggered by the play button.
     */
    @FXML
    public void onPlay(ActionEvent event) {
        try {
            // Loads the main game layout from FXML
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Parent root = fxmlLoader.load();

            // Initializes the GameController to begin game logic
            GuiController c = fxmlLoader.getController();
            GameController game = new GameController(c);
            game.startGame();

            // Transitions the window scene from Menu to Game
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 600, 700);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the 'Quit' button click.
     * Terminates the application.
     *
     * @param event The action event triggered by the quit button.
     */
    @FXML
    public void onQuit(ActionEvent event) {
        System.exit(0);
    }
}