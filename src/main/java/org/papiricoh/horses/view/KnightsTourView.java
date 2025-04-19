package org.papiricoh.horses.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.papiricoh.horses.controller.KnightsTourController;
import org.papiricoh.horses.model.KnightsTourModel;

public class KnightsTourView extends BorderPane implements KnightsTourController.Listener {

    private final int tileSize = 50;
    private final GridPane board = new GridPane();
    private final Label lblCounter = new Label();
    private final KnightsTourController controller;
    private Timeline autoPlay;
    private final int size;

    private KnightsTourView(int size) {
        this.size = size;
        KnightsTourModel model = new KnightsTourModel(size);
        this.controller = new KnightsTourController(model);
        this.controller.setListener(this);

        buildBoard(size);
        buildControls();
    }

    public static Parent create() { return new KnightsTourView(8); }

    // ----------------------------- UI helpers ------------------------------

    private void buildBoard(int size) {
        board.setAlignment(Pos.CENTER);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle(tileSize, tileSize);
                square.setFill(((r + c) % 2 == 0) ? Color.BEIGE : Color.SADDLEBROWN);
                cell.getChildren().add(square);
                board.add(cell, c, r);
            }
        }
        setCenter(board);
    }

    private void buildControls() {
        Button btnPrev = new Button("◀");
        Button btnPlay = new Button("▶");
        Button btnNext = new Button("▶");

        btnPrev.setOnAction(e -> controller.previous());
        btnNext.setOnAction(e -> controller.next());
        btnPlay.setOnAction(e -> togglePlay(btnPlay));

        HBox controls = new HBox(10, btnPrev, btnPlay, btnNext, lblCounter);
        controls.setAlignment(Pos.CENTER);
        setBottom(controls);
    }

    private void togglePlay(Button btnPlay) {
        if (autoPlay != null && autoPlay.getStatus() == Timeline.Status.RUNNING) {
            autoPlay.stop();
            btnPlay.setText("▶");
        } else {
            autoPlay = new Timeline(new KeyFrame(Duration.millis(400), e -> controller.next()));
            autoPlay.setCycleCount(Timeline.INDEFINITE);
            autoPlay.play();
            btnPlay.setText("❚❚");
        }
    }

    // ----------------------------- Listener --------------------------------

    @Override
    public void onStepChanged(int row, int col, int stepIndex, int total) {
        paintKnight(row, col, stepIndex);
        lblCounter.setText(String.format("%d / %d", stepIndex + 1, total));
    }

    private void paintKnight(int row, int col, int stepIndex) {
        // clear previous knight and numbers
        board.getChildren().forEach(node -> {
            StackPane cell = (StackPane) node;
            cell.getChildren().removeIf(child -> child instanceof Circle || child instanceof Text);
        });
        // draw numbers up to current step
        for (int i = 0; i <= stepIndex; i++) {
            int pos = controller.path.get(i);
            int r = pos / size;
            int c = pos % size;
            for (var node : board.getChildren()) {
                if (GridPane.getRowIndex(node) == r && GridPane.getColumnIndex(node) == c) {
                    StackPane cell = (StackPane) node;
                    Text number = new Text(Integer.toString(i + 1));
                    number.setFont(Font.font(tileSize * 0.4));
                    number.setFill(Color.DARKBLUE);
                    cell.getChildren().add(number);
                    break;
                }
            }
        }
        // draw knight as circle
        for (var node : board.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                Circle knight = new Circle(tileSize * 0.3, Color.RED);
                ((StackPane) node).getChildren().add(knight);
                break;
            }
        }
    }

    // -------------------------------- Entry helper -------------------------

    public static void main(String[] args) {
        javafx.application.Application.launch(Standalone.class, args);
    }

    public static class Standalone extends javafx.application.Application {
        @Override
        public void start(javafx.stage.Stage stage) {
            stage.setScene(new Scene(new KnightsTourView(8)));
            stage.setTitle("Knight's Tour Viewer");
            stage.show();
        }
    }
}
