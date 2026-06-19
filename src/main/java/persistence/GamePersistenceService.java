package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GamePersistenceService {

    public void saveGame(
            String winner,
            int roundsPlayed,
            String startedAt,
            String finishedAt,
            String[] playerNames,
            int[] scores)
            throws Exception {

        try (Connection connection =
                     DatabaseManager.getConnection()) {

            PreparedStatement gameStatement =
                    connection.prepareStatement(
                            """
                            INSERT INTO games
                            (started_at,
                             finished_at,
                             winner,
                             rounds)
                            VALUES (?, ?, ?, ?)
                            """,
                            PreparedStatement.RETURN_GENERATED_KEYS);

            gameStatement.setString(1, startedAt);
            gameStatement.setString(2, finishedAt);
            gameStatement.setString(3, winner);
            gameStatement.setInt(4, roundsPlayed);

            gameStatement.executeUpdate();

            ResultSet keys =
                    gameStatement.getGeneratedKeys();

            keys.next();
            int gameId = keys.getInt(1);

            for (int i = 0;
                 i < playerNames.length;
                 i++) {

                PreparedStatement scoreStatement =
                        connection.prepareStatement(
                                """
                                INSERT INTO scores
                                (game_id,
                                 player_name,
                                 score)
                                VALUES (?, ?, ?)
                                """);

                scoreStatement.setInt(1, gameId);
                scoreStatement.setString(2, playerNames[i]);
                scoreStatement.setInt(3, scores[i]);

                scoreStatement.executeUpdate();
            }
        }
    }
}