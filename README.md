# Midterm UNO CLI

This project is a standalone command-line implementation of an UNO-like card game written in Java.

The project began as a feature-grown procedural application where most responsibilities lived inside a single `Main` class. Through incremental refactoring, responsibilities were extracted into dedicated classes while preserving existing behavior through characterization tests.

The application supports:

* bot-only games
* human vs. bot games
* configurable numbers of bots and games
* target-score match play
* scoring across multiple games
* automated tests through Maven
* logging of important game events
* Docker-based execution

## Project Structure

* `CardUtils` - card parsing and scoring utilities
* `RuleEngine` - legal move validation
* `BotPlayer` - bot decision logic
* `ConsoleUI` - user input and interaction
* `GameState` - centralized game state
* `GameEngine` - game orchestration and turn loop

## Requirements

* Java 17
* Maven 3.9+
* Docker Desktop (optional)

## Local Build

```bash
mvn compile
```

## Run Tests

```bash
mvn test
```

## Run Application

```bash
mvn exec:java
```

## Example Runs

Run five quiet bot games:

```bash
mvn exec:java -Dexec.args="--bots 3 --games 5 --quiet"
```

Run an interactive game:

```bash
mvn exec:java -Dexec.args="--human --bots 2 --games 1"
```

Run rounds until a player reaches 500 points:

```bash
mvn exec:java -Dexec.args="--bots 3 --target 500"
```

Run database reports without playing a new game:

```bash
mvn exec:java -Dexec.args="--report all --player Bot1"
```

## Create Package

```bash
mvn package
```

## Run Packaged Jar

```bash
java -jar target/uno-cli-1.0.jar --bots 3 --games 5 --quiet
```

The packaged jar includes its runtime dependencies and has a `Main-Class` manifest entry.

## Docker Build

```bash
docker build -t uno-cli .
```

## Docker Run

```bash
docker run -it uno-cli
```

## Card Input Examples

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
```

## Logging

The application logs important events including:

* game start
* player turns
* cards played
* cards drawn
* invalid input
* game completion

Logging supplements normal CLI output and does not replace player-facing messages.

## Documentation

Additional documentation is available in the `docs` directory:

* `docs/rules.html` - implemented game rules
* `docs/midterm-exam.md` - original midterm brief
* `docs/rubric.md` - grading rubric
* `docs/refactoring-guide.md` - suggested refactoring path
* `docs/refactoring-report.md` - performed refactorings and preserved behaviors
* `docs/extension-readiness.md` - future extension opportunities
* `docs/rules-supported.md` - final-project UNO rules and variants
* `docs/final-report.md` - final-project implementation report

## Submission

Assignment 4 deliverables include:

* Maven build configuration (`pom.xml`)
* JUnit test integration (`mvn test`)
* logging implementation
* `Dockerfile`
* updated `README.md`
* refactored source code and documentation
