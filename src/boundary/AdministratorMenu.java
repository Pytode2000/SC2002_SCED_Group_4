package boundary;

import controller.AccountController;
import entity.Administrator;
import interfaces.MenuInterface;
import java.util.Scanner;

public class AdministratorMenu implements MenuInterface {

    private final Administrator administrator;
    AccountController accountController = new AccountController(); // For Login and Register methods.

    // Import controller thats patient need e.g., appointment etc.
    // All the functions should be in controller. this menu class just uses it.
    public AdministratorMenu(Administrator administrator) {
        this.administrator = administrator;
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Administrator Menu ---");
            System.out.println("1. View and Manage Hospital Staff"); // AccountController
            System.out.println("2. View Appointments Details"); // AppointmentController
            System.out.println("3. View and Manage Medication Inventory"); // InventoryController
            System.out.println("4. Approve Replenishment Requests"); // InventoryController
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // View and Manage Hospital Staff();
                    accountController.register(true); // isAdmin = false.
                    break;
                case "2":
                    // View Appointments details();
                    break;
                case "3":
                    // View and Manage Medication Inventory();
                    break;
                case "4":
                    // Approve Replenishment Requests();
                    break;
                case "0":
                    System.out.println("Logging out...");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

}
