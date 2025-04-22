package org.papiricoh.horses.model;

import org.papiricoh.core.model.Piece;

import java.util.ArrayList;
import java.util.List;

public class KnightsTourModel extends Piece {

    private final int size;
    private final List<Integer> path = new ArrayList<>();

    // Knight move offsets
    private static final int[] DR = {-2, -1, 1, 2, 2, 1, -1, -2};
    private static final int[] DC = {1, 2, 2, 1, -1, -2, -2, -1};

    public KnightsTourModel(int size) {
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");
        this.size = size;
        solve();
    }

    public int getSize() { return size; }
    public List<Integer> getPath() { return List.copyOf(path); }

    private void solve() {
        int[][] board = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) board[r][c] = -1;
        }
        int startR = 0, startC = 0;
        board[startR][startC] = 0;
        path.add(startR * size + startC);
        if (!dfs(board, startR, startC, 1)) {
            throw new IllegalStateException("No tour found for size=" + size);
        }
    }

    private boolean dfs(int[][] board, int r, int c, int move) {
        if (move == size * size) return true; // all squares visited
        // generate moves sorted by Warnsdorff degree
        int bestIdx = -1, minDeg = 9;
        int[] order = new int[8];
        for (int i = 0; i < 8; i++) order[i] = i;
        for (int i = 0; i < 8; i++) {
            int nr = r + DR[i], nc = c + DC[i];
            if (isFree(board, nr, nc)) {
                int deg = degree(board, nr, nc);
                if (deg < minDeg) {
                    minDeg = deg; bestIdx = i;
                }
            }
        }
        if (bestIdx == -1) return false;
        // try best move first, then others
        int[] priority = new int[8];
        priority[0] = bestIdx;
        int pos = 1;
        for (int i = 0; i < 8; i++) if (i != bestIdx) priority[pos++] = i;

        for (int idx : priority) {
            int nr = r + DR[idx], nc = c + DC[idx];
            if (!isFree(board, nr, nc)) continue;
            board[nr][nc] = move;
            path.add(nr * size + nc);
            if (dfs(board, nr, nc, move + 1)) return true;
            // backtrack
            board[nr][nc] = -1;
            path.remove(path.size() - 1);
        }
        return false;
    }

    private boolean isFree(int[][] board, int r, int c) {
        return r >= 0 && c >= 0 && r < size && c < size && board[r][c] == -1;
    }

    private int degree(int[][] board, int r, int c) {
        int deg = 0;
        for (int i = 0; i < 8; i++) {
            int nr = r + DR[i], nc = c + DC[i];
            if (isFree(board, nr, nc)) deg++;
        }
        return deg;
    }
}
