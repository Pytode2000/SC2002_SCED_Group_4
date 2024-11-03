package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.InventoryController;
import entity.Administrator;
import interfaces.MenuInterface;
import java.util.Scanner;

public class AdministratorMenu implements MenuInterface {

    private final Administrator administrator;
    AccountController accountController = new AccountController(); // For Login and Register methods.
    AppointmentController appointmentController = new AppointmentController();
    InventoryController inventoryController = new InventoryController();

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
                    manageStaff(scanner);
                    break;
                case "2":
                    // View Appointments details();
                    appointmentController.displayDoctorAppointmentDetails();
                    break;
                case "3":
                    // View and Manage Medication Inventory();
                    manageInventory(scanner);
                    break;
                case "4":
                    // Approve Replenishment Requests();
                    inventoryController.approveReplenishmentRequests();
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

    private void manageStaff(Scanner scanner) {
        boolean backToMenu = false;

        while (!backToMenu) {
            System.out.println("\n--- Staff Management ---");
            System.out.println("1. View Staff");
            System.out.println("2. Add Staff");
            System.out.println("3. Update Staff");
            System.out.println("4. Remove Staff");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Call a method to view staff
                    accountController.viewStaff();
                    break;
                case "2":
                    // Call a method to add staff
                    accountController.register(true); // isAdmin = false.
                    break;
                case "3":
                    // Call a method to update staff
                    accountController.updateStaff(scanner);
                    break;
                case "4":
                    // Call a method to remove staff
                    accountController.removeStaff(scanner);
                    break;
                case "0":
                    backToMenu = true; // Go back to the main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private void manageInventory(Scanner scanner) {
        boolean backToMenu = false;

        while (!backToMenu) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. View Inventory");
            System.out.println("2. Add Medicine");
            System.out.println("3. Update Medicine");
            System.out.println("4. Remove Medicine");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Call a method to view inventory
                    inventoryController.displayInventory();
                    break;
                case "2":
                    // Call a method to add item
                    inventoryController.addMedicine(scanner);
                    break;
                case "3":
                    // Call a method to update item
                    inventoryController.updateMedicine(scanner);
                    break;
                case "4":
                    // Call a method to remove item
                    inventoryController.removeMedicine(scanner);
                    break;
                case "0":
                    backToMenu = true; // Go back to the main menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

}
