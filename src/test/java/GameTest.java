import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    // ─── Card parsing ────────────────────────────────────────────────────────

    @Test
    void testCardParsing() {
        assertEquals("R", CardUtils.color("R5"));
        assertEquals("DRAW_TWO", CardUtils.rank("G+2"));
        assertEquals(50, CardUtils.points("W4"));
        assertEquals(20, CardUtils.points("RS"));
        assertEquals(5, CardUtils.points("R5"));
        assertEquals("REVERSE", CardUtils.rank("RR"));
        assertEquals("SKIP", CardUtils.rank("YS"));
        assertEquals("", CardUtils.color("W"));
        assertEquals("", CardUtils.color("W4"));
        assertEquals("WILD", CardUtils.rank("W"));
        assertEquals("WILD_DRAW_FOUR", CardUtils.rank("W4"));
        assertEquals(0, CardUtils.points("R0"));
        assertEquals(9, CardUtils.points("B9"));
        assertEquals(50, CardUtils.points("W"));
    }

    // ─── Legal play validation ───────────────────────────────────────────────

    @Test
    void testRuleLegality() {
        assertTrue(RuleEngine.isLegal("R2", "R9", ""));
        assertTrue(RuleEngine.isLegal("G9", "R9", ""));
        assertTrue(RuleEngine.isLegal("B3", "W", "B"));
        assertFalse(RuleEngine.isLegal("G3", "W", "B"));
        assertFalse(RuleEngine.isLegal("B3", "R9", ""));
        assertTrue(RuleEngine.isLegal("GS", "RS", ""));
        assertTrue(RuleEngine.isLegal("Y+2", "R+2", ""));
        assertTrue(RuleEngine.isLegal("BR", "RR", ""));
        assertTrue(RuleEngine.isLegal("W", "R5", ""));
        assertTrue(RuleEngine.isLegal("W4", "B2", ""));
        // wild up card with no called color — non-wild must be rejected
        assertFalse(RuleEngine.isLegal("R5", "W", ""));
        assertFalse(RuleEngine.isLegal("B3", "W4", ""));
        // wild is still legal even when up is wild with no color called
        assertTrue(RuleEngine.isLegal("W", "W4", ""));
    }

    @Test
    void testWildCalledColorControlsLegalPlay() {
        assertTrue(RuleEngine.isLegal("R5", "W", "R"));
        assertFalse(RuleEngine.isLegal("B5", "W", "R"));
        assertTrue(RuleEngine.isLegal("GS", "W4", "G"));
        assertFalse(RuleEngine.isLegal("YS", "W4", "G"));
    }

    // ─── Deck composition ────────────────────────────────────────────────────

    @Test
    void testDeckComposition() {
        GameState state = new GameState();
        GameEngine engine = new GameEngine(state);
        ArrayList<String> deck = engine.buildDeck();

        assertEquals(108, deck.size());

        String[] colors = {"R", "Y", "G", "B"};
        for (String c : colors) {
            assertEquals(1, count(deck, c + "0"),  c + "0 should appear once");
            for (int n = 1; n <= 9; n++) {
                assertEquals(2, count(deck, c + n), c + n + " should appear twice");
            }
            assertEquals(2, count(deck, c + "S"),  c + "S should appear twice");
            assertEquals(2, count(deck, c + "R"),  c + "R should appear twice");
            assertEquals(2, count(deck, c + "+2"), c + "+2 should appear twice");
        }
        assertEquals(4, count(deck, "W"),  "W should appear four times");
        assertEquals(4, count(deck, "W4"), "W4 should appear four times");
    }

    // ─── Bot behavior ────────────────────────────────────────────────────────

    @Test
    void testBotBehavior() {
        // prefers non-wild legal card
        ArrayList<String> h = new ArrayList<>();
        h.add("B3");
        h.add("R4");
        h.add("W");
        assertEquals(1, BotPlayer.chooseCard(h, "R9", ""));

        // falls back to wild when nothing else matches
        ArrayList<String> h2 = new ArrayList<>();
        h2.add("B3");
        h2.add("W");
        assertEquals(1, BotPlayer.chooseCard(h2, "R9", ""));

        // returns -1 when no legal card and no wild
        ArrayList<String> h3 = new ArrayList<>();
        h3.add("B3");
        assertEquals(-1, BotPlayer.chooseCard(h3, "R9", ""));

        // color choice: majority color wins
        ArrayList<String> colorHand = new ArrayList<>();
        colorHand.add("B1");
        colorHand.add("B2");
        colorHand.add("R3");
        assertEquals("B", BotPlayer.chooseColor(colorHand));
    }

    // ─── Scoring ─────────────────────────────────────────────────────────────

    @Test
    void testScoring() {
        GameState state = new GameState();
        state.playerNames.add("Bot1");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.playerNames.add("Bot2");
        state.humanPlayers.add(false);
        ArrayList<String> loserHand = new ArrayList<>();
        loserHand.add("R5");   // 5
        loserHand.add("GS");   // 20
        loserHand.add("W");    // 50
        state.hands.add(loserHand);

        state.playerNames.add("Bot3");
        state.humanPlayers.add(false);
        ArrayList<String> loserHand2 = new ArrayList<>();
        loserHand2.add("B9");  // 9
        loserHand2.add("W4");  // 50
        state.hands.add(loserHand2);

        state.currentPlayer = 0;
        GameEngine engine = new GameEngine(state);

        assertEquals(134, engine.calculateWinnerScore()); // 5+20+50+9+50
    }

    // ─── Turn advancement ────────────────────────────────────────────────────

    @Test
    void testTurnAdvancement() {
        GameState state = threePlayerState();
        state.currentPlayer = 0;
        state.direction = 1;
        state.next();
        assertEquals(1, state.currentPlayer);

        state.next();
        assertEquals(2, state.currentPlayer);

        // wrap around
        state.next();
        assertEquals(0, state.currentPlayer);

        // reverse direction
        state.direction = -1;
        state.next();
        assertEquals(2, state.currentPlayer);

        // two-player wrap in reverse
        GameState s2 = twoPlayerState();
        s2.currentPlayer = 0;
        s2.direction = -1;
        s2.next();
        assertEquals(1, s2.currentPlayer);
    }

    // ─── Card effects ────────────────────────────────────────────────────────

    @Test
    void testDrawTwoViaApplyCardEffect() {
        GameState state = twoPlayerState();
        state.deck.add("R1");
        state.deck.add("B2");
        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("R+2");

        // player 1 drew two, turn skipped back to player 0
        assertEquals(2, state.hands.get(1).size());
        assertEquals(0, state.currentPlayer);
    }

    @Test
    void testSkipEffect() {
        GameState state = threePlayerState();
        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("RS");

        // player 1 skipped, lands on player 2
        assertEquals(2, state.currentPlayer);
    }

    @Test
    void testReverseThreePlayers() {
        GameState state = threePlayerState();
        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("RR");

        assertEquals(-1, state.direction);
        // next() in reverse from 0 goes to player 2
        assertEquals(2, state.currentPlayer);
    }

    @Test
    void testReverseTwoPlayersActsAsSkip() {
        GameState state = twoPlayerState();
        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("RR");

        // direction flipped
        assertEquals(-1, state.direction);
        // same player goes again: next() twice wraps back to 0
        assertEquals(0, state.currentPlayer);
    }

    @Test
    void testWildDrawFourEffect() {
        GameState state = threePlayerState();
        state.deck.add("R1");
        state.deck.add("R2");
        state.deck.add("R3");
        state.deck.add("R4");
        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("W4");

        // player 1 drew four, turn lands on player 2
        assertEquals(4, state.hands.get(1).size());
        assertEquals(2, state.currentPlayer);
    }

    // ─── Draw/pass behavior ──────────────────────────────────────────────────

    @Test
    void testDrawPassBotPlaysLegalDrawnCard() {
        GameState state = new GameState();
        state.playerNames.add("Bot1");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.playerNames.add("Bot2");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.upCard = "R9";
        state.deck.add("R5"); // legal
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        int result = engine.handleDraw("Bot1", state.hands.get(0));

        assertEquals(0, result);
    }

    @Test
    void testDrawPassBotPassesIllegalDrawnCard() {
        GameState state = new GameState();
        state.playerNames.add("Bot1");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.playerNames.add("Bot2");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.upCard = "R9";
        state.deck.add("B3"); // illegal
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        int result = engine.handleDraw("Bot1", state.hands.get(0));

        // -2 means handleDraw already advanced the turn
        assertEquals(-2, result);
        // card was still added to hand
        assertEquals(1, state.hands.get(0).size());
    }

    // ─── UNO call and penalty ────────────────────────────────────────────────

    @Test
    void testMissedUnoPenaltyAddsCards() {
        GameState state = new GameState();
        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.hands.get(0).add("R5");
        state.deck.add("B1");
        state.deck.add("B2");
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyMissedUnoPenalty(0);

        assertEquals(3, state.hands.get(0).size());
    }

    @Test
    void testUnoPenaltyNotAppliedAfterValidCall() {
        GameState state = new GameState();
        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.hands.get(0).add("R5");
        state.deck.add("B1");
        state.deck.add("B2");
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.markUnoCall(0);
        engine.applyMissedUnoPenalty(0);

        // penalty must NOT apply because UNO was called
        assertEquals(1, state.hands.get(0).size());
    }

    @Test
    void testUnocalledFlagResetsBetweenRounds() {
        GameState state = new GameState();
        state.quiet = true;
        // simulate leftover true flag from a previous round
        state.unoCalled[0] = true;

        // the reset that playGame() does at round start
        for (int i = 0; i < state.unoCalled.length; i++) {
            state.unoCalled[i] = false;
        }

        assertFalse(state.unoCalled[0]);
    }

    // ─── Multi-round target score ─────────────────────────────────────────────

    @Test
    void testTargetScoreDetection() {
        GameState state = new GameState();
        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.scores[0] = 500;
        state.targetScore = 500;

        GameEngine engine = new GameEngine(state);
        assertTrue(engine.hasReachedTarget());
        assertEquals(0, engine.targetWinnerIndex());
    }

    @Test
    void testTargetScoreNotReachedYet() {
        GameState state = new GameState();
        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.scores[0] = 499;
        state.targetScore = 500;

        GameEngine engine = new GameEngine(state);
        assertFalse(engine.hasReachedTarget());
        assertEquals(-1, engine.targetWinnerIndex());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private int count(ArrayList<String> cards, String expected) {
        int total = 0;
        for (String card : cards) {
            if (card.equals(expected)) total++;
        }
        return total;
    }

    private GameState twoPlayerState() {
        GameState state = new GameState();
        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        state.playerNames.add("B");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        return state;
    }

    private GameState threePlayerState() {
        GameState state = twoPlayerState();
        state.playerNames.add("C");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());
        return state;
    }
}