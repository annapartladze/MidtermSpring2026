# UNO Rules Supported

## Deck Composition
Standard 108-card deck built in `GameEngine.buildDeck()`:
- Four colors: R (red), Y (yellow), G (green), B (blue)
- One 0 per color (4 total)
- Two each of 1-9 per color (72 total)
- Two Skip per color (8 total)
- Two Reverse per color (8 total)
- Two Draw Two per color (8 total)
- Four Wild (4 total)
- Four Wild Draw Four (4 total)

## Legal Play Validation
Implemented in `RuleEngine.isLegal()`. A card is legal when:
- its color matches the current active color, OR
- its number matches the top card number, OR
- its action type matches the top card action type, OR
- it is a Wild or Wild Draw Four

After a Wild is played the called color becomes the active color.
If the up card is a Wild but no color has been called, non-wild cards are rejected.

## Skip
Next player loses their turn. Play continues with the player after the skipped one.
Tested for both 2-player and 3-player games.

## Reverse
Turn direction flips between clockwise (1) and counterclockwise (-1).
Two-player variant: Reverse acts like Skip — the current player goes again.
Documented and tested in `testReverseTwoPlayersActsAsSkip`.

## Draw Two
Next player draws two cards and loses their turn.
Stacking: Not implemented.

## Wild
Player chooses the next active color. Human players are prompted (R/Y/G/B).
Bots pick the color they hold the most of. Called color gates all future legal-play checks.

## Wild Draw Four
Player chooses color; next player draws four cards and loses their turn.
Challenge rule: Not implemented.

## Draw/Pass Behavior
Player draws one card. If the drawn card is legal:
- Bot plays it immediately.
- Human is prompted: Play drawn card? y/n
  If not legal, the turn passes.
  Tested for both the legal and illegal drawn-card cases.

## UNO Call and Missed-UNO Penalty
When a player reaches one card, UNO is triggered immediately after the card is played:
- Bots call UNO automatically.
- Human players are prompted (Call UNO? y/n).
- If a human declines, two penalty cards are drawn.
- The unoCalled flag is reset at the start of every round so a previous round's
  call does not carry over.
  Timing: Penalty applied at the moment the hand reaches one card, before the next player's turn.

## Round Scoring
Round winner receives points equal to the sum of all cards remaining in opponents' hands:
- Number cards: face value (0-9)
- Skip, Reverse, Draw Two: 20 points each
- Wild, Wild Draw Four: 50 points each

## Multi-Round Game to Target Score
Rounds repeat until a player reaches or exceeds the target score.
Default target: 500 points. Configurable via --target N.
Final winner announced with their total score.

---

## Simplifications and Variants

Feature                      | Status
-----------------------------|---------------------------
Draw Two stacking            | Not implemented
Wild Draw Four challenge     | Not implemented
Bot strategy                 | Greedy: first legal non-wild, then wild
Starting wild card           | Redrawn until a non-wild card is the up card
Target score                 | 500 by default, configurable via --target
Two-player Reverse           | Acts as Skip (documented above)