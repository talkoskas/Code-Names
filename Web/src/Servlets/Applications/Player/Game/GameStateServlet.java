package Servlets.Applications.Player.Game;

import GameIO.GameManager;
import Util.Config;
import GameObjects.*;
import WebAppObjects.Managers.PlayerManager;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet
public class GameStateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter(Config.USERNAME_ATTRIBUTE);
        String roomName = request.getParameter(Config.ROOM_NAME);
        PrintWriter out = response.getWriter();
        RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
        Room room = rm.getRoom(roomName);

        PlayerManager pm = (PlayerManager) getServletContext().getAttribute(Config.PLAYER_MANAGER_ATTRIBUTE);
        Player player = pm.getPlayer(username);

        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        try {
            if (room == null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Room not found");
                out.print(jsonResponse.toString());
                return;
            }

            GameManager gm = room.getGameManager();
            if (gm == null) {

                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Game not initialized");
                out.print(jsonResponse.toString());
                return;
            }
            synchronized (gm) {
                Game game = gm.getGame();
                if (game == null) {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Game not found");
                    out.print(jsonResponse.toString());
                    return;
                }

                jsonResponse.put("status", "success");
                jsonResponse.put("phase", gm.getPhase());
                jsonResponse.put("currentTeamName", game.getTeams().get(gm.getCurrTeamTurn()).getName());
                jsonResponse.put("isGameOver", game.getGameOver());
                jsonResponse.put("finalMessage", game.getFinalMessage());

                StringBuilder gameStateBuilder = new StringBuilder();

                if (gm.getPhase() == Config.DEFINER_PHASE) {
                    gameStateBuilder.append("\nPhase: Definer phase").append("\n");
                } else if (gm.getPhase() == Config.GUESSER_PHASE) {
                    gameStateBuilder.append("\nPhase: Guesser phase").append("\n");
                }

                gameStateBuilder.append("Current Team Turn: ").append(game.getTeams().get(gm.getCurrTeamTurn()).getName()).append("\n");
                if (player.isDefiner() && (gm.getPhase() != Config.GAME_INACTIVE && gm.getPhase() != Config.PENDING)) {
                    gameStateBuilder.append(game.getBoard().toString()).append("\n");
                } else {
                    gameStateBuilder.append(game.getBoard().toStringGuessMode()).append("\n");
                }


                if (!gm.isGameActive()) {
                    gameStateBuilder.append("Teams:\n");
                    for (Team team : game.getTeams()) {
                        gameStateBuilder.append(team.getPendingTeamStatus());
                    }
                }

                List<Team> teams = game.getTeams();
                Team currentTeam = teams.get(gm.getCurrTeamTurn());
                int activeTeams = 0, totalTeams = teams.size();
                for(Team team : teams){
                    if(!team.isDone()){
                        ++activeTeams;
                    }
                }
                jsonResponse.put("activeTeams", activeTeams);
                jsonResponse.put("totalTeams", totalTeams);
                if (gm.getPhase() == Config.GUESSER_PHASE) {
                    gameStateBuilder.append(currentTeam.getActiveTeamStatus());
                    gameStateBuilder.append("Hint: ").append(currentTeam.getHint()).append("\n");
                    gameStateBuilder.append("Guesses (made / allowed): ").append(currentTeam.getMadeGuesses())
                            .append(" / ").append(currentTeam.getAllowedGuesses()).append("\n");
                }
                jsonResponse.put("gameState", gameStateBuilder.toString());


                if (player != null) {
                    jsonResponse.put("playerRole", player.isDefiner() ? "definer" : "guesser");
                    jsonResponse.put("playerTeam", player.getTeam());
                }
                if (gm.getPhase() == Config.GUESSER_PHASE) {
                    jsonResponse.put("guessMessage", currentTeam.getGuessMessage());
                } else {
                    jsonResponse.put("guessMessage", "");
                }
            }

        } catch (JSONException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error creating JSON response: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }

    private String getPhaseString(int phase) {
        switch (phase) {
            case Config.DEFINER_PHASE:
                return "Definer Phase";
            case Config.GUESSER_PHASE:
                return "Guesser Phase";
            case Config.GAME_INACTIVE:
                return "Game Inactive - waiting for players to join the game";
            default:
                return "Unknown Phase";
        }
    }
}
