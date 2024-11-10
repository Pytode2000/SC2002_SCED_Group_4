package utility;

import java.util.Scanner;

public class PrintUtils {

    // Pauses execution until the user presses Enter
    public static void pause() {
        System.out.println("Press Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Waits for the user to press Enter
    }
}
