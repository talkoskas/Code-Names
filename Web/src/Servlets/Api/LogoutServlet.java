package Servlets.Api;

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
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        UserManager um = (UserManager) request.getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        if (username == null || username.isEmpty()) {
            jsonResponse.put("status", "fail");
            jsonResponse.put("message", "No username provided");
        } else {
            User user = um.containsUser(username);
            if (user == null) {
                jsonResponse.put("status", "fail");
                jsonResponse.put("message", "User not found");
            } else {
                String res = um.removeUser(user);
                if (res.equals("Deleted")) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Successfully logged out");
                } else {
                    jsonResponse.put("status", "fail");
                    jsonResponse.put("message", "Error during logout: " + res);
                }
            }
        }

        out.print(jsonResponse.toString());
    }
}