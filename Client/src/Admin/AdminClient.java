package Admin;

import GameIO.IOUtil;
import Util.*;
import okhttp3.*;
import org.json.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import UIUtil.GameConfig;

public class AdminClient {
    private static final String BASE_URL = "http://localhost:8080" + Config.C_PATH;
    private final OkHttpClient client;
    private final Scanner scanner;
    private AdminUser adminUser;
    private boolean isLoggedIn = false;

    public AdminClient() {
        this.client = new OkHttpClient();
        this.scanner = new Scanner(System.in);
        this.adminUser = new AdminUser();
    }

    public void start(){
        System.out.println("Checking admin status...");
        checkAndLoginAdmin();
    }

    private void checkAndLoginAdmin() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.CHECK_ADMIN)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error checking admin status: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean adminExists = jsonResponse.getBoolean("adminExists");

                    if (adminExists) {
                        System.out.println("An admin is already logged in. You cannot run this admin app.");
                        System.exit(0);
                    } else {
                        registerDefaultAdmin();
                    }
                } else {
                    System.out.println("Failed to check admin status. Server responded with code: " + response.code());
                }
            }
        });
    }

    private void registerDefaultAdmin() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "Admin");
        jsonObject.put("isAdmin", true);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.REGISTER)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error during admin registration: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getString("status").equals("success")) {
                        System.out.println("Default admin registered successfully.");
                        loginDefaultAdmin();
                    } else {
                        System.out.println("Failed to register default admin: " + jsonResponse.getString("message"));
                    }
                } else {
                    System.out.println("Failed to register default admin. Server responded with code: " + response.code());
                }
            }
        });
    }


    private void loginDefaultAdmin() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "Admin");
        jsonObject.put("isAdmin", true);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.LOGIN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error during admin login: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getString("status").equals("success")) {
                        System.out.println("Default admin logged in successfully.");
                        adminUser = new AdminUser(jsonResponse.getInt(Config.MAX_COMMAND_ATTRIBUTE));
                        isLoggedIn = true;
                        adminMenu();
                    } else {
                        System.out.println("Failed to log in default admin: " + jsonResponse.getString("message"));
                    }
                } else {
                    System.out.println("Failed to log in default admin. Server responded with code: " + response.code());
                }
            }
        });
    }

    private void updateAdminMaxCommand(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", Config.ADMIN);
        jsonObject.put("isAdmin", true);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.UPDATE_MAX_COMMAND)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Error during admin get max command update: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getString("status").equals("success")) {
                        adminUser.setMaxCommand(jsonResponse.getInt(Config.MAX_COMMAND_ATTRIBUTE));
                    } else {
                        System.out.println("Failed to get the admins max command: " + jsonResponse.getString("message"));
                    }
                } else {
                    System.out.println("Failed to get the admins max command. Server responded with code: " + response.code());
                }
            }
        });
    }

    private void adminMenu() {
        while (isLoggedIn) {
            updateAdminMaxCommand();
            displayMainMenu();
            int maxCommand = adminUser.getMaxCommand();
            int choice = Util.Static.getValidIntInput("Choose an option (between 1 and "
                            + (maxCommand + 1) + "): ",1, maxCommand + 1);

            switch (choice) {
                case 1:
                    uploadGameFile();
                    break;
                case 2:
                    if(adminUser.getMaxCommand() == Config.FILE_UPLOAD_COMMAND_INT){
                        logout();
                        isLoggedIn = false;
                        return;
                    }
                    displayGameRoomsInfo();
                    break;
                case 3:
                    if(adminUser.getMaxCommand() == Config.ROOM_DETAILS_INT){
                        logout();
                        isLoggedIn = false;
                        return;
                    }
                    if(adminUser.getMaxCommand() < Config.WATCH_ACTIVE_GAME_INT){
                        System.out.println("You are not authorized to watch an active game yet.");
                        return;
                    }
                    watchActiveGame();
                    break;
                case 4:
                    logout();
                    isLoggedIn = false;
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        AdminClient client = new AdminClient();
        client.start();
    }

    public boolean loadPendingGame() {
        JSONObject jsonObject = new JSONObject();
        String xmlPath, dictionaryPath, xmlPathMessage, dictionaryPathMessage;
        StringBuilder result = new StringBuilder();
        boolean xmlPathValid, dictionaryPathValid;
        //GameCreator tmpGame = new GameCreator();
        System.out.println("Enter your xml file path: ");
        xmlPath = IOUtil.readInput(String.class);
        System.out.println("Enter your dictionary file path: ");
        dictionaryPath = IOUtil.readInput(String.class);
        xmlPathMessage = IOUtil.isXmlPathValid(xmlPath);
        dictionaryPathMessage = IOUtil.isDictionaryPathValid(dictionaryPath);
        xmlPathValid = xmlPathMessage.equals(GameConfig.VALID);
        dictionaryPathValid = dictionaryPathMessage.equals(GameConfig.VALID);
        if (xmlPathValid && dictionaryPathValid) {
            // If both paths are valid start uploading these 2 game files to the file upload servlet


//            loadGameFile(xmlPath, dictionaryPath);

        } else {
            if (!xmlPathValid) {
                result.append(xmlPathMessage);
            }
            if (!dictionaryPathValid) {
                result.append(dictionaryPathMessage);
            }
        }
        //return result.toString();
        // For comiplation!!
        return true;
    }


    private boolean login() {

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"username\":\"" + Config.ADMIN + "\",\"isAdmin\":true}"
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.LOGIN)
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                String status = jsonResponse.getString("status");

                switch (status) {
                    case "success":
                        System.out.println("Admin login successful!");
                        this.adminUser = new AdminUser();
                        this.adminUser.setMaxCommand(jsonResponse.getInt(Config.MAX_COMMAND_ATTRIBUTE));
                        return true;
                    case "not_registered":
                        System.out.println(jsonResponse.getString("message"));
                        return register();
                    case "error":
                        System.out.println("Login failed: " + jsonResponse.getString("message"));
                        System.out.println("Context path: " + BASE_URL);
                        System.out.println("Context path of response: " + jsonResponse.getString("contextPath"));
                        return false;
                    default:
                        System.out.println("Unknown response status: " + status);
                        return false;
                }
            } else {
                System.out.println("Login failed. Server responded with code: " + response.code());
                System.out.println("Context path: " + BASE_URL);
                return false;
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
    }


    private boolean register() {

        System.out.println("Enter new admin username:");
        String username = scanner.nextLine();

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"username\":\"" + username + "\",\"isAdmin\":true}"
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.REGISTER)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    System.out.println("Admin registration successful!" +
                            " Please log in to your newly registered account.");
                    login();
                    return true;

                } else {
                    System.out.println("Registration failed: " + jsonResponse.getString("message"));
                    return false;
                }
            } else {
                System.out.println("Registration failed. Server responded with code: " + response.code());
                return false;
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }

    }

    private void logout() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.LOGOUT + "?username=" + adminUser.getUsername())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            JSONObject jsonResponse = new JSONObject(response.body().string());
            System.out.println(jsonResponse.getString("message"));
        } catch (IOException e) {
            System.out.println("Error during logout: " + e.getMessage());
        } finally {
            shutdownClient();
            scanner.close();
        }
    }



    private void displayMainMenu() {
        StringBuilder result = new StringBuilder();
        List<String> tmp = new ArrayList<>();
        int i = 0;
        if(adminUser == null){
            System.out.println("Error: Not logged in. Please log in first.");
            return;
        }
        else {
            tmp.add("\n1. Upload Game File");
            tmp.add("\n2. Display Game Rooms Info");
            tmp.add("\n3. Watch Active Game");
            result.append("\nAdmin Main Menu:");
            for (i = 0; i < adminUser.getMaxCommand(); ++i){
                result.append(tmp.get(i));
            }
            result.append("\n" + (i + 1) + ". Exit");
            System.out.println(result.toString());
        }
    }


    private void uploadGameFile() {
        if (adminUser == null || adminUser.getMaxCommand() < GameConfig.FILE_UPLOAD) {
            System.out.println("Error: Not authorized to perform this action.");
            return;
        }

        System.out.println("Enter dictionary name:");
        String dictionaryName = scanner.nextLine();
        System.out.println("Enter dictionary file path:");
        String dictionaryPath = scanner.nextLine();
        System.out.println("Enter XML file path:");
        String xmlPath = scanner.nextLine();
        String xmlValid = Static.isPathValid(xmlPath, ".xml"),
                dictionaryValid = Static.isPathValid(dictionaryPath, ".txt");
        File xmlFile = new File(xmlPath);
        File dictionaryFile = new File(dictionaryPath);

        // TODO - add an option to print that BOTH files are invalid if so.

        if(!xmlValid.equals(Config.VALID)){
            System.out.println("Error: Invalid XML file path (doesn't end with .xml)");
            return;
        }
        if(!dictionaryValid.equals(Config.VALID)){
            System.out.println("Error: Invalid dictionary file path (doesn't end with .txt)");
            return;
        }

        if (!xmlFile.exists() || !dictionaryFile.exists()) {
            System.out.println("Error: One or both files do not exist.");
            return;
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", adminUser.getUsername())
                .addFormDataPart("dictionaryName", dictionaryName)
                .addFormDataPart("xml", xmlFile.getName(),
                        RequestBody.create(MediaType.parse("application/xml"), xmlFile))
                .addFormDataPart("dictionary", dictionaryFile.getName(),
                        RequestBody.create(MediaType.parse("text/plain"), dictionaryFile))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + Config.FILE_UPLOAD)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    System.out.println("Game Room Created Successfully");
                    System.out.println(jsonResponse.getString("roomDetails"));

                    if (jsonResponse.has("newMaxCommand")) {
                        adminUser.setMaxCommand(jsonResponse.getInt("newMaxCommand"));
                    }
                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to upload files. Server responded with code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error uploading files: " + e.getMessage());
        }
    }

    private void displayGameRoomsInfo() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.ADMIN_DISPLAY_GAME_ROOMS + "?username=" + Config.ADMIN)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                System.out.println("Status of response: " + jsonResponse.getString("status"));
                if(jsonResponse.getString("status").equals(Config.SUCCESS)){
                    System.out.println("Game Rooms Information:");
                    System.out.println(jsonResponse.getString("roomsDetails"));
                }
                else{
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }

            } else {
                System.out.println("Failed to retrieve game rooms information.");
            }
        } catch (IOException e) {
            System.out.println("Error retrieving game rooms info: " + e.getMessage());
        }
    }

    private void watchActiveGame() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.ADMIN_WATCH_GAME + "?username=" + adminUser.getUsername() + "&action=join")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    System.out.println("Active Games:");
                    JSONArray activeRooms = jsonResponse.getJSONArray("activeRooms");
                    for (int i = 0; i < activeRooms.length(); i++) {
                        JSONObject room = activeRooms.getJSONObject(i);
                        int activeTeams = room.getInt("activeTeams"), totalTeams = room.getInt("totalTeams");
                        System.out.println((i + 1) + ". " + room.getString("name") + " (Active teams: "
                                + activeTeams + " / " + totalTeams + ")");
                    }
                    System.out.print("Enter room number to watch (or 0 to cancel): ");
                    int choice = Integer.parseInt(scanner.nextLine());
                    if (choice > 0 && choice <= activeRooms.length()) {
                        String roomName = activeRooms.getJSONObject(choice - 1).getString("name");
                        watchGame(roomName);
                    }
                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to retrieve active games.");
            }
        } catch (IOException e) {
            System.out.println("Error retrieving active games: " + e.getMessage());
        }
    }

    private void watchGame(String roomName) {
        while (true) {
            System.out.println("\n1. Get game status");
            System.out.println("2. Return to main menu");
            int choice = Static.getValidIntInput("Enter your choice (1 or 2): ", 1, 2);

            if (choice == 1) {
                displayGameStatus(roomName);
            } else if (choice == 2) {
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayGameStatus(String roomName) {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.ADMIN_WATCH_GAME + "?username=" + adminUser.getUsername() + "&action=watch&roomName=" + roomName)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {

                    if (jsonResponse.getBoolean("gameOver")) {
                        System.out.println("Game Over!");
                        return;
                    }
                    String teamName = jsonResponse.getString("currentTeam");
                    System.out.println("\nCurrent Game State:");
                    System.out.println(jsonResponse.getString("gameState"));
                    System.out.println("Phase: " + jsonResponse.getString("phaseDescription"));
                    System.out.println("Current Team: " + teamName);
                    System.out.println("Score: " + jsonResponse.getInt("wordScore")
                            + " / " + jsonResponse.getInt("wordTotal"));
                    System.out.println(teamName + " has performed " + jsonResponse.getInt("turns")
                            + " turns so far");
                    if (jsonResponse.has("guessMessage")) {
                        System.out.println("Hint: " + jsonResponse.getString("hint"));
                        System.out.println("Guesses: " + jsonResponse.getInt("madeGuesses")  + " / "
                                + jsonResponse.getInt("allowedGuesses"));
                        System.out.println("Guess Message: " + jsonResponse.getString("guessMessage"));

                    }
                    else if(jsonResponse.has("hint")){
                        System.out.println("Hint: " + jsonResponse.getString("hint"));
                        System.out.println("Guesses: " + jsonResponse.getInt("madeGuesses")  + " / "
                                + jsonResponse.getInt("allowedGuesses"));
                        System.out.println();
                    }

                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to retrieve game state. Server responded with code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error watching game: " + e.getMessage());
        }
    }

    private void shutdownClient() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
        if (client.cache() != null) {
            try {
                client.cache().close();
            } catch (IOException e) {
                System.out.println("Error closing OkHttpClient cache: " + e.getMessage());
            }
        }
    }
}
