package UIUtil;

public class GameConfig {
    private static int maxWords, maxBlackWords;
    private static int rowsPerWord, colsPerWord;
    public static final int PENDING = 4, END_TURN = -1;
    public static final int MIN_TEAMS_PER_GAME = 2, MAX_TEAMS_PER_GAME = 4;
    public static final int CHARS_PER_ROW_CARD = 24;
    public static final int ROWS_IN_CARD_DEFINER = 5, ROWS_IN_CARD_GUESSER = 4;
    public static final int FILE_UPLOAD = 1, ROOM_DETAILS = 2;
    public static final int WATCH_ACTIVE_GAME = 3, EXIT_GAME = 4;

    public static final int GAME_INACTIVE = 1, DEFINER_PHASE = 2, GUESSER_PHASE = 3;
    public static final int GET_GAME_STATUS = 1, PERFORM_TURN = 2;
    public static final int DISPLAY_GAME_INFO = 1, JOIN_GAME = 2, EXIT = 3;
    public static final String GAME_OVER = "Game over", ONGOING = "Ongoing";
    public static final String XML_ENDIAN = ".xml", DICTIONARY_ENDIAN = ".txt";
    public static final String VALID = "Valid";



    public static int getMaxWords(){
        return maxWords;
    }

    public static int getMaxBlackWords(){
        return maxBlackWords;
    }

    public static int getRowsPerWord(){
        return rowsPerWord;
    }

    public static int getColsPerWord(){
        return colsPerWord;
    }

    public static void setMaxWords(int maxWords){
        GameConfig.maxWords = maxWords;
    }

    public static void setMaxBlackWords(int maxBlackWords){
        GameConfig.maxBlackWords = maxBlackWords;
    }

    public static void setRowsPerWord(int rowsPerWord){
        GameConfig.rowsPerWord = rowsPerWord;
    }

    public static void setColsPerWord(int colsPerWord){
        GameConfig.colsPerWord = colsPerWord;
    }

}
