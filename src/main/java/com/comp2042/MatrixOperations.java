/**
 * A utility class providing static methods for matrix manipulations.
 * It handles collision detection, merging bricks into the board, and clearing full rows.
 */
package com.comp2042;

import com.comp2042.model.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixOperations {

    // Private constructor prevents instantiation, as this is a utility class
    // intended to be accessed statically.
    private MatrixOperations() {
    }

    /**
     * Checks for collisions between the active brick and the game board.
     * It verifies if any part of the brick's matrix overlaps with existing blocks
     * on the board or exceeds the board boundaries.
     *
     * @param matrix The current state of the game board.
     * @param brick  The matrix representation of the falling brick.
     * @param x      The x-coordinate of the brick.
     * @param y      The y-coordinate of the brick.
     * @return true if a collision is detected, false otherwise.
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                // Check if the brick cell is solid (non-zero) and if it hits a wall or another block
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper method to determine if a coordinate is outside the grid dimensions.
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    // Creates a deep copy of a 2D integer array to prevent mutation of the original data.
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick into the background grid (locking it in place).
     *
     * @param filledFields The current board matrix.
     * @param brick        The brick matrix to merge.
     * @param x            The x-position.
     * @param y            The y-position.
     * @return A new matrix with the brick merged.
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Checks for and removes any fully filled rows from the board.
     *
     * @param matrix The current board matrix.
     * @return A {@link ClearRow} object containing the new matrix and score details.
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        // Iterate through the matrix to find rows that are fully filled (no zeros)
        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }

        // Reconstruct the board: fill from the bottom up with the remaining rows
        // Top rows are filled with empty arrays (new int[]) to replace cleared ones
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }

        // Calculate score: The bonus increases quadratically with the number of lines cleared at once
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of matrices.
     *
     * @param list The list to copy.
     * @return A new list containing independent copies of the arrays.
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }
}