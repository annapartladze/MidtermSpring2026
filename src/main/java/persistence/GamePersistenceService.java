package persistence;

import org.apache.ibatis.session.SqlSession;

import persistence.entity.Game;
import persistence.mapper.GameMapper;

public class GamePersistenceService {

    public void saveGame(
            String winner,
            int roundsPlayed,
            String startedAt,
            String finishedAt,
            String[] playerNames,
            int[] scores)
            throws Exception {

        DatabaseManager.initialize();

        SqlSession session =
                MyBatisUtil.getFactory().openSession();

        try {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            for (String playerName : playerNames) {
                mapper.insertPlayerIfMissing(playerName);
            }
            mapper.insertPlayerIfMissing(winner);

            Integer winnerPlayerId = mapper.findPlayerId(winner);

            Game game = new Game();
            game.setStartedAt(startedAt);
            game.setFinishedAt(finishedAt);
            game.setWinner(winner);
            game.setWinnerPlayerId(winnerPlayerId);
            game.setRounds(roundsPlayed);

            mapper.insertGame(game);

            for (int round = 1; round <= roundsPlayed; round++) {
                mapper.insertRound(game.getId(), round);
            }

            for (int i = 0; i < playerNames.length; i++) {
                Integer playerId = mapper.findPlayerId(playerNames[i]);
                mapper.insertScore(game.getId(), playerId, scores[i]);
            }

            session.commit();

        } finally {
            session.close();
        }
    }
}
