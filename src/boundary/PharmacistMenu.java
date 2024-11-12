package boundary;

import controller.AppointmentOutcomeController;
import controller.InventoryController;
import controller.PrescriptionController;
import entity.Pharmacist;
import interfaces.MenuInterface;
import java.util.Scanner;

public class PharmacistMenu implements MenuInterface {

    private final Pharmacist pharmacist;
    private final InventoryController inventoryController = new InventoryController();
    private final AppointmentOutcomeController appointmentOutcomeController = new AppointmentOutcomeController();
    public static final String ANSI_TRUE_LIGHT_PURPLE = "\u001B[38;2;221;160;221m"; // Light Purple (#DDA0DD)

    // Constructor initializes the pharmacist instance.
    public PharmacistMenu(Pharmacist pharmacist) {
        this.pharmacist = pharmacist;
    }

    @Override
    public void displayMenu() {
        PrescriptionController prescriptionController = new PrescriptionController();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        // Display the Pharmacist menu and handle choices
        while (!exit) {
            System.out.println(ANSI_TRUE_LIGHT_PURPLE + "\n╔════════════════════════════════════════╗");
            System.out.println("║             Pharmacist Menu            ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("1. View Appointment Outcome Record");
            System.out.println("2. Update Prescription Status");
            System.out.println("3. View Medication Inventory");
            System.out.println("4. Submit Replenishment Request");
            System.out.println("0. Logout");
            System.out.println("══════════════════════════════════════════");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Display all pending appointment outcomes
                    appointmentOutcomeController.displayAllPendingAppointmentOutcomes();
                    break;
                case "2":
                    // Update prescription status
                    prescriptionController.updatePrescriptionStatus();
                    break;
                case "3":
                    // Display current medication inventory
                    inventoryController.displayInventory();
                    break;
                case "4":
                    // Submit a replenishment request for medications
                    inventoryController.requestReplenishment();
                    break;
                case "0":
                    // Log out and exit menu
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
