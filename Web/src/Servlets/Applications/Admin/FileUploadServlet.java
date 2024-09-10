package Servlets.Applications.Admin;


import GameIO.IOUtil;
import GameObjects.*;
import GameUtilV2.ECNGame;
import Util.*;
import WebAppObjects.Managers.DictionariesManager;
import WebAppObjects.Managers.EcnGameManager;
import WebAppObjects.Managers.RoomManager;
import WebAppObjects.Objects.Dictionary;
import WebAppObjects.Objects.Room;
import WebAppObjects.Objects.User;
import WebAppObjects.Managers.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import org.json.JSONObject;
import jakarta.servlet.ServletException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
@WebServlet
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // User authentication
            String username = request.getParameter("username");
            UserManager um = (UserManager) getServletContext().getAttribute(Config.USER_MANAGER_ATTRIBUTE);

            if (username == null || um == null || um.containsUser(username) == null || !um.containsUser(username).isAdmin()) {
                throw new ServletException("Authentication failed");
            }

            // Get other managers
            RoomManager rm = (RoomManager) getServletContext().getAttribute(Config.ROOM_MANAGER_ATTRIBUTE);
            DictionariesManager dm = (DictionariesManager) getServletContext().getAttribute(Config.DICTIONARIES_MANAGER_ATTRIBUTE);
            EcnGameManager egm = (EcnGameManager) getServletContext().getAttribute(Config.ECN_GAME_MANAGER_ATTRIBUTE);

            // Get parts and parameters
            Part xmlPart = request.getPart("xml");
            Part dictionaryPart = request.getPart("dictionary");
            String dictionaryName = request.getParameter("dictionaryName");

            // Validate inputs
            if (xmlPart == null || dictionaryPart == null || dictionaryName == null) {
                throw new ServletException("Missing required files or parameters");
            }

            // Deserialize XML
            ECNGame ecnGame = deserializeECNFromPart(xmlPart);
            if (ecnGame == null || ecnGame.getECNBoard() == null) {
                throw new ServletException("Invalid XML file or ECNBoard is null");
            }

            // Use ecnGame name as room name
            String roomName = ecnGame.getName();
            if (roomName == null || roomName.isEmpty()) {
                throw new ServletException("ECNGame name is missing or empty");
            }

            // Check if room already exists
            if (rm.containsRoom(roomName)) {
                throw new ServletException("Room already exists");
            }

            // Create dictionary
            Dictionary dictionary = createDictionaryFromPart(dictionaryPart, dictionaryName);
            dm.addDictionary(dictionary);

            // Add ECNGame to EcnGameManager
            egm.addGame(roomName, ecnGame);

            /* TODO - Here (below), After processing words, check if words size is at least the given word and
                black word counts and return an appropriate response in turn for it
             */

            // Process words
            Set<String> words = IOUtil.processWords(dictionary.getDictionary());

            if (words == null) {
                throw new ServletException("Not enough words in dictionary for the specified game");
            }

            // Create game
            GameCreator gameCreator = new GameCreator(ecnGame, new ArrayList<>(words));
            if (gameCreator.isGameNull()) {
                throw new ServletException(gameCreator.getFinalMessage());
            }

            // Validate game preferences
            String validationResult = gameCreator.areGamePreferencesValidV2();
            if (!validationResult.equals(Config.VALID)) {
                throw new ServletException(validationResult);
            }

            // Create and add room
            Room room = new Room(roomName, dictionaryName, gameCreator);
            String addRoomResult = rm.addRoom(room);
            if (!addRoomResult.equals(Config.VALID)) {
                throw new ServletException(addRoomResult);
            }

            // Success
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Game Room Created Successfully");
            jsonResponse.put("roomDetails", room.getRoomDetails(false, false));

            // Update admin's max command if necessary
            User admin = um.containsUser(username);
            if (admin.getAdminMaxCommand() == Config.FILE_UPLOAD_COMMAND_INT) {
                if(!rm.areActiveRoomsEmpty()){
                    admin.setAdminMaxCommand(Config.WATCH_ACTIVE_GAME_INT);
                }
                else{
                    admin.setAdminMaxCommand(Config.ROOM_DETAILS_INT);
                }
                jsonResponse.put("newMaxCommand", admin.getAdminMaxCommand());
            }

        } catch (ServletException e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", e.getMessage());
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(jsonResponse.toString());
    }

    private Dictionary createDictionaryFromPart(Part filePart, String dictionaryName) throws IOException {
        Set<String> words = new BufferedReader(new InputStreamReader(filePart.getInputStream()))
                .lines().collect(Collectors.toSet());
        return new Dictionary(dictionaryName, words);
    }

    private ECNGame deserializeECNFromPart(Part filePart) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(ECNGame.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (InputStream inputStream = filePart.getInputStream()) {
            return (ECNGame) unmarshaller.unmarshal(inputStream);
        }
    }
}