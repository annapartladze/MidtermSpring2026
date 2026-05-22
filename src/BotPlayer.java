import java.util.ArrayList;

public class BotPlayer {

    static int chooseCard(ArrayList<String> hand,
                          String upCard,
                          String calledColor) {

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);

            boolean ok =
                    RuleEngine.isLegal(card, upCard, calledColor);

            if (CardUtils.rank(card).equals("DRAW_TWO") && ok) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);

            boolean ok =
                    RuleEngine.isLegal(card, upCard, calledColor);

            if (CardUtils.rank(card).equals("SKIP") && ok) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);

            boolean ok =
                    RuleEngine.isLegal(card, upCard, calledColor);

            if (CardUtils.rank(card).equals("NUMBER") && ok) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }

        return -1;
    }

    static String chooseColor(ArrayList<String> hand) {

        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;

        for (int i = 0; i < hand.size(); i++) {

            String c = CardUtils.color(hand.get(i));

            if (c.equals("R")) {
                r++;
            } else if (c.equals("Y")) {
                y++;
            } else if (c.equals("G")) {
                g++;
            } else if (c.equals("B")) {
                b++;
            }
        }

        if (r >= y && r >= g && r >= b) {
            return "R";
        } else if (y >= r && y >= g && y >= b) {
            return "Y";
        } else if (g >= r && g >= y && g >= b) {
            return "G";
        } else {
            return "B";
        }
    }
}