package GameObjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;
import GameUtilV2.*;
import Util.GameConfig;


@XmlRootElement
public class Game implements Serializable, GameLogic {
    private WinnerKeys winnerKeys;
    private RankList rankList;
    private int numOfTeams = 0, winners, losers = 0, currTeamTurn = 0;
    private Board board;
    private List<Team> teams;
    private boolean gameOver = false, lastTeamWon = false;
    private String name, finalMessage = GameConfig.ONGOING;

    public Game() {
        board = new Board();
        teams = new ArrayList<>();
        this.name = null;
        this.winners = 0;
    }

    public Game(int wordTotal, int blackWordsTotal) {
        this.board = new Board(wordTotal, blackWordsTotal);
        this.teams = new ArrayList<>();
        this.name = null;
        this.winners = 0;
    }

    public Game(int numOfTeams, int wordTotal, int blackWordsTotal, List<Team> teams) {
        HashSet<Team> teamSet = new HashSet<>(teams);
        this.numOfTeams = numOfTeams;
        this.board = new Board(wordTotal, blackWordsTotal);
        this.teams = new ArrayList<>(teamSet);
        this.numOfTeams = teams.size();
        this.name = null;
        this.winners = 0;
        this.winnerKeys = new WinnerKeys(teams.size());
        //this.rankList = new RankList(teams.size());
    }

    @XmlElement
    public int getCurrTeamTurn(){ return currTeamTurn; }

    public void setCurrTeamTurn(int currTeamTurn){ this.currTeamTurn = currTeamTurn; }

    @XmlElement
    public String getFinalMessage(){
        return finalMessage;
    }

    public void setFinalMessage(String finalMessage){
        this.finalMessage = finalMessage;
    }

    @XmlElement
    public int getLoser(){ return losers; }

    public void setLosers(int losers){ this.losers = losers; }

    public int incrementLosers(){ return ++losers; }

    @XmlElement
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @XmlElement
    public WinnerKeys getWinnerKeys() {
        return winnerKeys;
    }

    public void setWinnerKeys(WinnerKeys winnerKeys) {
        this.winnerKeys = winnerKeys;
    }

//    @XmlElement
//    public RankList getRankList() {
//        return rankList;
//    }
//
//    public void setRankList(RankList rankList) { this.rankList = rankList; }

    @XmlElement
    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @XmlElement
    public String getName() { return name;}

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public boolean getGameOver(){ return gameOver;}

    public void setGameOver(boolean gameOver){ this.gameOver = gameOver;}

    public int getNumOfTeams() {
        return numOfTeams;
    }

    public void setNumOfTeams(int numOfTeams) {
        this.numOfTeams = numOfTeams;
    }

    public Team getTeamByName(String name){
        Team currTeam;
        for(int i = 0; i < getNumOfTeams(); ++i){
            currTeam = teams.get(i);
            if(currTeam.getName().equals(name)){
                return currTeam;
            }
        }
        return null;
    }

    public boolean isGameOver() {
        int activeCount = 0;
        List<Team> teamsToRank = new ArrayList<>();
        for (Team team : teams) {
            if (!team.isDone()) {
                ++activeCount;
            } else if (!team.isRanked()) {
                teamsToRank.add(team);
            }
        }

        for (Team team : teamsToRank) {
            if (team.getWon()) {
                win(team, team.getWinCause());
            } else {
                lose(team, team.getLoseCause());
            }
        }

        if (activeCount == 1) {
            for (Team team : teams) {
                if (!team.isDone()) {
                    win(team, "by being the last team standing");
                    break;
                }
            }
        }

        if (activeCount < 2) {
            setRankings();
            return true;
        }
        return false;
    }


    public static List<Team> ECNTeamsToTeamList(ECNTeams teams){
        List<ECNTeam> tmp = new ArrayList<>(teams.getECNTeam());
        List<Team> teamList = new ArrayList<>();
        for(ECNTeam team : tmp){
            teamList.add(Team.ECNtoTeam(team));
        }
        return teamList;
    }

    public static Game ECNGameToGame(ECNGame ecnGame, List<String> words) {
        Game game = new Game();
        game.setName(ecnGame.getName());
        game.setTeams(ECNTeamsToTeamList(ecnGame.getECNTeams()));
        game.setNumOfTeams(game.getTeams().size());

        // Change the below line so the words will be extracted before calling the method
        game.board = Board.ECNtoBoard(ecnGame.getECNBoard(), words);
        return game;
    }
//
//    private String isGameValid(){
//        if
//    }

    @Override
    public String toString() {
        String res = board.toString();
        res = res.concat("\nCodeNames game with " + this.numOfTeams + " teams:\n");
        for(int i = 0; i < getNumOfTeams(); ++i){
            res = res.concat(teams.get(i).toString() + "\n");
        }
        return res;
    }

    private boolean isGuessCorrect(int teamIndex, int cardIndex){
        List<Card> currTeamWords = teams.get(teamIndex).getWords();
        Card currCard = board.getCardBoard().get(cardIndex);
        return currTeamWords.contains(currCard);
    }

    private boolean isGuessBlack(int cardIndex){
        Card currCard = board.getCardBoard().get(cardIndex);
        return currCard.isBlack();
    }

    private boolean isCardGuessed(int cardIndex){
        Card curr = board.getCardBoard().get(cardIndex);
        return curr.isGuessed();
    }


    public String configureGuess(int teamIndex, int cardIndex){
        Team currTeam = teams.get(teamIndex);
        Card currCard = board.getCardBoard().get(cardIndex);
        String res;
        if(isGuessBlack(cardIndex)){
            currTeam.setBlackScore(true);
            currTeam.setGameOver(true);
            res = lose(currTeam, "by guessing a black card");


        } else if (isCardGuessed(cardIndex)) {
            res = "\nTeam " + currTeam.getName() + " guessed a card that was already guessed";
        }
        else if(currCard.getTeam().equals("Neutral")) {
            currCard.setGuessed(true);
            res = "\nTeam " + currTeam.getName() + " guessed a neutral word";
        }
        else{
                Team wordOwner = getTeamByName(currCard.getTeam());
                currCard.setGuessed(true);      // Mark word as guessed
                if(isGuessCorrect(teamIndex, cardIndex)) {
                    currTeam.incrementWordScore();
                    res = "\nTeam " + currTeam.getName() + " guessed correctly!";
                    if(currTeam.isDone()){
                        res += win(currTeam, "by guessing all cards");
                    }

                }
                else{
                    wordOwner.incrementWordScore();
                    if(currTeam.getName().equals(wordOwner.getName())) {
                        res = "\nTeam " + currTeam.getName() + " guessed correctly!";
                    }
                    else{
                        res = "\nTeam " + currTeam.getName() + " guessed " + wordOwner.getName() + "'s card";
                    }
                    if(wordOwner.isDone()){
                        res += win(wordOwner, "by guessing all cards");
                    }

            }
        }
        currTeam.incrementMadeGuesses();
        if(isGameOver()){
            res += setRankings();
        }
        return res;
    }

    public String win(Team team, String cause) {
        if (!team.isRanked()) {
            team.setGameOver(true);
            team.setWon(true);
            team.setWinCause(cause);
            //rankList.win(team);
            winnerKeys.rankTeam(team, true, cause);
        }
        return team.getFinalMessage();
    }

    public String lose(Team team, String cause) {
        if (!team.isRanked()) {
            team.setGameOver(true);
            team.setWon(false);
            team.setLoseCause(cause);
            //rankList.lose(team);
            winnerKeys.rankTeam(team, false, cause);
        }
        return team.getFinalMessage();
    }

    public String setRankings() {
        String res = winnerKeys.toString();
        //String res = rankList.toString();
        setGameOver(true);
        setFinalMessage(res);
        return getFinalMessage();
    }
}
