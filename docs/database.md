# Database Documentation

## Database
SQLite database stored in:

uno.db

## Tables

### games
- id
- started_at
- finished_at
- winner
- rounds

### scores
- id
- game_id
- player_name
- score

## Persistence Features
The application persists:
- player names
- game timestamps
- rounds played
- winner
- per-player scores

## Reports
The application provides:
- recent games
- player win count
- highest scores

## Running

Compile:
mvn compile

Run:
mvn exec:java

Run tests:
mvn test