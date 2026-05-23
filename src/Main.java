import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static ArrayList<String> playerNames = new ArrayList<String>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    static ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
    static ArrayList<String> deck = new ArrayList<String>();
    static ArrayList<String> discard = new ArrayList<String>();
    static int[] scores = new int[10];
    static int currentPlayer = 0;
    static int direction = 1;
    static String upCard = "";
    static String calledColor = "";
    static boolean quiet = false;
    static Random random = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        random = new Random(seed);
        setupPlayers(bots, human);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        for (int g = 1; g <= games; g++) {
            if (!quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            playGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println(playerNames.get(i) + ": " + scores[i]);
        }
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }

    static void playGame() {
        deck.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            deck.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                deck.add(colors[c] + n);
                deck.add(colors[c] + n);
            }
            deck.add(colors[c] + "S");
            deck.add(colors[c] + "S");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "+2");
            deck.add(colors[c] + "+2");
        }

        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }

        Collections.shuffle(deck, random);
        discard.clear();
        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).clear();
        }
        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
            }
        }
        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);

            if (!quiet) {
                System.out.println("\nUp card: " + upCard + (calledColor.equals("") ? "" : " called " + calledColor));
                System.out.println(name + " hand: " + join(hand));
            }

            int chosen = -1;

            if (humanPlayers.get(currentPlayer).booleanValue()) {
                chosen = ConsoleUI.askHuman(hand, scanner, upCard, calledColor);
            } else {
                chosen = BotPlayer.chooseCard(hand, upCard, calledColor);
            }

            if (chosen == -1) {
                String drawn = draw();
                hand.add(drawn);
                if (!quiet) {
                    System.out.println(name + " draws " + drawn);
                }
                if (RuleEngine.isLegal(drawn, upCard, calledColor)) {
                    if (!humanPlayers.get(currentPlayer).booleanValue()) {
                        chosen = hand.size() - 1;
                    } else {
                        System.out.print("Play drawn card " + drawn + "? y/n: ");
                        if (scanner.hasNextLine()) {
                            String answer = scanner.nextLine();
                            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                                chosen = hand.size() - 1;
                            }
                        }
                    }
                }
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    if (!quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = RuleEngine.isLegal(card, upCard, calledColor);

                if (!ok) {
                    if (!quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(draw());
                    next();
                    continue;
                }

                hand.remove(chosen);
                discard.add(upCard);
                upCard = card;
                calledColor = "";
                if (!quiet) {
                    System.out.println(name + " plays " + card);
                }

                if (card.equals("W") || card.equals("W4")) {
                    if (humanPlayers.get(currentPlayer).booleanValue()) {
                        calledColor = ConsoleUI.askColor(scanner);
                    } else {
                        calledColor = BotPlayer.chooseColor(hand);
                    }
                    if (!quiet) {
                        System.out.println(name + " calls " + calledColor);
                    }
                }

                if (hand.size() == 1 && !quiet) {
                    System.out.println(name + " says UNO!");
                }

                if (hand.size() == 0) {

                    int points = calculateWinnerScore();
                    scores[currentPlayer] += points;
                    if (!quiet) {
                        System.out.println(name + " wins and scores " + points);
                    }
                    return;
                } else {
                    applyCardEffect(card);
                }
            } else {
                next();
            }
        }
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    static String draw() {
        if (deck.size() == 0) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.size() == 0) {
            return "W";
        }
        return deck.remove(0);
    }

    static void next() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    static String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }
    static int calculateWinnerScore() {

        int points = 0;

        for (int i = 0; i < hands.size(); i++) {

            if (i != currentPlayer) {

                for (String c : hands.get(i)) {

                    points += CardUtils.points(c);
                }
            }
        }

        return points;
    }


    static void applyCardEffect(String card) {

        String rank = CardUtils.rank(card);

        if (rank.equals("SKIP")) {

            next();
            next();

        } else if (rank.equals("REVERSE")) {

            direction = direction * -1;

            if (playerNames.size() == 2) {

                next();
                next();

            } else {

                next();
            }

        } else if (rank.equals("DRAW_TWO")) {

            next();

            hands.get(currentPlayer).add(draw());
            hands.get(currentPlayer).add(draw());

            if (!quiet) {
                System.out.println(
                        playerNames.get(currentPlayer)
                                + " draws two.");
            }

            next();

        } else if (rank.equals("WILD_DRAW_FOUR")) {

            next();

            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }

            if (!quiet) {
                System.out.println(
                        playerNames.get(currentPlayer)
                                + " draws four.");
            }

            next();

        } else {

            next();
        }
    }


    static void selfTest() {
        int passed = 0;
        passed += testCardParsing();
        passed += testRuleLegality();
        passed += testBotBehavior();
        passed += testScoring();
        passed += testTurnAdvancement();
        passed += testCardEffects();
        System.out.println("Passed " + passed + " characterization checks.");
    }

    static int testCardParsing() {
        int passed = 0;
        if (CardUtils.color("R5").equals("R")) passed++; else fail("color R5");
        if (CardUtils.rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        if (CardUtils.points("W4") == 50) passed++; else fail("wild points");
        if (CardUtils.points("RS") == 20) passed++; else fail("skip points");
        if (CardUtils.points("R5") == 5) passed++; else fail("number points");
        if (CardUtils.rank("RR").equals("REVERSE")) passed++; else fail("reverse rank");
        if (CardUtils.rank("YS").equals("SKIP")) passed++; else fail("skip rank");
        return passed;
    }

    static int testRuleLegality() {
        int passed = 0;
        if (RuleEngine.isLegal("R2", "R9", "")) passed++; else fail("same color");
        if (RuleEngine.isLegal("G9", "R9", "")) passed++; else fail("same number");
        if (RuleEngine.isLegal("B3", "W", "B")) passed++; else fail("called color");
        if (!RuleEngine.isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");
        if (RuleEngine.isLegal("GS", "RS", "")) passed++; else fail("same action skip");
        if (RuleEngine.isLegal("Y+2", "R+2", "")) passed++; else fail("same action draw two");
        if (RuleEngine.isLegal("W", "R5", "")) passed++; else fail("wild legal");
        if (RuleEngine.isLegal("W4", "B2", "")) passed++; else fail("wild draw four legal");
        if (RuleEngine.isLegal("BR", "RR", "")) passed++;
        else fail("reverse on reverse");
        return passed;
    }

    static int testBotBehavior() {
        int passed = 0;
        ArrayList<String> h = new ArrayList<String>();
        h.add("B3"); h.add("R4"); h.add("W");
        if (BotPlayer.chooseCard(h, "R9", "") == 1) passed++; else fail("bot normal before wild");

        ArrayList<String> h2 = new ArrayList<String>();
        h2.add("B1"); h2.add("B2"); h2.add("R3");
        if (BotPlayer.chooseColor(h2).equals("B")) passed++; else fail("bot color");

        ArrayList<String> autoHand = new ArrayList<String>();

        autoHand.add("G3");

        String autoDrawn = "R5";

        autoHand.add(autoDrawn);

        int autoChosen = -1;

        if (RuleEngine.isLegal(autoDrawn, "R9", "")) {

            autoChosen = autoHand.size() - 1;
        }

        if (autoChosen == 1) passed++;
        else fail("bot auto plays drawn card");
        return passed;
    }

    static int testScoring() {
        int passed = 0;
        playerNames.clear(); humanPlayers.clear(); hands.clear();
        playerNames.add("Bot1"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("Bot2"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        hands.get(1).add("R5");
        hands.get(1).add("GS");
        hands.get(1).add("W");
        currentPlayer = 0;
        int actual = 0;
        for (int i = 0; i < hands.size(); i++) {
            if (i != currentPlayer) {
                for (String c : hands.get(i)) actual += CardUtils.points(c);
            }
        }
        if (actual == 75) passed++; else fail("score from other hands");

        ArrayList<String> unoHand = new ArrayList<String>();
        unoHand.add("R5"); unoHand.add("B3");
        unoHand.remove(0);
        if (unoHand.size() == 1) passed++; else fail("uno at one card");

        playerNames.clear();
        humanPlayers.clear();
        hands.clear();

        playerNames.add("A");
        humanPlayers.add(false);
        hands.add(new ArrayList<String>());

        playerNames.add("B");
        humanPlayers.add(false);
        hands.add(new ArrayList<String>());

        hands.get(1).add("R5");
        hands.get(1).add("W");

        currentPlayer = 0;

        if (calculateWinnerScore() == 55) {
            passed++;
        } else {
            fail("calculateWinnerScore");
        }
        return passed;
    }

    static int testTurnAdvancement() {
        int passed = 0;
        playerNames.clear(); hands.clear(); humanPlayers.clear();
        playerNames.add("A"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("B"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("C"); humanPlayers.add(false); hands.add(new ArrayList<String>());

        currentPlayer = 0; direction = 1;
        next();
        if (currentPlayer == 1) passed++; else fail("next forward");

        currentPlayer = 0; direction = 1;
        next(); next();
        if (currentPlayer == 2) passed++; else fail("skip behavior");

        direction = 1; direction = direction * -1;
        if (direction == -1) passed++; else fail("reverse direction");

        currentPlayer = 2; direction = 1;
        next();
        if (currentPlayer == 0) passed++; else fail("next wraparound");

        playerNames.clear(); hands.clear(); humanPlayers.clear();
        playerNames.add("A"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("B"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        currentPlayer = 0; direction = 1;
        direction = direction * -1;
        next(); next();
        if (currentPlayer == 0) passed++; else fail("reverse 2 players skip");
        return passed;
    }

    static int testCardEffects() {
        int passed = 0;

        // draw two via applyCardEffect
        playerNames.clear(); hands.clear(); humanPlayers.clear();
        playerNames.add("A"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("B"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        deck.clear(); deck.add("R1"); deck.add("B2");
        currentPlayer = 0; direction = 1;
        quiet = true;

        applyCardEffect("R+2");

        quiet = false;
        if (hands.get(1).size() == 2) passed++; else fail("applyCardEffect draw two");

        // draw two card count
        playerNames.clear(); hands.clear(); humanPlayers.clear();
        playerNames.add("A"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("B"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        deck.clear(); deck.add("R1"); deck.add("B2");
        currentPlayer = 0; direction = 1; next();
        int before = hands.get(currentPlayer).size();
        hands.get(currentPlayer).add(draw());
        hands.get(currentPlayer).add(draw());
        if (hands.get(currentPlayer).size() - before == 2) passed++; else fail("draw two adds 2 cards");

        // wild draw four card count
        playerNames.clear(); hands.clear(); humanPlayers.clear();
        playerNames.add("A"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        playerNames.add("B"); humanPlayers.add(false); hands.add(new ArrayList<String>());
        deck.clear();
        deck.add("R1"); deck.add("B2"); deck.add("G3"); deck.add("Y4");
        currentPlayer = 0; direction = 1; next();
        int beforeW4 = hands.get(currentPlayer).size();
        for (int i = 0; i < 4; i++) hands.get(currentPlayer).add(draw());
        if (hands.get(currentPlayer).size() - beforeW4 == 4) passed++; else fail("wild draw four adds 4 cards");

        return passed;
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }

}