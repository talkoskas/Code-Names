package GameIO;

import GameObjects.Game;
import GameObjects.Team;
import GameObjects.*;
import UIUtil.GameConfig;
import java.util.List;


public class GameManager {

    private Object lock = new Object();
    private Game game;
    private int currTeamTurn = 0, maxCommand = GameConfig.DISPLAY_GAME_INFO;     // Saves the maximum command index that is executable at the moment
    private int phase = GameConfig.PENDING;
    private boolean gameActive = false;


    public GameManager(Game game) {
        if (game == null) {
            return;
        }
        this.game = game;
    }


    public void start(List<Team> teams) {
        synchronized (lock) {
            if (game == null || game.getBoard() == null) {
                throw new IllegalStateException("Game or Board is not initialized");
            }

            game.getBoard().createNewBoard(game.getTeams());

            game.setTeams(teams);
            for (Team team : game.getTeams()) {
                team.initialize();

            }
            game.setWinnerKeys(new WinnerKeys(teams.size()));
            currTeamTurn = 0;
            phase = GameConfig.DEFINER_PHASE;
            this.game.setGameOver(false);
            this.gameActive = true;

        }
    }

    public String getGameState(boolean definer) {
        StringBuilder state = new StringBuilder();

        if (gameActive) {
            Team team = game.getTeams().get(currTeamTurn);
            state.append("\nGame status - Active");
            state.append("\nCurrent teams turn:\nName - " + team.getName());
            state.append("\nCurrent score -  " + team.getWordScore() + " / " + team.getWordTotal());
            state.append("\nTurns performed so far - " + team.getTurns());
            state.append("\nNext teams turn - " + game.getTeams().get(getNextTeamsTurnIndex()).getName());
            if (definer) {
                state.append(game.getBoard().toString());
            } else {
                state.append(game.getBoard().toStringGuessMode());
            }
        } else {
            state.append("\nGame status - Pending");
        }
        return state.toString();
    }


    public String advancePhase() {
        synchronized (lock) {
            if (phase == GameConfig.GUESSER_PHASE) {
                this.phase = GameConfig.DEFINER_PHASE;
                incrementTeamIndex();
            } else {
                this.phase = GameConfig.GUESSER_PHASE;
            }
            if (game.isGameOver()) {
                this.phase = GameConfig.GAME_INACTIVE;
                return GameConfig.GAME_OVER;
            }
            return GameConfig.ONGOING;
        }
    }


    public void endTurn() {
        Team currTeam = game.getTeams().get(currTeamTurn);
        resetTeamGuessesMade();
        currTeam.setAllowedGuesses(1);
        advancePhase();
    }

    public synchronized String configureGuess(int guess) {
        Team currTeam = game.getTeams().get(currTeamTurn);
        if (guess == GameConfig.END_TURN) {
            endTurn();
            if (currTeam.getMadeGuesses() > 0) {
                return currTeam.getGuessMessage();
            }
            return "Team " + currTeam.getName() + " has ended their turn early";
        }

        String res = game.configureGuess(currTeamTurn, guess);
        if (currTeam.isBlackChosen() || currTeam.isGameOver()) {
            endTurn();  // End the turn immediately if black card was chosen or team's game is over
            if (game.isGameOver()) {
                synchronized (game) {
                    this.phase = GameConfig.GAME_INACTIVE;
                }
                return game.getFinalMessage();
            }
            return res;
        }
        if (game.getGameOver()) {
            synchronized (game) {
                this.phase = GameConfig.PENDING;
            }
            return game.getFinalMessage();
        }


        if (currTeam.getMadeGuesses() == 1) {
            currTeam.setGuessMessage(res);
        } else if (currTeam.getMadeGuesses() > 1) {
            currTeam.addGuessMessage(res);
        }

        if (currTeam.isGameOver()) {
            endTurn();
        } else if (currTeam.areGuessesDone()) {
            currTeam.setAllowedGuesses(1);
            resetTeamGuessesMade();
            advancePhase();
        }

        return res;

    }


    public void resetTeamGuessesMade() {
        for (Team team : game.getTeams()) {
            team.setMadeGuesses(0);
        }
    }

    public synchronized boolean isGameOver() {
        return game.getGameOver();
    }


    private void incrementTeamIndex() {
        game.getTeams().get(currTeamTurn).incrementTurns();
        currTeamTurn = getNextTeamsTurnIndex();
        game.getTeams().get(currTeamTurn).setMadeGuesses(0);
        game.getTeams().get(currTeamTurn).setAllowedGuesses(1);
        game.getTeams().get(currTeamTurn).setGuessMessage("");
    }

    private int getNextTeamsTurnIndex() {
        int currTeam = currTeamTurn;
        ++currTeam;
        if (currTeam >= game.getTeams().size()) {
            currTeam = currTeam % game.getTeams().size();
        }
        while (game.getTeams().get(currTeam).isGameOver()) {
            ++currTeam;
            if (currTeam >= game.getTeams().size()) {
                currTeam = currTeam % game.getTeams().size();
            }
        }
        return currTeam;
    }


    private String getTurnMessage() {
        Team curr = game.getTeams().get(currTeamTurn);
        return curr.getName() + "'s turn - score: " + curr.getWordScore() + "/" + curr.getWordTotal();
    }

    public String guesserNotTurnMessage() {
        Team currTeam = game.getTeams().get(currTeamTurn);
        StringBuilder sb = new StringBuilder();
        sb.append("Guessers phase");
        sb.append(game.getBoard().toStringGuessMode());
        sb.append("Guesses performed this turn: " + currTeam.getMadeGuesses());
        sb.append("Enter your guess (by card index), or enter to end your turn");
        return sb.toString();
    }

    public String guesserTurnMessage(int guesses) {
        Team currTeam = game.getTeams().get(currTeamTurn);
        StringBuilder sb = new StringBuilder();
        sb.append("Guessers phase");
        sb.append(game.getBoard().toStringGuessMode());
        sb.append("\nEnter your guesses.");
        sb.append("\nHint: " + currTeam.getHint());
        sb.append("\nMaximum guesses: " + guesses);
        sb.append("\nGuesses performed this turn: " + currTeam.getMadeGuesses());
        sb.append("\nEnter your guess (by card index), or enter to end your turn");
        return sb.toString();
    }

    public String definerTurnMessage() {
        Team currTeam = game.getTeams().get(currTeamTurn);
        StringBuilder result = new StringBuilder();
        result.append(getTurnMessage());
        // Actual turn execution - definers phase first
        result.append("Definers phase" + game.getBoard());
        result.append("Enter your definition:");
        return result.toString();
    }

    public boolean isGameNull() {
        return this.game == null;
    }

    public Game getGame() {
        return game;
    }

    public int getCurrTeamTurn() {
        return currTeamTurn;
    }

    public int getPhase() {
        return phase;
    }

    public String getFinalMessage() {
        return this.game.getFinalMessage();
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public void setGameOver(boolean gameOver) {
        this.gameActive = gameOver;
        synchronized (this) {
            this.game.isGameOver();
            this.game.setGameOver(gameOver);
            this.gameActive = false;
            this.phase = GameConfig.GAME_INACTIVE;
        }
    }


    @Override
    public String toString() {
        String msg = "\nNext turn - Team " + game.getTeams().get(currTeamTurn).getName() + "'s turn";
        return game.toString() + msg;
    }
}