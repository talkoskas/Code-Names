package Servlets.Applications.Admin;

import GameIO.GameManager;
import GameObjects.GameCreator;
import GameObjects.Team;
import Util.*;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.Room;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet
public class WatchGameAdmin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String roomName = request.getParameter("roomName");
        String action = request.getParameter("action"); // "join" or "watch"

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);

            User admin = um.containsUser(username);
            if (admin == null || !admin.isAdmin()) {
                throw new ServletException("Authentication failed or user is not an admin");
            }

            if ("join".equals(action)) {
                // Logic for joining a game to watch
                List<Room> activeRooms = rm.getActiveRooms();
                JSONArray roomsArray = new JSONArray();
                for (Room room : activeRooms) {
                    JSONObject roomObject = new JSONObject();
                    roomObject.put("name", room.getRoomName());
                    roomObject.put("players", room.getTotalPlayersSignedUp());

                    // Add activeTeams and totalTeams information
                    int activeTeams = 0, totalTeams = room.getGameManager().getGame().getTeams().size();
                    for (Team team : room.getGameManager().getGame().getTeams()) {
                        if (!team.isGameOver()) {
                            activeTeams++;
                        }
                    }
                    roomObject.put("activeTeams", activeTeams);
                    roomObject.put("totalTeams", totalTeams);

                    roomsArray.put(roomObject);
                }
                jsonResponse.put("activeRooms", roomsArray);
                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Active rooms retrieved successfully");

            } else if ("watch".equals(action)) {
                // Logic for watching a game
                Room room = rm.getActiveRoom(roomName);
                if (room == null) {
                    throw new ServletException("Couldn't find active room: " + roomName);
                }

                GameCreator gc = room.getGameCreator();
                GameManager gm = room.getGameManager();
                if (gc == null || gc.isGameNull()) {
                    throw new ServletException("GameCreator was null or Game was null.");
                }

                Team currTeam = gc.getGame().getTeams().get(gc.getCurrTeamTurn());
                List<Team> teams = gc.getGame().getTeams();
                int activeTeams = 0, totalTeams = teams.size();
                for (Team team : teams) {
                    if(!team.isGameOver()){
                        ++activeTeams;
                    }
                }

                jsonResponse.put("status", "success");
                jsonResponse.put("gameState", gc.getGame().getBoard().toString());
                jsonResponse.put("phase", gm.getPhase());
                jsonResponse.put("currentTeam", currTeam.getName());
                jsonResponse.put("wordScore", currTeam.getWordScore());
                jsonResponse.put("wordTotal", currTeam.getWordTotal());
                jsonResponse.put("turns", currTeam.getTurns());
                jsonResponse.put("activeTeams", activeTeams);
                jsonResponse.put("totalTeams", totalTeams);

                if (gm.getPhase() == Config.DEFINER_PHASE) {
                    jsonResponse.put("phaseDescription", "Definer phase");

                } else if (gm.getPhase() == Config.GUESSER_PHASE) {
                    jsonResponse.put("phaseDescription", "Guesser phase");
                    jsonResponse.put("hint", currTeam.getHint());
                    jsonResponse.put("madeGuesses", currTeam.getMadeGuesses());
                    jsonResponse.put("allowedGuesses", currTeam.getAllowedGuesses());

                    if (currTeam.hasGuessedAlready()) {
                        jsonResponse.put("guessMessage", currTeam.getGuessMessage());
                    }
                }
                else {
                    if (gm.getPhase() == Config.PENDING) {
                        jsonResponse.put("phaseDescription", "Pending phase");
                    }
                    else{
                        jsonResponse.put("phaseDescription", "Unknown phase");
                    }
                }

                boolean gameOver = gm.getPhase() == Config.GAME_INACTIVE;
                jsonResponse.put("gameOver", gameOver);

                if (gameOver) {
                    jsonResponse.put("finalMessage", gc.getFinalMessage());
                }
            } else {
                throw new ServletException("Invalid action specified");
            }
        } catch (ServletException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Unexpected error: " + e.getMessage());
        }

        out.print(jsonResponse);
    }
}