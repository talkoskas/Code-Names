package Player;

import GameObjects.*;

public class PlayerUser {
    Player player;
    private int maxCommand;

    public PlayerUser(String username, int maxCommand) {
        this.player = new Player(username);
        this.maxCommand = maxCommand;
    }

    public String getUsername() {
        return player.getName();
    }

    public int getMaxCommand() {
        return maxCommand;
    }

    public void setMaxCommand(int maxCommand) {
        this.maxCommand = maxCommand;
    }

    public String getSelectedRoomName() {
        return player.getRoomName();
    }

    public void setSelectedRoomName(String selectedRoomName) {
        this.player.setRoomName(selectedRoomName);
    }

    public String getTeamName(){
        return player.getTeam();
    }

    public void setTeamName(String teamName){
        this.player.setTeam(teamName);
    }

    public boolean isDefiner(){ return player.isDefiner(); }

    public void setDefiner(boolean definer){
        this.player.setDefiner(definer);
    }
}