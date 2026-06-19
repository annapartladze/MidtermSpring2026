package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatisticsService {

    public void recentGames() throws Exception {

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                            SELECT id,
                                   winner,
                                   rounds,
                                   finished_at
                            FROM games
                            ORDER BY id DESC
                            LIMIT 5
                            """);

            ResultSet rs = statement.executeQuery();

            System.out.println("\nRecent Games:");

            while (rs.next()) {
                System.out.println(
                        "Game " + rs.getInt("id")
                                + " Winner: "
                                + rs.getString("winner")
                                + " Rounds: "
                                + rs.getInt("rounds")
                                + " Finished: "
                                + rs.getString("finished_at"));
            }
        }
    }

    public void playerWins(String player)
            throws Exception {

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                            SELECT COUNT(*)
                            FROM games
                            WHERE winner = ?
                            """);

            statement.setString(1, player);

            ResultSet rs =
                    statement.executeQuery();

            if (rs.next()) {
                System.out.println(
                        player
                                + " has "
                                + rs.getInt(1)
                                + " wins.");
            }
        }
    }

    public void highestScores()
            throws Exception {

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            PreparedStatement statement =
                    connection.prepareStatement(
                            """
                            SELECT player_name,
                                   score
                            FROM scores
                            ORDER BY score DESC
                            LIMIT 5
                            """);

            ResultSet rs =
                    statement.executeQuery();

            System.out.println(
                    "\nHighest Scores:");

            while (rs.next()) {
                System.out.println(
                        rs.getString("player_name")
                                + ": "
                                + rs.getInt("score"));
            }
        }
    }
}