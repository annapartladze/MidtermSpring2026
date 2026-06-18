import java.util.ArrayList;
import java.util.Collections;

public class GameEngine {

    GameState state;

    GameEngine(GameState state) {
        this.state = state;
    }

    void playGame() {
        state.deck.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            state.deck.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                state.deck.add(colors[c] + n);
                state.deck.add(colors[c] + n);
            }
            state.deck.add(colors[c] + "S");
            state.deck.add(colors[c] + "S");
            state.deck.add(colors[c] + "R");
            state.deck.add(colors[c] + "R");
            state.deck.add(colors[c] + "+2");
            state.deck.add(colors[c] + "+2");
        }
        for (int i = 0; i < 4; i++) {
            state.deck.add("W");
            state.deck.add("W4");
        }

        Collections.shuffle(state.deck, state.random);
        state.discard.clear();
        for (int i = 0; i < state.hands.size(); i++) {
            state.hands.get(i).clear();
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
        state.calledColor = "";
        state.direction = 1;
        state.currentPlayer = state.random.nextInt(state.playerNames.size());

        int guard = 0;
        while (guard < 3000) {
            guard++;
            String name = state.playerNames.get(state.currentPlayer);
            ArrayList<String> hand = state.hands.get(state.currentPlayer);

            if (!state.quiet) {
                System.out.println("\nUp card: " + state.upCard + (state.calledColor.equals("") ? "" : " called " + state.calledColor));
                System.out.println(name + " hand: " + state.join(hand));
            }

            int chosen = chooseMove(hand);

            if (chosen == -1) {
                chosen = handleDraw(name, hand);
            }

            if (chosen >= 0) {
                if (chosen >= hand.size()) {
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

                if (hand.size() == 1 && !state.quiet) {
                    System.out.println(name + " says UNO!");
                }

                if (hand.size() == 0) {
                    int points = calculateWinnerScore();
                    state.scores[state.currentPlayer] += points;
                    if (!state.quiet) {
                        System.out.println(name + " wins and scores " + points);
                    }
                    return;
                } else {
                    applyCardEffect(card);
                }
            } else {
                state.next();
            }
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
        return -1;
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
}