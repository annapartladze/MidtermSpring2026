import java.util.Random;

import persistence.DatabaseManager;
import persistence.GamePersistenceService;
import persistence.StatisticsService;

public class Main {

    public static void main(String[] args) {

        try {
            DatabaseManager.initialize();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean quietFlag = false;
        long seed = System.currentTimeMillis();
        String report = null;
        String reportPlayer = "Bot1";

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quietFlag = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--report") && i + 1 < args.length) {
                report = args[++i];
            } else if (args[i].equals("--player") && i + 1 < args.length) {
                reportPlayer = args[++i];
            } else if (args[i].equals("--help")) {
                System.out.println(
                        "Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N] [--report recent|wins|scores|all] [--player NAME]");
                return;
            }
        }

        if (report != null) {
            try {
                StatisticsService stats =
                        new StatisticsService();

                if (report.equals("recent") || report.equals("all")) {
                    stats.recentGames();
                }
                if (report.equals("wins") || report.equals("all")) {
                    stats.playerWins(reportPlayer);
                }
                if (report.equals("scores") || report.equals("all")) {
                    stats.highestScores();
                }
                if (!report.equals("recent") &&
                        !report.equals("wins") &&
                        !report.equals("scores") &&
                        !report.equals("all")) {
                    System.out.println(
                            "Unknown report. Use recent, wins, scores, or all.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        GameState state = new GameState();
        state.quiet = quietFlag;
        state.random = new Random(seed);
        state.setupPlayers(bots, human);

        if (state.playerNames.size() < 2 ||
                state.playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        GameEngine engine = new GameEngine(state);

        for (int g = 1; g <= games; g++) {

            if (!state.quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }

            engine.playGame();

            try {
                GamePersistenceService service =
                        new GamePersistenceService();

                service.saveGame(
                        state.winner,
                        state.roundsPlayed,
                        state.startedAt.toString(),
                        state.finishedAt.toString(),
                        state.playerNames.toArray(new String[0]),
                        state.scores
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(
                    state.playerNames.get(i)
                            + ": "
                            + state.scores[i]);
        }

        try {
            StatisticsService stats =
                    new StatisticsService();

            stats.recentGames();
            stats.playerWins(state.winner);
            stats.highestScores();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
