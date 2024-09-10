package Servlets.Applications.Admin;

import Util.Config;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public class CheckAdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);
        boolean adminExists = (um.getAdmin() != null);

        jsonResponse.put("adminExists", adminExists);
        out.print(jsonResponse.toString());
    }
}