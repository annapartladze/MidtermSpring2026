package persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GamePersistenceServiceTest {

    @Test
    void saveGameShouldNotThrow() {
        GamePersistenceService service =
                new GamePersistenceService();

        assertDoesNotThrow(() ->
                service.saveGame(
                        "Bot1",
                        10,
                        "2026-06-19T17:00:00",
                        "2026-06-19T17:01:00",
                        new String[]{"Bot1", "Bot2"},
                        new int[]{50, 0}
                ));
    }
}