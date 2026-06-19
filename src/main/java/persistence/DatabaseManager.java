package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:uno.db";

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize()
            throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS games (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        started_at TEXT,
                        finished_at TEXT,
                        winner TEXT,
                        rounds INTEGER
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS scores (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        game_id INTEGER,
                        player_name TEXT,
                        score INTEGER
                    )
                    """);
        }
    }
}