package GameObjects;

import GameUtilV2.ECNTeam;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
public class Team implements Serializable, TeamLogic {
    private String name, hint, finalMessage;
    private int wordScore = 0, wordTotal, turns = 0, rank = 0;
    private int guesserCount, definerCount, allowedGuesses, madeGuesses;
    private Set<Player> players;
    private String guessMessage, winCause = "", loseCause = "";
    private boolean blackScore = false, won = false, gameOver = false, ranked = false;
    List<Card> words;


    public Team(){}

    public Team(String name, int wordTotal, int guessers, int definerCount){
        this.name = name;
        this.wordTotal = wordTotal;
        this.guesserCount = guessers;
        this.definerCount = definerCount;
        this.words = new ArrayList<>();
        this.players = new HashSet<>();
        this.hint = "";
        this.finalMessage = "";
        this.guessMessage = "";
        this.allowedGuesses = 5;
        this.madeGuesses = 0;
    }

    public void initialize(){
        setWordScore(0);
        setBlackScore(false);
        setHint("");
        setAllowedGuesses(1);
        setWon(false);
        setGameOver(false);
        setGuessMessage("");
        setFinalMessage("");

    }

    public void addGuesser(String playername){
        players.add(new Player(playername));
    }

    public void addDefiner(String playername){
        players.add(new Player(playername, true));
    }

    public boolean containsPlayer(String playerName){
        for(Player p : players){
            if(p.getName().equals(playerName)){
                return true;
            }
        }
        return false;
    }



    // TODO - Complete methods after starting clients code and initiating players
//    public String addPlayer(Player player){
//        return addPlayer(player, false);
//    }

//    public String addPlayer(Player player, boolean definer){
//
//    }
    @XmlElement
    public String getGuessMessage(){ return guessMessage; }

    public void setGuessMessage(String guessMessage){ this.guessMessage = guessMessage; }

    public void addGuessMessage(String guessMessage){ this.guessMessage += guessMessage; }

    public boolean hasGuessedAlready(){ return madeGuesses >= 1; }

    public void addFinalMessage(String msg){
        finalMessage += msg;
    }

    public int getRank(){ return rank; }

    public void setRankLoss(int rank){ this.rank = rank; }



    public void setRank(int rank) {
        this.rank = rank;
        this.ranked = true;
        switch(rank) {
            case 1:
                setFinalMessage("\nTeam " + name + " - 1st place");
                break;
            case 2:
                setFinalMessage("\nTeam " + name + " - 2nd place");
                break;
            case 3:
                setFinalMessage("\nTeam " + name + " - 3rd place");
                break;
            default:
                setFinalMessage("\nTeam " + name + " - " + rank + "th place");
                break;
        }
    }

    public boolean isRanked(){ return ranked; }

    public String getWinCause() {
        return winCause;
    }

    public void setWinCause(String winCause) {
        this.winCause = winCause;
    }

    public String getLoseCause() {
        return loseCause;
    }


    public void setLoseCause(String loseCause) {
        this.loseCause = loseCause;
    }


    @XmlElement
    public boolean getWon(){ return won; }

    public void setWon(boolean won){ this.won = won; }

    @XmlElement
    public int getMadeGuesses(){return madeGuesses;}

    public void setMadeGuesses(int madeGuesses){
        this.madeGuesses = madeGuesses;
    }

    public int incrementMadeGuesses() {
        return ++madeGuesses;
    }

    @XmlElement
    public int getGuesserCount(){ return guesserCount; }

    public void setGuesserCount(int guesserCount){ this.guesserCount = guesserCount; }

    @XmlElement
    public int getDefinerCount(){ return definerCount; }

    public void setDefinerCount(int definerCount){ this.definerCount = definerCount; }

    @XmlElement
    public boolean isGameOver(){ return gameOver; }

    public void setGameOver(boolean gameOver){this.gameOver = gameOver; }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getFinalMessage(){return finalMessage;}

    public void setFinalMessage(String finalMessage){this.finalMessage = finalMessage;}

    @XmlElement
    public int getWordScore() {
        return wordScore;
    }

    public void setWordScore(int wordScore) {
        this.wordScore = wordScore;
    }

    @XmlElement
    public boolean isBlackChosen() {
        return blackScore;
    }

    public void setBlackScore(boolean blackScore) {
        this.blackScore = blackScore;
    }

    @XmlElement
    public int getWordTotal(){
        return wordTotal;
    }


    public void setWordTotal(int wordTotal) {
        this.wordTotal = wordTotal;
    }

    @XmlElement
    public String getHint() { return hint; }

    public void setHint(String hint) { this.hint = hint; }

    @XmlElement
    public int getAllowedGuesses(){ return allowedGuesses; }

    public void setAllowedGuesses(int allowedGuesses){ this.allowedGuesses = allowedGuesses; }

    @XmlElement
    public List<Card> getWords(){
        return words;
    }

    public void setWords(List<Card> words){
        this.words = words;
    }

    @XmlElement
    public Set<Player> getPlayers(){
        return players;
    }

    public void setPlayers(Set<Player> players){
        this.players = players;
    }

    public Set<Player> getDefiners(){
        Set<Player> definers = new HashSet<>();
        for(Player p : players){
            if(p.isDefiner()){
                definers.add(p);
            }
        }
        return definers;
    }

    public Set<Player> getGuessers(){
        Set<Player> guessers = new HashSet<>();
        for(Player p : players){
            if(!p.isDefiner()){
                guessers.add(p);
            }
        }

        return guessers;
    }


    @XmlElement
    public int getTurns(){
        return turns;
    }

    public void setTurns(int turns){
        this.turns = turns;
    }

    public void addWord(Card card){
        words.add(card);
    }

    public void incrementWordScore(){
        ++wordScore;
    }

    public void incrementTurns(){
        ++turns;
    }

    public static Team ECNtoTeam(ECNTeam t1){
        return new Team(t1.getName(), t1.getCardsCount(), t1.getGuessers(), t1.getDefiners());
    }

    public boolean areGuessesDone(){
        return madeGuesses == allowedGuesses;
    }

    public boolean isDone(){
        return (wordScore == wordTotal) || blackScore || gameOver;
    }


    public String getTeamInfo(){
        return "Team name - " + name + "\nWord count: " + wordTotal ;
    }

    public String getPendingTeamStatus(){
        StringBuilder result = new StringBuilder();
        result.append("Team name - " + name + "\n");
        result.append("Definers signed up - " + getDefiners().size() + " / " + definerCount + "\n");
        result.append("Guessers signed up - " + getGuessers().size() + " / " + guesserCount + "\n");
        return result.toString();
    }

    public String getActiveTeamStatus(){
        StringBuilder result = new StringBuilder();
        result.append("Team name - " + name + "\n");
        result.append("Score - " + wordScore + " / " + wordTotal + "\n");
        return result.toString();
    }


    @Override
    public String toString() {
        return "Team - " + name + "\nPerformed " + turns + " turns\n" +
                "Guessed / Total words: " + wordScore + " / " + wordTotal ;
    }






}
