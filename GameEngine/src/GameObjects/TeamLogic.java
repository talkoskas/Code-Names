package GameObjects;

import java.util.List;

public interface TeamLogic {
    String getName();
    int getWordScore();
    boolean isBlackChosen();
    int getWordTotal();
    List<Card> getWords();
     int getTurns();
     void setName(String name);
     void setWordScore(int wordScore);
     void setBlackScore(boolean blackChosen);
     void setWordTotal(int wordTotal);
     void setWords(List<Card> words);
     void setTurns(int turns);
     String toString();
     boolean isDone();
}
