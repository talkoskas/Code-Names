package GameIO;

import GameObjects.Team;

import java.util.List;

public interface UIGameCreator {
     // boolean loadGameFile();
     // void saveGameFile();
     String printGameDetails();
     void start(List<Team> teams);
     String definerTurnMessage();
     String guesserTurnMessage(int guesses);
}
