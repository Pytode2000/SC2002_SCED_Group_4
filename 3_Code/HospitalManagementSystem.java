
import java.util.Scanner;

public class HospitalManagementSystem {

    public static void main(String[] args) {

        AccountController accountController = new AccountController();
        Scanner scanner = new Scanner(System.in);
        String choice;

        System.out.println("Welcome to the Hospital Management System.");

        // Loop until user decides to exit with option 0
        do {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");

            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Login functionality is under construction.");
                    // Should return a user type. then call the respective controllers
                    break;
                case "2":
                    accountController.register();
                    break;
                case "0":
                    System.out.println("Exiting. Thank you for using the Hospital Management System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (!choice.equals("0"));

        scanner.close();
    }
}
