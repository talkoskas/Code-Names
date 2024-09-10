package Util;

import java.util.*;

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

    public static List<List<String>> splitStringSetToLists(Set<String> words,
                                                           int regularCount, int blackCount) {
        List<List<String>> lists = new ArrayList<>();
        List<String> regular = new ArrayList<>();
        List<String> black = new ArrayList<>();

        // Convert Set to List for easier random access
        List<String> wordList = new ArrayList<>(words);

        // Shuffle the list to ensure random selection
        Collections.shuffle(wordList);

        // Fill the regular list
        for (int i = 0; i < regularCount && i < wordList.size(); i++) {
            regular.add(wordList.get(i));
        }

        // Fill the black list
        for (int i = regularCount; i < regularCount + blackCount && i < wordList.size(); i++) {
            black.add(wordList.get(i));
        }

        // Add both lists to the result
        lists.add(regular);
        lists.add(black);

        return lists;
    }
}
