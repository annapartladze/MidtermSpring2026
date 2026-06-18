import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleUI {

    public static int askHuman(
            ArrayList<String> hand,
            Scanner scanner,
            String up,
            String call) {

        while (true) {

            System.out.print("Choose card index/code or draw: ");

            if (!scanner.hasNextLine()) {
                return -1;
            }

            String input =
                    scanner.nextLine().trim();

            if (input.equalsIgnoreCase("draw")) {
                return -1;
            }

            // Preserve original behavior:
            // illegal index causes penalty later in Main
            try {

                int idx = Integer.parseInt(input);

                if (idx >= 0 && idx < hand.size()) {
                    return idx;
                }

            } catch (NumberFormatException e) {
                // Continue to card-code matching
            }

            for (int i = 0; i < hand.size(); i++) {

                if (hand.get(i).equalsIgnoreCase(input)) {

                    if (RuleEngine.isLegal(
                            hand.get(i),
                            up,
                            call)) {

                        return i;

                    } else {

                        System.out.println(
                                "That card is not legal.");
                    }
                }
            }

            System.out.println(
                    "Card not found.");
        }
    }

    public static String askColor(Scanner scanner) {

        while (true) {

            System.out.print("Choose color (R/Y/G/B): ");

            if (!scanner.hasNextLine()) {
                return "R";
            }

            String input =
                    scanner.nextLine().trim().toUpperCase();

            if (input.equals("R")
                    || input.equals("Y")
                    || input.equals("G")
                    || input.equals("B")) {

                return input;
            }

            System.out.println(
                    "Invalid color selection.");
        }
    }
}