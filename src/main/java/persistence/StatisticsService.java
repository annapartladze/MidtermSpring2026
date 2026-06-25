package persistence;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import persistence.mapper.GameMapper;
import persistence.report.HighestScore;
import persistence.report.RecentGame;

public class StatisticsService {

    public List<RecentGame> recentGames() throws Exception {

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            List<RecentGame> games =
                    mapper.recentGames(5);
            System.out.println("\nRecent Games:");

            for (RecentGame game : games) {
                System.out.println(
                        "Game " + game.getId()
                                + " Winner: "
                                + game.getWinner()
                                + " Rounds: "
                                + game.getRounds()
                                + " Finished: "
                                + game.getFinishedAt());
            }

            return games;
        }
    }

    public int playerWins(String player)
            throws Exception {

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            int wins = mapper.playerWins(player);

            System.out.println(
                    player
                            + " has "
                            + wins
                            + " wins.");
            return wins;
        }
    }

    public List<HighestScore> highestScores()
            throws Exception {

        try (SqlSession session =
                     MyBatisUtil.getFactory().openSession()) {

            GameMapper mapper =
                    session.getMapper(GameMapper.class);

            List<HighestScore> scores =
                    mapper.highestScores(5);
            System.out.println(
                    "\nHighest Scores:");

            for (HighestScore score : scores) {
                System.out.println(
                        score.getPlayerName()
                                + ": "
                                + score.getScore());
            }

            return scores;
        }
    }
}
