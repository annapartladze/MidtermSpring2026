import java.util.Random;

public class Main {

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean quietFlag = false;
        long seed = System.currentTimeMillis();

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
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        GameState state = new GameState();
        state.quiet = quietFlag;
        state.random = new Random(seed);
        state.setupPlayers(bots, human);

        if (state.playerNames.size() < 2 || state.playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        GameEngine engine = new GameEngine(state);

        for (int g = 1; g <= games; g++) {
            if (!state.quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            engine.playGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }
    }
}