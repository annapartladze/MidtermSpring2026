package persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class StatisticsServiceTest {

    @Test
    void statisticsMethodsShouldNotThrow() {
        StatisticsService stats =
                new StatisticsService();

        assertDoesNotThrow(() -> {
            stats.recentGames();
            stats.playerWins("Bot1");
            stats.highestScores();
        });
    }
}