package Player;

import GameIO.DTO.GameStateDTO;
import Util.Static;
import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.util.Scanner;
import Util.Config;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PlayerClient {
    private static final String BASE_URL = "http://localhost:8080" + Config.C_PATH;
    private final OkHttpClient client;
    private final Scanner scanner;
    private boolean isLoggedIn = false, inGame = false;
    private PlayerUser playerUser;
    private GameStateDTO gameState;
    private int maxGameCommand = Config.CLIENT_ROOM_DETAILS_INT;
    private static final Logger logger = Logger.getLogger(PlayerClient.class.getName());


    public PlayerClient() {
        this.client = new OkHttpClient();
        this.scanner = new Scanner(System.in);
    }


    public void start() {
        System.out.println("Welcome to Code Names v2!");
        registerAndLogin();

        while (isLoggedIn) {
            displayMainMenu();
            int maxChoice = (playerUser.getMaxCommand() >= Config.CLIENT_JOIN_INT) ? 3 : 2;
            int choice = Static.getValidIntInput("Enter your choice (between 1 and " + maxChoice + "): ", 1, maxChoice);
            switch (choice) {
                case 1:
                    displayGameRoomsInfo();
                    break;
                case 2:
                    if (playerUser.getMaxCommand() >= Config.CLIENT_JOIN_INT) {
                        joinGame();
                    } else {
                        logout();
                        isLoggedIn = false;
                    }
                    break;
                case 3:
                    if (playerUser.getMaxCommand() >= Config.CLIENT_JOIN_INT) {
                        logout();
                        isLoggedIn = false;
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void registerAndLogin() {
        while (!isLoggedIn) {
            System.out.println("Please enter a unique username to register:");
            String username = scanner.nextLine();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("isAdmin", false);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    jsonObject.toString()
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + Config.REGISTER)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getString("status").equals("success")) {
                        System.out.println("Registration successful! Logging you in...");
                        login(username);
                    } else {
                        System.out.println("Registration failed: " + jsonResponse.getString("message"));
                    }
                } else {
                    System.out.println("Registration failed. Server responded with code: " + response.code());
                }
            } catch (IOException | JSONException e) {
                System.out.println("Error during registration: " + e.getMessage());
            }
        }
    }

    private void login(String username) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Config.USERNAME_ATTRIBUTE, username);
        jsonObject.put(Config.IS_ADMIN_ATTRIBUTE, false);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonObject.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.LOGIN)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.getString("status").equals("success")) {
                    System.out.println("Login successful!");
                    playerUser = new PlayerUser(username, jsonResponse.getInt(Config.MAX_COMMAND_ATTRIBUTE));
                    isLoggedIn = true;
                } else {
                    System.out.println("Login failed: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Login failed. Server responded with code: " + response.code());
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }


    private void displayGameRoomsInfo() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.CLIENT_DISPLAY_GAME_ROOMS + "?username=" + playerUser.getUsername())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    System.out.println("Game Rooms Information:");
                    System.out.println(jsonResponse.getString("roomsDetails"));
                    if (jsonResponse.has("maxCommand")) {
                        int newMaxCommand = jsonResponse.getInt("maxCommand");
                        playerUser.setMaxCommand(newMaxCommand);
                    }
                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to retrieve game rooms information.");
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error retrieving game rooms info: " + e.getMessage());
        }
    }



    private void displayMainMenu() {
        System.out.println("\nPlayer Main Menu:");
        System.out.println("1. Display Game Rooms Info");
        if (playerUser.getMaxCommand() >= Config.CLIENT_JOIN_INT) {
            System.out.println("2. Join Game");
            System.out.println("3. Logout");
        } else {
            System.out.println("2. Logout");
        }
        System.out.print("Enter your choice: ");
    }

    private void displayGameMenu(){
        if (inGame == false) {
            if(gameState.isGameOver()){
                System.out.println("Game Over!\n");
                System.out.println(gameState.getFinalMessage());
                return;
            }
            System.out.println("\nYou are not currently in a game. Join a pending game first!");
            return;
        }
        System.out.println("1. Get game status");
        if(playerUser.getMaxCommand() >= Config.EXECUTE_TURN){
            System.out.println("2. Execute turn");
        }
    }


    private void joinGame() {
        if (playerUser.getMaxCommand() < Config.CLIENT_JOIN_INT) {
            System.out.println("You are not authorized to join a game.");
            return;
        }

        try {
            String roomsDetails = getPendingRoomsDetails();
            System.out.println("Available Rooms:");
            System.out.println(roomsDetails);

            int roomChoice = Util.Static.getValidIntInput("Enter room number to join: ", 1, Integer.MAX_VALUE) - 1;

            // Get available teams for the selected room
            JSONObject teamsRequest = new JSONObject();
            teamsRequest.put("username", playerUser.getUsername());
            teamsRequest.put("roomIndex", roomChoice);

            RequestBody teamsBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    teamsRequest.toString()
            );

            Request teamsListRequest = new Request.Builder()
                    .url(BASE_URL + Config.GET_AVAILABLE_TEAMS)
                    .post(teamsBody)
                    .build();

            try (Response teamsResponse = client.newCall(teamsListRequest).execute()) {
                if (teamsResponse.isSuccessful()) {
                    JSONObject teamsJsonResponse = new JSONObject(teamsResponse.body().string());
                    if (teamsJsonResponse.getString("status").equals("success")) {
                        JSONArray teams = teamsJsonResponse.getJSONArray("teams");
                        System.out.println("Available Teams:");
                        List<Integer> availableTeamIndices = new ArrayList<>();
                        for (int i = 0; i < teams.length(); i++) {
                            availableTeamIndices.add(i);
                            System.out.println(availableTeamIndices.size() + ". " + teams.getString(i));
                        }

                        if (availableTeamIndices.isEmpty()) {
                            System.out.println("No available teams to join.");
                            return;
                        }

                        int userChoice = Util.Static.getValidIntInput("Enter team number to join: ", 1, availableTeamIndices.size()) - 1;
                        int teamChoice = availableTeamIndices.get(userChoice);

                        System.out.println("Choose your role:");
                        System.out.println("1. Definer");
                        System.out.println("2. Guesser");
                        int roleChoice = Util.Static.getValidIntInput("Enter your choice (1 or 2): ", 1, 2);
                        String role = (roleChoice == 1) ? "definer" : "guesser";

                        JSONObject jsonRequest = new JSONObject();
                        jsonRequest.put("username", playerUser.getUsername());
                        jsonRequest.put("roomIndex", roomChoice);
                        jsonRequest.put("teamIndex", teamChoice);
                        jsonRequest.put("role", role);

                        RequestBody body = RequestBody.create(
                                MediaType.parse("application/json"),
                                jsonRequest.toString()
                        );

                        Request request = new Request.Builder()
                                .url(BASE_URL + Config.JOIN_GAME)
                                .post(body)
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful()) {
                                JSONObject jsonResponse = new JSONObject(response.body().string());
                                if (jsonResponse.getString("status").equals("success")) {
                                    System.out.println("Successfully joined the game!");
                                    String roomName = jsonResponse.getString("roomName");
                                    String team = jsonResponse.getString("team");
                                    boolean isDefiner = jsonResponse.getBoolean("isDefiner");
                                    playerUser.setSelectedRoomName(roomName);
                                    playerUser.setTeamName(team);
                                    playerUser.setDefiner(isDefiner);
                                    enterGameState();
                                } else {
                                    System.out.println("Error joining game: " + jsonResponse.getString("message"));
                                    System.out.println("Please try again with a different team or role.");
                                }
                            } else {
                                System.out.println("Failed to join game. Server responded with code: " + response.code());
                            }
                        }
                    } else {
                        System.out.println("Error getting available teams: " + teamsJsonResponse.getString("message"));
                    }
                } else {
                    System.out.println("Failed to get available teams. Server responded with code: " + teamsResponse.code());
                }
            }
        } catch (Exception e) {
            System.out.println("Error during join game process: " + e.getMessage());
        }
    }


    private void enterGameState() {
        inGame = true;
        while (inGame) {
            try {
                updateGameState();
                if (gameState.isGameOver()) {
                    System.out.println("Game Over!\n");
                    System.out.println(gameState.getFinalMessage());
                    inGame = false;
                    break;  // Exit the game loop immediately
                }
                displayGameMenu();
                int choice = Static.getValidIntInput("\nEnter your choice: ", 1, playerUser.getMaxCommand());
                switch (choice) {
                    case 1:
                        displayGameStatus();
                        break;
                    case 2:
                        if (playerUser.getMaxCommand() >= Config.EXECUTE_TURN) {
                            executeTurn();
                        } else {
                            inGame = false;  // Exit the game
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Returning to game menu...");
            }
        }
        System.out.println("Returning to main menu...");
    }
    private void updateGameState() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.GAME_STATE + "?username=" + playerUser.getUsername() + "&roomName=" + playerUser.getSelectedRoomName())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    this.gameState = new GameStateDTO(
                            jsonResponse.getInt("phase"),
                            jsonResponse.getString("currentTeamName"),
                            jsonResponse.getBoolean("isGameOver"),
                            jsonResponse.getString("gameState"),
                            jsonResponse.getString("playerRole"),
                            jsonResponse.getString("playerTeam"),
                            jsonResponse.getString("guessMessage"),
                            jsonResponse.getString("finalMessage")
                    );

                    updateClientState(gameState);

                    if (gameState.isGameOver()) {
                        System.out.println("Game Over!");
                        System.out.println(gameState.getFinalMessage());
                        inGame = false;
                    } else {
                        System.out.println(gameState);  // This will print the full game state
                    }
                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to retrieve game state. Server responded with code: " + response.code());
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error retrieving game state: " + e.getMessage());
        }
    }

    private void updateClientState(GameStateDTO gameState) {
        boolean isPlayerTurn = gameState.getCurrentTeamName().equals(playerUser.getTeamName());
        boolean isPlayerPhase = (gameState.getPlayerRole().equals("definer") && gameState.getPhase() == Config.DEFINER_PHASE) ||
                (gameState.getPlayerRole().equals("guesser") && gameState.getPhase() == Config.GUESSER_PHASE);

        if (isPlayerTurn && isPlayerPhase) {
            playerUser.setMaxCommand(Config.EXECUTE_TURN);
        } else {
            playerUser.setMaxCommand(Config.CLIENT_ROOM_DETAILS_INT);
        }
    }
    private void displayGameStatus() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.GAME_STATE
                        + "?username=" + playerUser.getUsername() + "&roomName=" + playerUser.getSelectedRoomName())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    this.gameState = new GameStateDTO(
                            jsonResponse.getInt("phase"),
                            jsonResponse.getString("currentTeamName"),
                            jsonResponse.getBoolean("isGameOver"),
                            jsonResponse.getString("gameState"),
                            jsonResponse.getString("playerRole"),
                            jsonResponse.getString("playerTeam"),
                            jsonResponse.getString("guessMessage"),
                            jsonResponse.getString("finalMessage")
                    );
                    System.out.println(gameState);

                    if(gameState.getPhase() == Config.GUESSER_PHASE){
                        System.out.println("\n" + gameState.getGuessMessage());
                    }

                } else {
                    System.out.println("Error: " + jsonResponse.getString("message"));
                }
            } else {
                System.out.println("Failed to retrieve game status.");
            }
        } catch (IOException e) {
            System.out.println("Error retrieving game status: " + e.getMessage());
        }
    }

    private void executeTurn() {
        if (playerUser.isDefiner()) {
            executeDefinerTurn();
        } else {
            executeGuesserTurn();
        }
    }

    private void executeDefinerTurn() {
        System.out.println("Enter your hint:");
        String hint = scanner.nextLine();
        System.out.println();
        int numWords = Static.getValidIntInput("Enter number of words: ", 1, Integer.MAX_VALUE);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("username", playerUser.getUsername());
        jsonRequest.put("roomName", playerUser.getSelectedRoomName());
        jsonRequest.put("hint", hint);
        jsonRequest.put("guesses", numWords);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonRequest.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.DEFINER)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                System.out.println(jsonResponse.getString("message"));
            } else {
                System.out.println("Failed to execute turn.");
            }
        } catch (IOException e) {
            System.out.println("Error executing turn: " + e.getMessage());
        }
    }

    private void executeGuesserTurn() {
        int guess = Static.getValidIntInput("Enter your guess (card number) - or enter 0 to end your turn: ",
                0, Integer.MAX_VALUE);

        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("username", playerUser.getUsername());
        jsonRequest.put("roomName", playerUser.getSelectedRoomName());
        jsonRequest.put("guess", guess);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                jsonRequest.toString()
        );

        Request request = new Request.Builder()
                .url(BASE_URL + Config.GUESSER)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                String status = jsonResponse.getString("status");
                String message = jsonResponse.getString("message");

                if (status.equals("success")) {
                    System.out.println(message);
                }else if(status.equals("teamout")){
                    System.out.println("Your team is out of the game: " + message);
                    if (playerUser.getTeamName().equals(gameState.getCurrentTeamName())) {
                        inGame = false;  // Exit the game loop if it's the player's team
                    }
                } else if (status.equals("gameover")) {
                    System.out.println("Game Over: " + message);
                    inGame = false;  // Exit the game loop
                } else {
                    System.out.println("Error: " + message);
                }
            } else {
                System.out.println("Failed to execute turn. Server responded with code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("Error executing turn: " + e.getMessage());
        }
    }


    private String getPendingRoomsDetails() throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.CLIENT_DISPLAY_GAME_ROOMS + "?username=" + playerUser.getUsername())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                if (jsonResponse.getString("status").equals("success")) {
                    return jsonResponse.getString("roomsDetails");
                } else {
                    throw new IOException("Error: " + jsonResponse.getString("message"));
                }
            } else {
                throw new IOException("Failed to retrieve pending rooms. Server responded with code: " + response.code());
            }
        }
    }



    private void logout() {
        Request request = new Request.Builder()
                .url(BASE_URL + Config.LOGOUT + "?username=" + playerUser.getUsername())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(response.body().string());
                System.out.println(jsonResponse.getString("message"));
                isLoggedIn = false;
            } else {
                System.out.println("Logout failed. Server responded with code: " + response.code());
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error during logout: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        PlayerClient playerClient = new PlayerClient();
        playerClient.start();
    }

}

