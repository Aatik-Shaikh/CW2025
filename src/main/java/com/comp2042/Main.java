/**
 * The entry point for the Tetris application.
 * This class initializes the JavaFX platform, loads external resources, and launches the application window.
 */

package com.comp2042;

import com.comp2042.util.ResourceLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    /**
     * Starts the primary stage of the application.
     * It loads the Start Menu FXML and sets up the initial scene.
     *
     * @param primaryStage The primary window for this application.
     * @throws Exception If the FXML resource cannot be found or loaded.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceLoader.loadResources();


        // Find the FXML file for the Start Menu
        URL location = getClass().getClassLoader().getResource("startMenu.fxml");

        // Safety check
        if (location == null) {
            System.err.println("Could not find startMenu.fxml. Ensure it is in the resources folder.");
            return;
        }

        // Load the visual layout from the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}