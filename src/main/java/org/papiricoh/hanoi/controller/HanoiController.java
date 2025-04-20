package org.papiricoh.hanoi.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import org.papiricoh.core.dao.MoveDao;
import org.papiricoh.hanoi.model.HanoiModel;

import java.sql.SQLException;

public class HanoiController {

    private final HanoiModel model;
    private final IntegerProperty step = new SimpleIntegerProperty(0);
    private Timeline autoPlay;
    private final MoveDao dao = new MoveDao();

    public HanoiController(HanoiModel model) {
        this.model = model;
    }

    public IntegerProperty stepProperty() { return step; }
    public int getStep()                  { return step.get(); }

    public void next() {
        if (step.get() < model.getMoves().size()) {
            var move = model.getMoves().get(step.get());
            step.set(step.get() + 1);
            try {
                dao.addMove(3, step.get(),
                        "Move " + step.get() + ": " + move.fromPeg() + "→" + move.toPeg(),
                        null);                               // o JSON con más datos
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public void previous() {
        if (step.get() > 0) step.set(step.get() - 1);
    }

    public void play(int msPerMove) {
        stop();
        autoPlay = new Timeline(new KeyFrame(Duration.millis(msPerMove), e -> next()));
        autoPlay.setCycleCount(model.getMoves().size() - step.get());
        autoPlay.play();
    }

    public void stop() {
        if (autoPlay != null) autoPlay.stop();
    }

    public HanoiModel getModel() { return model; }
}