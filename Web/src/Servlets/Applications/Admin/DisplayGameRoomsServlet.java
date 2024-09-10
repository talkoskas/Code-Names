package Servlets.Applications.Admin;

import Util.Config;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Managers.UserManager;
import WebAppObjects.Objects.User;
import Util.StaticMethods;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class DisplayGameRoomsServlet extends HttpServlet {
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

        if (user == null || !user.isAdmin()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Authentication failed");
        } else {
            jsonResponse.put("status", "success");
            jsonResponse.put("roomsDetails", rm.getPendingRoomsDetails());
        }

        out.print(jsonResponse.toString());
    }
}