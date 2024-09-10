package GameObjects;
import Util.StringUtil;
import Util.GameConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@XmlRootElement
public class Card implements Comparable<Card>, Serializable, CardLogic {
    private String word, team, guess;
    private int indexInBoard;
    private boolean black;
    private boolean guessed;


    public Card(){
        this.word = "";
        this.team = "";
        this.guess = "";
        this.black = false;
        this.guessed = true;
    }

    public Card(String word){
        this.word = word;
        this.black = false;
        this.guessed = false;
    }

    public Card(String word, boolean black){
        this.word = word;
        this.black = black;
        this.guessed = false;
    }

    public Card(String word, boolean black, int indexInBoard){
        this.word = word;
        this.black = black;
        this.indexInBoard = indexInBoard;
        this.guessed = false;
    }

    public Card(String word, String team, int indexInBoard){
        this.word = word;
        this.team = team;
        this.indexInBoard = indexInBoard;
        this.black = false;
        this.guessed = false;
    }

    @XmlElement
    public String getWord() {
        return word;
    }


    public void setWord(String word) {
        this.word = word;
    }


    @XmlElement
    public String getTeam() {
        return team;
    }


    public void setTeam(String team) {
        this.team = team;
    }

    @XmlElement
    public int getIndexInBoard() {
        return indexInBoard;
    }

    public void setIndexInBoard(int indexInBoard) {
        this.indexInBoard = indexInBoard;
    }

    @XmlElement
    public void setGuessed(boolean guessed) {
        this.guessed = guessed;
    }

    public boolean isGuessed(){
        return this.guessed;
    }

    /*@XmlElement
    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getGuess(){
        return this.guess;
    }
     */
    @XmlElement
    public boolean isBlack(){
        return this.black;
    }

    public void setBlack(boolean black){
        this.black = black;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"border: 1px solid #000; padding: 10px; margin: 5px; width: 200px; text-align: center;\">");

        if (!isGuessed()) {
            sb.append("<div style=\"font-weight: bold;\">")
                    .append(word)
                    .append("</div>");

            sb.append("<div style=\"color: gray;\">")
                    .append("(").append(team).append(")")
                    .append("</div>");

            sb.append("<div style=\"color: gray;\">")
                    .append("[").append(indexInBoard + 1).append("]")
                    .append("</div>");
        } else {
            sb.append("<div style=\"font-weight: bold;\">&nbsp;</div>");
            sb.append("<div style=\"color: gray;\">&nbsp;</div>");
            sb.append("<div style=\"color: gray;\">&nbsp;</div>");
        }

        sb.append("</div>");
        return sb.toString();
    }

    public String toStringConsoleMode() {
        String innerTeam, innerIndex, innerWord;
        if(!isGuessed()) {
            innerTeam = StringUtil.padString("(" + team + ")", GameConfig.CHARS_PER_ROW_CARD);
            innerIndex = StringUtil.padString("[" + (indexInBoard + 1) + "]", GameConfig.CHARS_PER_ROW_CARD);
            innerWord = StringUtil.padString(word, GameConfig.CHARS_PER_ROW_CARD);
        }
        else{
            innerTeam = StringUtil.padString("", GameConfig.CHARS_PER_ROW_CARD);
            innerIndex = StringUtil.padString("", GameConfig.CHARS_PER_ROW_CARD);
            innerWord = StringUtil.padString("", GameConfig.CHARS_PER_ROW_CARD);
        }
        String lower = StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD);
        String upper = StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD);
        return upper + '\n' + innerWord + '\n' + innerTeam + '\n' +
               innerIndex + '\n' + lower + '\n';
    }

    public List<String> toStringList(boolean withTeams){
        List<String> lst = new ArrayList<>(GameConfig.ROWS_IN_CARD_DEFINER);
        lst.add(StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD));
        lst.add(StringUtil.padString(word, GameConfig.CHARS_PER_ROW_CARD));

        if(withTeams){
            lst.add(StringUtil.padString("Team (" + team + ")", GameConfig.CHARS_PER_ROW_CARD));
        }
        lst.add(StringUtil.padString("Index [" + (indexInBoard + 1) + "]", GameConfig.CHARS_PER_ROW_CARD));
        lst.add(StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD));
        return lst;
    }

    /*public List<String>toStringListGuessMode(){
        List<String> lst = new ArrayList<>();
        lst.add(StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD));
        lst.add(StringUtil.padString(word, GameConfig.CHARS_PER_ROW_CARD));
        lst.add(StringUtil.padString("Index = " + indexInBoard, GameConfig.CHARS_PER_ROW_CARD));
        lst.add(StringUtil.duplicateChar('-', GameConfig.CHARS_PER_ROW_CARD));
        return lst;
    }*/

    @Override
    public int compareTo(Card o) {
        return Integer.compare(indexInBoard, o.indexInBoard);
    }
}
