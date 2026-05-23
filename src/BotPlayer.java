import java.util.ArrayList;

public class BotPlayer {

    public static int chooseCard(
            ArrayList<String> hand,
            String up,
            String call) {

        // Prefer non-wild legal cards first
        for (int i = 0; i < hand.size(); i++) {

            String card = hand.get(i);

            if (!card.startsWith("W")
                    && RuleEngine.isLegal(card, up, call)) {

                return i;
            }
        }

        // Use wilds only if needed
        for (int i = 0; i < hand.size(); i++) {

            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }

        return -1;
    }

    public static String chooseColor(ArrayList<String> hand) {

        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;

        for (String card : hand) {

            String c = CardUtils.color(card);

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

        int max = Math.max(Math.max(r, y), Math.max(g, b));

        if (max == r) return "R";
        if (max == y) return "Y";
        if (max == g) return "G";

        return "B";
    }
}