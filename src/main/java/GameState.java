import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.time.LocalDateTime;

public class GameState {

    ArrayList<String> playerNames = new ArrayList<String>();
    ArrayList<Boolean> humanPlayers = new ArrayList<Boolean>();
    ArrayList<ArrayList<String>> hands = new ArrayList<ArrayList<String>>();
    ArrayList<String> deck = new ArrayList<String>();
    ArrayList<String> discard = new ArrayList<String>();
    int[] scores = new int[10];
    boolean[] unoCalled = new boolean[10];
    int currentPlayer = 0;
    int direction = 1;
    String upCard = "";
    String calledColor = "";
    int targetScore = 500;

    int roundsPlayed = 0;
    String winner = "";

    LocalDateTime startedAt;
    LocalDateTime finishedAt;

    boolean quiet = false;
    Random random = new Random();
    Scanner scanner = new Scanner(System.in);

    void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        for (int i = 0; i < unoCalled.length; i++) {
            unoCalled[i] = false;
        }
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

    String draw() {
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

    void next() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) {
            currentPlayer = 0;
        }
        if (currentPlayer < 0) {
            currentPlayer = playerNames.size() - 1;
        }
    }

    String join(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }
}
