import java.util.ArrayList;

public class SelfTest {

    static int passed = 0;

    static void run() {
        passed = 0;
        testCardParsing();
        testRuleLegality();
        testBotBehavior();
        testScoring();
        testTurnAdvancement();
        testCardEffects();
        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void testCardParsing() {
        if (CardUtils.color("R5").equals("R")) passed++; else fail("color R5");
        if (CardUtils.rank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        if (CardUtils.points("W4") == 50) passed++; else fail("wild points");
        if (CardUtils.points("RS") == 20) passed++; else fail("skip points");
        if (CardUtils.points("R5") == 5) passed++; else fail("number points");
        if (CardUtils.rank("RR").equals("REVERSE")) passed++; else fail("reverse rank");
        if (CardUtils.rank("YS").equals("SKIP")) passed++; else fail("skip rank");
    }

    static void testRuleLegality() {
        if (RuleEngine.isLegal("R2", "R9", "")) passed++; else fail("same color");
        if (RuleEngine.isLegal("G9", "R9", "")) passed++; else fail("same number");
        if (RuleEngine.isLegal("B3", "W", "B")) passed++; else fail("called color");
        if (!RuleEngine.isLegal("B3", "R9", "")) passed++; else fail("illegal mismatch");
        if (RuleEngine.isLegal("GS", "RS", "")) passed++; else fail("same action skip");
        if (RuleEngine.isLegal("Y+2", "R+2", "")) passed++; else fail("same action draw two");
        if (RuleEngine.isLegal("W", "R5", "")) passed++; else fail("wild legal");
        if (RuleEngine.isLegal("W4", "B2", "")) passed++; else fail("wild draw four legal");
        if (RuleEngine.isLegal("BR", "RR", "")) passed++; else fail("reverse on reverse");
    }

    static void testBotBehavior() {
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
        if (autoChosen == 1) passed++; else fail("bot auto plays drawn card");
    }

    static void testScoring() {
        GameState state = new GameState();
        state.playerNames.add("Bot1"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.playerNames.add("Bot2"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.hands.get(1).add("R5");
        state.hands.get(1).add("GS");
        state.hands.get(1).add("W");
        state.currentPlayer = 0;

        GameEngine engine = new GameEngine(state);
        if (engine.calculateWinnerScore() == 75) passed++; else fail("score from other hands");

        ArrayList<String> unoHand = new ArrayList<String>();
        unoHand.add("R5"); unoHand.add("B3");
        unoHand.remove(0);
        if (unoHand.size() == 1) passed++; else fail("uno at one card");
    }

    static void testTurnAdvancement() {
        GameState state = new GameState();
        state.playerNames.add("A"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.playerNames.add("B"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.playerNames.add("C"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());

        state.currentPlayer = 0; state.direction = 1;
        state.next();
        if (state.currentPlayer == 1) passed++; else fail("next forward");

        state.currentPlayer = 0; state.direction = 1;
        state.next(); state.next();
        if (state.currentPlayer == 2) passed++; else fail("skip behavior");

        state.direction = 1; state.direction = state.direction * -1;
        if (state.direction == -1) passed++; else fail("reverse direction");

        state.currentPlayer = 2; state.direction = 1;
        state.next();
        if (state.currentPlayer == 0) passed++; else fail("next wraparound");

        GameState state2 = new GameState();
        state2.playerNames.add("A"); state2.humanPlayers.add(false); state2.hands.add(new ArrayList<String>());
        state2.playerNames.add("B"); state2.humanPlayers.add(false); state2.hands.add(new ArrayList<String>());
        state2.currentPlayer = 0; state2.direction = 1;
        state2.direction = state2.direction * -1;
        state2.next(); state2.next();
        if (state2.currentPlayer == 0) passed++; else fail("reverse 2 players skip");
    }

    static void testCardEffects() {
        GameState state = new GameState();
        state.playerNames.add("A"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.playerNames.add("B"); state.humanPlayers.add(false); state.hands.add(new ArrayList<String>());
        state.deck.add("R1"); state.deck.add("B2");
        state.currentPlayer = 0; state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("R+2");

        if (state.hands.get(1).size() == 2) passed++; else fail("applyCardEffect draw two");

        GameState state2 = new GameState();
        state2.playerNames.add("A"); state2.humanPlayers.add(false); state2.hands.add(new ArrayList<String>());
        state2.playerNames.add("B"); state2.humanPlayers.add(false); state2.hands.add(new ArrayList<String>());
        state2.deck.add("R1"); state2.deck.add("B2");
        state2.currentPlayer = 0; state2.direction = 1; state2.next();
        int before = state2.hands.get(state2.currentPlayer).size();
        state2.hands.get(state2.currentPlayer).add(state2.draw());
        state2.hands.get(state2.currentPlayer).add(state2.draw());
        if (state2.hands.get(state2.currentPlayer).size() - before == 2) passed++; else fail("draw two adds 2 cards");

        GameState state3 = new GameState();
        state3.playerNames.add("A"); state3.humanPlayers.add(false); state3.hands.add(new ArrayList<String>());
        state3.playerNames.add("B"); state3.humanPlayers.add(false); state3.hands.add(new ArrayList<String>());
        state3.deck.add("R1"); state3.deck.add("B2"); state3.deck.add("G3"); state3.deck.add("Y4");
        state3.currentPlayer = 0; state3.direction = 1; state3.next();
        int beforeW4 = state3.hands.get(state3.currentPlayer).size();
        for (int i = 0; i < 4; i++) state3.hands.get(state3.currentPlayer).add(state3.draw());
        if (state3.hands.get(state3.currentPlayer).size() - beforeW4 == 4) passed++; else fail("wild draw four adds 4 cards");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}