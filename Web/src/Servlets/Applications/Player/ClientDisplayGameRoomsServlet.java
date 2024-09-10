package Servlets.Applications.Player;

import Util.Config;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;


@WebServlet
public class ClientDisplayGameRoomsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext context = getServletContext();
        RoomManager rm = (RoomManager) context.getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
        UserManager um = (UserManager) context.getAttribute(Config.USER_MANAGER_ATTRIBUTE);

        String username = req.getParameter("username");
        User user = um.containsUser(username);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        if (user == null) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "You are not logged in");
        } else {
            jsonResponse.put("status", "success");
            String roomsDetails = rm.getPendingRoomsDetails();
            jsonResponse.put("roomsDetails", roomsDetails);

            boolean hasPendingRooms = !roomsDetails.equals(Config.PENDING_EMPTY_MESSAGE);
            int currentMaxCommand = user.getClientMaxCommand();
            int newMaxCommand;

            if (hasPendingRooms) {
                newMaxCommand = Config.CLIENT_JOIN_INT;
            } else {
                newMaxCommand = Config.CLIENT_ROOM_DETAILS_INT;
            }

            user.setClientMaxCommand(newMaxCommand);
            jsonResponse.put("maxCommand", newMaxCommand);
        }

        out.print(jsonResponse.toString());
    }
}