package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.ForgetPasswordController;
import controller.InventoryController;
import entity.Administrator;
import interfaces.MenuInterface;
import java.util.Scanner;

public class AdministratorMenu implements MenuInterface {

    private final Administrator administrator;
    AccountController accountController = new AccountController(); // For Login and Register methods.
    AppointmentController appointmentController = new AppointmentController();
    InventoryController inventoryController = new InventoryController();
    ForgetPasswordController forgetPasswordController = new ForgetPasswordController();


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
            System.out.println("5. Manage Forget Password Requests"); // ForgetPasswordController

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

                    case "5":
                    // Approve Replenishment Requests();
                    forgetPasswordController.processForgetPasswordRequests();
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
            accountController.viewStaff();
            System.err.println("1. Filter Staff by Role/Gender/Age");
            System.out.println("2. Add Staff");
            System.out.println("3. Update Staff");
            System.out.println("4. Remove Staff");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Call a method to filter staff
                    filterStaff(scanner);
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

    private void filterStaff(Scanner scanner) {
        System.out.println("\n--- Filter Staff ---");
        System.out.println("1. Filter by Role");
        System.out.println("2. Filter by Gender");
        System.out.println("3. Filter by Age");
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                accountController.filterByRole(scanner);
                break;
            case "2":
                accountController.filterByGender(scanner);
                break;
            case "3":
                accountController.filterByAge(scanner);
                break;
            case "0":
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private void manageInventory(Scanner scanner) {
        boolean backToMenu = false;

        while (!backToMenu) {
            System.out.println("\n--- Inventory Management ---");
            inventoryController.displayInventory();
            System.out.println("1. Add Medicine");
            System.out.println("2. Update Medicine");
            System.out.println("3. Remove Medicine");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Call a method to add item
                    inventoryController.addMedicine(scanner);
                    break;
                case "2":
                    // Call a method to update item
                    inventoryController.updateMedicine(scanner);
                    break;
                case "3":
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
