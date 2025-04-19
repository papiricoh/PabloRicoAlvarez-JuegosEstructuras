package org.papiricoh.hanoi.view;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.papiricoh.hanoi.controller.HanoiController;
import org.papiricoh.hanoi.model.HanoiModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;


public class HanoiView extends BorderPane {

    private static final double WIDTH = 600, HEIGHT = 400;
    private static final double PEG_WIDTH = 6;

    private final Canvas canvas = new Canvas(WIDTH, HEIGHT);
    private final HanoiController controller;

    private final Color[] diskColors = { Color.LIGHTBLUE , Color.LIGHTGREEN , Color.LIGHTPINK,
                                         Color.LIGHTYELLOW, Color.ORANGE     , Color.VIOLET };

    private HanoiView(int disks) {
        HanoiModel model = new HanoiModel(disks);
        this.controller = new HanoiController(model);

        draw(model.stateAt(0));
        controller.stepProperty().addListener((obs, oldV, newV) ->
            draw(model.stateAt(newV.intValue()))
        );

        Button prev  = new Button("⟨");
        Button next  = new Button("⟩");
        Button play  = new Button("▶");
        Button stop  = new Button("■");

        prev.setOnAction(e -> controller.previous());
        next.setOnAction(e -> controller.next());
        play.setOnAction(e -> controller.play(500));
        stop.setOnAction(e -> controller.stop());

        ToolBar bar = new ToolBar(prev, next, play, stop);
        bar.setOrientation(Orientation.HORIZONTAL);

        setCenter(canvas);
        setBottom(bar);
    }


    public static Parent create(int disks) { return new HanoiView(disks); }


    public static class Demo extends Application {
        @Override public void start(Stage stage) {
            stage.setScene(new Scene(create(5)));
            stage.setTitle("Towers of Hanoi");
            stage.show();
        }
        public static void main(String[] args) { launch(); }
    }


    private void draw(List<Deque<Integer>> pegs) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        double pegGap = WIDTH / 3.0;
        double baseY  = HEIGHT - 40;
        double diskHeight = 20;


        g.setFill(Color.SADDLEBROWN);
        for (int i = 0; i < 3; i++) {
            double x = pegGap / 2 + i * pegGap - PEG_WIDTH / 2;
            g.fillRect(x, HEIGHT * 0.2, PEG_WIDTH, baseY - HEIGHT * 0.2);
        }


        for (int pegIndex = 0; pegIndex < 3; pegIndex++) {
            List<Integer> stack = new ArrayList<>(pegs.get(pegIndex));
            Collections.reverse(stack);

            for (int level = 0; level < stack.size(); level++) {
                int disk = stack.get(level);

                double maxDiskWidth = pegGap * 0.8;
                double widthFactor  = (double) disk / controller.getModel().getDiskCount();
                double diskWidth    = 40 + (maxDiskWidth - 40) * widthFactor;

                double x = pegGap / 2 + pegIndex * pegGap - diskWidth / 2;
                double y = baseY - (level + 1) * diskHeight;

                g.setFill(diskColors[(disk - 1) % diskColors.length]);
                g.fillRoundRect(x, y, diskWidth, diskHeight, 10, 10);
                g.setStroke(Color.BLACK);
                g.strokeRoundRect(x, y, diskWidth, diskHeight, 10, 10);
            }
        }
    }
}