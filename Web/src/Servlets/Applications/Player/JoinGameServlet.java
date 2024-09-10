package Servlets.Applications.Player;

import Util.*;
import GameObjects.*;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.*;
import WebAppObjects.Managers.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet
public class JoinGameServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            JSONObject jsonRequest = new JSONObject(reader.lines().collect(Collectors.joining()));

            String username = jsonRequest.getString("username");
            int roomIndex = jsonRequest.getInt("roomIndex");
            int teamIndex = jsonRequest.getInt("teamIndex");
            String role = jsonRequest.getString("role");

            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);

            User user = um.containsUser(username);
            if (user == null) {
                throw new ServletException("User not found");
            }

            List<Room> pendingRooms = rm.getPendingRooms();
            if (roomIndex < 0 || roomIndex >= pendingRooms.size()) {
                throw new ServletException("Invalid room index");
            }

            Room selectedRoom = pendingRooms.get(roomIndex);
            GameCreator gc = selectedRoom.getGameCreator();
            List<Team> teams = gc.getGame().getTeams();
            List<Team> availableTeams = new ArrayList<>();
            for(Team team : teams) {
                if(team.getPlayers().size() != team.getGuesserCount() + team.getDefinerCount()){
                    availableTeams.add(team);
                }
            }

            if (teamIndex < 0 || teamIndex >= teams.size()) {
                throw new ServletException("Invalid team index");
            }

            Team selectedTeam = availableTeams.get(teamIndex);
            boolean isDefiner = role.equals("definer");

            Player player = new Player(username, selectedTeam.getName(), isDefiner);
            pm.addPlayer(player);

            String result = rm.addPlayerToRoom(player, selectedRoom);

            // In case of a newly activated room, update the admins max command to 3 (if admin exists in um)
            if(!rm.areActiveRoomsEmpty()){
                User admin = um.getAdmin();
                if(admin != null){
                    System.out.println("Set admins maxCommand to 3!!!!");
                    admin.setAdminMaxCommand(Config.WATCH_ACTIVE_GAME_INT);
                }
            }


            if (Config.VALID.equals(result)) {
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Successfully joined the game");
                jsonResponse.put("roomName", selectedRoom.getRoomName());
                jsonResponse.put("team", selectedTeam.getName());
                jsonResponse.put("isDefiner", player.isDefiner());
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Error joining game: " + result);
            }

        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error during join game process: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}