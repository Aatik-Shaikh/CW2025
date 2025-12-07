/**
 * Manages the persistence of high scores to a local file.
 * It handles reading, writing, sorting, and limiting the high score list.
 */
package com.comp2042;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class HighScoreManager {

    // The name of the file where scores are persisted locally
    private static final String FILE_PATH = "highscores.txt";
    // The maximum number of top scores to maintain
    private static final int MAX_SCORES = 3;

    /**
     * Retrieves the top scores from the highscores file.
     * It reads the file line by line, parses the integers, sorts them in
     * descending order, and returns the top 3.
     *
     * @return A List of integers representing the top scores. Returns an empty list if the file does not exist.
     */
    public static List<Integer> getTopScores() {
        List<Integer> scores = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        // Check if the file exists before attempting to read to avoid errors
        if (!Files.exists(path)) {
            return scores; // Return empty list if no file exists
        }

        try {
            // Java Stream API is used here for concise reading, filtering, and sorting
            scores = Files.lines(path)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .sorted(Collections.reverseOrder()) // Sort High to Low
                    .limit(MAX_SCORES)                  // Keep only the top N scores
                    .collect(Collectors.toList());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Adds a new score to the high score list if it qualifies.
     * It updates the file with the new top 3 scores.
     *
     * @param newScore The score achieved in the recent game.
     */
    public static void addScore(int newScore) {
        List<Integer> scores = getTopScores();
        scores.add(newScore);

        // Re-sort and keep only top 3
        List<Integer> top3 = scores.stream()
                .sorted(Collections.reverseOrder())
                .limit(MAX_SCORES)
                .collect(Collectors.toList());

        saveScores(top3);
    }

    /**
     * Helper method to write the list of scores to the file system.
     * It overwrites the existing file with the updated list of top scores.
     *
     * @param scores The list of scores to write.
     */
    private static void saveScores(List<Integer> scores) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (Integer score : scores) {
                writer.write(score.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}