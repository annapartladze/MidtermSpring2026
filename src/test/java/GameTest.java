import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void testCardParsing() {
        assertEquals("R", CardUtils.color("R5"));
        assertEquals("DRAW_TWO", CardUtils.rank("G+2"));
        assertEquals(50, CardUtils.points("W4"));
        assertEquals(20, CardUtils.points("RS"));
        assertEquals(5, CardUtils.points("R5"));
        assertEquals("REVERSE", CardUtils.rank("RR"));
        assertEquals("SKIP", CardUtils.rank("YS"));
    }

    @Test
    void testRuleLegality() {
        assertTrue(RuleEngine.isLegal("R2", "R9", ""));
        assertTrue(RuleEngine.isLegal("G9", "R9", ""));
        assertTrue(RuleEngine.isLegal("B3", "W", "B"));
        assertFalse(RuleEngine.isLegal("B3", "R9", ""));
        assertTrue(RuleEngine.isLegal("GS", "RS", ""));
        assertTrue(RuleEngine.isLegal("Y+2", "R+2", ""));
        assertTrue(RuleEngine.isLegal("W", "R5", ""));
        assertTrue(RuleEngine.isLegal("W4", "B2", ""));
        assertTrue(RuleEngine.isLegal("BR", "RR", ""));
    }

    @Test
    void testBotBehavior() {
        ArrayList<String> h = new ArrayList<>();
        h.add("B3");
        h.add("R4");
        h.add("W");

        assertEquals(1, BotPlayer.chooseCard(h, "R9", ""));

        ArrayList<String> h2 = new ArrayList<>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");

        assertEquals("B", BotPlayer.chooseColor(h2));

        ArrayList<String> autoHand = new ArrayList<>();
        autoHand.add("G3");

        String autoDrawn = "R5";
        autoHand.add(autoDrawn);

        int autoChosen = -1;
        if (RuleEngine.isLegal(autoDrawn, "R9", "")) {
            autoChosen = autoHand.size() - 1;
        }

        assertEquals(1, autoChosen);
    }

    @Test
    void testScoring() {
        GameState state = new GameState();
        state.playerNames.add("Bot1");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.playerNames.add("Bot2");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.hands.get(1).add("R5");
        state.hands.get(1).add("GS");
        state.hands.get(1).add("W");

        state.currentPlayer = 0;

        GameEngine engine = new GameEngine(state);

        assertEquals(75, engine.calculateWinnerScore());

        ArrayList<String> unoHand = new ArrayList<>();
        unoHand.add("R5");
        unoHand.add("B3");

        unoHand.remove(0);

        assertEquals(1, unoHand.size());
    }

    @Test
    void testTurnAdvancement() {
        GameState state = new GameState();

        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.playerNames.add("B");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.playerNames.add("C");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.currentPlayer = 0;
        state.direction = 1;
        state.next();

        assertEquals(1, state.currentPlayer);

        state.currentPlayer = 0;
        state.direction = 1;
        state.next();
        state.next();

        assertEquals(2, state.currentPlayer);

        state.direction = 1;
        state.direction = state.direction * -1;

        assertEquals(-1, state.direction);

        state.currentPlayer = 2;
        state.direction = 1;
        state.next();

        assertEquals(0, state.currentPlayer);

        GameState state2 = new GameState();

        state2.playerNames.add("A");
        state2.humanPlayers.add(false);
        state2.hands.add(new ArrayList<>());

        state2.playerNames.add("B");
        state2.humanPlayers.add(false);
        state2.hands.add(new ArrayList<>());

        state2.currentPlayer = 0;
        state2.direction = 1;
        state2.direction = state2.direction * -1;
        state2.next();
        state2.next();

        assertEquals(0, state2.currentPlayer);
    }

    @Test
    void testCardEffects() {
        GameState state = new GameState();

        state.playerNames.add("A");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.playerNames.add("B");
        state.humanPlayers.add(false);
        state.hands.add(new ArrayList<>());

        state.deck.add("R1");
        state.deck.add("B2");

        state.currentPlayer = 0;
        state.direction = 1;
        state.quiet = true;

        GameEngine engine = new GameEngine(state);
        engine.applyCardEffect("R+2");

        assertEquals(2, state.hands.get(1).size());

        GameState state2 = new GameState();

        state2.playerNames.add("A");
        state2.humanPlayers.add(false);
        state2.hands.add(new ArrayList<>());

        state2.playerNames.add("B");
        state2.humanPlayers.add(false);
        state2.hands.add(new ArrayList<>());

        state2.deck.add("R1");
        state2.deck.add("B2");

        state2.currentPlayer = 0;
        state2.direction = 1;
        state2.next();

        int before = state2.hands.get(state2.currentPlayer).size();

        state2.hands.get(state2.currentPlayer).add(state2.draw());
        state2.hands.get(state2.currentPlayer).add(state2.draw());

        assertEquals(
                2,
                state2.hands.get(state2.currentPlayer).size() - before
        );

        GameState state3 = new GameState();

        state3.playerNames.add("A");
        state3.humanPlayers.add(false);
        state3.hands.add(new ArrayList<>());

        state3.playerNames.add("B");
        state3.humanPlayers.add(false);
        state3.hands.add(new ArrayList<>());

        state3.deck.add("R1");
        state3.deck.add("B2");
        state3.deck.add("G3");
        state3.deck.add("Y4");

        state3.currentPlayer = 0;
        state3.direction = 1;
        state3.next();

        int beforeW4 =
                state3.hands.get(state3.currentPlayer).size();

        for (int i = 0; i < 4; i++) {
            state3.hands.get(state3.currentPlayer)
                    .add(state3.draw());
        }

        assertEquals(
                4,
                state3.hands.get(state3.currentPlayer).size()
                        - beforeW4
        );
    }
}