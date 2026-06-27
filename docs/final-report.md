# Final Project Report

## UNO Rules Implemented

All core UNO rules are implemented and tested:

### Deck Composition
Standard 108-card deck built in `GameEngine.buildDeck()`. Four colors (R, Y, G, B),
one 0 per color, two each of 1–9, Skip, Reverse, Draw Two per color, four Wild,
four Wild Draw Four. Verified by `testDeckComposition` which checks every card type
and count individually.

### Legal Play Validation
`RuleEngine.isLegal()` checks match by color, number, action type, and wild rules.
After a Wild is played the called color gates all future legality checks. If the up
card is a Wild but no color has been called, non-wild cards are correctly rejected.
Covered by `testRuleLegality` and `testWildCalledColorControlsLegalPlay`.

### Skip
Next player loses their turn. Play continues with the player after the skipped one.
Implemented in `GameEngine.applyCardEffect()`. Tested in `testSkipEffect`.

### Reverse
Turn direction flips between clockwise (1) and counterclockwise (-1).
Two-player variant: Reverse acts like Skip — the current player goes again.
Implemented in `applyCardEffect()`. Tested in `testReverseThreePlayers` and
`testReverseTwoPlayersActsAsSkip`.

### Draw Two
Next player draws two cards and loses their turn. Implemented in `applyCardEffect()`.
Tested directly via `testDrawTwoViaApplyCardEffect`.

### Wild
Player chooses the next active color. Human players are prompted (R/Y/G/B). Bots
pick the color they hold the most of via `BotPlayer.chooseColor()`. Called color
gates all subsequent legal-play checks. Tested in `testWildCalledColorControlsLegalPlay`.

### Wild Draw Four
Player chooses color; next player draws four cards and loses their turn.
Tested in `testWildDrawFourEffect`.

### Draw/Pass Behavior
Player draws one card. If the drawn card is legal, a bot plays it immediately.
A human is prompted "Play drawn card? y/n". If not legal the turn advances.
`handleDraw()` returns the card index when played, or -2 when passing (turn
already advanced inside the method to avoid double-advancing).
Tested in `testDrawPassBotPlaysLegalDrawnCard` and `testDrawPassBotPassesIllegalDrawnCard`.

### UNO Call and Missed-UNO Penalty
When a player reaches one card, UNO is triggered immediately after the card is played.
Bots call automatically. Humans are prompted. Declining draws two penalty cards.
The `unoCalled` flag is reset at the start of every `playGame()` call so a previous
round's call does not carry over.
Tested in `testMissedUnoPenaltyAddsCards`, `testUnoPenaltyNotAppliedAfterValidCall`,
and `testUnocalledFlagResetsBetweenRounds`.

### Round Scoring
Winner receives points equal to the sum of all cards in opponents' hands:
number cards at face value, Skip/Reverse/Draw Two at 20, Wild/Wild Draw Four at 50.
Implemented in `GameEngine.calculateWinnerScore()`. Tested in `testScoring` with
a three-player scenario covering all card types.

### Multi-Round Game to Target Score
Rounds repeat until a player reaches or exceeds the target score (default 500,
configurable via --target N). Final winner announced with total score.
Tested in `testTargetScoreDetection` and `testTargetScoreNotReachedYet`.

---

## How to Play from the CLI

Build:

    mvn package

Run an interactive game:

    java -jar target/uno-cli-1.0.jar --human --bots 2

On your turn the game shows the up card and your hand with indices:

    Up card: R5
    You hand: 0:B3 1:R9 2:W
    Choose card index/code or draw:

Enter an index (1), a card code (R9), or draw. After playing a Wild or
Wild Draw Four you are asked to choose a color (R/Y/G/B). When you reach one
card you are asked to call UNO (y/n). Declining draws two penalty cards.

Other useful flags:

    --bots N        number of bot players (default 3)
    --games N       number of games to play
    --quiet         suppress per-turn output
    --seed N        fixed random seed for reproducibility
    --target N      play until a player reaches N points
    --report all    show recent games, wins, and scores

---

## Architecture: Game Logic vs CLI

Class         | Responsibility
--------------|---------------------------------------------------------------
CardUtils     | Card parsing — color, rank, points. No I/O.
RuleEngine    | Legal play validation. No I/O. Fully unit-testable.
BotPlayer     | Bot card and color selection. No I/O. Fully unit-testable.
GameState     | All mutable game state. No logic, no I/O.
GameEngine    | Turn loop and rule orchestration. Calls ConsoleUI only for human prompts.
ConsoleUI     | All Scanner and System.out interaction isolated here.
Main          | CLI argument parsing and outer game loop only.

`RuleEngine`, `CardUtils`, `BotPlayer`, and all `GameEngine` effect methods
(`applyCardEffect`, `calculateWinnerScore`, `handleDraw`, `applyMissedUnoPenalty`)
are testable without a Scanner or any console. Tests never need to mock I/O.

Persistence is handled by MyBatis in the `persistence` package.
`GamePersistenceService` saves each completed game to SQLite.
`StatisticsService` queries recent games, player wins, and highest scores.

---

## Tests Added

All tests are in `src/test/java/GameTest.java`. Run with:

    mvn test

Test                                    | What it covers
----------------------------------------|-----------------------------------------------
testCardParsing                         | color, rank, points for every card type
testRuleLegality                        | all legal and illegal play combinations
testWildCalledColorControlsLegalPlay    | called color gates future legality
testDeckComposition                     | every card type and count in a 108-card deck
testBotBehavior                         | prefers non-wild, falls back to wild, color majority
testScoring                             | 3-player score with all card types
testTurnAdvancement                     | direction, wrap-around, 2 and 3 player
testDrawTwoViaApplyCardEffect           | Draw Two gives cards and skips turn
testSkipEffect                          | Skip advances past next player
testReverseThreePlayers                 | direction flips, correct next player
testReverseTwoPlayersActsAsSkip         | 2-player Reverse acts as Skip
testWildDrawFourEffect                  | next player draws 4 and is skipped
testDrawPassBotPlaysLegalDrawnCard      | bot plays legal drawn card
testDrawPassBotPassesIllegalDrawnCard   | bot passes illegal drawn card, turn advances
testMissedUnoPenaltyAddsCards          | missed UNO draws two cards
testUnoPenaltyNotAppliedAfterValidCall  | valid UNO call blocks penalty
testUnocalledFlagResetsBetweenRounds    | flag resets each round
testTargetScoreDetection               | target score reached detected correctly
testTargetScoreNotReachedYet           | target not reached returns -1

Persistence tests in `GamePersistenceServiceTest` and `StatisticsServiceTest` cover
saving and querying games, players, rounds, scores, and wins using an isolated
in-memory SQLite database.

---

## Known Limitations

- No Wild Draw Four challenge rule
- No Draw Two stacking
- Bot strategy is greedy (first legal non-wild card, then wild); no lookahead
- CLI only; no GUI or network play
- GameEngine still contains some System.out calls for informational messages
  (draws, penalties); these are guarded by state.quiet but not fully routed
  through ConsoleUI