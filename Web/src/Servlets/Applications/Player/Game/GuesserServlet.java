package Servlets.Applications.Player.Game;

import GameObjects.GameCreator;
import GameIO.GameManager;
import GameObjects.*;
import Util.*;
import WebAppObjects.Managers.*;
import WebAppObjects.Objects.Room;
import WebAppObjects.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class GuesserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String roomName = request.getParameter("roomName");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);
            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);

            User guesser = um.containsUser(username);
            if (guesser == null) {
                throw new ServletException("User not found");
            }

            Room room = rm.getActiveRoom(roomName);
            if (room == null) {
                throw new ServletException("Couldn't find active room");
            }

            GameManager gm = room.getGameManager();
            if (gm == null || gm.isGameNull()) {
                throw new ServletException("GameCreator was null or Game was null");
            }

            Player player = pm.getPlayer(username);
            if (player == null) {
                throw new ServletException("Couldn't find player");
            }

            if (player.isDefiner()) {
                throw new ServletException("You are a definer");
            }

            if (gm.getGame().isGameOver()) {
                jsonResponse.put("status", "gameover");
                jsonResponse.put("message", gm.getFinalMessage());
            } else {
                Team currTeam = gm.getGame().getTeams().get(gm.getCurrTeamTurn());
                jsonResponse.put("phase", gm.getPhase());
                jsonResponse.put("currentTeam", currTeam.getName());
                jsonResponse.put("isPlayerTurn", currTeam.containsPlayer(player.getName()));

                if (gm.getPhase() == Config.GUESSER_PHASE) {
                    if (currTeam.containsPlayer(player.getName())) {
                        jsonResponse.put("allowedGuesses", currTeam.getAllowedGuesses());
                        jsonResponse.put("message", gm.guesserTurnMessage(currTeam.getAllowedGuesses()));
                        jsonResponse.put("board", gm.getGame().getBoard().toString());
                    } else {
                        jsonResponse.put("message", gm.guesserNotTurnMessage());
                    }
                    if (currTeam.hasGuessedAlready()) {
                        jsonResponse.put("guessMessage", currTeam.getGuessMessage());
                    }
                } else {
                    jsonResponse.put("message", "Definers phase");
                    jsonResponse.put("board", gm.getGame().getBoard().toStringGuessMode());
                }
            }
            jsonResponse.put("status", "success");
        } catch (ServletException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        }

        out.print(jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // Read JSON data from request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());

            String username = jsonRequest.getString("username");
            String roomName = jsonRequest.getString("roomName");
            int guess = jsonRequest.getInt("guess");

            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);
            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);

            User guesser = um.containsUser(username);
            if (guesser == null) {
                throw new ServletException("User not found");
            }

            Room room = rm.getActiveRoom(roomName);
            if (room == null) {
                throw new ServletException("Couldn't find active room");
            }
            GameCreator gc = room.getGameCreator();
            GameManager gm = room.getGameManager();
            if (gm == null || gm.isGameNull()) {
                throw new ServletException("GameCreator was null or Game was null");
            }

            Player player = pm.getPlayer(username);
            if (player == null) {
                throw new ServletException("Couldn't find player");
            }

            if (player.isDefiner()) {
                throw new ServletException("You are a definer");
            }

            if (gm.getPhase() == Config.GUESSER_PHASE) {
                String result = gm.configureGuess(guess - 1);
                Team currTeam = gm.getGame().getTeams().get(gm.getCurrTeamTurn());
                String status = "";
                if(currTeam.isDone()){
                    status = "teamout";
                }
                if(gm.isGameOver()){
                    String finalMessage = gm.getFinalMessage();
                    status = "gameover";
                    result = finalMessage;
                    gm.setPhase(Config.GAME_INACTIVE);
                    /* TODO - make a method for end game that resets all necessary data-members and objects
                    *   that need re-initialization like the players maxCommand after finishing a game (within gm)*/

                }
                jsonResponse.put("status", status);
                jsonResponse.put("message", result);
            } else if (gm.getPhase() == Config.DEFINER_PHASE) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Someone has beaten you to the last guess. Your team's turn has already ended.");
            } else {
                jsonResponse.put("status", "gameover");
                jsonResponse.put("message", gm.getFinalMessage());
            }
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            System.out.println(e.getMessage());
            jsonResponse.put("message", e.getMessage());
        }

        out.print(jsonResponse);
    }
}