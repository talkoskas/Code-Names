package Util;


import GameIO.GameManager;
import GameObjects.GameCreator;
import GameObjects.Player;
import GameObjects.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Room {
    private String roomName, dictionaryName;
    private GameCreator gc;
    private GameManager gm;
    private int gameStatus = Config.PENDING, currentGuessers, currentDefiners;
    private final List<Team> teams;
    private final Map<String, Player> players;
    private Map<String, Integer> teamGuesserCounts, teamDefinerCounts;



    public Room(String roomName, String dictionaryName, GameCreator gc) {
        this.roomName = roomName;
        this.dictionaryName = dictionaryName;
        this.gc = gc;
        this.gm = new GameManager(gc.getGame());
        this.teams = gc.getGame().getTeams();
        this.players = new HashMap<>();
        this.gameStatus = Config.PENDING;
        this.currentGuessers = 0;
        this.currentDefiners = 0;
        this.teamGuesserCounts = new HashMap<>();
        this.teamDefinerCounts = new HashMap<>();
        for (Team team : teams) {
            teamGuesserCounts.put(team.getName(), 0);
            teamDefinerCounts.put(team.getName(), 0);
        }
    }

    public synchronized String getPlayerDetails(){
        StringBuilder result = new StringBuilder();
        result.append(gc.printGameDetails(true));

        return result.toString();
    }


    public synchronized int getTotalPlayersSignedUp() {
        return players.size();
    }

    public synchronized int getTotalPlayersNeeded() {
        return teams.stream().mapToInt(team -> team.getGuesserCount() + team.getDefinerCount()).sum();
    }

    public String getRoomName() {
        return roomName;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public GameCreator getGameCreator() {
        return gc;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

//    public void setGameCreator(GameCreator gc) {
//        this.gc = gc;
//    }

    public String getRoomDetails(boolean player, boolean definer){
        StringBuilder res = new StringBuilder("Room name: " + roomName);
        res.append("\nDictionary name: " + dictionaryName);

        if(gameStatus == Config.PENDING){
            res.append("\nGame status: Pending");
            if(player){
                res.append("\n" + gc.printGameDetails(player));
            }

        }
        else{
            res.append("\nGame status: Active");
            res.append("\n" + gm.getGameState(definer));
        }

        return res.toString();
    }

    public synchronized void resetToPending(){

    }

    public synchronized String addPlayer(Player player) {
        String teamName = player.getTeam();
        if (gameStatus != Config.PENDING) {
            return "Cannot join an active game!";
        }
        if (players.containsKey(player.getName())) {
            return player.getName() + " has already joined the room! Cannot join twice.";
        }

        Team team = teams.stream()
                .filter(t -> t.getName().equals(teamName))
                .findFirst()
                .orElse(null);

        if (team == null) {
            return "Couldn't find team " + teamName + "!";
        }

        int currentGuesserCount = teamGuesserCounts.get(teamName);
        int currentDefinerCount = teamDefinerCounts.get(teamName);

        if (player.isDefiner()) {
            if (currentDefinerCount >= team.getDefinerCount()) {
                return "No more definer slots available in this team!";
            }
            teamDefinerCounts.put(teamName, currentDefinerCount + 1);
            team.addDefiner(player.getName());
            ++this.currentDefiners;
        } else {
            if (currentGuesserCount >= team.getGuesserCount()) {
                return "No more guesser slots available in this team!";
            }
            teamGuesserCounts.put(teamName, currentGuesserCount + 1);
            team.addGuesser(player.getName());
            ++this.currentGuessers;
        }

        players.put(player.getName(), player);
        player.setTeam(teamName);
        checkAndActivateGame();
        return Config.VALID;
    }

    private void checkAndActivateGame() {
        boolean allTeamsFull = teams.stream().allMatch(team ->
                teamGuesserCounts.get(team.getName()) == team.getGuesserCount() &&
                        teamDefinerCounts.get(team.getName()) == team.getDefinerCount());

        if (allTeamsFull) {

            gm.start(teams);
            gameStatus = Config.ACTIVE;
            // Additional logic to start the game
        }
    }

    public synchronized List<String> getAvailableTeams() {
        return teams.stream()
                .filter(team -> teamGuesserCounts.get(team.getName()) < team.getGuesserCount() ||
                        teamDefinerCounts.get(team.getName()) < team.getDefinerCount())
                .map(Team::getName)
                .collect(Collectors.toList());
    }

    public String getTeamRolesInfo(String teamName) {
        Team team = teams.stream()
                .filter(t -> t.getName().equals(teamName))
                .findFirst()
                .orElse(null);

        if (team == null) {
            return "Team not found";
        }

        int guesserSlots = team.getGuesserCount() - teamGuesserCounts.get(teamName);
        int definerSlots = team.getDefinerCount() - teamDefinerCounts.get(teamName);

        return "Available slots in " + teamName + ":" +
                "Guessers: " + guesserSlots  +
                "Definers: " + definerSlots;
    }

    public void setGameCreator(GameCreator gc) {
        this.gc = gc;
    }

    public void setGameManager(GameManager gm) {
        this.gm = gm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomName.equals(room.roomName);
    }


    public GameManager getGameManager(){ return gm; }


}
