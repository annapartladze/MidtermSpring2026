package persistence.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import persistence.entity.Game;
import persistence.report.HighestScore;
import persistence.report.RecentGame;

public interface GameMapper {

    void createPlayersTable();

    void createGamesTable();

    void createRoundsTable();

    void createScoresTable();

    List<Map<String, Object>> gameColumns();

    List<Map<String, Object>> scoreColumns();

    void addWinnerPlayerIdToGames();

    void addPlayerIdToScores();

    void clearScores();

    void clearRounds();

    void clearGames();

    void clearPlayers();

    void insertPlayerIfMissing(@Param("name") String name);

    Integer findPlayerId(@Param("name") String name);

    void insertGame(Game game);

    void insertRound(
            @Param("gameId") int gameId,
            @Param("roundNumber") int roundNumber);

    void insertScore(
            @Param("gameId") int gameId,
            @Param("playerId") int playerId,
            @Param("score") int score);

    List<RecentGame> recentGames(@Param("limit") int limit);

    int playerWins(@Param("player") String player);

    List<HighestScore> highestScores(@Param("limit") int limit);

    int countGames();

    int countPlayers();

    int countRounds();

    int countScores();
}
