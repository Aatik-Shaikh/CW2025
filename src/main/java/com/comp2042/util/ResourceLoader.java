package com.comp2042.util;

import javafx.scene.text.Font;
import java.net.URL;

public class ResourceLoader {

    private static final String FONT_PATH_1 = "/digital.ttf";
    private static final String FONT_PATH_2 = "/PressStart2P-Regular.ttf";

    /**
     * Loads global fonts and resources needed for the application.
     * This should be called once when the application starts.
     */
    public static void loadResources() {
        loadFont(FONT_PATH_1, 38);
        loadFont(FONT_PATH_2, 38);
    }

    private static void loadFont(String path, double size) {
        try {
            URL fontUrl = ResourceLoader.class.getResource(path);
            if (fontUrl != null) {
                Font.loadFont(fontUrl.toExternalForm(), size);
                System.out.println("SUCCESS: Loaded " + path);
            } else {
                System.err.println("CRITICAL: " + path + " not found in resources.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}