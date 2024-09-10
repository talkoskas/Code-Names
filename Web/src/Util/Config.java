package Util;

public class Config {

    public static final int CLIENT_ROOM_DETAILS_INT = 1, CLIENT_JOIN_INT = 2, CLIENT_LEAVE_INT = 3;
    public static final int FILE_UPLOAD_COMMAND_INT = 1, ROOM_DETAILS_INT = 2, WATCH_ACTIVE_GAME_INT = 3;
    public static final int EXIT_GAME_INT = 4;
    public static final String PENDING_EMPTY_MESSAGE = "No pending rooms found in server";
    public static final String ACTIVE_EMPTY_MESSAGE = "No active rooms found in server";
    public static final String MAX_COMMAND_ATTRIBUTE = "maxCommand", USERNAME_ATTRIBUTE = "username", IS_ADMIN_ATTRIBUTE = "isAdmin";;
    public static final String SUCCESS = "success";
    public static final String VALID = "Valid";
    public static final String WELCOME = "/welcome", LOGIN = "/api/login", LOGOUT = "/api/logout";
    public static final String REGISTER = "/api/register", CLIENT_MAIN_MENU = "/api/player/main-menu";
    public static final String CLIENT_DISPLAY_GAME_ROOMS = "/api/player/display-game-rooms";
    public static final String JOIN_GAME = "/api/player/join-game", GAME_STATE = "/api/player/game-state";
    public static final String GAME_ROOM = "/client/game-room", ADMIN_MAIN_MENU = "/api/admin/main-menu";
    public static final String ADMIN_DISPLAY_GAME_ROOMS = "/api/admin/display-game-rooms";
    public static final String FILE_UPLOAD = "/api/admin/file-upload", ADMIN_JOIN_WATCH = "/admin/join-watch";
    public static final String GUESSER = "/api/player/game/guesser", DEFINER = "/api/player/game/definer";
    public static final String UPDATE_MAX_COMMAND = "/api/admin/update-max-command";
    public static final String ROOM_MANAGER_ATTRIBUTE = "roomManager";
    public static final String DICTIONARIES_MANAGER_ATTRIBUTE = "dictionariesManager";
    public static final String PLAYER_MANAGER_ATTRIBUTE = "playerManager";
    public static final String USER_MANAGER_ATTRIBUTE = "userManager", WAITING_ROOM = "/api/player/waiting-room";
    public static final String ECN_GAME_MANAGER_ATTRIBUTE = "ecnGameManager", ADMIN_WATCH_GAME = "/api/admin/watch-game";
    public static final String ROOM_NAME = "roomName", USERNAME = "username", GET = "GET", POST = "POST";
    public static final String GAME_OVER = "Game over", ONGOING = "Ongoing";
    public static final int GAME_INACTIVE = 1, DEFINER_PHASE = 2, GUESSER_PHASE = 3, PENDING = 4, ACTIVE = 5;


}
