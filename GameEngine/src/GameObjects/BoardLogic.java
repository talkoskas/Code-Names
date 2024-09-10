package GameObjects;

import java.util.List;

public interface BoardLogic {
     int getRows();
     int getCols();
     List<Card> getCardBoard();
     List<Card> getCards();
     List<Card> getBlackCards();
     void setRows(int rows);
     void setCols(int cols);
     void setCardBoard(List<Card> cardBoard);
     void setCards(List<Card> cards);
     void setBlackCards(List<Card> blackCards);
     void createNewBoard(List<Team> teams);
     String toString();
     String toStringGuessMode();
}
