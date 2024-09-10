package Servlets.Api;

import Util.Config;
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
public class LoginServlet extends HttpServlet {
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
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);

            User user = um.containsUser(username);


            if (user == null) {
                jsonResponse.put("status", "not_registered");
                jsonResponse.put("message", "User not found. Please register first.");
            } else if (isAdmin != user.isAdmin()) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", isAdmin ? "You are not registered as an admin." : "This is an admin account. Please use the admin login.");
            } else {
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute(Config.USERNAME_ATTRIBUTE) == null) {
                    session = request.getSession(true);
                    session.setAttribute(Config.USERNAME_ATTRIBUTE, username);
                    session.setAttribute(Config.IS_ADMIN_ATTRIBUTE, user.isAdmin());

                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Login successful");
                    jsonResponse.put(Config.USERNAME_ATTRIBUTE, username);
                    jsonResponse.put(Config.IS_ADMIN_ATTRIBUTE, user.isAdmin());

                    String pendingDetails = rm.getPendingRoomsDetails();
                    String activeDetails = rm.getActiveRoomsDetails();

                    if(isAdmin){
                        if(pendingDetails.equals(Config.PENDING_EMPTY_MESSAGE)){
                            if(activeDetails.equals(Config.ACTIVE_EMPTY_MESSAGE)){
                                jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.FILE_UPLOAD_COMMAND_INT);
                            }
                            else {
                                jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.WATCH_ACTIVE_GAME_INT);
                            }
                        }
                        else{
                            if(activeDetails.equals(Config.ACTIVE_EMPTY_MESSAGE)){
                                jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.ROOM_DETAILS_INT);
                            }
                            else{
                                jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.WATCH_ACTIVE_GAME_INT);
                            }
                        }
                    }
                    else{
                        if(pendingDetails.equals(Config.PENDING_EMPTY_MESSAGE)){
                            jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.CLIENT_ROOM_DETAILS_INT);
                        }
                        else{
                            jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, Config.CLIENT_JOIN_INT);
                        }
                    }



                    //jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, user.isAdmin() ? user.getAdminMaxCommand() : user.getClientMaxCommand());
                } else {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Already logged in");
                    jsonResponse.put(Config.USERNAME_ATTRIBUTE, username);
                    jsonResponse.put(Config.IS_ADMIN_ATTRIBUTE, user.isAdmin());
                    jsonResponse.put(Config.MAX_COMMAND_ATTRIBUTE, user.isAdmin() ? user.getAdminMaxCommand() : user.getClientMaxCommand());
                }
            }
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error during login process: " + e.getMessage());
        }

        out.print(jsonResponse);
    }
}