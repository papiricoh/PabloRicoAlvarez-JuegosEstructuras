package org.papiricoh.core.dao;

import org.papiricoh.core.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoveDao {

    public int newGame(String type) throws SQLException {
        try (var pst = Db.get().prepareStatement(
                 "INSERT INTO game(type) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, type);
            pst.executeUpdate();
            try (var rs = pst.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : -1; }
        }
    }

    public void addMove(int gameId, int idx, String description, String data) throws SQLException {
        try (var pst = Db.get().prepareStatement(
                 "INSERT INTO move(game_id, idx, description, data) VALUES (?,?,?,?)")) {
            pst.setInt   (1, gameId);
            pst.setInt   (2, idx);
            pst.setString(3, description);
            pst.setString(4, data);
            pst.executeUpdate();
        }
    }

    public record Move(int idx, String description, String data) {}

    public List<Move> findMoves(int gameId) throws SQLException {
        try (var pst = Db.get().prepareStatement(
                 "SELECT idx, description, data FROM move WHERE game_id=? ORDER BY idx")) {
            pst.setInt(1, gameId);
            try (var rs = pst.executeQuery()) {
                List<Move> list = new ArrayList<>();
                while (rs.next())
                    list.add(new Move(rs.getInt(1), rs.getString(2), rs.getString(3)));
                return list;
            }
        }
    }

    public int idFor(String type) throws SQLException {
        try (PreparedStatement pst = Db.get().prepareStatement(
                "SELECT id FROM game WHERE type=?")) {
            pst.setString(1, type);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                else throw new IllegalStateException("Game type not found: " + type);
            }
        }
    }
}
