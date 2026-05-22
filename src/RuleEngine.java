public class RuleEngine {

    static boolean isLegal(String card, String up, String call) {

        if (card.startsWith("W")) {
            return true;
        }

        if (CardUtils.color(card).equals(CardUtils.color(up))) {
            return true;
        }

        if (!call.equals("")
                && CardUtils.color(card).equals(call)) {
            return true;
        }

        if (CardUtils.rank(card).equals(CardUtils.rank(up))
                && !CardUtils.rank(card).equals("NUMBER")) {
            return true;
        }

        return CardUtils.rank(card).equals("NUMBER")
                && CardUtils.rank(up).equals("NUMBER")
                && CardUtils.number(card) == CardUtils.number(up);
    }
}