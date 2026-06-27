import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEngine {

    private static final Logger logger =
            LoggerFactory.getLogger(GameEngine.class);

    GameState state;

    GameEngine(GameState state) {
        this.state = state;
    }

    ArrayList<String> buildDeck() {
        ArrayList<String> cards = new ArrayList<String>();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            cards.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                cards.add(colors[c] + n);
                cards.add(colors[c] + n);
            }
            cards.add(colors[c] + "S");
            cards.add(colors[c] + "S");
            cards.add(colors[c] + "R");
            cards.add(colors[c] + "R");
            cards.add(colors[c] + "+2");
            cards.add(colors[c] + "+2");
        }
        for (int i = 0; i < 4; i++) {
            cards.add("W");
            cards.add("W4");
        }
        return cards;
    }

    void playGame() {
        state.startedAt  = LocalDateTime.now();
        state.finishedAt = null;
        state.roundsPlayed = 0;
        state.winner = "";
        logger.info("UNO game started");

        state.deck.clear();
        state.deck.addAll(buildDeck());

        Collections.shuffle(state.deck, state.random);
        state.discard.clear();

        for (int i = 0; i < state.hands.size(); i++) {
            state.hands.get(i).clear();
        }

        // FIX: reset unoCalled flags at the start of every round
        for (int i = 0; i < state.unoCalled.length; i++) {
            state.unoCalled[i] = false;
        }

        for (int i = 0; i < state.playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                state.hands.get(i).add(state.draw());
            }
        }

        state.upCard = state.draw();
        while (state.upCard.startsWith("W")) {
            state.discard.add(state.upCard);
            state.upCard = state.draw();
        }
        state.calledColor   = "";
        state.direction     = 1;
        state.currentPlayer = state.random.nextInt(state.playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            state.roundsPlayed++;
            String name = state.playerNames.get(state.currentPlayer);

            logger.info("{}'s turn", name);

            ArrayList<String> hand = state.hands.get(state.currentPlayer);

            if (!state.quiet) {
                System.out.println("\nUp card: " + state.upCard
                        + (state.calledColor.equals("") ? "" : " called " + state.calledColor));
                System.out.println(name + " hand: " + state.join(hand));
            }

            int chosen = chooseMove(hand);

            if (chosen == -1) {
                chosen = handleDraw(name, hand);
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
                    logger.warn("{} selected an invalid index", name);
                    if (!state.quiet) {
                        System.out.println(name + " selected an invalid index and draws a penalty card.");
                    }
                    hand.add(state.draw());
                    state.next();
                    continue;
                }

                String card = hand.get(chosen);
                boolean ok = RuleEngine.isLegal(card, state.upCard, state.calledColor);

                if (!ok) {
                    logger.warn("{} attempted illegal card {}", name, card);
                    if (!state.quiet) {
                        System.out.println(name + " tried illegal card " + card + " and draws a penalty card.");
                    }
                    hand.add(state.draw());
                    state.next();
                    continue;
                }

                hand.remove(chosen);
                state.discard.add(state.upCard);
                state.upCard = card;
                logger.info("{} played {}", name, card);
                state.calledColor = "";
                if (!state.quiet) {
                    System.out.println(name + " plays " + card);
                }

                if (card.equals("W") || card.equals("W4")) {
                    if (state.humanPlayers.get(state.currentPlayer).booleanValue()) {
                        state.calledColor = ConsoleUI.askColor(state.scanner);
                    } else {
                        state.calledColor = BotPlayer.chooseColor(hand);
                    }
                    if (!state.quiet) {
                        System.out.println(name + " calls " + state.calledColor);
                    }
                }

                // reset UNO flag if hand is no longer at 1
                if (hand.size() != 1) {
                    state.unoCalled[state.currentPlayer] = false;
                }

                if (hand.size() == 1) {
                    handleUnoCall(state.currentPlayer);
                }

                if (hand.size() == 0) {
                    state.winner     = name;
                    state.finishedAt = LocalDateTime.now();

                    int points = calculateWinnerScore();
                    state.scores[state.currentPlayer] += points;

                    if (!state.quiet) {
                        System.out.println(name + " wins and scores " + points);
                    }
                    logger.info("{} won the game with {} points", name, points);
                    return;
                } else {
                    applyCardEffect(card);
                }

            } else if (chosen == -1) {
                // no draw happened, just pass
                state.next();
            }
            // chosen == -2 means handleDraw already called state.next()
        }
        if (!state.quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    int chooseMove(ArrayList<String> hand) {
        if (state.humanPlayers.get(state.currentPlayer).booleanValue()) {
            return ConsoleUI.askHuman(hand, state.scanner, state.upCard, state.calledColor);
        } else {
            return BotPlayer.chooseCard(hand, state.upCard, state.calledColor);
        }
    }

    int handleDraw(String name, ArrayList<String> hand) {
        String drawn = state.draw();
        hand.add(drawn);
        logger.info("{} drew {}", name, drawn);

        if (!state.quiet) {
            System.out.println(name + " draws " + drawn);
        }
        if (RuleEngine.isLegal(drawn, state.upCard, state.calledColor)) {
            if (!state.humanPlayers.get(state.currentPlayer).booleanValue()) {
                return hand.size() - 1;
            } else {
                System.out.print("Play drawn card " + drawn + "? y/n: ");
                if (state.scanner.hasNextLine()) {
                    String answer = state.scanner.nextLine();
                    if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                        return hand.size() - 1;
                    }
                }
            }
        }
        state.next();
        return -2; // turn already advanced inside here
    }

    void handleUnoCall(int playerIndex) {
        String name = state.playerNames.get(playerIndex);

        if (!state.humanPlayers.get(playerIndex).booleanValue()) {
            markUnoCall(playerIndex);
            if (!state.quiet) {
                System.out.println(name + " says UNO!");
            }
            return;
        }

        System.out.print("Call UNO? y/n: ");
        if (state.scanner.hasNextLine()) {
            String answer = state.scanner.nextLine();
            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                markUnoCall(playerIndex);
                if (!state.quiet) {
                    System.out.println(name + " says UNO!");
                }
                return;
            }
        }

        applyMissedUnoPenalty(playerIndex);
    }

    void markUnoCall(int playerIndex) {
        state.unoCalled[playerIndex] = true;
    }

    void applyMissedUnoPenalty(int playerIndex) {
        ArrayList<String> hand = state.hands.get(playerIndex);
        if (hand.size() == 1 && !state.unoCalled[playerIndex]) {
            hand.add(state.draw());
            hand.add(state.draw());
            if (!state.quiet) {
                System.out.println(
                        state.playerNames.get(playerIndex)
                                + " missed UNO and draws two.");
            }
        }
    }

    int calculateWinnerScore() {
        int points = 0;
        for (int i = 0; i < state.hands.size(); i++) {
            if (i != state.currentPlayer) {
                for (String c : state.hands.get(i)) {
                    points += CardUtils.points(c);
                }
            }
        }
        return points;
    }

    void applyCardEffect(String card) {
        String rank = CardUtils.rank(card);

        if (rank.equals("SKIP")) {
            state.next();
            state.next();
        } else if (rank.equals("REVERSE")) {
            state.direction = state.direction * -1;
            if (state.playerNames.size() == 2) {
                // two-player: Reverse acts like Skip
                state.next();
                state.next();
            } else {
                state.next();
            }
        } else if (rank.equals("DRAW_TWO")) {
            state.next();
            state.hands.get(state.currentPlayer).add(state.draw());
            state.hands.get(state.currentPlayer).add(state.draw());
            if (!state.quiet) {
                System.out.println(state.playerNames.get(state.currentPlayer) + " draws two.");
            }
            state.next();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            state.next();
            for (int i = 0; i < 4; i++) {
                state.hands.get(state.currentPlayer).add(state.draw());
            }
            if (!state.quiet) {
                System.out.println(state.playerNames.get(state.currentPlayer) + " draws four.");
            }
            state.next();
        } else {
            state.next();
        }
    }

    boolean hasReachedTarget() {
        return targetWinnerIndex() >= 0;
    }

    int targetWinnerIndex() {
        for (int i = 0; i < state.playerNames.size(); i++) {
            if (state.scores[i] >= state.targetScore) {
                return i;
            }
        }
        return -1;
    }
}