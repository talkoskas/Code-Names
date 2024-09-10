package Servlets.Applications.Player.Game;

import GameIO.GameManager;
import GameObjects.*;
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
public class DefinerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String roomName = request.getParameter("roomName");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);

            User definer = um.containsUser(username);
            if (definer == null) {
                System.out.println("Username -  " + username);
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

            if (!player.isDefiner()) {
                throw new ServletException("You are a guesser");
            }

            Team currTeam = gm.getGame().getTeams().get(gm.getCurrTeamTurn());
            jsonResponse.put("phase", gm.getPhase());
            jsonResponse.put("currentTeam", currTeam.getName());
            jsonResponse.put("isPlayerTurn", currTeam.containsPlayer(player.getName()));
            jsonResponse.put("board", gm.getGame().getBoard().toString());

            if (gm.getPhase() == Config.DEFINER_PHASE) {
                if (currTeam.containsPlayer(player.getName())) {
                    jsonResponse.put("message", gm.definerTurnMessage());
                } else {
                    jsonResponse.put("message", "Other team's definer phase");
                }
            } else if (gm.getPhase() == Config.GUESSER_PHASE) {
                jsonResponse.put("message", "Guessers phase");
            } else {
                jsonResponse.put("status", "gameover");
                jsonResponse.put("message", gm.getFinalMessage());
                return;
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
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());

            String username = jsonRequest.getString("username");
            String roomName = jsonRequest.getString("roomName");
            String hint = jsonRequest.getString("hint");
            int guesses = jsonRequest.getInt("guesses");

            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);

            User definer = um.containsUser(username);
            if (definer == null) {
                System.out.println("Username -  " + username);
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

            if (!player.isDefiner()) {
                throw new ServletException("You are a guesser");
            }

            if (gm.getPhase() == Config.DEFINER_PHASE) {
                Game game = gm.getGame();
                Team currentTeam = game.getTeams().get(gm.getCurrTeamTurn());

                currentTeam.setHint(hint);
                currentTeam.setAllowedGuesses(guesses);
                String result = gm.advancePhase();

                jsonResponse.put("status", "success");
                if (result.equals(Config.ONGOING)) {
                    jsonResponse.put("message", "Hint submitted successfully. Waiting for guessers.");
                } else {
                    jsonResponse.put("status", "gameover");
                    jsonResponse.put("message", "Game over!");
                }
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "It's not your turn to define. Waiting for guessers.");
            }
        } catch (ServletException | NumberFormatException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        }catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        }
        out.print(jsonResponse);
    }
}