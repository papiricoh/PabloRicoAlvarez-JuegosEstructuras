package org.papiricoh.queens.controller;

import org.papiricoh.queens.model.NQueensModel;

import java.util.List;

public class NQueensController {

    public interface Listener {
        void onSolutionChanged(int[] solution, int index, int total);
    }

    private final List<int[]> solutions;
    private int index = 0;
    private Listener listener;

    public NQueensController(NQueensModel model) {
        this.solutions = model.getSolutions();
    }

    public void setListener(Listener l) {
        this.listener = l;
        if (l != null && !solutions.isEmpty()) {
            l.onSolutionChanged(solutions.get(index), index, solutions.size());
        }
    }

    public void next() {
        if (solutions.isEmpty()) return;
        index = (index + 1) % solutions.size();
        if (listener != null) listener.onSolutionChanged(solutions.get(index), index, solutions.size());
    }

    public void previous() {
        if (solutions.isEmpty()) return;
        index = (index - 1 + solutions.size()) % solutions.size();
        if (listener != null) listener.onSolutionChanged(solutions.get(index), index, solutions.size());
    }
}
