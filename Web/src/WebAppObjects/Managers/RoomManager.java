package WebAppObjects.Managers;

import GameObjects.*;
import GameIO.IOUtil;
import GameUtilV2.ECNGame;
import Util.Config;
import WebAppObjects.Objects.Dictionary;
import WebAppObjects.Objects.Room;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomManager {
    private final Set<Room> rooms;
    private final List<Room> pendingRooms = new ArrayList<>();
    private final List<Room> activeRooms = new ArrayList<>();
    EcnGameManager egm;
    DictionariesManager dm;


    public RoomManager() {
        this.rooms = new HashSet<>();
    }
    public RoomManager(EcnGameManager egm, DictionariesManager dm) {
        this.dm = dm;
        this.egm = egm;
        this.rooms = new HashSet<>();
    }
    public synchronized String addRoom(Room room) {
    /* TODO - Add a condition to locate the dictionary file of the room in fileUploadServlet
       before performing this method. If dictionary file isn't found, don't perform this method.
       */
        if (rooms.contains(room)) {
            return "Room name already exists - please provide a unique room name";
        } else {
            rooms.add(room);
            pendingRooms.add(room);
            return Config.VALID;
        }
    }

    public synchronized boolean containsRoom(String roomName){
        for (Room room : rooms) {
            if(room.getRoomName().equals(roomName)){
                return true;
            }
        }
        return false;
    }

    public synchronized String getRoomsDetails(boolean player){
        StringBuilder roomsDetails = new StringBuilder();
        int i = 1;
        for (Room room : rooms) {
            roomsDetails.append( "\nRoom " + i + ":\n" + room.getRoomDetails(player, false) + "\n");

        }
        return roomsDetails.toString();
    }


    public synchronized String getPendingRoomsDetails() {
        StringBuilder roomsDetails = new StringBuilder();
        int i = 0;
        if(pendingRooms.isEmpty()){
            return Config.PENDING_EMPTY_MESSAGE;
        }
        for (Room room : pendingRooms) {
            ++i;
            roomsDetails.append("\n\nRoom " + i + ":\n" + room.getRoomDetails(true, false));
        }
        return roomsDetails.toString();
    }

    public synchronized String getActiveRoomsDetails(){
        StringBuilder roomsDetails = new StringBuilder();
        if(activeRooms.isEmpty()){
            return Config.ACTIVE_EMPTY_MESSAGE;
        }
        for (Room room : activeRooms) {
            roomsDetails.append(room.getRoomDetails(false, false) + "\n");
        }
        return roomsDetails.toString();
    }

    public synchronized int getPendingRoomsCount(){
        return pendingRooms.size();
    }

    public synchronized List<Room> getPendingRooms(){
        return pendingRooms;
    }

    public synchronized Room getRoom(String roomName){
        for (Room room : rooms) {
            if(room.getRoomName().equals(roomName)){
                return room;
            }
        }
        return null;
    }

    public synchronized Set<Room> getRooms(){ return rooms;}


    public synchronized Room getActiveRoom(String roomName){
        for (Room room : activeRooms) {
            if(room.getRoomName().equals(roomName)){
                return room;
            }
        }
        return null;
    }
    private GameCreator createNewGameCreator(Room oldRoom) {
        Dictionary d;
        ECNGame ecnGame;
        // Get the ECNGame from the EcnGameManager
        synchronized (egm) {
            ecnGame = egm.getECNGameByName(oldRoom.getRoomName());
        }
        // Get the dictionary
        synchronized (dm) {
            d = dm.getDictionaryByName(oldRoom.getDictionaryName());
        }
        // Process words
        if(d != null && ecnGame != null) {
            Set<String> words = IOUtil.processWords(d.getDictionary());
                    //ecnGame.getECNBoard().getCardsCount(), ecnGame.getECNBoard().getBlackCardsCount());
            // Create and return a new GameCreator
            return new GameCreator(ecnGame, new ArrayList<>(words));
        }
        return null;

    }

    public synchronized List<Room> getActiveRooms(){return activeRooms;}

    public synchronized int getActiveRoomsCount(){ return activeRooms.size();}

    public synchronized boolean areActiveRoomsEmpty(){ return activeRooms.isEmpty();}

    public synchronized boolean arePendingRoomsEmpty(){return pendingRooms.isEmpty();}

    public synchronized Room getPendingRoom(String roomName){
        for (Room room : pendingRooms) {
            if(room.getRoomName().equals(roomName)){
                return room;
            }
        }
        return null;
    }


    public void resetRoom(String roomName) {
        Room room = getActiveRoom(roomName);
        if (room != null) {
            // Remove from active rooms
            activeRooms.remove(room);
            GameCreator g = createNewGameCreator(room);
            // Create a new Room object with reset game state
            if(g!=null) {
                Room newRoom = new Room(room.getRoomName(), room.getDictionaryName(), g);
                pendingRooms.add(newRoom);
            }
            // Add to pending rooms

        }
    }

    public synchronized String addPlayerToRoom(Player player, Room room) {
        boolean wasPending = (Config.PENDING == room.getGameStatus());
        String result = room.addPlayer(player);
        if(result.equals(Config.VALID)){
            if(wasPending && Config.ACTIVE == room.getGameStatus()){
                pendingRooms.remove(room);
                activeRooms.add(room);
            }
        }
        return result;
    }

}
