package GameObjects;

import GameUtilV2.ECNBoard;
import GameUtilV2.ECNLayout;
import Util.GameConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.*;
@XmlRootElement
public class Board implements Serializable, BoardLogic {
    private int rows, cols, wordCount, blackWordsCount;
    private List<Card> blackCards;
    private List<Card> cards;
    private List<Card> cardBoard;

    public Board(){
        rows = 0;
        cols = 0;
        wordCount = 0;
        blackWordsCount = 0;
        cardBoard = new ArrayList<>();
        cards = new ArrayList<>();
        blackCards = new ArrayList<>();
    }

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    public Board(int rows, int cols, int wordCount, int blackWordsCount) {
        this.rows = rows;
        this.cols = cols;
        this.wordCount = wordCount;
        this.blackWordsCount = blackWordsCount;
    }

    @XmlElement
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @XmlElement
    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    @XmlElement
    public List<Card> getCardBoard() {
        return cardBoard;
    }

    public void setCardBoard(List<Card> board) {
        this.cardBoard = board;
    }

    @XmlElement
    public List<Card> getCards(){
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @XmlElement
    public List<Card> getBlackCards() {
        return blackCards;
    }

    public void setBlackCards(List<Card> blackCards) {
        this.blackCards = blackCards;
    }

    public void setCardBoard(int index, Card toAdd){
        cardBoard.set(index, toAdd);
    }


    public void setCard(List<Card> cards){
        this.cards = cards;
    }


    public Card getCard(int index) {
        return cards.get(index);
    }

    @XmlElement
    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    @XmlElement
    public int getBlackWordsCount() {
        return blackWordsCount;
    }

    public void setBlackWordsCount(int blackWordsCount){
        this.blackWordsCount = blackWordsCount;
    }



    // Method receives the size of the Cards list after shuffling and filtering,
    // and shuffles the regular cards list and filters it so it is of given size
    private void shuffleRegularCards(int size){
        Set<Integer> indexes = new HashSet<>();
        for(int i = 0; i < wordCount; i++){
            indexes.add(i);
        }
        List<Integer> indices = new ArrayList<>(indexes);
        Collections.shuffle(indices); // Shuffle the indices
        List<Card> regularCards = new ArrayList<>();
        for(int i = 0; i < size; i++){
            regularCards.add(cards.get(indices.get(i)));
        }
        setCards(regularCards);
    }

    private void shuffleBlackCards(int size){
        Set<Integer> indexes = new HashSet<>();
        for(int i = 0; i < blackWordsCount; ++i){
            indexes.add(i);
        }
        List<Integer> indices = new ArrayList<>(indexes);
        List<Card> newBlackCards = new ArrayList<>();
        for(int i = 0; i < size; ++i){
            newBlackCards.add(blackCards.get(indices.get(i)));
        }
        setBlackCards(newBlackCards);
    }



    // Method receives a shuffled list and resets its indexes in board (for each card) to the
    // newly assigned index of the card after shuffling
    private static void resetIndexes(List<Card> list){
        for(int i = 0; i < list.size(); ++i){
            Card curr = list.get(i);
            curr.setIndexInBoard(i);
        }
    }


    // Method resets board cards after filtering both words and blackWords list, and
    // assigns teams to each card as well
    private void resetBoardCards(List<Team> teams) {
        Set<Card> cardsTotal = new HashSet<>();
        cardsTotal.addAll(cards);
        cardsTotal.addAll(blackCards);
        assignTeamsToCard(teams, cardsTotal);
    }



    private void assignTeamsToCard(List<Team> teams, Set<Card> filtered){
        Map<String, Integer> map = new HashMap<>();
        List<Card> toAssign = new ArrayList<>(filtered);
        Team currTeam;
        int i = 0, count = 0;


        for(Team team : teams){
            map.put(team.getName(), team.getWordTotal());
        }

        for(i = 0; i < teams.size(); ++i){
            currTeam = teams.get(i);
            for(int j = 0; j < map.get(currTeam.getName()); ++count){
                Card curr = toAssign.get(count);
                if(curr.isBlack()){
                    curr.setTeam("Black");

                }
                else {
                    curr.setTeam(currTeam.getName());
                    currTeam.addWord(curr);
                    ++j;    // Increment j only when a non-black card was added
                }
            }
        }
        for(i = count; i < toAssign.size(); ++i){
            Card curr = toAssign.get(i);
            if(curr.isBlack()){
                curr.setTeam("Black");
            }
            else{
                curr.setTeam("Neutral");
            }
        }

        // After shuffling, sort the cards in board by their index values
        // to get the final board
        toAssign.sort(Comparator.comparingInt(Card::getIndexInBoard));
        resetIndexes(toAssign);
        // Activate this method after shuffling cards in board and assigning indexes
        setCardBoard(toAssign);

    }

   private void resetGuesses(){
        for(Card card : cardBoard){
            card.setGuessed(false);
        }
   }

    public void createNewBoard(List<Team> teams){
        resetGuesses();
        shuffleRegularCards(wordCount);
        shuffleBlackCards(blackWordsCount);
        resetBoardCards(teams);
    }

    public static List<List<String>> divideIntoBlackAndWords(Set<String> dictionary, int cardsCount, int blackCardsCount) {
        List<String> words = new ArrayList<>(dictionary);
        if (words.size() < cardsCount + blackCardsCount) {
            throw new IllegalArgumentException("Not enough words in dictionary");
        }
        Collections.shuffle(words);
        List<String> blackWords = new ArrayList<>(words.subList(0, blackCardsCount));
        List<String> remainingWords = new ArrayList<>(words.subList(blackCardsCount, cardsCount + blackCardsCount));
        return Arrays.asList(remainingWords, blackWords);
    }
    public static Board ECNtoBoard(ECNBoard b, List<String> words){
        ECNLayout l = b.getECNLayout();
        List<List<String>> totalWords;
        Board board;

        // If xml files card and black cards counts are less than boards size,
        // add to cards count to add neutral cards
        if(b.getCardsCount() + b.getBlackCardsCount() < l.getRows() * l.getColumns()){
            int diff = l.getRows() * l.getColumns() - b.getCardsCount() - b.getBlackCardsCount();
            b.setCardsCount(b.getCardsCount() + diff);

        }
        totalWords = divideIntoBlackAndWords(new HashSet<String>(words), b.getCardsCount(), b.getBlackCardsCount());
        board = new Board(l.getRows(), l.getColumns() ,b.getCardsCount(), b.getBlackCardsCount());


        // Utilize the fact the makeCardList method returns a list of lists, which contains
        // the list of total board cards as the 1st, blackCards as 2nd and gameWords as 3rd lists
        // respectively in the returned object

        List<List<Card>> cards = makeCardList(totalWords.get(0), totalWords.get(1));
        board.setCardBoard(cards.get(0));
        board.setBlackCards(cards.get(1));
        board.setCards(cards.get(2));
        return board;
    }

    private String buildStringForCardRow(List<List<String>> cards, int begin, int end){
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < GameConfig.ROWS_IN_CARD_DEFINER; ++row) { // Assuming there are 5 lines in each card
            for (int i = begin; i < end; ++i) {
                List<String> card = cards.get(i);
                if (row < card.size()) {
                    sb.append(card.get(row)).append("  "); // Adjust spacing as needed
                } else {
                    sb.append("      "); // Adjust spacing as needed
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static void shuffleCardList(List<Card> list){
        Collections.shuffle(list);
        resetIndexes(list);
    }

    public static List<Card> toCardList(List<String> list){
        return toCardList(list, false);
    }


    // HERE!!!!!
    public static List<Card> toCardList(List<String> list, boolean black) {
        List<Card> result = new ArrayList<>();
        int validWordIndex = 0;
        for (String word : list) {
            // Trim the word and replace multiple spaces with a single space
            String cleanedWord = word.trim().replaceAll("\\s+", " ");

            if (!cleanedWord.isEmpty()) {
                Card curr = new Card(cleanedWord, black, validWordIndex);
                result.add(curr);
                validWordIndex++;
            }
        }
        return result;
    }

    // Method makeCardList receives a list of words and a list of black words and returns (and
    // creates) a list of 2 lists - a list of (all) cards for the game, and a list of black cards
    // as the 2nd list in the outer list

    // Here!! The problem is within this method! Or not??
    public static List<List<Card>> makeCardList(List<String> words, List<String> black) {
        List<List<Card>> result = new ArrayList<>(3);
        List<Card> cards, allCards, blackCards;
        Collections.shuffle(words);
        Collections.shuffle(black);
        cards = toCardList(words);
        blackCards = toCardList(black, true);

        allCards = mergeAndShuffleLists(cards, blackCards);


        result.add(allCards);
        result.add(blackCards);
        result.add(cards);
        return result;
    }


    public static List<Card> mergeAndShuffleLists(List<Card> regular, List<Card> black) {
        List<Card> mergedList = new ArrayList<>();
        mergedList.addAll(regular);
        mergedList.addAll(black);
        shuffleCardList(mergedList);
        return mergedList;
    }

    // Definer mode toString method (with teams print)
    @Override
    public String toString() {
        List<List<String>> cards = new ArrayList<>();
        String result = "";
        for (Card card : cardBoard) {
            List<String> cardLines = card.toStringList(true);
            cards.add(cardLines);
        }

        for (int i = 0; i < cards.size(); i += cols) {
            result += buildStringForCardRow(cards, i, i + cols);
            result += "\n\n";
        }

        return result;
    }


    public String toStringGuessMode(){
        List<List<String>> cards = new ArrayList<>();
        String result = "";

        // For every card in the cardboard, get its list of string lines without teams (guess mode)
        for (Card card : cardBoard) {
            List<String> cardLines = card.toStringList(false);
            cards.add(cardLines);   // Add cards String lines to the card list of String lists
        }

        // Concat result String in loop with each card rows String
        for (int i = 0; i < cards.size(); i += cols) {
            result += buildStringForCardRow(cards, i, i + cols);
            result += "\n";
        }

        return result;
    }



    public String toStringConsoleGuessMode(){
        List<List<String>> cards = new ArrayList<>();
        String result = "";

        // For every card in the cardboard, get its list of string lines without teams (guess mode)
        for (Card card : cardBoard) {
            List<String> cardLines = card.toStringList(false);
            cards.add(cardLines);   // Add cards String lines to the card list of String lists
        }

        // Concat result String in loop with each card rows String
        for (int i = 0; i < cards.size(); i += cols) {
            result += buildStringForCardRow(cards, i, i + cols);
            result += "\n";
        }

        return result;
    }
}
