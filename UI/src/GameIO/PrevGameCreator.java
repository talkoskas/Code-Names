package GameIO;

import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Team;
import GameUtilV2.ECNGame;
import UIUtil.GameConfig;
import Util.StringUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.HashSet;
import java.util.List;

public class PrevGameCreator{
//    private String xmlPath;
//    private Game game;
//    private List<String> gameWords, blackWords;
//    private int maxWords, maxBlack, currTeamTurn;
//
//
//    public PrevGameCreator() {
//        loadGameFile();
//    }
//
//    public PrevGameCreator(boolean existing) {
//        if(existing){
//            loadExistingGameFile();
//
//        }
//        else {
//            while(!loadGameFile()){
//                System.out.println("Try again - ");
//            }
//        }
//    }
//
//    public String getXmlPath() {
//        return xmlPath;
//    }
//
//    public boolean loadExistingGameFile(){
//        return loadGameFile(true);
//    }
//
//    // Loads game file while checking for validity, returns an instance of the
//    // game corresponding to the xml path given
//    public boolean loadGameFile(){
//        return loadGameFile(false);
//    }
//
//    private boolean loadGameFile(boolean existing){
//        System.out.println("Enter your xml file path: ");
//        xmlPath = IOUtil.readInput(String.class);
//        xmlPath = isPathValid();
//        if(xmlPath.equals("False")){
//            return false;
//        }
//        if(existing) {
//            return xmlInstanceToGame();
//        }
//        return xmlToGame();
//    }
//
//    public void saveGameFile(){
//        System.out.println("Enter the path to the folder in which you'd like to save your game: ");
//        xmlPath = IOUtil.readInput(String.class);
//        xmlPath = isPathValid(false);
//        try {
//            System.out.println("Enter your new files name: ");
//            String toAdd = IOUtil.readInput(String.class);
//            if(!xmlPath.endsWith("/")){
//                xmlPath = xmlPath.concat("/");
//            }
//            if(toAdd.startsWith("/")){
//                toAdd = toAdd.substring(1);
//            }
//            String filePath = xmlPath.concat(toAdd);
//            if(!isPathXml(filePath)){
//                filePath = filePath.replace(".", "");
//                filePath = filePath.concat(".xml");
//            }
//
//            serializeGameTo(filePath);
//            //System.out.println("Saved game to given path successfully");
//        }
//        catch (JAXBException e) {
//            System.out.println("JAXB Exception: " + e);
//            e.printStackTrace();
//        }
//    }
//
//    private boolean xmlToGame() {
//        try {
//            ECNGame g = deserializeECNFrom(xmlPath);
//            gameWords = StringUtil.splitStringToList(g.getECNWords().getECNGameWords(), " ", "\n");
//            blackWords = StringUtil.splitStringToList(g.getECNWords().getECNBlackWords(), " ", "\n");
//            maxWords = gameWords.size();
//            maxBlack = blackWords.size();
//            game = Game.ECNGameToGame(g);
//            String result = areGamePreferencesValid();
//            if(result.equals("Valid")){
//                System.out.println("Game loaded, initiating game...");
//            }
//            else{
//                System.out.println(result);
//            }
//            return result.equals("Valid");
//        }
//        catch(JAXBException e) {
//            System.out.println("JAXBException - " + e);
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    private boolean xmlInstanceToGame() {
//        try{
//            game = deserializeGameFrom(xmlPath);
//            return true;
//        }
//        catch(JAXBException e) {
//            System.out.println("JAXBException - " + e);
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    private String areGamePreferencesValid(){
//        Board board = game.getBoard();
//        int words = board.getWordCount(), blackWords = board.getBlackWordsCount();
//        int rows = board.getRows(), cols = board.getCols();
//        int sumOfTeamWords = 0;
//
//        if (words + blackWords <= rows * cols){
//            if(blackWords <= maxBlack) {
//                for(int i = 0; i < game.getNumOfTeams(); ++i){
//                    sumOfTeamWords += game.getTeams().get(i).getWordTotal();
//                }
//                if(words <= maxWords && sumOfTeamWords <= words){
//                    if(areTeamNamesUnique(game.getTeams())) {
//                        return "Valid";
//                    }
//                    return "Error 6 - Team names are not unique";
//                }
//                if(sumOfTeamWords <= words){
//                    return "Error 5 - Word counts are bigger than size of the board (rows times cols)";
//                }
//                return "Error 4 - The sum of card counts for all teams is greater than the total amount of words chosen";
//            }
//            return "Error 3 - Black words count for board are invalid - should be less than amount of black words in xml file";
//        }
//        return "Error 2 - Sum of word counts (for all teams) is greater than the amount of words in game";
//
//    }
//
//    private static ECNGame deserializeECNFrom(String xmlPath) throws JAXBException {
//        // Create JAXB Context
//        JAXBContext context = JAXBContext.newInstance(ECNGame.class);
//
//        // Create Unmarshaller
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//
//        try {
//            // Unmarshal XML and return java object of ECNGame type
//            return (ECNGame) unmarshaller.unmarshal(new FileReader(xmlPath));
//        }
//        catch (FileNotFoundException e) {
//            System.out.println("File not found: " + xmlPath + "\n Enter a valid path for your file: ");
//            xmlPath = IOUtil.readInput(String.class);
//            return PrevGameCreator.deserializeECNFrom(xmlPath);
//        }
//    }
//
//    private static Game deserializeGameFrom(String xmlPath) throws JAXBException {
//        // Create JAXB Context
//        JAXBContext context = JAXBContext.newInstance(Game.class);
//
//        // Create Unmarshaller
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//
//        try {
//            // Unmarshal XML and return java object of Game type
//            return (Game) unmarshaller.unmarshal(new FileReader(xmlPath));
//        }
//        catch (FileNotFoundException e) {
//            System.out.println("File not found: " + xmlPath + "\n Enter a valid path for your file: ");
//            xmlPath = IOUtil.readInput(String.class);
//            return PrevGameCreator.deserializeGameFrom(xmlPath);
//        }
//    }
//
//    private void serializeGameTo(String xmlPath) throws JAXBException {
//        try {
//            // Create JAXB Context for the Game class
//            System.out.println("Creating JAXB Context...");
//            JAXBContext context = JAXBContext.newInstance(Game.class);
//            System.out.println("JAXB Context created successfully.");
//
//            // Create Marshaller
//            Marshaller marshaller = context.createMarshaller();
//
//            // Serialize Game object to XML file
//            FileWriter fw = new FileWriter(xmlPath);
//            marshaller.marshal(game, fw);
//            fw.close();
//            System.out.println("Saved game to given path successfully");
//
//        } catch (JAXBException e) {
//            System.out.println("Error occurred while serializing the game: " + e.getMessage());
//            throw new JAXBException("Error occurred while serializing the game: " + e.getMessage(), e);
//        } catch (IOException e) {
//            System.out.println("IOException occurred: " + e.getMessage());
//        }
//    }
//
//
//
//    private static boolean isPathXml(String path) {
//        return path.endsWith(".xml");
//    }
//
//    public static boolean fileExists(String filePath) {
//        File file = new File(filePath);
//        return file.exists();
//    }
//
//    private String isPathValid(){
//        return isPathValid(true);
//    }
//
//    /* Method isPathValid makes sure the path is a path for a xml file, and it returns
//       the given xmlPath if valid and returns a different valid path if the given path is
//       invalid (enters recursive loop until the given path is valid)    */
//    private String isPathValid(boolean load){
//        if(isPathXml(xmlPath) && load) {
//            if(fileExists(xmlPath)) {
//                return xmlPath;
//            }
//            else{
//                System.out.println("Error 1 - Invalid XML path (file does not exist in given path)");
//                return "False";
//            }
//        }
//        else if(load){
//            System.out.println("Error 1 - Invalid XML path (file is not xml type - needs to end with .xml)");
//            return "False";
//        }
//        else{
//            if(fileExists(xmlPath)){
//                return xmlPath;
//            }
//            System.out.println("Directory doesn't exist.");
//            return "False";
//        }
//    }
//
//
//
//    public static boolean areTeamNamesUnique(List<Team> teams) {
//        HashSet<Team> teamSet = new HashSet<>(teams);
//        return teamSet.size() == teams.size();
//    }
//
//    public void printGameDetails(){
//        System.out.println("Game Details:\n");
//        System.out.println("Total words (from xml): " + maxWords + "\nTotal black words (from xml): " + maxBlack);
//        System.out.println("Word count in game: " + game.getBoard().getWordCount());
//        System.out.println("Black word count in game: " + game.getBoard().getBlackWordsCount());
//        List<Team> teams = game.getTeams();
//        System.out.println("Teams details:\n");
//        for(Team team : teams){
//            System.out.println(team.getTeamInfo());
//        }
//    }
//
//
//
//    public void start(){
//        game.getBoard().createNewBoard(game.getTeams());
//        currTeamTurn = 0;
//    }
//
//    public boolean startTurn(){
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        int maxGuess = currTeam.getWordTotal() - currTeam.getWordScore();
//        printTurnMessage();
//        // Actual turn execution - definers phase first
//        System.out.println("Definers phase\n" + game.getBoard());
//        System.out.println("Enter your definition: ");
//        String hint = IOUtil.readInput(String.class);
//        System.out.println("How many cards is your definition relevant for?");
//        int words = IOUtil.readInputInRange(1, maxGuess);
//
//        // Guessers turn
//        System.out.println("Guessers phase\n Hint: " + hint  + "\n" + game.getBoard().toStringGuessMode() + "\n");
//        System.out.println("\nEnter your choice - up to " + words + " words (enter -1 to end your guesses): ");
//        getAndConfigureChoices(words);
//        // Increment the team index after turn is done
//        incrementTeamIndex();
//        currTeam.incrementTurns();
//        if(game.isGameOver()){
//            System.out.println("Game Over - " + game.getFinalMessage());
//        }
//        return game.isGameOver();
//    }
//
//    private void printTurnMessage(){
//        Team curr = game.getTeams().get(currTeamTurn);
//        System.out.println(curr.getName() + "'s turn - score: " + curr.getWordScore() + "/" + curr.getWordTotal());
//    }
//
//
//    // Should get each choice separately and print the board after choice was made and
//    // after checking if it was correct or not, and after applying the changes (from
//    // the choice) to the board (before printing again after the choice)
//    private void getAndConfigureChoices(int max){
//        int currentChoice = 0;
//        int i = 1;
//        System.out.println("Max - " + max);
//        while(currentChoice != -2 && i < max + 1){
//            currentChoice = IOUtil.readInputInRangeExcept(1, game.getBoard().getCardBoard().size(), -1) - 1;
//            if(currentChoice != -2) {
//                String msg = game.configureGuess(currTeamTurn, currentChoice);
//                System.out.println(game.getBoard().toStringGuessMode());
//                System.out.println(msg);
//                if(game.isGameOver()) {
//                    return;
//                }
//            }
//            ++i;
//        }
//    }
//
//    private void incrementTeamIndex(){
//        currTeamTurn += 1;
//        if(currTeamTurn >= game.getTeams().size()){
//            currTeamTurn = currTeamTurn % game.getTeams().size();
//        }
//    }
//
//    @Override
//    public String toString(){
//        String msg = "\nNext turn - Team " + game.getTeams().get(currTeamTurn).getName() + "'s turn";
//        return game.toString() + msg;
//    }




}



//
//
//public class PrevGameCreator {
//    private Game game;
//    private List<String> gameWords;
//    private int maxWords;
//    private String message = GameConfig.VALID;
//
//    public PrevGameCreator() {
//    }
//
//    public PrevGameCreator(ECNGame ecnGame, List<String> gameWords) {
//        this.gameWords = gameWords;
//        this.maxWords = gameWords.size();
//
//        if (ecnGame.getECNBoard().getCardsCount() + ecnGame.getECNBoard().getBlackCardsCount() > maxWords) {
//            this.game = null;
//            this.message = "Card count + black card count is bigger than the amount of words in dictionary!";
//            return;
//        }
//
//        this.game = Game.ECNGameToGame(ecnGame, gameWords);
//    }
//
//    public boolean isGameNull() {
//        return this.game == null;
//    }
//
//    public Game getGame() {
//        return this.game;
//    }
//
//    public String getFinalMessage() {
//        return message;
//    }
//
//}
