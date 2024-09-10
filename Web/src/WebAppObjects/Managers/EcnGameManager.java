package WebAppObjects.Managers;

import GameUtilV2.ECNGame;

import java.util.*;

public class EcnGameManager {
    final Map<String, ECNGame> games;
    public EcnGameManager() {
        games = new HashMap<String, ECNGame>();
    }

    public synchronized void addGame(String name, ECNGame game) {
        games.put(name, game);
    }

    public synchronized void removeGame(String name) {
        games.remove(name);
    }

    public synchronized final Map<String, ECNGame> getGames() {
        return new HashMap<>(games);
    }

    public synchronized final ECNGame getECNGameByName(String name) {
        return games.get(name);
    }
}
