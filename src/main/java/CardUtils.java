public class CardUtils {

    public static String color(String card) {

        if (card == null || card.length() == 0 || card.startsWith("W")) {
            return "";
        }

        return card.substring(0, 1);
    }

    public static String rank(String card) {

        if (card == null || card.length() == 0) {
            return "";
        }

        if (card.equals("W")) {
            return "WILD";
        }

        if (card.equals("W4")) {
            return "WILD_DRAW_FOUR";
        }

        String suffix = card.substring(1);

        if (suffix.equals("S")) {
            return "SKIP";
        }

        if (suffix.equals("R")) {
            return "REVERSE";
        }

        if (suffix.equals("+2")) {
            return "DRAW_TWO";
        }

        return "NUMBER";
    }

    public static int number(String card) {

        if (!rank(card).equals("NUMBER")) {
            return -1;
        }

        try {
            return Integer.parseInt(card.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int points(String card) {

        String r = rank(card);

        if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) {
            return 50;
        }

        if (r.equals("SKIP")
                || r.equals("REVERSE")
                || r.equals("DRAW_TWO")) {
            return 20;
        }

        int num = number(card);

        return num >= 0 ? num : 0;
    }
}