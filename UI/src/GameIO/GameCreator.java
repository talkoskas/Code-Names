package GameIO;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import GameObjects.WinnerKeys;
import GameUtilV2.*;
import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Team;
import UIUtil.GameConfig;
import Util.StringUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class GameCreator {
    //private String xmlPath;
    //private String dictPath;
    private Game game;
    // Now modifying my gameCreator to only have the gameWords List<String> (was blackWords too)
    private List<String> gameWords, blackWords;
    // Removed maxBlackWords from data members below!! note if something went wrong
    private int maxWords, maxBlack, currTeamTurn;
    private int requiredGuessers = 0, requiredDefiners = 0, guessesMade;
    private int phase;
    private String message = GameConfig.VALID;


    public GameCreator() {}

    public GameCreator(ECNGame ecnGame, List<String> gameWords) {
        this.gameWords = gameWords;
        this.maxWords = gameWords.size();

        if(ecnGame.getECNBoard().getCardsCount() + ecnGame.getECNBoard().getBlackCardsCount() > maxWords) {
            this.game = null;
            this.message = "Card count + black card count is bigger than the amount of words in dictionary!";
            return;
        }

        this.game = Game.ECNGameToGame(ecnGame, gameWords);
//        this.cardCount = ecnGame.getECNBoard().getCardsCount();
//        this.blackCount = ecnGame.getECNBoard().getBlackCardsCount();
        for(Team team : game.getTeams()) {
            this.requiredDefiners += team.getDefinerCount();
            this.requiredGuessers += team.getGuesserCount();
        }
        this.phase = GameConfig.GAME_INACTIVE;

    }


    public boolean isGameNull() { return this.game == null; }

    public Game getGame() { return this.game; }

    public String getFinalMessage() {
        return message;
    }


    public String areTeamsPlayersValid(){
        for(Team team : game.getTeams()){
            if(team.getGuesserCount() < 1){
                return "Team " + team.getName() + " has " + team.getGuesserCount() + " guessers";
            }
            else if(team.getDefinerCount() > 1){
                return "Team " + team.getName() + " has " + team.getDefinerCount() + " definers";
            }
        }
        return "Valid";
    }


    public static boolean areTeamNamesUnique(List<Team> teams) {
        HashSet<Team> teamSet = new HashSet<>(teams);
        return teamSet.size() == teams.size();
    }

    public String areGamePreferencesValidV2(){
        String result = areGamePreferencesValidV1();
        String tmp;
        if(result.equals("Valid")){
            if(game.getTeams().size() >= GameConfig.MIN_TEAMS_PER_GAME
                    && game.getTeams().size() <= GameConfig.MAX_TEAMS_PER_GAME) {
                tmp = areTeamsPlayersValid();
                if (tmp.equals("Valid")) {
                    return "Valid";
                }
                return tmp;
            }
            return "Team count is invalid with " + game.getTeams().size()
                    + " Teams. Provide a team count between " + GameConfig.MIN_TEAMS_PER_GAME
                    + " and " + GameConfig.MAX_TEAMS_PER_GAME;
        }
        return result;
    }

    // TODO - Update restrictions according to v2!
    public String areGamePreferencesValidV1(){
        Board board = game.getBoard();
        int words = board.getWordCount(), blackWords = board.getBlackWordsCount();
        int rows = board.getRows(), cols = board.getCols();
        int sumOfTeamWords = 0;

        if (words + blackWords <= rows * cols){
            if(words + blackWords <= maxWords) {
                for(int i = 0; i < game.getNumOfTeams(); ++i){
                    sumOfTeamWords += game.getTeams().get(i).getWordTotal();
                }
                if(words <= maxWords && sumOfTeamWords <= words){
                    if(areTeamNamesUnique(game.getTeams())) {
                        return "Valid";
                    }
                    return "Error 6 - Team names are not unique";
                }
                if(sumOfTeamWords > words){
                    return "Error 5 - Word count is smaller than the sum of team words";
                }
                return "Error 4 - The sum of card counts for all teams is greater than the total amount of words chosen";
            }
            return "Error 2 - Sum of word counts (for all teams) is greater than the amount of words in game"
                    + "\n with " + words + " words, and with " + blackWords + " black words"
                    + "\n with " + maxWords + " total words read from xml file";
        }
        return "Error 3 - Words + blackWords are bigger than the dimensions of the board - "
                +"\n with " + words + " words, and with " + blackWords + " black words"
                + "\n and with rows x cols = " + board.getRows() + "x" + board.getCols();

    }



    public String printGameDetails(boolean player){
        StringBuilder result = new StringBuilder();

        result.append("Game Details:");
        result.append("\nTotal words (from xml): " + maxWords );
        result.append("\nRows x Columns: " + game.getBoard().getRows() + " x "
                + game.getBoard().getCols());
        result.append("\nWord count in game: " + game.getBoard().getWordCount() );
        result.append("\nBlack word count in game: " + game.getBoard().getBlackWordsCount() );
        List<Team> teams = game.getTeams();
        result.append("\nTeams details:");
        for(Team team : teams){
            result.append("\n" + team.getTeamInfo());
            if(player){
                result.append("\nCurrent definers: " + team.getDefiners().size() + " / " + team.getDefinerCount());
                result.append("\nCurrent guessers: " + team.getGuessers().size() + " / " + team.getGuesserCount());
            }
            else{
                result.append("\nDefiners required: " + team.getDefinerCount());
                result.append("\nGuessers required: " + team.getGuesserCount());
            }

        }

        return result.toString();
    }

    public int getPhase(){ return phase; }


    public void setFinalMessage(String message){
        game.setFinalMessage(message);
    }

    private static ECNGame deserializeECNFrom(String xmlPath) throws JAXBException {
        // Create JAXB Context
        JAXBContext context = JAXBContext.newInstance(ECNGame.class);

        // Create Unmarshaller
        Unmarshaller unmarshaller = context.createUnmarshaller();

        try {
            // Unmarshal XML and return java object of ECNGame type
            return (ECNGame) unmarshaller.unmarshal(new FileReader(xmlPath));
        }
        catch (FileNotFoundException e) {
            xmlPath = IOUtil.readInput(String.class);
            return GameCreator.deserializeECNFrom(xmlPath);
        }
    }

    private static Game deserializeGameFrom(String xmlPath) throws JAXBException {
        // Create JAXB Context
        JAXBContext context = JAXBContext.newInstance(Game.class);

        // Create Unmarshaller
        Unmarshaller unmarshaller = context.createUnmarshaller();

        try {
            // Unmarshal XML and return java object of Game type
            return (Game) unmarshaller.unmarshal(new FileReader(xmlPath));
        }
        catch (FileNotFoundException e) {
            xmlPath = IOUtil.readInput(String.class);
            return GameCreator.deserializeGameFrom(xmlPath);
        }
    }

    private void serializeGameTo(String xmlPath) throws JAXBException {
        try {
            // Create JAXB Context for the Game class
            JAXBContext context = JAXBContext.newInstance(Game.class);

            // Create Marshaller
            Marshaller marshaller = context.createMarshaller();

            // Serialize Game object to XML file
            FileWriter fw = new FileWriter(xmlPath);
            marshaller.marshal(game, fw);
            fw.close();

        } catch (JAXBException e) {
            System.out.println("Error occurred while serializing the game: " + e.getMessage());
            throw new JAXBException("Error occurred while serializing the game: " + e.getMessage(), e);
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        }
    }



    private static boolean isPathEndValid(String path, String end) {
        return path.endsWith(end);
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // Should get each choice separately and print the board after choice was made and
    // after checking if it was correct or not, and after applying the changes (from
    // the choice) to the board (before printing again after the choice)
    private void getAndConfigureChoices(int max){
        int currentChoice = 0;
        int i = 1;
        while(currentChoice != -2 && i < max + 1){
            currentChoice = IOUtil.readInputInRangeExcept(1, game.getBoard().getCardBoard().size(), -1) - 1;
            if(currentChoice != -2) {
                String msg = game.configureGuess(currTeamTurn, currentChoice);
                if(game.isGameOver()) {
                    return;
                }
            }
            ++i;
        }
    }

}
