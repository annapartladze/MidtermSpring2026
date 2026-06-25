package persistence;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import persistence.report.HighestScore;
import persistence.report.RecentGame;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsServiceTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUpDatabase() throws Exception {
        System.setProperty(
                "uno.database.url",
                "jdbc:sqlite:" + tempDir.resolve("uno-test.db"));
        MyBatisUtil.resetFactory();
        DatabaseManager.initialize();
        DatabaseManager.clearAllData();

        GamePersistenceService service =
                new GamePersistenceService();

        service.saveGame(
                "Bot1",
                3,
                "2026-06-19T17:00:00",
                "2026-06-19T17:01:00",
                new String[]{"Bot1", "Bot2"},
                new int[]{40, 5}
        );

        service.saveGame(
                "Bot2",
                4,
                "2026-06-19T18:00:00",
                "2026-06-19T18:02:00",
                new String[]{"Bot1", "Bot2"},
                new int[]{10, 60}
        );
    }

    @Test
    void statisticsMethodsReturnStoredReportData() throws Exception {
        StatisticsService stats =
                new StatisticsService();

        List<RecentGame> recentGames =
                stats.recentGames();
        List<HighestScore> highestScores =
                stats.highestScores();

        assertEquals(2, recentGames.size());
        assertEquals("Bot2", recentGames.get(0).getWinner());
        assertEquals(4, recentGames.get(0).getRounds());
        assertEquals(1, stats.playerWins("Bot1"));
        assertEquals(1, stats.playerWins("Bot2"));
        assertEquals("Bot2", highestScores.get(0).getPlayerName());
        assertEquals(60, highestScores.get(0).getScore());
    }
}
