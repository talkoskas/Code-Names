package GameObjects;
import java.util.List;

public interface GameLogic {
     String configureGuess(int teamIndex, int cardIndex);
     String toString();
     List<Team> getTeams();
     Board getBoard();
     String getFinalMessage();


}
