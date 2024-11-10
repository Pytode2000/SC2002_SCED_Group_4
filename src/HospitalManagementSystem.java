
import controller.AccountController;
import controller.ForgetPasswordController;
import controller.MenuController;
import entity.User;
import java.util.Scanner;

public class HospitalManagementSystem {

    public static void main(String[] args) {

        // Initialize controllers for account and password management
        AccountController accountController = new AccountController();
        ForgetPasswordController forgetPasswordController = new ForgetPasswordController();
        Scanner scanner = new Scanner(System.in);
        String choice;

        // Main loop to display options until user chooses to exit
        do {
            System.out.println("\n--- Hospital Management System ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Forgot Password");
            System.out.println("0. Exit Program");
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
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (!choice.equals("0"));
    }
}
