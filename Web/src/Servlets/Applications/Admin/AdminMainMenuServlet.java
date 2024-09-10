package Servlets.Applications.Admin;

import Util.Config;
import Util.StaticMethods;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet
public class AdminMainMenuServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
        User user = um.containsUser(username);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        if(username == null || username.isEmpty() || user == null || !user.isAdmin()) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Authentication failed");
        } else {
            jsonResponse.put("status", "success");
            jsonResponse.put("username", username);
            jsonResponse.put("maxCommand", user.getAdminMaxCommand());
        }

        out.print(jsonResponse.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Similar modifications to return JSON instead of HTML
    }
}