package WebAppObjects.Managers;

import java.util.HashMap;
import java.util.Map;
import GameObjects.*;

public class PlayerManager {
    private final Map<String, Player> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getName());
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

}
