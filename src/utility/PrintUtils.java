package utility;

import java.util.Scanner;

public class PrintUtils {

    // Method to pause execution until the user presses Enter
    public static void pause() {
        System.out.println("Press Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Wait for user to hit Enter
    }
}
