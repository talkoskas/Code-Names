package Servlets.Api;

import Util.*;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            JSONObject jsonRequest = new JSONObject(reader.lines().collect(Collectors.joining()));
            String username = jsonRequest.getString(Config.USERNAME_ATTRIBUTE);
            boolean isAdmin = jsonRequest.getBoolean(Config.IS_ADMIN_ATTRIBUTE);

            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);

            if (um.containsUser(username) != null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Username already exists");
            } else if (isAdmin && um.getAdmin() != null) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "An admin user already exists");
            } else {
                User newUser = new User(username, isAdmin);
                String result = um.addUser(newUser);

                if (result.equals("Created")) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "User registered successfully");
                    jsonResponse.put(Config.USERNAME_ATTRIBUTE, username);
                    jsonResponse.put(Config.IS_ADMIN_ATTRIBUTE, isAdmin);

                } else {
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Failed to register user: " + result);
                }
            }
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error during registration process: " + e.getMessage());
        }

        out.print(jsonResponse);
    }
}