# Extension Readiness

## Proposed Extension

Smarter and configurable bot strategies.

---

## Why The Current Design Supports Extensions

The refactored design now separates several responsibilities into dedicated classes:

- CardUtils handles card parsing and scoring
- RuleEngine handles rule validation
- BotPlayer handles bot decision logic
- ConsoleUI handles console interaction
- applyCardEffect centralizes gameplay effect behavior

Because responsibilities are separated, new features can now be added with fewer changes across the system.

---

## Exactly Where The Extension Would Go

To add smarter bot strategies, BotPlayer.chooseCard() would become an
interface method. Main would hold one BotPlayer instance per player index.
The current signature:

    static int chooseCard(ArrayList<String> hand, String upCard, String calledColor)

would become an interface:

    interface BotStrategy {
        int chooseCard(ArrayList<String> hand, String upCard, String calledColor);
        String chooseColor(ArrayList<String> hand);
    }

Main would replace the humanPlayers boolean list with a BotStrategy
reference per player. No changes to RuleEngine, CardUtils, ConsoleUI,
or applyCardEffect would be needed.

The remaining difficulty is that chooseCard() currently receives only
the current hand, upCard, and calledColor. A strategy that reads
opponent hands would need Main to pass the full hands list as an
additional parameter. That change would affect only the call site in
Main and the interface signature, not any other class.

---

## Example Future Extensions

The current design now better supports several future extensions.

### Improved Bot AI

Different strategies can be implemented as BotStrategy implementations
without modifying gameplay orchestration logic.

Possible examples:
- aggressive bots that prioritize draw two and skip cards
- defensive bots that hold wilds until necessary
- difficulty levels selectable at startup
- probability-based decisions using remaining deck size
- card counting heuristics using visible hand information

---

### Additional Rules

New UNO rules can be added inside RuleEngine and applyCardEffect without
duplicating validation logic across the project.

Possible examples:
- stacking draw cards
- jump-in rules
- house rules
- challenge rules for wild draw four
- score multipliers

---

### Alternate User Interfaces

Because console interaction is isolated in ConsoleUI, future interfaces
could be added more easily. ConsoleUI.askHuman() and askColor() could be
replaced by implementations that read from a different input source without
touching RuleEngine, BotPlayer, or applyCardEffect.

Possible examples:
- graphical UI
- web interface
- network multiplayer interface
- mobile interface

---

### Better Card Modeling

The current primitive string representation could later be replaced with
a dedicated Card value object while affecting fewer parts of the system.
CardUtils would be the only class requiring significant changes since all
parsing is already centralized there.

This would:
- remove repeated string parsing
- improve type safety
- simplify legality checking
- simplify scoring logic

---

## Remaining Difficulties

Some extension difficulties still remain:

- Main still manages global state directly
- gameplay orchestration remains centralized in playGame
- cards still use primitive string representation
- chooseCard() would need an additional parameter to support
  opponent-aware strategies
- game state is not encapsulated into a dedicated GameState object,
  which would make passing state to strategies cleaner

The current global mutable state inside Main would also make future
multiplayer synchronization more difficult.

---

## Conclusion

The refactored structure provides a significantly safer foundation for
future development while preserving the original game behavior.
Responsibilities are now more clearly separated, gameplay rules are
centralized, gameplay effects are directly testable, and the bot
extension point is concrete enough to implement without touching
RuleEngine, CardUtils, ConsoleUI, or applyCardEffect.