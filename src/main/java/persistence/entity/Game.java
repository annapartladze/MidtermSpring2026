package persistence.entity;

public class Game {

    private int id;
    private String startedAt;
    private String finishedAt;
    private String winner;
    private int winnerPlayerId;
    private int rounds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public void setWinnerPlayerId(int winnerPlayerId) {
        this.winnerPlayerId = winnerPlayerId;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }
}
