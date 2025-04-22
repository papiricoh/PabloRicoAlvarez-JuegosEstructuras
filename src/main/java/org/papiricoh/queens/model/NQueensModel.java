package org.papiricoh.queens.model;

import org.papiricoh.core.model.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NQueensModel extends Piece {

    private final int size;
    private final List<int[]> solutions = new ArrayList<>();

    /**
     * Solves the puzzle for an N×N board and stores every solution
     * as an int[] where index = row and value = column of the queen.
     */
    public NQueensModel(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Board size must be >= 1");
        }
        this.size = size;
        backtrack(new int[size], 0, new boolean[size], new boolean[2 * size], new boolean[2 * size]);
    }

    public int getSize() {
        return size;
    }

    /**
     * @return an *immutable* list of all solutions found.
     */
    public List<int[]> getSolutions() {
        return List.copyOf(solutions);
    }

    private void backtrack(int[] queenInRow, int row,
                           boolean[] colUsed, boolean[] diag1Used, boolean[] diag2Used) {
        if (row == size) {
            solutions.add(Arrays.copyOf(queenInRow, size));
            return;
        }
        for (int col = 0; col < size; col++) {
            int d1 = row - col + size; // index shift to keep >= 0
            int d2 = row + col;
            if (colUsed[col] || diag1Used[d1] || diag2Used[d2]) continue;
            // place queen
            queenInRow[row] = col;
            colUsed[col] = diag1Used[d1] = diag2Used[d2] = true;
            backtrack(queenInRow, row + 1, colUsed, diag1Used, diag2Used);
            // remove queen
            colUsed[col] = diag1Used[d1] = diag2Used[d2] = false;
        }
    }
}