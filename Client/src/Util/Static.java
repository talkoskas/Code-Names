package Util;

import java.util.Scanner;

public class Static {

    public static String isPathValid(String path, String endian) {
        String result;
        if(path.endsWith(endian)) {
            return Config.VALID;
        }
        else{
            return "\nPath does not end with " + endian;
        }
    }

    public static int getValidIntInput(String prompt, int min, int max) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
