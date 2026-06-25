# Database Documentation

## Framework Choice

Persistence uses MyBatis with SQLite. MyBatis was chosen because it keeps SQL in mapper configuration (`src/main/resources/GameMapper.xml`) while letting the Java service classes call typed mapper methods instead of embedding JDBC statements.

The application database defaults to:

```text
uno.db
```

The JDBC URL can be changed with:

```bash
mvn exec:java -Duno.database.url=jdbc:sqlite:path/to/database.db
```

## Schema Setup

`DatabaseManager.initialize()` creates the schema through MyBatis mapper methods. The schema is:

### players

- `id`
- `name`

### games

- `id`
- `started_at`
- `finished_at`
- `winner`
- `winner_player_id`
- `rounds`

`winner_player_id` references `players.id`.

### rounds

- `id`
- `game_id`
- `round_number`

Each game saves one row per played round.

### scores

- `id`
- `game_id`
- `player_id`
- `player_name`
- `score`

`player_id` references `players.id`, so scores are related to players through identifiers. `player_name` is retained for compatibility with older local databases.

## Persisted Data

Each completed game stores:

- player rows in `players`
- one `games` row with the winner linked by `winner_player_id`
- one `rounds` row per round
- one `scores` row per player linked by `player_id`

## Report Mode

Reports can be run without playing a new game:

```bash
mvn exec:java -Dexec.args="--report recent"
mvn exec:java -Dexec.args="--report wins --player Bot1"
mvn exec:java -Dexec.args="--report scores"
mvn exec:java -Dexec.args="--report all --player Bot1"
```

The report queries are defined in `GameMapper.xml`:

- recent games
- player win count
- highest scores

## Test Database Behavior

Persistence tests do not use `uno.db`. Each test creates an isolated SQLite database under JUnit's temporary directory by setting:

```text
uno.database.url=jdbc:sqlite:<temp-dir>/uno-test.db
```

The tests reset the MyBatis session factory, initialize the schema, clear test rows, save sample games, and assert the stored rows and report results.

Run tests:

```bash
mvn test
```
