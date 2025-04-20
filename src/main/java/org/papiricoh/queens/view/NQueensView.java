package org.papiricoh.queens.view;

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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.papiricoh.core.dao.MoveDao;
import org.papiricoh.queens.controller.NQueensController;
import org.papiricoh.queens.model.NQueensModel;

import java.sql.SQLException;

public class NQueensView extends BorderPane implements NQueensController.Listener {

    private final int tileSize = 50;
    private final GridPane board = new GridPane();
    private final Label lblCounter = new Label();
    private final NQueensController controller;
    private final MoveDao dao = new MoveDao();

    private NQueensView(int size) {
        NQueensModel model = new NQueensModel(size);
        this.controller = new NQueensController(model);
        this.controller.setListener(this);

        buildBoard(size);
        buildControls();
    }

    /**
     * Factory so AppLauncher can instantiate via NQueensView.create().
     */
    public static Parent create() {
        return new NQueensView(8); // default to classic 8×8
    }

    // ------------------------------------------------ private helpers -------

    private void buildBoard(int size) {
        board.setAlignment(Pos.CENTER);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = new StackPane();
                Rectangle square = new Rectangle(tileSize, tileSize);
                boolean light = (row + col) % 2 == 0;
                square.setFill(light ? Color.BEIGE : Color.SADDLEBROWN);
                cell.getChildren().add(square);
                board.add(cell, col, row);
            }
        }
        setCenter(board);
    }

    private void buildControls() {
        Button btnPrev = new Button("◀ Previous");
        Button btnNext = new Button("Next ▶");
        btnPrev.setOnAction(e -> controller.previous());
        btnNext.setOnAction(e -> controller.next());

        HBox buttons = new HBox(10, btnPrev, btnNext, lblCounter);
        buttons.setAlignment(Pos.CENTER);
        setBottom(buttons);
    }

    // ------------------------------------------------ Controller.Listener ---

    @Override
    public void onSolutionChanged(int[] solution, int index, int total) {
        paintQueens(solution);
        lblCounter.setText(String.format("%d / %d", index + 1, total));
    }

    private void paintQueens(int[] queens) {
        // remove previous queens (keep just the squares)
        board.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null && ((StackPane) node).getChildren().size() > 1);
        int size = queens.length;
        for (int row = 0; row < size; row++) {
            int col = queens[row];
            // get cell at (col,row)
            for (var node : board.getChildren()) {
                if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                    Text queen = new Text("\u265B");
                    queen.setFont(Font.font(tileSize * 0.8));
                    ((StackPane) node).getChildren().add(queen);
                    try {
                        dao.addMove(1, node.hashCode(),
                                "Move queen: " + node.hashCode() + ": " +  GridPane.getRowIndex(node) / size + "/" + GridPane.getColumnIndex(node),
                                null);
                    } catch (SQLException ex) { ex.printStackTrace(); }
                    break;
                }
            }
        }
    }

    // -------------------------------------------------- Entry point helper --

    /**
     * Utility main so you can run this class directly from the IDE.
     * In a modular project remember to export the package in module-info.java.
     */
    public static void main(String[] args) {
        javafx.application.Application.launch(Standalone.class, args);
    }

    // Nested Application to satisfy JavaFX launcher when running standalone
    public static class Standalone extends javafx.application.Application {
        @Override
        public void start(javafx.stage.Stage stage) {
            stage.setScene(new Scene(new NQueensView(8)));
            stage.setTitle("N Queens Viewer");
            stage.show();
        }
    }
}