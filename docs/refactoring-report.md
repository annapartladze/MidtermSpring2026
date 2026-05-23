# Refactoring Report

## Characterized Behavior

The following behaviors were preserved through characterization tests:

- matching cards by color
- matching cards by number
- matching by action type
- wild card legality
- wild draw four legality
- skip behavior
- reverse behavior
- draw two behavior
- draw penalty behavior
- UNO state behavior
- score calculation from remaining hands
- bot card selection behavior
- bot color selection behavior
- turn advancement behavior
- wraparound turn behavior
- reverse behavior with two players
- bot auto-playing a drawn legal card

A total of 29 characterization checks were implemented and repeatedly executed during refactoring to preserve behavior.

---

## Major Design Problems Found

The original implementation contained several design smells:

- one very large Main class
- duplicated legality checking logic
- mixed gameplay and console interaction
- bot logic mixed with rule logic
- gameplay effect logic embedded inside the main loop
- primitive-heavy card representation
- global mutable state
- long gameplay loop
- duplicated helper methods

---

## Refactorings Performed

The project was refactored incrementally while preserving behavior.

### Extracted CardUtils

A new CardUtils class was created to centralize:

- card color parsing
- card rank parsing
- card number parsing
- point calculation

Benefits:
- removed duplicated helper logic
- centralized card parsing
- improved consistency
- simplified gameplay code

Additional defensive validation was also added for malformed card values.

---

### Extracted RuleEngine

A RuleEngine class was introduced to centralize legal move validation.

Benefits:
- removed duplicated legality checks
- established a single source of truth for rule validation
- simplified gameplay code
- simplified bot logic
- improved testability

---

### Extracted BotPlayer

Bot behavior was moved into a dedicated BotPlayer class.

Responsibilities extracted:
- bot card selection
- bot color selection

Benefits:
- separated AI behavior from gameplay orchestration
- simplified Main
- improved readability
- made future bot strategies easier to implement

---

### Extracted ConsoleUI

Human interaction code was moved into ConsoleUI.

Responsibilities extracted:
- reading user moves
- validating user input
- reading wild card color choices

Benefits:
- separated console interaction from gameplay logic
- simplified Main
- improved maintainability
- improved future UI extensibility

Additional input safety handling was also added for malformed or closed input streams.

---

### Extracted applyCardEffect

Card effect handling logic was extracted from the large gameplay loop into a dedicated helper method.

Responsibilities isolated:
- skip behavior
- reverse behavior
- draw two behavior
- wild draw four behavior

Benefits:
- reduced complexity inside playGame
- improved readability
- improved direct testability of gameplay rules
- centralized effect behavior into one location

The extracted method can now be tested directly without running the full CLI game loop.

---
### Extracted calculateWinnerScore

Winner score calculation logic was extracted from playGame into a dedicated helper method.

Benefits:
- reduced gameplay loop complexity
- centralized score calculation
- improved readability
- improved direct testability
- simplified winner handling logic

### Modularized Characterization Tests

The original large selfTest method was decomposed into smaller focused helper methods:

- testCardParsing
- testRuleLegality
- testBotBehavior
- testScoring
- testTurnAdvancement
- testCardEffects

Benefits:
- improved readability
- improved maintainability
- clearer test organization
- simpler future test extension

---

### Simplified Gameplay Validation

Gameplay validation inside playGame was updated to reuse RuleEngine instead of repeating legality logic.

---

### Removed Dead Code

Obsolete helper methods and duplicated logic were removed from Main after extraction.

---

## Benefits Of The Refactored Design

The resulting design has several improvements:

- reduced duplication
- clearer separation of responsibilities
- centralized rule validation
- centralized effect handling
- cleaner bot behavior
- cleaner console interaction
- improved maintainability
- improved readability
- easier future extension support
- safer future rule modification
- improved direct testability

---

## Tradeoffs Made

Extracting applyCardEffect() centralizes all card effect logic and makes
it directly testable, but it still reads from and writes to global state
(currentPlayer, direction, hands, deck). A cleaner design would pass
game state as parameters, but that required changing every method
signature and risked introducing bugs during the midterm window.

ConsoleUI.askHuman loops on unrecognized card-code input rather than
returning a raw index. This preserves the penalty behavior for
out-of-range numeric indices, which Main still handles at the
chosen >= hand.size() check. Unrecognized card codes no longer reach
that penalty path. This change is intentional.

Cards remain primitive strings throughout. Replacing them with a Card
value object would eliminate parsing in CardUtils entirely but required
changing every method signature across all five classes.

---

## Remaining Design Issues

Some limitations still remain:

- Main still coordinates significant game state
- global mutable state still exists
- playGame still performs orchestration responsibilities
- cards are still represented as primitive strings

These limitations were intentionally not fully redesigned in order to preserve behavior and avoid rewriting the entire application.

---

## Refactoring Strategy

The refactoring process followed a safe incremental strategy:

1. preserve behavior first
2. add characterization tests
3. extract small responsibilities
4. centralize duplicated logic
5. modularize tests
6. remove dead code
7. repeatedly rerun tests after each refactor

## Refactoring Steps In Order

Each step below was followed by rerunning scripts/test.sh to confirm
behavior was preserved before continuing.

1. Added characterization tests for skip rank, reverse rank, draw two
   rank, action matching, wild legality — 17 checks passing
2. Extracted CardUtils with color(), rank(), number(), points()
   — reran tests, 17 passing
3. Extracted RuleEngine.isLegal() — reran tests, 17 passing
4. Updated Main to call RuleEngine instead of inline legality logic
   — reran tests, 17 passing
5. Extracted BotPlayer.chooseCard() and chooseColor()
   — reran tests, 17 passing
6. Extracted ConsoleUI.askHuman() and askColor()
   — CLI ran correctly, reran tests, 17 passing
7. Added characterization tests for scoring, turn advancement,
   skip simulation, reverse, wraparound — 23 checks passing
8. Extracted applyCardEffect() from the game loop
   — reran tests, 23 passing
9. Added testCardEffects() calling applyCardEffect() directly
   — 29 checks passing
10. Split selfTest() into six focused helper methods
    — reran tests, 29 passing

This minimized risk while progressively improving maintainability and structure.