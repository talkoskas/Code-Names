package GameObjects;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import GameObjects.WinnerKeys;
import GameUtilV2.*;
import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Team;
import Util.GameConfig;
import Util.StringUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class GameCreator{
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

//
//    private boolean xmlToGame(Set<String> words) {
//        try {
//            ECNGame g = deserializeECNFrom(xmlPath);
//            List<List<String>> tmp = StringUtil.splitStringSetToLists(words,
//                    g.getECNBoard().getCardsCount(), g.getECNBoard().getBlackCardsCount());
//            
//            
//            maxWords = gameWords.size();
//            
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



//    public GameCreator() {
//        loadGameFile();
//    }

//
//    public GameCreator(boolean existing) {
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

//    public String getXmlPath() {
//        return xmlPath;
//    }

    public WinnerKeys getWinnerKeys(){
        return game.getWinnerKeys();
    }

    public void setWinnerKeys(WinnerKeys winnerKeys){
        this.game.setWinnerKeys(winnerKeys);
    }
    

    public int getCurrTeamTurn(){ return currTeamTurn; }

    public int getPhase(){ return phase; }

    public int getRequiredGuessers() { return this.requiredGuessers; }

    public int getRequiredDefiners() { return this.requiredDefiners; }
//
//    public String getFinalMessage(){
//        if(game == null){
//            return message;
//        }
//        return game.getFinalMessage();
//    }
//
//    public void setFinalMessage(String message){
//        game.setFinalMessage(message);
//    }
//
//    public String advancePhase(){
//        synchronized (lock) {
//            if (phase == GameConfig.GUESSER_PHASE) {
//                this.phase = GameConfig.DEFINER_PHASE;
//                incrementTeamIndex();
//            } else {
//                this.phase = GameConfig.GUESSER_PHASE;
//            }
//            if (game.isGameOver()) {
//                this.phase = GameConfig.GAME_INACTIVE;
//                return GameConfig.GAME_OVER;
//            }
//            return GameConfig.ONGOING;
//        }
//    }

//
//    private boolean xmlToGame() throws IOException {
//        try {
//
//            ECNGame g = deserializeECNFrom(xmlPath);
//            ECNBoard b = g.getECNBoard();
//            List<List<String>> tmp;
////            gameWords = StringUtil.splitStringToList(g.getECNWords().getECNGameWords(), " ", "\n");
////            blackWords = StringUtil.splitStringToList(g.getECNWords().getECNBlackWords(), " ", "\n");
////            maxWords = gameWords.size();
////            maxBlack = blackWords.size();
//
//            // Load the dictionary word file before calling ECNGameToGame
//            dictPath = (String)g.getECNDictionaryFile();
//            tmp = loadWordDictionary(dictPath, b.getCardsCount(), b.getBlackCardsCount());
//            game = Game.ECNGameToGame(g, tmp.get(0), tmp.get(1));
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
//    public static Set<String> loadDictFile(String fileName){
//        if(!isDictFileValid(fileName)){
//            return null;
//        }
//        try{
//            return loadAndProcessWords(fileName);
//        }catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static boolean isDictFileValid(String fileName){
//        String res = isPathValid(fileName, true, ".txt");
//        if(res.equals("False") || !fileExists(fileName)){
//            return false;
//        }
//        return true;
//    }
//
//    private boolean xmlInstanceToGame() {
//        try{
//        game = deserializeGameFrom(xmlPath);
//        return true;
//    }
//        catch(JAXBException e) {
//            System.out.println("JAXBException - " + e);
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//

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
            return null;
//            xmlPath = IOUtil.readInput(String.class);
//            return GameCreator.deserializeECNFrom(xmlPath);
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
            return null;
//            xmlPath = IOUtil.readInput(String.class);
//            return GameCreator.deserializeGameFrom(xmlPath);
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


    public String printGameDetails(){
        StringBuilder result = new StringBuilder();

        result.append("Game Details:");
        result.append("Total words (from xml): " + maxWords + "");
        result.append("Rows x Columns: " + game.getBoard().getRows() + " x "
                + game.getBoard().getCols() +"<br>");
        result.append("Word count in game: " + game.getBoard().getWordCount() + "");
        result.append("Black word count in game: " + game.getBoard().getBlackWordsCount() + "");
        List<Team> teams = game.getTeams();
        result.append("Teams details:");
        for(Team team : teams){
            result.append(team.getTeamInfo());
        }
        return result.toString();
    }

    @Override
    public String toString(){
        String msg = "\nNext turn - Team " + game.getTeams().get(currTeamTurn).getName() + "'s turn";
        return game.toString() + msg;
    }
}


//
//    public void start(List<Team> teams){
//        synchronized (lock) {
//            if (game == null || game.getBoard() == null) {
//                throw new IllegalStateException("Game or Board is not initialized");
//            }
//
//            game.getBoard().createNewBoard(game.getTeams());
//
//            game.setTeams(teams);
//            for(Team team : game.getTeams()){
//                team.initialize();
//
//            }
//            game.setWinnerKeys(new WinnerKeys(teams.size()));
//            currTeamTurn = 0;
//            phase = GameConfig.DEFINER_PHASE;
//            this.game.setGameOver(false);
//
//
//        }
//    }
//
//
//    private String getTurnMessage(){
//        Team curr = game.getTeams().get(currTeamTurn);
//        return curr.getName() + "'s turn - score: " + curr.getWordScore() + "/" + curr.getWordTotal();
//    }
//
//    public String guesserNotTurnMessage(){
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        StringBuilder sb = new StringBuilder();
//        sb.append("Guessers phase");
//        sb.append(game.getBoard().toStringGuessMode());
//        sb.append("Guesses performed this turn: " + currTeam.getMadeGuesses() + "");
//        sb.append("Enter your guess (by card index), or enter to end your turn");
//        return sb.toString();
//    }
//
//    public String guesserTurnMessage(int guesses){
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        StringBuilder sb = new StringBuilder();
//        sb.append("Guessers phase");
//        sb.append(game.getBoard().toStringGuessMode());
//        sb.append("Enter your guesses.");
//        sb.append("Hint: " + currTeam.getHint());
//        sb.append("Maximum guesses: " + guesses);
//        sb.append("Guesses performed this turn: " + currTeam.getMadeGuesses());
//        sb.append("Enter your guess (by card index), or enter to end your turn");
//        return sb.toString();
//    }
//
//    // !!!!!! NOW HERE !!!!!!!
//    public String definerTurnMessage() {
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        int maxGuess = currTeam.getWordTotal() - currTeam.getWordScore();
//        StringBuilder result = new StringBuilder();
//        result.append(getTurnMessage());
//        // Actual turn execution - definers phase first
//        result.append("Definers phase" + game.getBoard());
//        result.append("Enter your definition:");
//        return result.toString();
//    }
//
//    // From now on no Scanners - therefore the below code is in note
////        String hint = UIUtil.readInput(String.class);
////        System.out.println("How many cards is your definition relevant for?");
////        int words = UIUtil.readInputInRange(1, maxGuess);
////
////        // Guessers turn
////        System.out.println("Guessers phase\n Hint: " + hint  + "\n" + game.getBoard().toStringGuessMode() + "\n");
////        System.out.println("\nEnter your choice - up to " + words + " words (enter -1 to end your guesses): ");
////        getAndConfigureChoices(words);
////        // Increment the team index after turn is done
////        incrementTeamIndex();
////        currTeam.incrementTurns();
////        if(game.isGameOver()){
////            System.out.println("Game Over - " + game.getFinalMessage());
////        }
////        return game.isGameOver();
//
//
//    public void endTurn(){
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        resetTeamGuessesMade();
//        currTeam.setAllowedGuesses(1);
//        advancePhase();
//    }
//
//    public String configureGuess(int guess){
//        Team currTeam = game.getTeams().get(currTeamTurn);
//        if(guess == GameConfig.END_TURN){
//            endTurn();
//            if(currTeam.getMadeGuesses() > 0){
//                return currTeam.getGuessMessage();
//            }
//            return "Team " + currTeam.getName() + " has ended their turn early";
//        }
//        ++this.guessesMade;
//        String res = game.configureGuess(currTeamTurn, guess);
//        if(game.getGameOver()){
//            this.phase = GameConfig.PENDING;
//            return game.getFinalMessage();
//        }
//
//
//        if(currTeam.getMadeGuesses() == 1){
//            currTeam.setGuessMessage(res);
//        }
//        else if(currTeam.getMadeGuesses() > 1){
//            currTeam.addGuessMessage(res);
//        }
//
//        if(currTeam.isGameOver()){
//            endTurn();
//        }
//        else if(currTeam.areGuessesDone()){
//            currTeam.setAllowedGuesses(1);
//            resetTeamGuessesMade();
//            advancePhase();
//        }
//
//        return res;
//    }
//
//    public void resetTeamGuessesMade(){
//        for (Team team : game.getTeams()) {
//            team.setMadeGuesses(0);
//        }
//    }
//
//    public boolean isGameOver(){
//        return game.getGameOver();
//    }
//
//
//
//
//
//    // Should get each choice separately and print the board after choice was made and
//    // after checking if it was correct or not, and after applying the changes (from
//    // the choice) to the board (before printing again after the choice)
//    private void getAndConfigureChoices(int max){
//        int currentChoice = 0;
//        int i = 1;
//        while(currentChoice != -2 && i < max + 1){
//            currentChoice = IOUtil.readInputInRangeExcept(1, game.getBoard().getCardBoard().size(), -1) - 1;
//            if(currentChoice != -2) {
//                String msg = game.configureGuess(currTeamTurn, currentChoice);
//                if(game.isGameOver()) {
//                    return;
//                }
//            }
//            ++i;
//        }
//    }
//
//    private void incrementTeamIndex(){
//        ++currTeamTurn;
//        if(currTeamTurn >= game.getTeams().size()){
//            currTeamTurn = currTeamTurn % game.getTeams().size();
//        }
//        while(game.getTeams().get(currTeamTurn).isGameOver()){
//            ++currTeamTurn;
//        }
//        game.getTeams().get(currTeamTurn).setMadeGuesses(0);
//        game.getTeams().get(currTeamTurn).setAllowedGuesses(1);
//        game.getTeams().get(currTeamTurn).setGuessMessage("");
//        guessesMade = 0;
//    }
//
