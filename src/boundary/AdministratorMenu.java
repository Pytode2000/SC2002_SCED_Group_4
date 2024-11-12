package boundary;

import controller.AccountController;
import controller.AppointmentController;
import controller.BillController;
import controller.ForgetPasswordController;
import controller.InventoryController;
import entity.Administrator;
import interfaces.MenuInterface;
import java.util.Scanner;

public class AdministratorMenu implements MenuInterface {

    private final Administrator administrator;
    private final AccountController accountController;
    private final AppointmentController appointmentController;
    private final InventoryController inventoryController;
    private final ForgetPasswordController forgetPasswordController;
    private final BillController billController = new BillController();
    public static final String ANSI_TRUE_LIGHT_RED = "\u001B[38;2;255;182;193m"; 

    // Initialize AdministratorMenu with necessary controllers for admin actions
    public AdministratorMenu(Administrator administrator) {
        this.administrator = administrator;
        this.accountController = new AccountController();
        this.appointmentController = new AppointmentController();
        this.inventoryController = new InventoryController();
        this.forgetPasswordController = new ForgetPasswordController();
    }

    @Override
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println(ANSI_TRUE_LIGHT_RED + "\n╔════════════════════════════════════════╗");
            System.out.println("║           Administrator Menu           ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. View and Manage Hospital Staff");
            System.out.println("2. View Appointment Details");
            System.out.println("3. View and Manage Medication Inventory");
            System.out.println("4. Approve Replenishment Requests");
            System.out.println("5. Manage Forget Password Requests");
            System.out.println("6. Send Bills to Patients");

            System.out.println("0. Logout");
            System.out.println("══════════════════════════════════════════");

            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    manageStaff(scanner);
                    break;
                case "2":
                    appointmentController.displayDoctorAppointmentDetails();
                    break;
                case "3":
                    manageInventory(scanner);
                    break;
                case "4":
                    inventoryController.approveReplenishmentRequests();
                    break;
                case "5":
                    forgetPasswordController.processForgetPasswordRequests();
                    break;
                case "6":
                    BillController.viewAndUpdatePendingBills();
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

    // Manage hospital staff options
    private void manageStaff(Scanner scanner) {
        boolean backToMenu = false;

        while (!backToMenu) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║             Staff Management           ║");
            System.out.println("╚════════════════════════════════════════╝");
            accountController.viewStaff();
            System.out.println("1. Filter Staff by Role/Gender/Age");
            System.out.println("2. Add Staff");
            System.out.println("3. Update Staff");
            System.out.println("4. Remove Staff");
            System.out.println("0. Back to Main Menu");
            System.out.println("══════════════════════════════════════════");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    filterStaff(scanner);
                    break;
                case "2":
                    accountController.register(true);
                    break;
                case "3":
                    accountController.updateStaff(scanner);
                    break;
                case "4":
                    accountController.removeStaff(scanner);
                    break;
                case "0":
                    backToMenu = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // Filter hospital staff by different attributes
    private void filterStaff(Scanner scanner) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              Filter Staff              ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.println("1. Filter by Role");
        System.out.println("2. Filter by Gender");
        System.out.println("3. Filter by Age");
        System.out.println("0. Back to Main Menu");
        System.out.println("══════════════════════════════════════════");
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

    // Manage medication inventory options
    private void manageInventory(Scanner scanner) {
        boolean backToMenu = false;

        while (!backToMenu) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║          Inventory Management          ║");
            System.out.println("╚════════════════════════════════════════╝");
            inventoryController.displayInventory();
            System.out.println("1. Add Medicine");
            System.out.println("2. Update Medicine");
            System.out.println("3. Remove Medicine");
            System.out.println("0. Back to Main Menu");
            System.out.println("══════════════════════════════════════════");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    inventoryController.addMedicine(scanner);
                    break;
                case "2":
                    inventoryController.updateMedicine(scanner);
                    break;
                case "3":
                    inventoryController.removeMedicine(scanner);
                    break;
                case "0":
                    backToMenu = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
