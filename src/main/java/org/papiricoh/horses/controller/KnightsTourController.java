package org.papiricoh.horses.controller;

import org.papiricoh.core.dao.MoveDao;
import org.papiricoh.horses.model.KnightsTourModel;

import java.sql.SQLException;
import java.util.List;

public class KnightsTourController {

    public interface Listener {
        void onStepChanged(int row, int col, int stepIndex, int total);
    }

    public final List<Integer> path;
    private int index = 0;
    private Listener listener;
    private final int size;
    private final MoveDao dao = new MoveDao();

    public KnightsTourController(KnightsTourModel model) {
        this.path = model.getPath();
        this.size = model.getSize();
    }

    public void setListener(Listener l) {
        this.listener = l;
        if (l != null) notifyListener();
    }

    public void next() {
        if (index < path.size() - 1) {
            int oldPos = path.get(index);
            int oldRow = oldPos / size;
            int oldCol = oldPos % size;
            index++;
            try {
                dao.addMove(2, path.get(index),
                        "Move " + path.get(index) + ": " + oldRow + "/" + oldCol + "→" + path.get(index) / size + "/" + path.get(index) % size,
                        null);
            } catch (SQLException ex) { ex.printStackTrace(); }
            notifyListener();
        }
    }

    public void previous() {
        if (index > 0) {
            index--;
            notifyListener();
        }
    }

    private void notifyListener() {
        int pos = path.get(index);
        int row = pos / size;
        int col = pos % size;
        if (listener != null) listener.onStepChanged(row, col, index, path.size());
    }
}
