package UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static String padString(String word, int totalLength) {
        int spacesToAdd = totalLength - word.length() - 2; // 2 for the '|' characters
        int leftSpaces = spacesToAdd / 2;
        int rightSpaces = spacesToAdd - leftSpaces;

        StringBuilder paddedWord = new StringBuilder("|");

        for (int i = 0; i < leftSpaces; i++) {
            paddedWord.append(" ");
        }

        paddedWord.append(word);

        for (int i = 0; i < rightSpaces; i++) {
            paddedWord.append(" ");
        }

        paddedWord.append("|");

        return paddedWord.toString();
    }

    public static String duplicateChar(char src, int times){
        StringBuilder newWord = new StringBuilder(src);
        while(times-- > 0){
            newWord.append(src);
        }
        return newWord.toString();
    }

    // Method splits a given string by its given delimiter while removing a certain string value
    // from it before splitting
    public static List<String> splitStringToList(String words, String delimiter, String toRemove){
        String filtered = words.replace(toRemove, "");
        String[] parts = filtered.split(delimiter);
        return new ArrayList<>(Arrays.asList(parts));
    }


    private static boolean isPathEndValid(String path, String end) {
        return path.endsWith(end);
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}
