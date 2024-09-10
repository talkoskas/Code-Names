package Servlets.Applications.Player;

import Util.*;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.Room;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import WebAppObjects.Managers.PlayerManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
@WebServlet
public class WaitingRoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String roomName = req.getParameter("roomName");
        RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
        UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
        PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        User user = um.containsUser(username);
        if (user == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not found");
        } else {
            Room room = rm.getRoom(roomName);

            if (room == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Room not found");
            } else {
                user.setSelectedRoom(room);

                if (room.getGameStatus() == Config.ACTIVE) {
                    jsonResponse.put("status", "active");
                    jsonResponse.put("message", "Game has started");
                } else {
                    jsonResponse.put("status", "waiting");
                    jsonResponse.put("playersJoined", room.getTotalPlayersSignedUp());
                    jsonResponse.put("totalPlayers", room.getTotalPlayersNeeded());
                }
            }
        }

        out.print(jsonResponse);
    }
}