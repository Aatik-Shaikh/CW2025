package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadFonts();

        URL location = getClass().getClassLoader().getResource("startMenu.fxml");
        if (location == null) {
            System.err.println("Could not find startMenu.fxml. Ensure it is in the resources folder.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFonts() {
        try {
            // [RESTORED] Load digital.ttf
            URL fontUrl = getClass().getResource("/digital.ttf");
            if (fontUrl != null) {
                Font.loadFont(fontUrl.toExternalForm(), 20);
                System.out.println("SUCCESS: Loaded digital.ttf");
            } else {
                System.err.println("CRITICAL: digital.ttf not found in resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}