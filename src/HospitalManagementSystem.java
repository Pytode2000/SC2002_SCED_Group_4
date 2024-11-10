
import controller.AccountController;
import controller.ForgetPasswordController;
import controller.MenuController;
import entity.User;
import java.util.Scanner;
import utility.PrintUtils;

public class HospitalManagementSystem {

    public static void main(String[] args) {

        // Initialize controllers for account and password management
        AccountController accountController = new AccountController();
        ForgetPasswordController forgetPasswordController = new ForgetPasswordController();
        Scanner scanner = new Scanner(System.in);
        String choice;

        // Main loop to display options until user chooses to exit
        do {
            System.out.println(" ___       __   _______   ___       ________  ________  _____ ______   _______      ");
            System.out.println("|\\  \\     |\\  \\|\\  ___ \\ |\\  \\     |\\   ____\\|\\   __  \\|\\   _ \\  _   \\|\\  ___ \\     ");
            System.out.println("\\ \\  \\    \\ \\  \\ \\   __/|\\ \\  \\    \\ \\  \\___|\\ \\  \\|\\  \\ \\  \\\\\\__\\ \\  \\ \\   __/|    ");
            System.out.println(" \\ \\  \\  __\\ \\  \\ \\  \\_|/_\\ \\  \\    \\ \\  \\    \\ \\  \\\\\\  \\ \\  \\\\|__| \\  \\ \\  \\_|/__  ");
            System.out.println("  \\ \\  \\|\\__\\_\\  \\ \\  \\_|\\ \\ \\  \\____\\ \\  \\____\\ \\  \\\\\\  \\ \\  \\    \\ \\  \\ \\  \\_|\\ \\ ");
            System.out.println("   \\ \\____________\\ \\_______\\ \\_______\\ \\_______\\ \\_______\\ \\__\\    \\ \\__\\ \\_______\\");
            System.out.println("    \\|____________|\\|_______|\\|_______|\\|_______|\\|_______|\\|__|     \\|__|\\|_______|");
            System.out.println("                                                      ");
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║       Hospital Management System       ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("0. Exit Program");
            System.out.println("══════════════════════════════════════════");

            System.out.print("\nEnter your choice: ");

            choice = scanner.nextLine();

            // Process user's choice
            switch (choice) {
                case "1":
                    // Log in the user and display the menu if successful
                    User currentUser = accountController.login();
                    if (currentUser != null) {
                        MenuController menuController = new MenuController(currentUser);
                        menuController.displayMenu();
                    }
                    break;

                case "2":
                    // Register a new user (not an admin)
                    accountController.register(false);
                    break;

                case "3":
                    // Handle a forgotten password request
                    forgetPasswordController.handleForgetPasswordRequest();
                    break;

                case "0":
                    // Exit the program
                    System.out.println("Exiting. Thank you for using the Hospital Management System.");

                    System.out.println(" ________  ________  ________  ________  ________      ___    ___ _______      ");
                    System.out.println("|\\   ____\\|\\   __  \\|\\   __  \\|\\   ___ \\|\\   __  \\    |\\  \\  /  /|\\  ___ \\     ");
                    System.out.println("\\ \\  \\___|\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\_|\\ \\ \\  \\|\\ /_   \\ \\  \\/  / | \\   __/|    ");
                    System.out.println(" \\ \\  \\  __\\ \\  \\\\\\  \\ \\  \\\\\\  \\ \\  \\ \\\\ \\ \\   __  \\   \\ \\    / / \\ \\  \\_|/__  ");
                    System.out.println("  \\ \\  \\|\\  \\ \\  \\\\\\  \\ \\  \\\\\\  \\ \\  \\_\\\\ \\ \\  \\|\\  \\   \\/  /  /   \\ \\  \\_|\\ \\ ");
                    System.out.println("   \\ \\_______\\ \\_______\\ \\_______\\ \\_______\\ \\_______\\__/  / /      \\ \\_______\\");
                    System.out.println("    \\|_______|\\|_______|\\|_______|\\|_______|\\|_______|\\___/ /        \\|_______|");
                    System.out.println("                                                     \\|___|/                    ");
                    PrintUtils.pause();
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (!choice.equals("0"));
    }
}
