package GameIO.DTO;


public class GameStateDTO {
    private int phase;
    private String currentTeamName;
    private boolean isGameOver;
    private String gameStateDescription, guessMessage, finalMessage;
    private String playerRole, playerTeam;

    // Constructor
    public GameStateDTO(int phase, String currentTeamName, boolean isGameOver,
                        String gameStateDescription, String playerRole, String playerTeam,
                        String guessMessage, String finalMessage) {
        this.phase = phase;
        this.currentTeamName = currentTeamName;
        this.isGameOver = isGameOver;
        this.gameStateDescription = gameStateDescription;
        this.playerRole = playerRole;
        this.playerTeam = playerTeam;
        this.guessMessage = guessMessage;
        this.finalMessage = finalMessage;
    }

    // Getters
    public int getPhase() { return phase; }
    public String getCurrentTeamName() { return currentTeamName; }
    public boolean isGameOver() { return isGameOver; }
    public String getGameStateDescription() { return gameStateDescription; }
    public String getPlayerRole() { return playerRole; }
    public String getPlayerTeam() { return playerTeam; }
    public String getGuessMessage() { return guessMessage; }
    public String getFinalMessage() { return finalMessage; }

    // You might want to add a toString() method for easy printing
    @Override
    public String toString() {
        return "GameState:\n" +
                "Game " + (isGameOver? "over" : "ongoing")  + "\n"
                + gameStateDescription;
    }
}