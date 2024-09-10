package Servlets.Applications.Admin;

import Util.Config;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Managers.UserManager;
import WebAppObjects.Objects.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class UpdateMaxCommandServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        // Read JSON data from request body
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        JSONObject jsonRequest = new JSONObject(sb.toString());

        String username = jsonRequest.getString("username");
        boolean isAdmin = jsonRequest.getBoolean("isAdmin");

        UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
        RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);

        User admin = um.containsUser(username);

        if (admin == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "User not found");
        } else {
            if (admin.isAdmin() && isAdmin) {
                int newMaxCommand;
                if (!rm.areActiveRoomsEmpty()) {
                    newMaxCommand = Config.WATCH_ACTIVE_GAME_INT;
                } else if (!rm.arePendingRoomsEmpty()) {
                    newMaxCommand = Config.ROOM_DETAILS_INT;
                } else {
                    newMaxCommand = Config.FILE_UPLOAD_COMMAND_INT;
                }

                admin.setAdminMaxCommand(newMaxCommand);
                jsonResponse.put("status", "success");
                jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, newMaxCommand);
            } else {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "User is not an admin");
            }
        }

        out.print(jsonResponse.toString());
    }

}
