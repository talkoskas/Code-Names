package GameIO;


import UIUtil.GameConfig;
import java.io.*;
import java.util.*;

// Class UIUtil contains static utility methods to check validity (mostly) for input
public class IOUtil {
    public static boolean isNumberInRange(int number, int min, int max){
        return number >= min && number <= max;
    }

    public static int readInputInRange(int min, int max){
        Scanner sc = new Scanner(System.in);
        Integer num = readInput(Integer.class);
        boolean condition1 = !isInputValid(Integer.class, num), condition2 = !isNumberInRange(num, min, max);
        while(condition1 || condition2){
            if(condition1){
                System.out.print("Input is not a valid number, please enter a number: ");
            }
            else{
                System.out.println("Number " + num + " is not within range.\nPlease enter a number between " + min + " and " + max);
            }
            num = readInput(Integer.class);
            condition1 = !isInputValid(Integer.class, num);
            condition2 = !isNumberInRange(num, min, max);
        }
        return num;
    }

    public static <T> T readInput(Class<T> cls) {
        Scanner scanner = new Scanner(System.in);
        T input = null;

        while (input == null) {
            try {
                if (cls == Integer.class || cls == int.class) {
                    input = cls.cast(scanner.nextInt());
                } else if (cls == String.class) {
                    input = cls.cast(scanner.nextLine());
                } else {
                    // Handle other types if needed
                    throw new IllegalArgumentException("Unsupported type: " + cls.getSimpleName());
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid " + cls.getSimpleName() + ".");
                scanner.nextLine(); // Consume the invalid input
            }
        }
        return input;
    }

    public static <T> boolean isInputValid(Class<?> inputType, T actual){
        if(!actual.getClass().equals(inputType))
            System.out.println("FLAG!!!!");
        return actual.getClass().equals(inputType);
    }

    public static int readInputInRangeExcept( int min, int max, int... except){
        Scanner sc = new Scanner(System.in);
        Integer num;
        int realMax = max, realMin = min;
        String msg = "";
        boolean condition = false;
        for(int exc : except){
            msg = msg.concat( exc + ", ");
            if (exc > realMax){
                realMax = exc;
            }
            if(exc < realMin){
                realMin = exc;
            }
        }
        msg = msg.concat("\b");

        num = readInputInRange(realMin, realMax);
        for(int exc : except) {
            if (num == exc) {
                condition = true;
                break;
            }
        }
        while(!condition && !isNumberInRange(num, min, max)){
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ", ");
            System.out.println("or enter " + msg);
            num = readInputInRange(realMin, realMax);
            for(int exc : except) {
                if (num == exc) {
                    condition = true;
                    break;
                }
            }
        }
        return num;
    }


    private static boolean isPathEndValid(String path, String end) {
        return path.endsWith(end);
    }

    private static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String isXmlPathValid(String path) {
        return isPathValid(path, true);
    }

    public static String isDictionaryPathValid(String path) {
        return isPathValid(path, false);
    }

    public static String isPathValid(String path, boolean xml){
           if(xml){
               if(isPathEndValid(path, GameConfig.XML_ENDIAN)){
                   if(fileExists(path)){
                       return GameConfig.VALID;
                   }
                   else{
                       return "File doesn't exist in given path " + path;
                   }
               }
               else{
                   return "File is not xml";
               }
           }
           else{
               if(isPathEndValid(path, GameConfig.DICTIONARY_ENDIAN)){
                   if(fileExists(path)) {
                       return GameConfig.VALID;
                   }
                   else{
                       return "File doesn't exist in given path " + path;
                   }
               }
               else{
                   return "File is not txt";
               }
           }

    }


    public static Set<String> processWords(Set<String> words) {
        // Add a check to see if cards count + black card count is bigger than
        // the amount of words in the dictionary
        Set<String> uniqueWords = new HashSet<>();

        for (String word : words) {
            // Split the string by spaces
            String[] splitWords = word.split(" ");
            for (String splitWord : splitWords) {
                // Remove specified characters from each split word
                String cleanedWord = splitWord.replaceAll("[\"=?:;.,_\\-)(*&^%$#@+!<>1234567890\\[\\]]", "");

                if (!cleanedWord.isEmpty()) {
                    uniqueWords.add(cleanedWord.toLowerCase()); // Add the cleaned word to the set
                }
            }
        }

        return uniqueWords;
    }

}


