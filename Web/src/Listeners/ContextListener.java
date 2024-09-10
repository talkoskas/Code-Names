package Listeners;

import Util.*;
import WebAppObjects.Managers.DictionariesManager;
import WebAppObjects.Managers.EcnGameManager;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Managers.UserManager;
import WebAppObjects.Objects.Room;
import WebAppObjects.Managers.PlayerManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@WebListener
public class ContextListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(ContextListener.class.getName());
    private ScheduledExecutorService scheduler;
    UserManager userManager;
    DictionariesManager dictionariesManager;
    RoomManager roomManager;
    PlayerManager playerManager;
    EcnGameManager egm;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        userManager = new UserManager();
        dictionariesManager = new DictionariesManager();
        egm = new EcnGameManager();
        roomManager = new RoomManager(egm, dictionariesManager);
        playerManager = new PlayerManager();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkRoomStatuses, 0, 10, TimeUnit.SECONDS);
        LOGGER.info("Initializing UserContextListener...");

        sce.getServletContext().setAttribute(Config.USER_MANAGER_ATTRIBUTE, userManager);
        sce.getServletContext().setAttribute(Config.DICTIONARIES_MANAGER_ATTRIBUTE, dictionariesManager);
        sce.getServletContext().setAttribute(Config.ROOM_MANAGER_ATTRIBUTE, roomManager);
        sce.getServletContext().setAttribute(Config.PLAYER_MANAGER_ATTRIBUTE, playerManager);
        sce.getServletContext().setAttribute(Config.ECN_GAME_MANAGER_ATTRIBUTE, egm);
        LOGGER.info("UserManager, DictionariesManager, and RoomManager set in ServletContext.");
    }

    private void checkRoomStatuses() {
        synchronized (roomManager) {
            List<Room> roomsToReset = new ArrayList<>();
            for (Room room : roomManager.getActiveRooms()) {
                if (room.getGameStatus() == Config.PENDING) {
                    roomsToReset.add(room);
                }
            }
            for (Room room : roomsToReset) {
                roomManager.resetRoom(room.getRoomName());
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
        scheduler.shutdownNow();
    }
}
