package com.comp2042;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class HighScoreManager {

    private static final String FILE_PATH = "highscores.txt";
    private static final int MAX_SCORES = 3;

    // Load scores from file
    public static List<Integer> getTopScores() {
        List<Integer> scores = new ArrayList<>();
        Path path = Paths.get(FILE_PATH);

        if (!Files.exists(path)) {
            return scores; // Return empty list if no file exists
        }

        try {
            scores = Files.lines(path)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .sorted(Collections.reverseOrder()) // Sort High to Low
                    .limit(MAX_SCORES)
                    .collect(Collectors.toList());
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return scores;
    }

    // Save a new score if it's high enough
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