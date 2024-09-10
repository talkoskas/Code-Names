package Servlets.Applications.Player;

import Util.Config;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet
public class GetAvailableTeamsServlet extends HttpServlet {
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

            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);

            List<Room> pendingRooms = rm.getPendingRooms();
            if (roomIndex < 0 || roomIndex >= pendingRooms.size()) {
                throw new ServletException("Invalid room index");
            }

            Room selectedRoom = pendingRooms.get(roomIndex);
            List<String> availableTeams = selectedRoom.getAvailableTeams();

            jsonResponse.put("status", "success");
            jsonResponse.put("teams", new JSONArray(availableTeams));

        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error getting available teams: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
    }
}