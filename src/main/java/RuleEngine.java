public class RuleEngine {

    public static boolean isLegal(String card, String up, String call) {

        if (card == null || up == null) {
            return false;
        }

        // Wilds are always legal
        if (card.startsWith("W")) {
            return true;
        }

        // Active called color after wild
        if (call != null && !call.equals("")) {
            return CardUtils.color(card).equals(call);
        }

        // If up card is a wild but no color has been called yet,
        // nothing non-wild is legal
        if (up.startsWith("W")) {
            return false;
        }

        // Match by color
        if (CardUtils.color(card).equals(CardUtils.color(up))) {
            return true;
        }

        // Match by action type
        String cardRank = CardUtils.rank(card);
        String upRank   = CardUtils.rank(up);

        if (cardRank.equals(upRank) && !cardRank.equals("NUMBER")) {
            return true;
        }

        // Match by number
        return cardRank.equals("NUMBER")
                && upRank.equals("NUMBER")
                && CardUtils.number(card) == CardUtils.number(up);
    }
}