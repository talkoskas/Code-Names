package Servlets.Applications.Player;

import Util.Config;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet
public class ClientMainMenuServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
        User user = um.containsUser(username);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        if(username == null || username.isEmpty() || user == null || user.isAdmin()){
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "You are not logged in as a client");
        } else {
            jsonResponse.put("status", "success");
            jsonResponse.put("username", username);
            jsonResponse.put("maxCommand", user.getClientMaxCommand());
        }

        out.print(jsonResponse.toString());
    }
}