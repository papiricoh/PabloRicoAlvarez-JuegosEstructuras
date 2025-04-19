package org.papiricoh.hanoi.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class HanoiModel {

    public record Move(int fromPeg, int toPeg) {}

    private final int disks;
    private final List<Move> moves = new ArrayList<>();

    public HanoiModel(int disks) {
        this.disks = disks;
        solve(disks, 0, 2, 1);          // pegs: 0=A, 1=B, 2=C
    }

    private void solve(int n, int from, int to, int aux) {
        if (n == 0) return;
        solve(n - 1, from, aux, to);
        moves.add(new Move(from, to));
        solve(n - 1, aux, to, from);
    }

    public int getDiskCount()      { return disks; }
    public List<Move> getMoves()   { return moves; }

    /** devuelve el estado del juego después de aplicar los `step` primeros movimientos */
    public List<Deque<Integer>> stateAt(int step) {

        List<Deque<Integer>> pegs = List.of(new ArrayDeque<>(), new ArrayDeque<>(), new ArrayDeque<>());
        for (int d = disks; d >= 1; d--) pegs.get(0).push(d);   // torre inicial
        for (int i = 0; i < step; i++) {
            Move m = moves.get(i);
            pegs.get(m.toPeg).push(pegs.get(m.fromPeg).pop());
        }
        return pegs;
    }
}