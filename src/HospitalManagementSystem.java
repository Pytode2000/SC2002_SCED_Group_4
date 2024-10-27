
import controller.AccountController;
import controller.MenuController;
import entity.User;
import java.util.Scanner;

public class HospitalManagementSystem {

    public static void main(String[] args) {

        AccountController accountController = new AccountController(); // For Login and Register methods.
        Scanner scanner = new Scanner(System.in);
        String choice;

        // Loop until user decides to exit with option 0.
        do {
            System.out.println("\n--- Hospital Management System ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("\nEnter your choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    User currentUser = accountController.login();
                    if (currentUser != null) {
                        MenuController menuController = new MenuController(currentUser);
                        menuController.displayMenu();
                    }
                    break;

                case "2":
                    accountController.register(false); // isAdmin = false.
                    break;

                case "0":
                    System.out.println("Exiting. Thank you for using the Hospital Management System.");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (!choice.equals("0"));
    }
}
