package GameObjects;

import java.util.List;

public interface CardLogic {
     String getWord();
     int getIndexInBoard();
     boolean isGuessed();
     String toString();
     List<String> toStringList(boolean withTeams);
}
