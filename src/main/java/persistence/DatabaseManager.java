package persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import persistence.mapper.GameMapper;

public class DatabaseManager {

    public static void initialize() {

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            mapper.createPlayersTable();
            mapper.createGamesTable();
            mapper.createRoundsTable();
            mapper.createScoresTable();

            if (!hasColumn(mapper.gameColumns(), "winner_player_id")) {
                mapper.addWinnerPlayerIdToGames();
            }
            if (!hasColumn(mapper.scoreColumns(), "player_id")) {
                mapper.addPlayerIdToScores();
            }

            session.commit();
        }
    }

    public static void clearAllData() {

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            mapper.clearScores();
            mapper.clearRounds();
            mapper.clearGames();
            mapper.clearPlayers();

            session.commit();
        }
    }

    private static boolean hasColumn(
            List<Map<String, Object>> columns,
            String expectedName) {

        for (Map<String, Object> column : columns) {
            Object name = column.get("name");
            if (name != null &&
                    expectedName.equalsIgnoreCase(name.toString())) {
                return true;
            }
        }
        return false;
    }
}
