package persistence;

import java.nio.file.Path;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import persistence.mapper.GameMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GamePersistenceServiceTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUpDatabase() {
        System.setProperty(
                "uno.database.url",
                "jdbc:sqlite:" + tempDir.resolve("uno-test.db"));
        MyBatisUtil.resetFactory();
        DatabaseManager.initialize();
        DatabaseManager.clearAllData();
    }

    @Test
    void saveGameStoresPlayersRoundsScoresAndWinner() throws Exception {
        GamePersistenceService service =
                new GamePersistenceService();

        service.saveGame(
                "Bot1",
                10,
                "2026-06-19T17:00:00",
                "2026-06-19T17:01:00",
                new String[]{"Bot1", "Bot2"},
                new int[]{50, 0}
        );

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            assertEquals(1, mapper.countGames());
            assertEquals(2, mapper.countPlayers());
            assertEquals(10, mapper.countRounds());
            assertEquals(2, mapper.countScores());
            assertEquals(1, mapper.playerWins("Bot1"));
            assertEquals(0, mapper.playerWins("Bot2"));
        }
    }
}
