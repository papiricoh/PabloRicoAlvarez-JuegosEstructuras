package org.papiricoh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.papiricoh.core.Db;
import org.papiricoh.core.view.LogView;
import org.papiricoh.hanoi.view.HanoiView;
import org.papiricoh.horses.view.KnightsTourView;
import org.papiricoh.queens.view.NQueensView;

import java.sql.SQLException;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Db.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        TabPane root = new TabPane();

        root.getTabs().add(new Tab("N‑Reinas", NQueensView.create()));
        root.getTabs().add(new Tab("Recorrido del Caballo", KnightsTourView.create()));
        root.getTabs().add(new Tab("Torres de Hanoi", HanoiView.create(6)));

        root.getTabs().add(new Tab("Histórico", LogView.create(1, 2, 3)));

        stage.setTitle("Juegos Algorítmicos");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}