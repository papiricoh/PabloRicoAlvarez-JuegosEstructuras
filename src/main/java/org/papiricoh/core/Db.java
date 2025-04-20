package org.papiricoh.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class Db {
    private static final String URL = "jdbc:sqlite:game_log.db";
    private static Connection conn;


    public static Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) conn = DriverManager.getConnection(URL);
        return conn;
    }


    public static void init() throws SQLException {
        try (Statement st = get().createStatement()) {
            st.executeUpdate("""
                 CREATE TABLE IF NOT EXISTS game (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     type TEXT NOT NULL,
                     started_at TEXT DEFAULT CURRENT_TIMESTAMP
                 );
                 CREATE TABLE IF NOT EXISTS move (
                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                     game_id INTEGER NOT NULL,
                     idx INTEGER NOT NULL,
                     description TEXT,
                     data TEXT,                     -- JSON o cualquier detalle
                     created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                     FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
                 );
                 INSERT OR IGNORE INTO game(type) VALUES
                                         ('NQUEENS'), ('KNIGHTS'), ('HANOI');
            """);
        }
    }

    public static void reset() throws SQLException {
        try (Statement st = get().createStatement()) {
            st.executeUpdate("""
                 DROP TABLE IF EXISTS game;
                 DROP TABLE IF EXISTS move;
            """);
        }
        init();
    }


    private Db() {}
}