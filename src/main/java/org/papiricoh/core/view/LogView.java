package org.papiricoh.core.view;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.papiricoh.core.Db;
import org.papiricoh.core.dao.MoveDao;

import java.sql.SQLException;
import java.util.Map;

public class LogView extends BorderPane {

    private final MoveDao dao = new MoveDao();
    private final TableView<MoveDao.Move> table = new TableView<>();

    // guarda los gameId activos de cada juego
    private final Map<GameType,Integer> gameIds;

    public enum GameType { NQUEENS, KNIGHTS, HANOI }

    /** Constructor: recibe los tres gameId que crean tus controladores */
    public LogView(int queensId, int knightsId, int hanoiId) {
        this.gameIds = Map.of(
                GameType.NQUEENS, queensId,
                GameType.KNIGHTS, knightsId,
                GameType.HANOI , hanoiId);

        /*------------- barra superior con Tabs -------------*/
        TabPane tabs = new TabPane(
                buildTab("N‑Queens" , GameType.NQUEENS),
                buildTab("Caballos" , GameType.KNIGHTS),
                buildTab("Hanoi"    , GameType.HANOI )
        );
        tabs.getSelectionModel().selectFirst();

        /*------------- tabla de movimientos -------------*/
        TableColumn<MoveDao.Move,Integer> colIdx  = new TableColumn<>("#");
        colIdx.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().idx()));
        TableColumn<MoveDao.Move,String>  colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().description()));
        table.getColumns().addAll(colIdx, colDesc);

        /*------------- botón RESET BD -------------*/
        Button reset = new Button("Reset DB");
        reset.setOnAction(e -> {
            try {
                Db.reset();
                table.getItems().clear();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        ToolBar bar = new ToolBar(reset); bar.setOrientation(Orientation.HORIZONTAL);

        /*------------- composición -------------*/
        setTop(tabs);
        setCenter(table);
        setBottom(bar);
    }

    private Tab buildTab(String title, GameType type) {
        Tab t = new Tab(title);
        t.setClosable(false);
        t.setOnSelectionChanged(ev -> {
            if (t.isSelected()) loadMoves(gameIds.get(type));
        });
        return t;
    }

    private void loadMoves(int gameId) {
        try {
            var data = FXCollections.observableArrayList(dao.findMoves(gameId));
            table.setItems(data);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Factory para tu TabPane principal */
    public static Parent create(int queensId, int knightsId, int hanoiId) {
        return new LogView(queensId, knightsId, hanoiId);
    }
}