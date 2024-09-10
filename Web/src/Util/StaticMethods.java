package Util;

import jakarta.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class StaticMethods {

    public static List<List<String>> divideIntoBlackAndWords(Set<String> dictionary, int cardsCount, int blackCardsCount) {
        List<String> words = new ArrayList<>(dictionary);
        List<String> blackWords = new ArrayList<>();
        if(cardsCount + blackCardsCount > dictionary.size()) {
            return null;
        }
        for (int i = 0; i < blackCardsCount; i++) {
            blackWords.add(words.remove(0));
        }

        List<String> remainingWords = new ArrayList<>(words.subList(0, cardsCount));
        return Arrays.asList(remainingWords, blackWords);
    }
}
