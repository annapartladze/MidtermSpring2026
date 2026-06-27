# UNO CLI — Final Project

A complete command-line UNO implementation in Java with Maven, SQLite, MyBatis, and JUnit 5.

## Project Structure

Class              | Role
-------------------|----------------------------------------------
CardUtils          | Card parsing and scoring
RuleEngine         | Legal move validation
BotPlayer          | Bot decision logic
ConsoleUI          | All user input and output
GameState          | Centralized mutable game state
GameEngine         | Turn loop and rule orchestration
Main               | CLI argument parsing and game loop
persistence/       | MyBatis + SQLite persistence layer

## Requirements

- Java 17
- Maven 3.9+
- Docker Desktop (optional)

## Build

    mvn compile

## Run Tests

    mvn test

## Run the Game

Bot-only game (default 3 bots):

    mvn exec:java

Interactive game with a human player:

    mvn exec:java -Dexec.args="--human --bots 2"

Play until someone reaches 500 points:

    mvn exec:java -Dexec.args="--bots 3 --target 500"

Five quiet bot games:

    mvn exec:java -Dexec.args="--bots 3 --games 5 --quiet"

View database reports without playing:

    mvn exec:java -Dexec.args="--report all --player Bot1"

## CLI Usage

On your turn you will see:

    Up card: R5  called G
    You hand: 0:B3 1:G9 2:W
    Choose card index/code or draw:

Enter an index (1), a card code (G9), or draw.
After a Wild or Wild Draw Four you are asked to choose a color (R/Y/G/B).
When you reach one card you are prompted to call UNO (y/n).
Declining draws two penalty cards.

## Card Codes

Code  | Card
------|----------------
R5    | Red 5
YS    | Yellow Skip
BR    | Blue Reverse
G+2   | Green Draw Two
W     | Wild
W4    | Wild Draw Four

## CLI Flags

Flag                              | Description
----------------------------------|------------------------------------------
--bots N                          | Number of bot players (default 3)
--games N                         | Number of games to play
--human                           | Add a human player
--quiet                           | Suppress per-turn output
--seed N                          | Fixed random seed
--target N                        | Play until a player reaches N points
--report recent/wins/scores/all   | Show database report
--player NAME                     | Player name for wins report

## Package and Run Jar

    mvn package
    java -jar target/uno-cli-1.0.jar --human --bots 2

## Docker

    docker build -t uno-cli .
    docker run -it uno-cli

## Documentation

- docs/rules-supported.md — which UNO rules are implemented and variants used
- docs/final-report.md — full implementation report

## Logging

SLF4J logs game start, turns, cards played, draws, invalid input, and game completion.
Logging supplements CLI output and does not replace player-facing messages.