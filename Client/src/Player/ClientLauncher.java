package Player;


import Admin.AdminClient;

import java.util.Scanner;

public class ClientLauncher {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1 for Admin Client, 2 for Player Client:");
        int choice = scanner.nextInt();
        
        if (choice == 1) {
            new AdminClient().start();
        } else if (choice == 2) {
            new PlayerClient().start();
        } else {
            System.out.println("Invalid choice");
        }
    }
}